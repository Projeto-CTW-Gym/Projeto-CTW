package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import model.User;
import model.dto.UserDTO;
import repositories.UserRepository;
import utils.PasswordUtils;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

@RequestScoped
public class UserService extends GenericEntityService<UserRepository, User> {

	private final String UPLOADED_FILE_PATH = "/Users/alunomanha/Documents/";// mudar o caminho da pasta
	//private final String UPLOADED_FILE_PATH = "C:/Users/Utilizador/Desktop/UpAcademy/img/";
	
	@Inject
	UserSubscriptionService userSubscriptionService;
	
	
	
	@Transactional
	public UserDTO updateEntity(long id, UserDTO entity) throws Exception {
//		System.out.println(entity.toString());
//		System.out.println(convertToUser(entity).toString());
//		System.out.println(convertToUserDTO(convertToUser(entity)).toString());
		return convertToUserDTO(repository.editEntity(convertToUser(entity)));
	}

	@Transactional
	public User findUserByEmail(String email) {
		return repository.findUserByEmail(email);
	}

	@Transactional
	public void createEntity(UserDTO userDTO) throws Exception {

		String email = userDTO.getEmail();

		if (!isValidEmailAddress(email)) {
			throw new BadRequestException("Invalid email");
		}

		User user = new User();

		// password->(hash, salt)
		String password = userDTO.getPassword();

		String[] hashCode = passwordToHashcode(password);

		// set fields to Entity
		user.setHashcode(hashCode[0]);
		user.setSalt(hashCode[1]);
		user.setEmail(email);
		user.setName(userDTO.getName());
		user.setImgUrl(userDTO.getImgUrl());

		// Adicionar entity ao repositorio
		repository.createEntity(user);
	}

	@Transactional
	public Collection<User> getUserSubscribedBySessionId(long sessionId) {

		return repository.getUserSubscribedBySessionId(sessionId);
	}
	
	@Transactional
	public long getUserProgress(long userId) {
		// TODO Auto-generated method stub
		return repository.getUserProgress(userId);
	}

	public User checkIfUserValid(UserDTO userDTO, String password) throws Exception {
		// User valid if both username and password are valid
		return checkIfPasswordValid(userDTO, password);
	}

	public User checkIfPasswordValid(UserDTO userDTO, String password) throws Exception {
		User myUser = repository.findUserByEmail(userDTO.getEmail());
		String key = myUser.getHashcode();
		String salt = myUser.getSalt();

		if (!PasswordUtils.verifyPassword(password, key, salt))
			throw new BadRequestException("Invalid Password");
		return myUser;
	}

	public UserDTO convertToUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setEmail(user.getEmail());
		userDTO.setImgUrl(user.getImgUrl());
		userDTO.setName(user.getName());
		userDTO.setRole(user.getRole());
		// userDTO.setProgress(user.getProgress()); //TODO
		return userDTO;
	}

	public User convertToUser(UserDTO userDTO) {
		String email = userDTO.getEmail();

		
		User user = repository.consultEntity(userDTO.getId());
		user.setId(userDTO.getId());
		if (userDTO.getPassword() != null) {
			if (userDTO.getPassword().length() > 0) {
				// password->(hash, salt)
				String password = userDTO.getPassword();

				String[] hashCode = passwordToHashcode(password);

				// set fields to Entity
				user.setHashcode(hashCode[0]);
				user.setSalt(hashCode[1]);
			}
		}
		if(email != null) {
			if (!isValidEmailAddress(email)) {
				throw new BadRequestException("Invalid email");
			}
			user.setEmail(email);
			}
		
		user.setName(userDTO.getName());
		//user.setImgUrl(userDTO.getImgUrl());

		return user;
	}
	
	@Override
	@Transactional
	public void deleteEntity(long id) {
		userSubscriptionService.removeAllSubsByUserId(id);
		repository.removeEntity(id);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// Password-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public String[] passwordToHashcode(String password) {
		String salt = PasswordUtils.generateSalt(50).get();
		String key = PasswordUtils.hashPassword(password, salt).get();
		String[] result = { key, salt };
		return result;
	}

	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	@Transactional
	public String saveImage(long id, MultipartFormDataInput input) {
		String fileName = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				
				fileName = UPLOADED_FILE_PATH + fileName;
				System.out.println(fileName);
				writeFile(bytes, fileName);

				// save na BD o path
				repository.updateImage(id, fileName);

				System.out.println("Done");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return "uploadFile is called, Uploaded file name : " + fileName;

	}

	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// save to somewhere
	private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}

	public File getUserImg(long id) {
		String imgUrl = repository.getImgUrl(id);
		System.out.println(imgUrl);
		 File file = new File(imgUrl);
		return file;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Email-Methods/////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void sendMessage(String conteudo, String instructorEmail) throws IOException {
		Mail mail = new Mail();
		Email fromEmail = new Email();
	    fromEmail.setName("João Barreto");
	    fromEmail.setEmail("eng.joao.barreto@gmail.com");
	    mail.setFrom(fromEmail);

	    mail.setTemplateId("d-c4d411f1e6ac47dcba26c1093136cb47");//fazer um template no sendgrid e por aqui o id

	    Personalization personalization = new Personalization();
	    personalization.addDynamicTemplateData("conteudo", conteudo);//conteudo variavel do template de SendFrid
	    personalization.addTo(new Email(instructorEmail));//por aqui o email pretendido
	    mail.addPersonalization(personalization);
		
		
		try {
			System.out.println(System.getProperty("SGKey"));
			SendGrid sg = new SendGrid(System.getProperty("SGKey"));
			Request request = new Request();
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	

}

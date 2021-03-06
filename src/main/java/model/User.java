package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name=User.GET_ALL_USERS, query="SELECT u FROM User u")
@NamedQuery(name=User.GET_ALL_USERS_IDS, query="SELECT u.id FROM User u")
@NamedQuery(name=User.GET_USERS_COUNT, query="SELECT COUNT(u) FROM User u")
@NamedQuery(name = User.GET_USER_BY_EMAIL, query= "Select u from User u WHERE u.email = :email")
public class User extends GenericEntity{

	private static final long serialVersionUID = 1L;
	

	public static final String GET_ALL_USERS = "User.getAllUsers";
	public static final String GET_ALL_USERS_IDS = "User.getAllUsersIds";
	public static final String GET_USERS_COUNT = "User.getUsersCount";
	public static final String GET_USER_BY_EMAIL = "User.getUserByEmail";
	
	@Column(unique=true)
	private String email;
	
	private String nome;
	private String role;
	private String hashcode;
	private String salt;
	
	public User() {
		
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	public String getHashcode() {
		return hashcode;
	}

	public void setHashcode(String hashcode) {
		this.hashcode = hashcode;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
}

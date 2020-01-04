package repositories;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import model.TrainingSession;

@ApplicationScoped
public class TrainingSessionRepository extends GenericEntityRepository<TrainingSession>{

	@Override
	protected Class<TrainingSession> getEntityClass() {
		return TrainingSession.class;
	}
	
	@Override
	protected String getAllEntityQueryName() {
		return TrainingSession.GET_ALL_TRAININGSESSIONS;
	}
	
	@Override
	protected String getAllEntityIdsQueryName() {
		return TrainingSession.GET_ALL_TRAININGSESSIONS_IDS;
	}

	@Override
	protected String getCountQueryName() {
		return TrainingSession.GET_TRAININGSESSIONS_COUNT;
	}

	public Collection<TrainingSession> listAllTodayTrainingSessions(Timestamp today) {
		return entityManager.createNamedQuery(TrainingSession.GET_ALL_TODAY_TRAININGSESSIONS, TrainingSession.class)
				.setParameter("timeStamp", today)
				.getResultList();
	}

	public Collection<TrainingSession> listAllIntervalTrainingSessions(Timestamp calTS, Timestamp calPlusOneTS) {

		return entityManager.createNamedQuery(TrainingSession.GET_ALL_INTERVAL_TRAININGSESSIONS, TrainingSession.class)
				.setParameter("interval", calTS)
				.setParameter("intervalPlus", calPlusOneTS)
				.getResultList();
	}

	public Collection<TrainingSession> getSessionsByUserId(long userId) {
		return entityManager.createNamedQuery(TrainingSession.GET_SESSIONS_BY_USER_ID, TrainingSession.class)
				.setParameter("userId", userId)
				.getResultList();
	}

	public Collection<TrainingSession> listAllPastTrainingSessionsSubscribed(long userId) {

		
		return entityManager.createNamedQuery(TrainingSession.GET_ALL_PAST_TRAININGSESSIONS_SUBSCRIBED, TrainingSession.class)
				.setParameter("userId", userId)
				.getResultList();
	}

	public Collection<TrainingSession> listAllUnansweredTrainingSessions(long userId) {
		
		return entityManager.createNamedQuery(TrainingSession.GET_ALL_UNANSWERED_TRAININGSESSIONS, TrainingSession.class)
				.setParameter("userId", userId)
				.getResultList();
	}

}

package model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;


@Entity
@NamedQuery(name=TrainingSession.GET_ALL_TRAININGSESSIONS, query="SELECT ts FROM TrainingSession ts")
@NamedQuery(name=TrainingSession.GET_ALL_TRAININGSESSIONS_IDS, query="SELECT ts.id FROM TrainingSession ts")
@NamedQuery(name=TrainingSession.GET_TRAININGSESSIONS_COUNT, query="SELECT COUNT(ts) FROM TrainingSession ts")
@NamedQuery(name=TrainingSession.GET_TRAININGSESSIONS_COUNT, query="SELECT COUNT(ts) FROM TrainingSession ts")
@NamedQuery(name=TrainingSession.GET_ALL_TODAY_TRAININGSESSIONS, query="SELECT ts from TrainingSession ts WHERE ts.sessionDate BETWEEN CURRENT_TIMESTAMP AND :timeStamp")
@NamedQuery(name=TrainingSession.GET_ALL_INTERVAL_TRAININGSESSIONS, query="SELECT ts from TrainingSession ts WHERE ts.sessionDate BETWEEN :interval AND :intervalPlus")
@NamedQuery(name =TrainingSession.GET_SESSIONS_BY_USER_ID, query= "Select us.trainingSession from UserSubscription us WHERE us.user.id = :userId")
@NamedQuery(name=TrainingSession.GET_ALL_PAST_TRAININGSESSIONS_SUBSCRIBED, query="Select us.trainingSession from UserSubscription us WHERE us.user.id = :userId AND us.trainingSession.sessionDate < CURRENT_TIMESTAMP")
@NamedQuery(name=TrainingSession.GET_ALL_UNANSWERED_TRAININGSESSIONS, query="Select us.trainingSession from UserSubscription us WHERE us.user.id = :userId AND us.subType = 'attendee' AND us.answered = FALSE AND us.trainingSession.sessionDate < CURRENT_TIMESTAMP")
public class TrainingSession extends GenericEntity{

	public static final String GET_ALL_TRAININGSESSIONS = "TrainingSession.getAllTrainingSessions";
	public static final String GET_ALL_TRAININGSESSIONS_IDS = "TrainingSession.getAllTrainingSessionsIds";
	public static final String GET_TRAININGSESSIONS_COUNT = "TrainingSession.getTrainingSessionsCount";
	public static final String GET_ALL_TODAY_TRAININGSESSIONS = "TrainingSession.getAllTodayTrainingSessions";
	public static final String GET_ALL_INTERVAL_TRAININGSESSIONS = "TrainingSession.getAllIntervalTrainingSessions";
	public static final String GET_SESSIONS_BY_USER_ID = "TrainingSession.getSessionsByUserId";
	public static final String GET_ALL_PAST_TRAININGSESSIONS_SUBSCRIBED = "TrainingSession.getAllTrainingSessionsSubscribed";
	public static final String GET_ALL_UNANSWERED_TRAININGSESSIONS = "TrainingSession.getAllUnansweredTrainingSessions";
	
	private static final long serialVersionUID = 1L;
	
	


		private String title;
		private String location;
		private int capacity;
		private String requirements;
		private Timestamp sessionDate;
		private String duration;
		
		public TrainingSession() {

		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}

		public String getRequirements() {
			return requirements;
		}

		public void setRequirements(String requirements) {
			this.requirements = requirements;
		}

		public Timestamp getSessionDate() {
			return sessionDate;
		}

		public void setSessionDate(Timestamp sessionDate) {
			this.sessionDate = sessionDate;
		}
		
		public String getDuration() {
			return duration;
		}

		public void setDuration(String duration) {
			this.duration = duration;
		}

		@Override
		public String toString() {
			return "TrainingSession [title=" + title + ", location=" + location + ", capacity=" + capacity
					+ ", requirements=" + requirements + ", sessionDate=" + sessionDate + ", duration=" + duration
					+ ", id=" + id + "]";
		}

}
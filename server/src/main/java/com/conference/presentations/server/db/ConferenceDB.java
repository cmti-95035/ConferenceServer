package com.conference.presentations.server.db;

import com.conference.presentations.server.Conference;
import com.conference.presentations.server.Presentation;
import com.conference.presentations.server.ResearchField;
import com.conference.presentations.server.User;

import java.util.List;

// interface for the SocialPlay Database access
public interface ConferenceDB {
	//research fields
	public List<ResearchField> getAllResearchFields();

	//user
	public User getUser(Integer userId);
	public User getUserFromEmail(String email);
	public Integer addUser(User entry);
	public boolean deleteUser(Integer userId);
	public boolean updateUser(User entry, Integer userId);
	public List<User> getAllUsers();

	// conference
	public Conference getConference(Integer conferenceId);
	public Integer addConference(Conference entry);
	public boolean deleteConference(Integer conferenceId);
	public boolean updateConference(Conference entry, Integer conferenceId);
	public List<Conference> getAllConferences();

	// presentation
	public Presentation getPresentation(Integer presentationId);
	public Integer addPresentation(Presentation entry);
	public boolean deletePresentation(Integer presentationId);
	public boolean updatePresentation(Presentation entry, Integer presentationId);
	public List<Presentation> getAllPresentations();
}

package com.conference.presentations.server.ds;

import com.conference.presentations.server.*;
import com.conference.presentations.server.db.ConferenceDB;
import com.conference.presentations.server.db.mysql.ConferenceDBImpl;

import java.util.List;

public class ConferenceDataServiceImpl implements ConferenceDataService {

	private String dbUrl;
	private String dbName;
	private String userName;
	private String password;
	private ConferenceDB db;

	public ConferenceDataServiceImpl(String url, String name, String uName, String pwd)
	{
		this.dbName = name;
		this.dbUrl = url;
		this.password = pwd;
		this.userName = uName;
		
		this.db = new ConferenceDBImpl(dbUrl, dbName, userName, password);
	}

	@Override
	public User getUser(Integer userId) {
		return db.getUser(userId);
	}

	@Override
	public User getUserFromEmail(String email) {
		return db.getUserFromEmail(email);
	}

	@Override
	public Integer addUser(User entry) {
		return db.addUser(entry);
	}

	@Override
	public boolean deleteUser(Integer userId) {
		return db.deleteUser(userId);
	}

	@Override
	public boolean updateUser(User entry, Integer userId) {
		return db.updateUser(entry, userId);
	}

	@Override
	public Conference getConference(Integer conferenceId) {
		return db.getConference(conferenceId);
	}

	@Override
	public Integer addConference(Conference entry) {
		return db.addConference(entry);
	}

	@Override
	public boolean deleteConference(Integer conferenceId) {
		return db.deleteConference(conferenceId);
	}

	@Override
	public boolean updateConference(Conference entry, Integer conferenceId) {
		return db.updateConference(entry, conferenceId);
	}

	@Override
	public Presentation getPresentation(Integer presentationId) {
		return db.getPresentation(presentationId);
	}

	@Override
	public Integer addPresentation(Presentation entry) {
		return db.addPresentation(entry);
	}

	@Override
	public boolean deletePresentation(Integer presentationId) {
		return db.deletePresentation(presentationId);
	}

	@Override
	public boolean updatePresentation(Presentation entry, Integer presentationId) {
		return db.updatePresentation(entry, presentationId);
	}

	@Override
	public List<ResearchField> getAllResearchFields() {
		return db.getAllResearchFields();
	}

	@Override
	public List<User> getAllUsers() {
		return db.getAllUsers();
	}

	@Override
	public List<Conference> getAllConferences() {
		return db.getAllConferences();
	}

	@Override
	public List<Presentation> getAllPresentations() {
		return db.getAllPresentations();
	}

	@Override
	public UnicefRequest getUnicefRequest(Integer unicefRequestId) {
		return db.getUnicefRequest(unicefRequestId);
	}

	@Override
	public Integer addUnicefRequest(UnicefRequest entry) {
		return db.addUnicefRequest(entry);
	}

	@Override
	public boolean deleteUnicefRequest(Integer unicefRequestId) {
		return db.deleteUnicefRequest(unicefRequestId);
	}

	@Override
	public boolean updateUnicefRequest(UnicefRequest entry, Integer unicefRequestId) {
		return db.updateUnicefRequest(entry, unicefRequestId);
	}

	@Override
	public List<UnicefRequest> getAllUnicefRequests() {
		return db.getAllUnicefRequests();
	}

	@Override
	public List<UnicefRequest> getAllUnicefIncomingRequests() {
		return db.getAllUnicefIncomingRequests();
	}

	@Override
	public List<UnicefRequest> getAllUnicefInprogressRequests() {
		return db.getAllUnicefInprogressRequests();
	}

	@Override
	public List<UnicefRequest> getAllUnicefCompletedRequests() {
		return db.getAllUnicefCompletedRequests();
	}

	@Override
	public IncomingSMS getIncomingSMS(Integer smsId) {
		return db.getIncomingSMS(smsId);
	}

	@Override
	public Integer addIncomingSMS(IncomingSMS entry) {
		return db.addIncomingSMS(entry);
	}
}

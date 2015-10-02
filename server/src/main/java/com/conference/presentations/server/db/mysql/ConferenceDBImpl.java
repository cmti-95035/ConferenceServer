package com.conference.presentations.server.db.mysql;

import com.conference.presentations.server.Conference;
import com.conference.presentations.server.Presentation;
import com.conference.presentations.server.User;
import com.conference.presentations.server.db.ConferenceDB;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.SetMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class ConferenceDBImpl implements ConferenceDB {

    private static Logger _log = LoggerFactory.getLogger(ConferenceDBImpl.class);
    private Connection conn;
    private final String SELECT_LAST_ID = "SELECT LAST_INSERT_ID()";
    private final String SELECT_ROW_COUNT = "SELECT ROW_COUNT();";
    private static final int TOTAL_TIPS = 144;
    private static Random random = new Random();
    private static final int TIPS_COUNT = 3;
    private static final long MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000l;
    private static final int DEFAULT_COUNT = 10;
    private static final String ROOTDIR = "/var/local/povi";
    private static final String DIR_SEPARATOR = "/";
    private static final String USERS = "users";
    private static final String CHILDREN = "children";
    private static final long FIVE_YEARS = 5 * 60 * 60 * 24 * 365 * 1000l;
    private static final long TEN_YEARS = 10 * 60 * 60 * 24 * 365 * 1000l;

    public ConferenceDBImpl(String dbUrl, String dbName, String userName,
                            String password) {
        this.conn = DBUtilities
                .getConnection(dbUrl, dbName, userName, password);
    }

    @Override
    public User getUser(Integer userId) {
        return null;
    }

    @Override
    public User getUserFromEmail(String email) {
        return null;
    }

    @Override
    public boolean addUser(User entry) {
        return false;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return false;
    }

    @Override
    public boolean updateUser(User entry, Integer userId) {
        return false;
    }

    @Override
    public Conference getConference(Integer conferenceId) {
        return null;
    }

    @Override
    public boolean addConference(Conference entry) {
        return false;
    }

    @Override
    public boolean deleteConference(Integer conferenceId) {
        return false;
    }

    @Override
    public boolean updateConference(Conference entry, Integer conferenceId) {
        return false;
    }

    @Override
    public Presentation getPresentation(Integer presentationId) {
        return null;
    }

    @Override
    public boolean addPresentation(Presentation entry) {
        return false;
    }

    @Override
    public boolean deletePresentation(Integer presentationId) {
        return false;
    }

    @Override
    public boolean updatePresentation(Presentation entry, Integer presentationId) {
        return false;
    }
}

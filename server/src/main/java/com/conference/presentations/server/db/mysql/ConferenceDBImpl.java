package com.conference.presentations.server.db.mysql;

import com.conference.presentations.server.*;
import com.conference.presentations.server.db.ConferenceDB;
import com.conference.presentations.server.impl.ServerUtils;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.IntegerArray;
import com.linkedin.data.template.SetMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ConferenceDBImpl implements ConferenceDB {

    private static Logger _log = LoggerFactory.getLogger(ConferenceDBImpl.class);
    private Connection conn;
    private final String SELECT_LAST_ID = "SELECT LAST_INSERT_ID()";
    private final String SELECT_ROW_COUNT = "SELECT ROW_COUNT();";
    private static Random random = new Random();
    private static final long MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000l;
    private static final String ROOTDIR = "/var/local/conference/upload";
    private static final String DIR_SEPARATOR = "/";
    private static final long FIVE_YEARS = 5 * 60 * 60 * 24 * 365 * 1000l;
    private static final long TEN_YEARS = 10 * 60 * 60 * 24 * 365 * 1000l;

    public ConferenceDBImpl(String dbUrl, String dbName, String userName,
                            String password) {
        this.conn = DBUtilities
                .getConnection(dbUrl, dbName, userName, password);
    }

    @Override
    public User getUser(Integer userId) {
        PreparedStatement selectUser = null;
        String methodName = "getUser";

        String selectString = "select * from user where userId=?;";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setInt(1, userId);
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                User user = new User();
                user.setId(rs.getInt("userId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setEmail(rs.getString("email"))
                        .setPhoneNumber(rs.getString("phoneNumber"), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString("address"), SetMode.IGNORE_NULL)
                        .setPassword(SymmetricEncryptionUtility.decrypt(rs.getString("hash")))
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setCountry(rs.getString("country"), SetMode.IGNORE_NULL);

                return user;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public User getUserFromEmail(String email) {
        PreparedStatement selectUser = null;
        String methodName = "getUser";

        String selectString = "select * from user where email=?;";

        try {
            selectUser = conn.prepareStatement(selectString);

            selectUser.setNString(1, email.toLowerCase());
            ResultSet rs = selectUser.executeQuery();
            if (rs.first()) {
                User user = new User();
                user.setId(rs.getInt("userId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setEmail(rs.getString("email"))
                        .setPhoneNumber(rs.getString("phoneNumber"), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString("address"), SetMode.IGNORE_NULL)
                        .setPassword(SymmetricEncryptionUtility.decrypt(rs.getString("hash")))
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setCountry(rs.getString("country"), SetMode.IGNORE_NULL);

                return user;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;

    }

    @Override
    public Integer addUser(User entry) {

        PreparedStatement insertUser = null;
        String methodName = "addUser";

        String insertString = "insert into user (name, email, hash, phoneNumber, fields, address, country, lastupdatetime) values (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            insertUser = conn.prepareStatement(insertString);

            insertUser.setNString(1, entry.getName());
            insertUser.setNString(2, entry.getEmail().toLowerCase());
            insertUser.setNString(3, SymmetricEncryptionUtility.encrypt(entry.getPassword()));
            insertUser.setNString(4, entry.getPhoneNumber(GetMode.NULL) == null ? "" : entry.getPhoneNumber());
            insertUser.setNString(5, DBUtilities.convertListToDelimitedString(entry.getFields()));
            insertUser.setNString(6, entry.getAddress());
            insertUser.setNString(7, entry.getCountry(GetMode.NULL));
            insertUser.setLong(8, new Date().getTime());
            insertUser.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_LAST_ID);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                Integer userId = rs.getInt(1);
                return userId;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  addUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertUser != null) {
                    insertUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        PreparedStatement deleteUser = null;
        String methodName = "deleteUser";

        String deleteString = "delete from user where userId = ?";

        try {
            deleteUser = conn.prepareStatement(deleteString);

            deleteUser.setInt(1, userId);
            deleteUser.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  deleteUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deleteUser != null) {
                    deleteUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean updateUser(User entry, Integer userId) {
        PreparedStatement updateUser = null;
        String methodName = "updateUser";

        String updateString = "update user set name=?, email=?, hash=?, phoneNumber=?, fields=?, address=?, country=?, lastupdatetime=? where userId = ?";

        try {
            updateUser = conn.prepareStatement(updateString);

            updateUser.setNString(1, entry.getName());
            updateUser.setNString(2, entry.getEmail().toLowerCase());
            updateUser.setNString(3, SymmetricEncryptionUtility.encrypt(entry.getPassword()));
            updateUser.setNString(4, entry.getPhoneNumber(GetMode.NULL) == null ? "" : entry.getPhoneNumber());
            updateUser.setNString(5, DBUtilities.convertListToDelimitedString(entry.getFields()));
            updateUser.setNString(6, entry.getAddress());
            updateUser.setNString(7, entry.getCountry(GetMode.NULL));
            updateUser.setLong(8, new Date().getTime());
            updateUser.setInt(9, userId);

            updateUser.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  updateUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updateUser != null) {
                    updateUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public List<ResearchField> getAllResearchFields() {
        PreparedStatement selectUser = null;
        String methodName = "getUser";

        String selectString = "select * from researchFields order by fieldId;";

        List<ResearchField> researchFields = new ArrayList<>();
        try {
            selectUser = conn.prepareStatement(selectString);

            ResultSet rs = selectUser.executeQuery();

            while (rs.next()) {
                ResearchField field = new ResearchField();
                field.setId(rs.getInt("fieldId")).setFieldName(rs.getString("fieldName"));

                researchFields.add(field);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return researchFields;
    }

    @Override
    public List<User> getAllUsers() {
        PreparedStatement selectUser = null;
        String methodName = "getUser";

        String selectString = "select * from user order by userId;";

        List<User> users = new ArrayList<>();
        try {
            selectUser = conn.prepareStatement(selectString);

            ResultSet rs = selectUser.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("userId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setEmail(rs.getString("email"))
                        .setPhoneNumber(rs.getString("phoneNumber"), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString("address"), SetMode.IGNORE_NULL)
                        .setPassword(SymmetricEncryptionUtility.decrypt(rs.getString("hash")))
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setCountry(rs.getString("country"), SetMode.IGNORE_NULL);

                users.add(user);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUser != null) {
                    selectUser.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return users;
    }

    @Override
    public Conference getConference(Integer conferenceId) {
        PreparedStatement selectConference = null;
        String methodName = "getConference";

        String selectString = "select * from conference where conferenceId=?;";

        try {
            selectConference = conn.prepareStatement(selectString);

            selectConference.setInt(1, conferenceId);
            ResultSet rs = selectConference.executeQuery();
            if (rs.first()) {
                Conference conference = new Conference();
                conference.setConferenceId(rs.getInt("conferenceId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setVenue(rs.getString("venue"))
                        .setConferenceTime(DBUtilities.joinDates(rs.getDate("startTime"), rs.getDate("endTime")))
                        .setOrganizer(rs.getString("organizer"), SetMode.IGNORE_NULL)
                        .setWebsite(rs.getString("website"), SetMode.IGNORE_NULL)
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setEmails(rs.getString("emails"), SetMode.IGNORE_NULL);

                return conference;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectConference != null) {
                    selectConference.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public Integer addConference(Conference entry) {
        PreparedStatement insertConference = null;
        String methodName = "addConference";

        String insertString = "insert into conference (name, venue, startTime, endTime, fields, organizer, website, lastupdatetime, emails) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            insertConference = conn.prepareStatement(insertString);

            Date[] dates = DBUtilities.separateDates(entry.getConferenceTime());
            insertConference.setNString(1, entry.getName());
            insertConference.setNString(2, entry.getVenue());
            insertConference.setDate(3, new java.sql.Date(dates[0].getTime()));
            insertConference.setDate(4, new java.sql.Date(dates[1].getTime()));
            insertConference.setNString(5, DBUtilities.convertListToDelimitedString(entry.getFields()));
            insertConference.setNString(6, entry.getOrganizer());
            insertConference.setNString(7, entry.getWebsite(GetMode.NULL));
            insertConference.setLong(8, new Date().getTime());
            insertConference.setNString(9, entry.getEmails());
            insertConference.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_LAST_ID);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                Integer conferenceId = rs.getInt(1);
                return conferenceId;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  addUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertConference != null) {
                    insertConference.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public boolean deleteConference(Integer conferenceId) {
        PreparedStatement deleteConference = null;
        String methodName = "deleteConference";

        String deleteString = "delete from conference where conferenceId = ?";

        try {
            deleteConference = conn.prepareStatement(deleteString);

            deleteConference.setInt(1, conferenceId);
            deleteConference.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  deleteUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deleteConference != null) {
                    deleteConference.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean updateConference(Conference entry, Integer conferenceId) {
        PreparedStatement updateConference = null;
        String methodName = "updateConference";

        String insertString = "update conference set name=?, venue=?, startTime=?, endTime=?, fields=?, organizer=?, website=?, lastupdatetime=?, emails=? where conferenceId=?;";

        try {
            updateConference = conn.prepareStatement(insertString);

            Date[] dates = DBUtilities.separateDates(entry.getConferenceTime());
            updateConference.setNString(1, entry.getName());
            updateConference.setNString(2, entry.getVenue());
            updateConference.setDate(3, new java.sql.Date(dates[0].getTime()));
            updateConference.setDate(4, new java.sql.Date(dates[1].getTime()));

            updateConference.setNString(5, DBUtilities.convertListToDelimitedString(entry.getFields()));
            updateConference.setNString(6, entry.getOrganizer());
            updateConference.setNString(7, entry.getWebsite(GetMode.NULL));
            updateConference.setLong(8, new Date().getTime());
            updateConference.setNString(9, entry.getEmails());
            updateConference.setInt(10, conferenceId);
            updateConference.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  addUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updateConference != null) {
                    updateConference.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public List<Conference> getAllConferences() {
        PreparedStatement selectConference = null;
        String methodName = "getAllConferences";

        List<Conference> conferences = new ArrayList<>();
        String selectString = "select * from conference order by startTime;";

        try {
            selectConference = conn.prepareStatement(selectString);

            ResultSet rs = selectConference.executeQuery();
            while (rs.next()) {
                Conference conference = new Conference();
                conference.setConferenceId(rs.getInt("conferenceId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setVenue(rs.getString("venue"))
                        .setConferenceTime(DBUtilities.joinDates(rs.getDate("startTime"), rs.getDate("endTime")))
                        .setOrganizer(rs.getString("organizer"), SetMode.IGNORE_NULL)
                        .setWebsite(rs.getString("website"), SetMode.IGNORE_NULL)
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setEmails(rs.getString("emails"), SetMode.IGNORE_NULL);

                conferences.add(conference);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectConference != null) {
                    selectConference.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return conferences;
    }

    @Override
    public Presentation getPresentation(Integer presentationId) {
        PreparedStatement selectPresentation = null;
        String methodName = "getPresentation";

        String selectString = "select * from presentation p, conference c, user u where p.conferenceId = c.conferenceId and p.userId = u.userId and p.presentationId=?;";

        try {
            selectPresentation = conn.prepareStatement(selectString);

            selectPresentation.setInt(1, presentationId);
            ResultSet rs = selectPresentation.executeQuery();
            if (rs.first()) {
                Presentation presentation = new Presentation();
                Conference conference = new Conference();
                conference.setConferenceId(rs.getInt("conferenceId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setVenue(rs.getString("venue"))
                        .setConferenceTime(DBUtilities.joinDates(rs.getDate("startTime"), rs.getDate("endTime")))
                        .setOrganizer(rs.getString("organizer"), SetMode.IGNORE_NULL)
                        .setWebsite(rs.getString("website"), SetMode.IGNORE_NULL)
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setEmails(rs.getString("emails"), SetMode.IGNORE_NULL);

                User user = new User();
                user.setId(rs.getInt("userId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setEmail(rs.getString("email"))
                        .setPhoneNumber(rs.getString("phoneNumber"), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString("address"), SetMode.IGNORE_NULL)
                        .setPassword(SymmetricEncryptionUtility.decrypt(rs.getString("hash")))
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setCountry(rs.getString("country"), SetMode.IGNORE_NULL);

                presentation.setPresentationId(rs.getInt("presentationId"))
                        .setTitle(rs.getString("title"), SetMode.IGNORE_NULL)
                        .setFileName(rs.getString("fileName"), SetMode.IGNORE_NULL)
                        .setAbs(rs.getString("abs"))
                        .setAuthors(rs.getString("authors"))
                        .setIsPrivate(rs.getBoolean("isPrivate"), SetMode.IGNORE_NULL)
                        .setConference(conference)
                        .setUser(user);

                return presentation;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectPresentation != null) {
                    selectPresentation.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public Integer addPresentation(Presentation entry) {
        PreparedStatement insertPresentation = null;
        String methodName = "addPresentation";

        String insertString = "insert into presentation (userId, conferenceId, title, authors, fileName, abs, isPrivate, lastupdatetime) values (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            insertPresentation = conn.prepareStatement(insertString);
            
            insertPresentation.setInt(1, entry.getUser().getId());
            insertPresentation.setInt(2, entry.getConference().getConferenceId());
            insertPresentation.setNString(3, entry.getTitle());
            insertPresentation.setNString(4, entry.getAuthors());
            insertPresentation.setNString(5, entry.getFileName(GetMode.NULL));
            insertPresentation.setNString(6, entry.getAbs());
            insertPresentation.setBoolean(7, entry.hasIsPrivate() ? entry.isIsPrivate() : false);
            insertPresentation.setLong(8, new Date().getTime());
            insertPresentation.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_LAST_ID);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                Integer conferenceId = rs.getInt(1);
                return conferenceId;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  addUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertPresentation != null) {
                    insertPresentation.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public boolean deletePresentation(Integer presentationId) {
        PreparedStatement deletePresentation = null;
        String methodName = "deletePresentation";

        String deleteString = "delete from presentation where presentationId = ?";

        try {
            deletePresentation = conn.prepareStatement(deleteString);

            deletePresentation.setInt(1, presentationId);
            deletePresentation.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  deleteUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deletePresentation != null) {
                    deletePresentation.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean updatePresentation(Presentation entry, Integer presentationId) {
        PreparedStatement updatePresentation = null;
        String methodName = "updatePresentation";

        String updateString = "update presentation set userId=?, conferenceId=?, title=?, authors=?, fileName=?, abs=?, isPrivate=?, lastupdatetime=? where presentationId=?;";

        try {
            updatePresentation = conn.prepareStatement(updateString);

            updatePresentation.setInt(1, entry.getUser().getId());
            updatePresentation.setInt(2, entry.getConference().getConferenceId());
            updatePresentation.setNString(3, entry.getTitle());
            updatePresentation.setNString(4, entry.getAuthors());
            updatePresentation.setNString(5, entry.getFileName(GetMode.NULL));
            updatePresentation.setNString(6, entry.getAbs());
            updatePresentation.setBoolean(7, entry.hasIsPrivate() ? entry.isIsPrivate() : false);
            updatePresentation.setLong(8, new Date().getTime());
            updatePresentation.setInt(9, presentationId);
            updatePresentation.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  addUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updatePresentation != null) {
                    updatePresentation.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }


    @Override
    public List<Presentation> getAllPresentations() {
        PreparedStatement selectPresentation = null;
        String methodName = "getAllPresentations";

        List<Presentation> presentations = new ArrayList<>();
        String selectString = "select * from presentation p, conference c, user u where p.conferenceId = c.conferenceId and p.userId = u.userId order by c.startTime desc;";

        try {
            selectPresentation = conn.prepareStatement(selectString);

            ResultSet rs = selectPresentation.executeQuery();
            while(rs.next()) {
                Presentation presentation = new Presentation();
                Conference conference = new Conference();
                conference.setConferenceId(rs.getInt("conferenceId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setVenue(rs.getString("venue"))
                        .setConferenceTime(DBUtilities.joinDates(rs.getDate("startTime"), rs.getDate("endTime")))
                        .setOrganizer(rs.getString("organizer"), SetMode.IGNORE_NULL)
                        .setWebsite(rs.getString("website"), SetMode.IGNORE_NULL)
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setEmails(rs.getString("emails"), SetMode.IGNORE_NULL);

                User user = new User();
                user.setId(rs.getInt("userId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setEmail(rs.getString("email"))
                        .setPhoneNumber(rs.getString("phoneNumber"), SetMode.IGNORE_NULL)
                        .setAddress(rs.getString("address"), SetMode.IGNORE_NULL)
                        .setPassword(SymmetricEncryptionUtility.decrypt(rs.getString("hash")))
                        .setFields(new IntegerArray(DBUtilities.convertDelimitedStringToList(_log, rs.getString("fields"))))
                        .setCountry(rs.getString("country"), SetMode.IGNORE_NULL);

                presentation.setPresentationId(rs.getInt("presentationId"))
                        .setTitle(rs.getString("title"), SetMode.IGNORE_NULL)
                        .setFileName(rs.getString("fileName"), SetMode.IGNORE_NULL)
                        .setAbs(rs.getString("abs"))
                        .setAuthors(rs.getString("authors"))
                        .setIsPrivate(rs.getBoolean("isPrivate"), SetMode.IGNORE_NULL)
                        .setConference(conference)
                        .setUser(user);

                presentations.add(presentation);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectPresentation != null) {
                    selectPresentation.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return presentations;
    }

    @Override
    public UnicefRequest getUnicefRequest(Integer unicefRequestId) {
        PreparedStatement selectUnicefRequest = null;
        String methodName = "getAllUnicefRequests";

        String selectString = "select * from UNICEF_REQUEST where requestId=?;";

        try {
            selectUnicefRequest = conn.prepareStatement(selectString);

            selectUnicefRequest.setInt(1, unicefRequestId);
            ResultSet rs = selectUnicefRequest.executeQuery();
            if(rs.first()) {

                return new UnicefRequest().setRequestId(rs.getInt("requestId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setDetail(rs.getString("details"), SetMode.IGNORE_NULL)
                        .setIdentifier(rs.getString("identifier"))
                        .setLatitude(rs.getDouble("latitude"), SetMode.IGNORE_NULL)
                        .setLongitute(rs.getDouble("longitude"), SetMode.IGNORE_NULL)
                        .setLastUpdated(ServerUtils.convertDateToString(rs.getDate("lastupdatetime")))
                        .setStatus(RequestState.valueOf(rs.getString("status").toUpperCase()));
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUnicefRequest != null) {
                    selectUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public Integer addUnicefRequest(UnicefRequest entry) {
        PreparedStatement insertUnicefRequest = null;
        String methodName = "addUnicefRequest";

        String insertString = "insert into UNICEF_REQUEST (name, identifier, details, latitude, longitude, status, lastupdatetime) values (?, ?, ?, ?, ?, ?, ?)";

        try {
            insertUnicefRequest = conn.prepareStatement(insertString);

            insertUnicefRequest.setNString(1, entry.getName());
            insertUnicefRequest.setNString(2, entry.getIdentifier(GetMode.NULL));
            insertUnicefRequest.setNString(3, entry.getDetail());
            insertUnicefRequest.setDouble(4, entry.hasLatitude() ? entry.getLatitude() : 0.0);
//            insertUnicefRequest.setDouble(4, entry.getLatitude(GetMode.NULL));
            insertUnicefRequest.setDouble(5, entry.hasLongitute() ? entry.getLongitute() : 0.0);
//            insertUnicefRequest.setDouble(5, entry.getLongitute(GetMode.NULL));
            insertUnicefRequest.setNString(6, entry.getStatus().toString().toLowerCase());
            insertUnicefRequest.setLong(7, new Date().getTime());
            insertUnicefRequest.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_LAST_ID);
            ResultSet rs = selectLastId.executeQuery();
            while (rs.next()) {
                Integer requestId = rs.getInt(1);
                return requestId;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (insertUnicefRequest != null) {
                    insertUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return null;
    }

    @Override
    public boolean deleteUnicefRequest(Integer unicefRequestId) {
        PreparedStatement deleteUnicefRequest = null;
        String methodName = "deleteUnicefRequest";

        String deleteString = "delete from UNICEF_REQUEST where unicefRequestId = ?";

        try {
            deleteUnicefRequest = conn.prepareStatement(deleteString);

            deleteUnicefRequest.setInt(1, unicefRequestId);
            deleteUnicefRequest.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + "  deleteUser Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (deleteUnicefRequest != null) {
                    deleteUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public boolean updateUnicefRequest(UnicefRequest entry, Integer unicefRequestId) {
        PreparedStatement updateUnicefRequest = null;
        String methodName = "updateUnicefRequest";

        String updateString = "update UNICEF_REQUEST set name=?, identifier=?, details=?, latitude=?, longitude=?, status=?, lastupdatetime=? where requestId=?;";

        try {
            updateUnicefRequest = conn.prepareStatement(updateString);

            updateUnicefRequest.setNString(1, entry.getName());
            updateUnicefRequest.setNString(2, entry.getIdentifier(GetMode.NULL));
            updateUnicefRequest.setNString(3, entry.getDetail());
            updateUnicefRequest.setDouble(4, entry.getLatitude(GetMode.NULL));
            updateUnicefRequest.setDouble(5, entry.getLongitute(GetMode.NULL));
            updateUnicefRequest.setNString(6, entry.getStatus().toString().toLowerCase());
            updateUnicefRequest.setLong(7, new Date().getTime());
            updateUnicefRequest.setInt(8, unicefRequestId);
            updateUnicefRequest.executeUpdate();
            PreparedStatement selectLastId = conn.prepareStatement(SELECT_ROW_COUNT);
            ResultSet rs = selectLastId.executeQuery();
            if (rs.first() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                conn.setAutoCommit(true);
                if (updateUnicefRequest != null) {
                    updateUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return false;
    }

    @Override
    public List<UnicefRequest> getAllUnicefRequests() {
        PreparedStatement selectUnicefRequest = null;
        String methodName = "getAllUnicefRequests";

        List<UnicefRequest> unicefRequests = new ArrayList<>();
        String selectString = "select * from UNICEF_REQUEST order by lastupdatetime desc;";

        try {
            selectUnicefRequest = conn.prepareStatement(selectString);

            ResultSet rs = selectUnicefRequest.executeQuery();
            while(rs.next()) {
                UnicefRequest unicefRequest = new UnicefRequest();

                unicefRequest.setRequestId(rs.getInt("requestId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setDetail(rs.getString("details"), SetMode.IGNORE_NULL)
                        .setIdentifier(rs.getString("identifier"))
                        .setLatitude(rs.getDouble("latitude"), SetMode.IGNORE_NULL)
                        .setLongitute(rs.getDouble("longitude"), SetMode.IGNORE_NULL)
                        .setLastUpdated(ServerUtils.convertDateToString(rs.getDate("lastupdatetime")))
                        .setStatus(RequestState.valueOf(rs.getString("status").toUpperCase()));

                unicefRequests.add(unicefRequest);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUnicefRequest != null) {
                    selectUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return unicefRequests;
    }

    @Override
    public List<UnicefRequest> getAllUnicefIncomingRequests() {
        PreparedStatement selectUnicefRequest = null;
        String methodName = "getAllUnicefIncomingRequests";

        List<UnicefRequest> unicefRequests = new ArrayList<>();
        String selectString = "select * from UNICEF_REQUEST where status = 'incoming' order by lastupdatetime desc;";

        try {
            selectUnicefRequest = conn.prepareStatement(selectString);

            ResultSet rs = selectUnicefRequest.executeQuery();
            while(rs.next()) {
                UnicefRequest unicefRequest = new UnicefRequest();

                unicefRequest.setRequestId(rs.getInt("requestId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setDetail(rs.getString("details"), SetMode.IGNORE_NULL)
                        .setIdentifier(rs.getString("identifier"))
                        .setLatitude(rs.getDouble("latitude"), SetMode.IGNORE_NULL)
                        .setLongitute(rs.getDouble("longitude"), SetMode.IGNORE_NULL)
                        .setLastUpdated(ServerUtils.convertDateToString(rs.getDate("lastupdatetime")))
                        .setStatus(RequestState.valueOf(rs.getString("status").toUpperCase()));

                unicefRequests.add(unicefRequest);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUnicefRequest != null) {
                    selectUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return unicefRequests;
    }

    @Override
    public List<UnicefRequest> getAllUnicefInprogressRequests() {
        PreparedStatement selectUnicefRequest = null;
        String methodName = "getAllUnicefInprogressRequests";

        List<UnicefRequest> unicefRequests = new ArrayList<>();
        String selectString = "select * from UNICEF_REQUEST where status = 'inprogress' order by lastupdatetime desc;";

        try {
            selectUnicefRequest = conn.prepareStatement(selectString);

            ResultSet rs = selectUnicefRequest.executeQuery();
            while(rs.next()) {
                UnicefRequest unicefRequest = new UnicefRequest();

                unicefRequest.setRequestId(rs.getInt("requestId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setDetail(rs.getString("details"), SetMode.IGNORE_NULL)
                        .setIdentifier(rs.getString("identifier"))
                        .setLatitude(rs.getDouble("latitude"), SetMode.IGNORE_NULL)
                        .setLongitute(rs.getDouble("longitude"), SetMode.IGNORE_NULL)
                        .setLastUpdated(ServerUtils.convertDateToString(rs.getDate("lastupdatetime")))
                        .setStatus(RequestState.valueOf(rs.getString("status").toUpperCase()));

                unicefRequests.add(unicefRequest);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUnicefRequest != null) {
                    selectUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return unicefRequests;
    }

    @Override
    public List<UnicefRequest> getAllUnicefCompletedRequests() {
        PreparedStatement selectUnicefRequest = null;
        String methodName = "getAllUnicefCompletedRequests";

        List<UnicefRequest> unicefRequests = new ArrayList<>();
        String selectString = "select * from UNICEF_REQUEST where status = 'complete' order by lastupdatetime desc;";

        try {
            selectUnicefRequest = conn.prepareStatement(selectString);

            ResultSet rs = selectUnicefRequest.executeQuery();
            while(rs.next()) {
                UnicefRequest unicefRequest = new UnicefRequest();

                unicefRequest.setRequestId(rs.getInt("requestId"))
                        .setName(rs.getString("name"), SetMode.IGNORE_NULL)
                        .setDetail(rs.getString("details"), SetMode.IGNORE_NULL)
                        .setIdentifier(rs.getString("identifier"))
                        .setLatitude(rs.getDouble("latitude"), SetMode.IGNORE_NULL)
                        .setLongitute(rs.getDouble("longitude"), SetMode.IGNORE_NULL)
                        .setLastUpdated(ServerUtils.convertDateToString(rs.getDate("lastupdatetime")))
                        .setStatus(RequestState.valueOf(rs.getString("status").toUpperCase()));

                unicefRequests.add(unicefRequest);
            }
        } catch (SQLException ex) {
            _log.error(methodName + " SQLException: " + ex.getMessage());
            _log.error(methodName + " SQLState: " + ex.getSQLState());
            _log.error(methodName + " VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            _log.error(methodName + " Encountered exception: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        } finally {
            try {
                if (selectUnicefRequest != null) {
                    selectUnicefRequest.close();
                }
            } catch (SQLException ex) {
                _log.error(methodName + " SQLException: " + ex.getMessage());
                _log.error(methodName + " SQLState: " + ex.getSQLState());
                _log.error(methodName + " VendorError: " + ex.getErrorCode());
            }
        }

        return unicefRequests;
    }

    @Override
    public IncomingSMS getIncomingSMS(Integer smsId) {
        return null;
    }

    @Override
    public Integer addIncomingSMS(IncomingSMS entry) {
        _log.error("incoming values: " + entry.getValues(GetMode.NULL));

        return 1;
    }
}

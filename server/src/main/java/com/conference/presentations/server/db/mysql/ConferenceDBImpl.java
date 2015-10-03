package com.conference.presentations.server.db.mysql;

import com.conference.presentations.server.Conference;
import com.conference.presentations.server.Presentation;
import com.conference.presentations.server.ResearchField;
import com.conference.presentations.server.User;
import com.conference.presentations.server.db.ConferenceDB;
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
            _log.error(methodName + "  getUser Encountered exception: " + ex.getMessage());
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
            _log.error(methodName + "  getUser Encountered exception: " + ex.getMessage());
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
            _log.error(methodName + "  getUser Encountered exception: " + ex.getMessage());
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
            _log.error(methodName + "  getUser Encountered exception: " + ex.getMessage());
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

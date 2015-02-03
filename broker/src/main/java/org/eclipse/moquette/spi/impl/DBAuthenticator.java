package org.eclipse.moquette.spi.impl;

import org.eclipse.moquette.server.IAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * Created by yycoder on 2015/1/8.
 */
public class DBAuthenticator implements IAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(DBAuthenticator.class);
    private static Connection DB_CONNECTION;
    private Properties props;

    public DBAuthenticator(Properties props) {
        this.props = props;
    }

    private void initConnection() {
        try {
            if (DB_CONNECTION == null || !DB_CONNECTION.isValid(2)) {
                if (DB_CONNECTION != null) {
                    try {
                        DB_CONNECTION.close();
                    } catch (SQLException e) {
                    }
                }
                DB_CONNECTION = DriverManager.getConnection(props.getProperty("db_url"),
                        props.getProperty("db_user"), props.getProperty("db_password"));
            }
        } catch (Exception e) {
            LOG.error("dbConnection init error {}", e);
        }
    }

    @Override
    public boolean checkValid(String username, String password) {
        if (username == null || "".equals(username) || password == null || "".equals(password))
            return false;
        long userId = Long.parseLong(username);
        String token = getTokenFromDB(userId);
        LOG.debug(String.format("authenticator userId :%d", userId));
        LOG.debug(String.format("authenticator token :%s", token));
        return password.equals(token);
    }

    private String getTokenFromDB(Long userId) {
        try {
            initConnection();
            Statement statement = DB_CONNECTION.createStatement();
            ResultSet rs = statement.executeQuery(String.format("select * from users where userId = %d", userId));
            rs.next();
            return rs.getString("token");
        } catch (Exception e) {
            LOG.error("get user token from db error {}");
        }
        return null;
    }
}

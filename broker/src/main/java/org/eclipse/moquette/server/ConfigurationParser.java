/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package org.eclipse.moquette.server;

import org.eclipse.moquette.commons.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.util.Properties;

/**
 * Mosquitto configuration parser.
 * <p/>
 * A line that at the very first has # is a comment
 * Each line has key value format, where the separator used it the space.
 *
 * @author andrea
 */
public class ConfigurationParser {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationParser.class);

    private Properties m_properties = new Properties();

    public ConfigurationParser() {
        m_properties.put("port", Integer.toString(Constants.PORT));
        m_properties.put("host", Constants.HOST);
        m_properties.put("websocket_port", Integer.toString(Constants.WEBSOCKET_PORT));
        m_properties.put("password_file", "");
        m_properties.put("authenticator", Constants.AUTHENTICATOR);
        m_properties.put("persistent", Constants.PERSISTENT);
        m_properties.put("db_user", Constants.DB_USER);
        m_properties.put("db_password", Constants.DB_PASSWORD);
        m_properties.put("driver_class", Constants.DRIVER_CLASS);
        m_properties.put("db_url", Constants.DB_URL);
        m_properties.put("mongo_ip", Constants.MONGO_IP);
        m_properties.put("mongo_port", Constants.MONGO_PORT);
        m_properties.put("mongo_db", Constants.MONGO_DB);
        m_properties.put("mongo_usr", Constants.MONGO_USR);
        m_properties.put("mongo_pwd", Constants.MONGO_PWD);
        m_properties.put("mongo_maxWaitTime", Constants.MONGO_MAXWAITTIME);
        m_properties.put("instance_id", Constants.INSTANCE_ID);
    }

    /**
     * Parse the configuration from file.
     */
    public void parse(File file) throws ParseException {
        if (file == null) {
            LOG.warn("parsing NULL file, so fallback on default configuration!");
            return;
        }
        if (!file.exists()) {
            LOG.warn(String.format("parsing not existing file %s, so fallback on default configuration!", file.getAbsolutePath()));
            return;
        }
        try {
            FileReader reader = new FileReader(file);
            parse(reader);
        } catch (FileNotFoundException fex) {
            LOG.warn(String.format("parsing not existing file %s, so fallback on default configuration!", file.getAbsolutePath()), fex);
            return;
        }
    }

    /**
     * Parse the configuration
     *
     * @throws ParseException if the format is not compliant.
     */
    void parse(Reader reader) throws ParseException {
        if (reader == null) {
            //just log and return default properties
            LOG.warn("parsing NULL reader, so fallback on default configuration!");
            return;
        }

        BufferedReader br = new BufferedReader(reader);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                int commentMarker = line.indexOf('#');
                if (commentMarker != -1) {
                    if (commentMarker == 0) {
                        //skip its a comment
                        continue;
                    } else {
                        //it's a malformed comment
                        throw new ParseException(line, commentMarker);
                    }
                } else {
                    if (line.isEmpty() || line.matches("^\\s*$")) {
                        //skip it's a black line
                        continue;
                    }

                    //split till the first space
                    int deilimiterIdx = line.indexOf(' ');
                    String key = line.substring(0, deilimiterIdx).trim();
                    String value = line.substring(deilimiterIdx).trim();

                    m_properties.put(key, value);
                }
            }
        } catch (IOException ex) {
            throw new ParseException("Failed to read", 1);
        }
    }

    public Properties getProperties() {
        return m_properties;
    }

}

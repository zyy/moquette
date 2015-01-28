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
package org.eclipse.moquette.commons;

/**
 * Contains some useful constants.
 */
public class Constants {
    public static final int PORT = 1883;
    public static final int WEBSOCKET_PORT = 8080;
    public static final String HOST = "0.0.0.0";
    public static final int DEFAULT_CONNECT_TIMEOUT = 10;
    public static final String AUTHENTICATOR = "all";
    public static final String PERSISTENT = "mongo";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "root_123";
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://127.0.0.1:9376/lukou?useUnicode=true&characterEncoding=UTF-8";
    public static final String MONGO_IP = "127.0.0.1";
    public static final String MONGO_PORT = "27017";
    public static final String MONGO_DB = "moquette";
    public static final String MONGO_USR = "root";
    public static final String MONGO_PWD = "root";
    public static final String MONGO_MAXWAITTIME = "3000";
    public static final String INSTANCE_ID = "2";
}

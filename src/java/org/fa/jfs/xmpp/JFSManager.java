package org.fa.jfs.xmpp;

/**
 * Copyright (c) 2013 Amdocs jNetX.
 * http://www.amdocs.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information of
 * Amdocs jNetX. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license
 * agreement you entered into with Amdocs jNetX.
 * <p/>
 * User: Sergey Skoptsov (sskoptsov@amdocs.com)
 * Date: 21.11.13
 * Time: 18:09
 * <p/>
 * $Id:
 */

public final class JFSManager {

    private static SessionManager sessionManager;

    private JFSManager() {

    }

    public static SessionManager getSessionManager() {
        if (sessionManager == null)
            sessionManager = new SessionManager();
        return sessionManager;
    }




}

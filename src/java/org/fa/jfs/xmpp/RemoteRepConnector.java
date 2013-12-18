package org.fa.jfs.xmpp;

import org.fa.jfs.common.Configuration;

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
 * Date: 18.12.13
 * Time: 20:06
 * <p/>
 * $Id:
 */

public interface RemoteRepConnector {

    public void setListener(RemoteRepListener listener);

    public boolean connect(Configuration cfg);
    public void updateLocalRevision(String revision);
    public void requestRemoteRepository();
    public void requestRemoteFiles();
    public void sendLocalFiles();

}

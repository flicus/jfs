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
 * Date: 18.12.13
 * Time: 20:02
 * <p/>
 * $Id:
 */

public interface RemoteRepListener {

    public void JFSPresenceReceived(JFSPacketExtension jfsInfo);

}

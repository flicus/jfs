/*
 * The MIT License
 *
 *  Copyright (c) 2013  Sergey Skoptsov (flicus@gmail.com), Alexey Marin (asmadews@gmail.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 *  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 *  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.fa.jfs;

import org.fa.jfs.common.Configuration;
import org.fa.jfs.common.ConfigurationFactory;
import org.fa.jfs.repository.FileConnector;
import org.fa.jfs.repository.LocalRepConnector;
import org.fa.jfs.repository.LocalRepListener;
import org.fa.jfs.xmpp.RemoteRepConnector;
import org.fa.jfs.xmpp.RemoteRepListener;
import org.fa.jfs.xmpp.XMPPRemoteConnector;
import org.fa.jfs.xmpp.packets.JFSGetRepository;
import org.fa.jfs.xmpp.packets.JFSInfo;

import java.io.File;

public final class JFSManager implements RemoteRepListener, LocalRepListener {

    private static RemoteRepConnector sessionManager;
    private static LocalRepConnector repositoryManager;
    private static Configuration cfg;

    private JFSManager() {
        cfg = ConfigurationFactory.getInstance().getConfiguration();
    }

    public RemoteRepConnector getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new XMPPRemoteConnector();
            sessionManager.setListener(this);
        }
        return sessionManager;
    }

    public LocalRepConnector getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = new FileConnector(cfg);
            repositoryManager.setListener(this);
        }
        return repositoryManager;
    }

    @Override
    public void jfsInfoReceived(String remoteAddress, JFSInfo jfsInfo) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void jfsGetRepositoryReceived(String remoteAddress, JFSGetRepository jfsGetRepository) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean jfsRemoteFileTranferReceived(String remoteAddress, String fileName) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void jfsRemoteFileReceived(File file) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

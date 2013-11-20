/*
 * The MIT License
 *
 *  Copyright (c) 2013.  Sergey Skoptsov (flicus@gmail.com), Alexey Marin (asmadews@gmail.com)
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

package org.fa.jfs.xmpp;

import org.fa.jfs.common.Configuration;
import org.fa.jfs.common.GUIDGenerator;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smackx.PEPListener;
import org.jivesoftware.smackx.PEPManager;
import org.jivesoftware.smackx.packet.PEPEvent;
import org.jivesoftware.smackx.provider.PEPProvider;

public class ConnectionManager {

    private Connection connection;
    private PEPManager pepManager;
    private String lastReceivedRevision;

    public ConnectionManager() {
        Connection.DEBUG_ENABLED = true;
        lastReceivedRevision = "";
    }

    public boolean connect(Configuration cfg) {
        boolean result = true;
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }

        ConnectionConfiguration config = new ConnectionConfiguration(cfg.getXmppServer(), new ProxyInfo(ProxyInfo.ProxyType.HTTP, "10.50.8.19", 8080, "", ""));
        config.setCompressionEnabled(true);
        config.setReconnectionAllowed(true);
        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);

        connection = new XMPPConnection(config);
        try {
            connection.connect();
            connection.login(cfg.getXmppLogin(), cfg.getXmppPassword());
        } catch (XMPPException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            result = false;
        }
        pepManager = new PEPManager(connection);
        pepManager.addPEPListener(new PEPListener() {
            @Override
            public void eventReceived(String from, PEPEvent event) {
                lastReceivedRevision = ((JFSNotification)event.getItem()).getRepositoryVersion();
                System.out.println(event.getItem());
            }
        });
        PEPProvider pepProvider = new PEPProvider();
        pepProvider.registerPEPParserExtension(JFSNotification.NAMESPACE, new JFSNotificationProvider());
        ProviderManager.getInstance().addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", pepProvider);

        return result;
    }

    public void publishRevision(String revision) {
        pepManager.publish(new JFSNotification(newId(), NotificationType.UPDATE, revision));
    }

    private String newId() {
        return Long.toHexString(GUIDGenerator.getGUID());
    }

    public void disconnect() {
        connection.disconnect();
    }

    public String getLastReceivedRevision() {
        return lastReceivedRevision;
    }
}

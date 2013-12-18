package org.fa.jfs.xmpp;

public final class JFSManager implements RemoteRepListener {

    private static XMPPRemoteConnector sessionManager;

    private JFSManager() {

    }

    public XMPPRemoteConnector getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new XMPPRemoteConnector();
            sessionManager.setListener(this);
        }
        return sessionManager;
    }


    @Override
    public void JFSPresenceReceived(JFSPacketExtension jfsInfo) {

    }
}

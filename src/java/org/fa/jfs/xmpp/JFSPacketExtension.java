package org.fa.jfs.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * Created with IntelliJ IDEA.
 * User: flicus
 * Date: 18.12.13
 * Time: 1:55
 * To change this template use File | Settings | File Templates.
 */
public class JFSPacketExtension implements PacketExtension {

    public static final String NAMESPACE = "http://0xffff.net/protocol/jfs-info";
    public static final String NAME = "jfs-info";

    private NotificationType notificationType;
    private String repositoryVersion;

    public JFSPacketExtension() {
    }

    public JFSPacketExtension(NotificationType notificationType, String repositoryVersion) {
        this.notificationType = notificationType;
        this.repositoryVersion = repositoryVersion;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getRepositoryVersion() {
        return repositoryVersion;
    }

    public void setRepositoryVersion(String repositoryVersion) {
        this.repositoryVersion = repositoryVersion;
    }

    @Override
    public String getElementName() {
        return NAME;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(NAME).append(" xmlns='").append(NAMESPACE).append("'>");

        //type
        buf.append("<").append("type").append(">");
        buf.append(notificationType.name());
        buf.append("</").append("type").append(">");

        //version
        buf.append("<").append("version").append(">");
        buf.append(repositoryVersion);
        buf.append("</").append("version").append(">");

        buf.append("</").append(NAME).append(">");
        return buf.toString();
    }
}

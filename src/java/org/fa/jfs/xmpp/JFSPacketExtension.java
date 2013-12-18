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

package org.fa.jfs.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

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

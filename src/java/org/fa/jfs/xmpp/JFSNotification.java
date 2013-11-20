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

import org.jivesoftware.smackx.packet.PEPItem;

public class JFSNotification extends PEPItem {

    public static final String NAMESPACE = "http://jabber.org/protocol/jfs-notification";
    public static final String NAME = "jfsnot";

    private NotificationType notificationType;
    private String repositoryVersion;


    /**
     * Creates a new PEPItem.
     */
    public JFSNotification(String id) {
        super(id);
    }

    public JFSNotification(String id, NotificationType notificationType, String repositoryVersion) {
        super(id);
        this.notificationType = notificationType;
        this.repositoryVersion = repositoryVersion;
    }

    @Override
    public String getNode() {
        return NAMESPACE;
    }

    @Override
    public String getItemDetailsXML() {
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

    @Override
    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append("item").append(" id=\"").append(id).append("\">");
        buf.append(getItemDetailsXML());
        buf.append("</").append("item").append(">");
        return buf.toString();
    }

    @Override
    public String getElementName() {
        return NAME;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
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
    public String toString() {
        return "JFSNotification{" +
                "notificationType=" + notificationType +
                ", repositoryVersion='" + repositoryVersion + '\'' +
                '}';
    }
}

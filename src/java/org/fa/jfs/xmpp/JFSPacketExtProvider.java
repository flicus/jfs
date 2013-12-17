package org.fa.jfs.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created with IntelliJ IDEA.
 * User: flicus
 * Date: 18.12.13
 * Time: 2:04
 * To change this template use File | Settings | File Templates.
 */
public class JFSPacketExtProvider implements PacketExtensionProvider {
    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        boolean done = false;
        JFSPacketExtension jfsPacketExtension = new JFSPacketExtension();
        while (!done) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if (name.equals(JFSPacketExtension.NAME)) {
                    //String id = parser.getAttributeValue("", "id");
                    jfsPacketExtension = new JFSPacketExtension();
                }
                if (name.equals("type")) {
                    String text = parser.nextText();
                    jfsPacketExtension.setNotificationType(NotificationType.valueOf(text));
                }
                if (name.equals("version")) {
                    jfsPacketExtension.setRepositoryVersion(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (name.equals(JFSPacketExtension.NAME)) {
                    done = true;
                }
            }

        }
        return jfsPacketExtension;
    }
}

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
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

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

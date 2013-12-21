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

package org.fa.jfs.xmpp.packets;

import org.fa.jfs.repository.RepositoryRecord;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class JFSGetRepositoryProvider implements PacketExtensionProvider {
    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        boolean done = false;
        JFSGetRepository getrep = new JFSGetRepository();
        while (!done) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if (name.equals(JFSGetRepository.NAME)) {
                    //String id = parser.getAttributeValue("", "id");
                    getrep = new JFSGetRepository();
                }
                if (name.equals("isRequest")) {
                    String text = parser.nextText();
                    getrep.setRequest(Boolean.valueOf(text));
                }
                if (name.equals("RepositoryRecords")) {
                    List<RepositoryRecord> records = parseRecords(parser);
                    getrep.setItems(records);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (name.equals(JFSGetRepository.NAME)) {
                    done = true;
                }
            }

        }
        return getrep;
    }

    private List<RepositoryRecord> parseRecords(XmlPullParser parser) throws Exception {
        boolean done = false;
        List<RepositoryRecord> list = new ArrayList<RepositoryRecord>();
        RepositoryRecord record = null;
        while (!done) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if (name.equals("RepositoryRecord")) {
                    record = new RepositoryRecord();
                }
                if (name.equals("Name")) {
                    record.setName(parser.nextText());
                }
                if (name.equals("Size")) {
                    record.setSize(parser.nextText());
                }
                if (name.equals("LastModified")) {
                    record.setLastModified(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (name.equals("RepositoryRecord")) {
                    list.add(record);
                }
                if (name.equals("RepositoryRecords")) {
                    done = true;
                }
            }
        }
        return list;
    }
}

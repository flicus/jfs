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

import java.util.List;

public class JFSGetRepository extends JFSPacket {

    public static final String NAME = "jfs-getrep";

    private boolean isRequest;
    private List<RepositoryRecord> items;

    public JFSGetRepository() {
    }

    public JFSGetRepository(boolean request) {
        isRequest = request;
    }

    public JFSGetRepository(boolean request, List<RepositoryRecord> items) {
        isRequest = request;
        this.items = items;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public List<RepositoryRecord> getItems() {
        return items;
    }

    public void setItems(List<RepositoryRecord> items) {
        this.items = items;
    }

    @Override
    public String getElementName() {
        return NAME;
    }

    @Override
    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(NAME).append(" xmlns='").append(NAMESPACE).append("'>");

        //request flag
        buf.append("<isRequest>");
        buf.append(isRequest);
        buf.append("</isRequest>");

        //items
        if (items != null) {
            buf.append("<RepositoryRecords>");
            for (RepositoryRecord record : items){
                buf.append(record.toXML());
            }
            buf.append("</RepositoryRecords>");
        }
        buf.append("</").append(NAME).append(">");
        return buf.toString();
    }
}

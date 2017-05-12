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

package org.fa.jfs.repository;

public class RepositoryRecord {
    private String name;
    private String size;
    private String lastModified;

    public RepositoryRecord() {
    }

    public RepositoryRecord(String name, String size, String lastModified) {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "RepositoryRecord{" +
                "name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", lastModified='" + lastModified + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("<RepositoryRecord>")
                .append("<Name>")
                .append(name)
                .append("</Name>")
                .append("<Size>")
                .append(size)
                .append("</Size>")
                .append("<LastModified>")
                .append(lastModified)
                .append("</LastModified>")
                .append("</RepositoryRecord>");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryRecord that = (RepositoryRecord) o;

        if (!lastModified.equals(that.lastModified)) return false;
        if (!name.equals(that.name)) return false;
        return size.equals(that.size);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + size.hashCode();
        result = 31 * result + lastModified.hashCode();
        return result;
    }
}

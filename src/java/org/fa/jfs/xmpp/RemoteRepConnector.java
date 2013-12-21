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

import org.fa.jfs.common.Configuration;
import org.fa.jfs.repository.RepositoryRecord;

import java.io.File;
import java.util.List;

public interface RemoteRepConnector {

    public void setListener(RemoteRepListener listener);

    public boolean connect(Configuration cfg);
    public void updateLocalRevision(String revision);
    public void requestRemoteRepository(String remoteAddress);
    public void sendLocalRepository(String remoteAddress, List<RepositoryRecord> repository);
    public void requestRemoteFileSet(String remoteAddress, List<RepositoryRecord> fileSet);
    public void sendLocalFile(String remoteAddress, File file2send);

}

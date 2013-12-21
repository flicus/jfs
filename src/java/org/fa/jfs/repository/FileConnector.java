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

import org.fa.jfs.common.Configuration;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileConnector implements LocalRepConnector {

    private LocalRepListener listener;
    private Configuration cfg;
    private String repositoryVersion = "";

    public FileConnector(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void setListener(LocalRepListener listener) {
        this.listener = listener;
    }

    @Override
    public List<RepositoryRecord> getRepository() {
        File path = new File(cfg.getRepositoryPath());
        List<RepositoryRecord> list = new ArrayList<RepositoryRecord>();
        if (path.isDirectory()) {
            File[] files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });
            for (File file : files) {
                RepositoryRecord rr = new RepositoryRecord();
                rr.setName(file.getName());
                rr.setSize(String.valueOf(file.length()));
                rr.setLastModified(String.valueOf(file.lastModified()));
                list.add(rr);
            }
        }
        return list;
    }

    @Override
    public List<RepositoryRecord> getRecordsToSync(List<RepositoryRecord> remoteRep) {
        List<RepositoryRecord> localRep = getRepository();
        List<RepositoryRecord> toSync = new ArrayList<RepositoryRecord>();
        for (RepositoryRecord remoteRecord : remoteRep) {
            boolean found = false;
            for (RepositoryRecord localRecord : localRep) {
                if (remoteRecord.getName().equals(localRecord.getName())) { //todo
                    found = true;
                }
            }
            if (!found) toSync.add(remoteRecord);
        }
        return toSync;
    }

    @Override
    public String getRepositoryVersion() {
        return repositoryVersion;
    }

    @Override
    public void storeFile(File newFile) {
        File toStore = new File(cfg.getRepositoryPath()+File.separator+newFile.getName());
        newFile.renameTo(toStore);
    }

    @Override
    public File getFile(RepositoryRecord record) {
        File toSend = new File(cfg.getRepositoryPath()+File.separator+record.getName());
        return toSend;
    }

    public void setRepositoryVersion(String repositoryVersion) {
        this.repositoryVersion = repositoryVersion;
    }
}

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

package org.fa.jfs.test;

import org.fa.jfs.common.Configuration;
import org.fa.jfs.common.ConfigurationFactory;
import org.fa.jfs.repository.FileConnector;
import org.fa.jfs.repository.RepositoryRecord;
import org.fa.jfs.xmpp.RemoteRepListener;
import org.fa.jfs.xmpp.XMPPRemoteConnector;
import org.fa.jfs.xmpp.packets.JFSGetRepository;
import org.fa.jfs.xmpp.packets.JFSInfo;
import org.junit.*;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class XmppTest {

    private Configuration cfg1 = ConfigurationFactory.getInstance().getConfiguration("./etc/configuration_local1.xml");
    private XMPPRemoteConnector sessionManager1 = new XMPPRemoteConnector();
    private FileConnector repositoryManager1 = new FileConnector(cfg1);


    private Configuration cfg2 = ConfigurationFactory.getInstance().getConfiguration("./etc/configuration_local2.xml");
    private XMPPRemoteConnector sessionManager2 = new XMPPRemoteConnector();
    private FileConnector repositoryManager2 = new FileConnector(cfg2);

    private String address1 = cfg1.getXmppLogin() + "@" + cfg1.getXmppServer();
    private String address2 = cfg2.getXmppLogin() + "@" + cfg2.getXmppServer();

    private CountDownLatch done = new CountDownLatch(1);



    @Before
    public void setUp() throws Exception {

        deleteFiles(new File(cfg1.getRepositoryPath()));
        deleteFiles(new File(cfg2.getRepositoryPath()));

        File testDir = new File("./etc/test");
        assertTrue(testDir.isDirectory());
        File[] files = testDir.listFiles();
        for (File file : files) {
            Files.copy(file.toPath(), new File(cfg1.getRepositoryPath()+File.separator+file.getName()).toPath());
        }

        repositoryManager1.setRepositoryVersion("2");
        repositoryManager2.setRepositoryVersion("1");

        System.out.println("! Peer1::connecting");
        assertTrue(sessionManager1.connect(cfg1));
        System.out.println("! Peer2::connecting");
        assertTrue(sessionManager2.connect(cfg2));
    }

    private void deleteFiles(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        for (File f : files) {
            f.delete();
        }
    }

    @Test
    public void testJFSGetRepository() throws InterruptedException {

        // peer1 have 2 files in its repository, peer2 have none
        // peer1 announce repository version, peer2 got this notification and request repository info from peer1
        // peer1 send repository to peer2, peer2 compares local rep with received rep of peer1 and decided to
        // request difference (these 2 files) from peer1, peer1 got fileset request and send requested files to
        // peer2. peer2 receives these files and stores it in local rep

        sessionManager1.setListener(new RemoteRepListener() {

            private Map<String, List<RepositoryRecord>> filesRequested = new HashMap<>();

            @Override
            public void jfsInfoReceived(String remoteAddress, JFSInfo jfsInfo) {
                System.out.println(String.format("! Peer1::Remote repository version received from: %s, %s", remoteAddress, jfsInfo.getRepositoryVersion()));
                if (!jfsInfo.getRepositoryVersion().equals(repositoryManager1.getRepositoryVersion())) {
                    System.out.println("! Peer1::Remote repository version is differ, sending RemoteRepReq");
                    sessionManager1.requestRemoteRepository(remoteAddress);
                }
            }

            @Override
            public void jfsGetRepositoryReceived(String remoteAddress, JFSGetRepository jfsGetRepository) {
                if (jfsGetRepository.isRequest()) {
                    if (jfsGetRepository.getItems() == null) {
                        System.out.println(String.format("! Peer1::Repository request received from: %s", remoteAddress));
                        sessionManager1.sendLocalRepository(remoteAddress, repositoryManager1.getRepository());
                    } else {
                        System.out.println(String.format("! Peer1::Repository sync received from: %s for files: ", remoteAddress, jfsGetRepository.getItems()));
                        for (RepositoryRecord record : jfsGetRepository.getItems()) {
                            sessionManager1.sendLocalFile(remoteAddress, repositoryManager1.getFile(record)); //todo check existance
                        }
                    }
                } else {
                    System.out.println(String.format("! Peer1::Requested repository received from: %s, %s", remoteAddress, jfsGetRepository.getItems()));
                    List<RepositoryRecord> toSync = repositoryManager1.getRecordsToSync(jfsGetRepository.getItems());
                    System.out.println("after getRecordsToSync");
                    if (toSync.size() > 0) {
                        System.out.println(String.format("! Peer1::Found repository diff: %s", toSync));
                        List<RepositoryRecord> requested = filesRequested.get(remoteAddress);
                        if (requested == null) {
                            requested = new ArrayList<RepositoryRecord>();
                            filesRequested.put(remoteAddress, requested);
                        }
                        requested.clear();
                        requested.addAll(toSync);
                        sessionManager1.requestRemoteFileSet(remoteAddress, toSync);
                    }
                }
            }

            @Override
            public boolean jfsRemoteFileTranferReceived(String remoteAddress, String fileName) {
                List<RepositoryRecord> requested = filesRequested.get(remoteAddress);
                boolean found = false;
                if (requested != null) {
                    for (RepositoryRecord record : requested) {
                        if (record.getName().equals(fileName)) {
                            System.out.println(String.format("! Peer1::Received file permitted: %s", fileName));
                            found = true;
                            break;
                        }
                    }
                }
                return found;
            }

            @Override
            public void jfsRemoteFileReceived(File file) {
                System.out.println("! Peer1::Remote file received, storing in repository: "+file);
                repositoryManager1.storeFile(file);
            }
        });

        sessionManager2.setListener(new RemoteRepListener() {

            private Map<String, List<RepositoryRecord>> filesRequested = new HashMap<>();

            @Override
            public void jfsInfoReceived(String remoteAddress, JFSInfo jfsInfo) {
                System.out.println(String.format("! Peer2::Remote repository version received from: %s, %s", remoteAddress, jfsInfo.getRepositoryVersion()));
                if (!jfsInfo.getRepositoryVersion().equals(repositoryManager2.getRepositoryVersion())) {
                    System.out.println("! Peer2::Remote repository version is differ, sending RemoteRepReq");
                    sessionManager2.requestRemoteRepository(remoteAddress);
                }
            }

            @Override
            public void jfsGetRepositoryReceived(String remoteAddress, JFSGetRepository jfsGetRepository) {
                if (jfsGetRepository.isRequest()) {
                    if (jfsGetRepository.getItems() == null) {
                        System.out.println(String.format("! Peer2::Repository request received from: %s", remoteAddress));
                        sessionManager2.sendLocalRepository(remoteAddress, repositoryManager2.getRepository());
                    } else {
                        System.out.println(String.format("! Peer2::Repository sync received from: %s for files: ", remoteAddress, jfsGetRepository.getItems()));
                        for (RepositoryRecord record : jfsGetRepository.getItems()) {
                            sessionManager2.sendLocalFile(remoteAddress, repositoryManager2.getFile(record)); //todo check existance
                        }
                    }
                } else {
                    System.out.println(String.format("! Peer2::Requested repository received from: %s, %s", remoteAddress, jfsGetRepository.getItems()));
                    List<RepositoryRecord> toSync = repositoryManager2.getRecordsToSync(jfsGetRepository.getItems());
                    if (toSync.size() > 0) {
                        System.out.println(String.format("! Peer2::Found repository diff: %s", toSync));
                        List<RepositoryRecord> requested = filesRequested.get(remoteAddress);
                        if (requested == null) {
                            requested = new ArrayList<RepositoryRecord>();
                            filesRequested.put(remoteAddress, requested);
                        }
                        requested.clear();
                        requested.addAll(toSync);
                        sessionManager2.requestRemoteFileSet(remoteAddress, toSync);
                    }
                }
            }

            @Override
            public boolean jfsRemoteFileTranferReceived(String remoteAddress, String fileName) {
                List<RepositoryRecord> requested = filesRequested.get(remoteAddress);
                boolean found = false;
                if (requested != null) {
                    for (RepositoryRecord record : requested) {
                        if (record.getName().equals(fileName)) {
                            System.out.println(String.format("! Peer2::Received file permitted: %s", fileName));
                            found = true;
                            break;
                        }
                    }
                }
                return found;
            }

            @Override
            public void jfsRemoteFileReceived(File file) {
                System.out.println("! Peer2::Remote file received, storing in repository: "+file);
                repositoryManager2.storeFile(file);
                done.countDown();
            }
        });

        System.out.println("! Peer1::Updating local repository version: " + repositoryManager1.getRepositoryVersion());
        sessionManager1.updateLocalRevision(repositoryManager1.getRepositoryVersion());
        done.await();
    }

    @After
    public void tearDown() throws Exception {
        sessionManager1.disconnect();
        sessionManager2.disconnect();
    }
}

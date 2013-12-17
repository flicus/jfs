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
import org.fa.jfs.xmpp.ConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmppTest {

    private ConnectionManager connectionManager1 = new ConnectionManager();
    private Configuration cfg1 = ConfigurationFactory.getInstance().getConfiguration("./configuration.xml");

    private ConnectionManager connectionManager2 = new ConnectionManager();
    private Configuration cfg2 = ConfigurationFactory.getInstance().getConfiguration("./configuration2.xml");


    @Before
    public void setUp() throws Exception {
//        connectionManager1 =
//        connectionManager2 = new ConnectionManager();
    }

    @Test
    public void testXmppConnect() throws Exception {
        assertTrue(connectionManager1.connect(cfg1));
        assertTrue(connectionManager2.connect(cfg2));

        //connectionManager1.subscribe(cfg2.getXmppLogin() + "@" + cfg2.getXmppServer());
        //Thread.sleep(1000);

        //connectionManager2.subscribe(cfg1.getXmppLogin() + "@" + cfg1.getXmppServer());
        //Thread.sleep(1000);

        String revision1 = "node1_1";
        connectionManager1.publishRevision(revision1);
        Thread.sleep(1000);
        assertEquals(revision1, connectionManager2.getLastReceivedRevision());


        String revision2 = "node2_1";
        connectionManager2.publishRevision(revision2);
        Thread.sleep(1000);
        assertEquals(revision2, connectionManager1.getLastReceivedRevision());

        //Thread.sleep(600*60*1000);
    }

    @After
    public void tearDown() throws Exception {
        connectionManager1.disconnect();
        connectionManager2.disconnect();
    }
}

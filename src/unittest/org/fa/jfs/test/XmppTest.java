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
import org.fa.jfs.xmpp.SessionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmppTest {

    private SessionManager sessionManager1 = new SessionManager();
    private Configuration cfg1 = ConfigurationFactory.getInstance().getConfiguration("./configuration.xml");

    private SessionManager sessionManager2 = new SessionManager();
    private Configuration cfg2 = ConfigurationFactory.getInstance().getConfiguration("./configuration2.xml");


    @Before
    public void setUp() throws Exception {
//        sessionManager1 =
//        sessionManager2 = new SessionManager();
    }

    @Test
    public void testXmppConnect() throws Exception {
        assertTrue(sessionManager1.connect(cfg1));
        assertTrue(sessionManager2.connect(cfg2));

        //sessionManager1.subscribe(cfg2.getXmppLogin() + "@" + cfg2.getXmppServer());
        //Thread.sleep(1000);

        //sessionManager2.subscribe(cfg1.getXmppLogin() + "@" + cfg1.getXmppServer());
        //Thread.sleep(1000);

        String revision1 = "node1_1";
        sessionManager1.publishRevision(revision1);
        Thread.sleep(1000*60*5);
        assertEquals(revision1, sessionManager2.getLastReceivedRevision());


        String revision2 = "node2_1";
        sessionManager2.publishRevision(revision2);
        Thread.sleep(1000*60*5);
        assertEquals(revision2, sessionManager1.getLastReceivedRevision());
    }

    @After
    public void tearDown() throws Exception {
        sessionManager1.disconnect();
        sessionManager2.disconnect();
    }
}

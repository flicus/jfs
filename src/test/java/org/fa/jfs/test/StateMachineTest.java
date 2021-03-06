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

package org.fa.jfs.test;

import com.thoughtworks.xstream.XStream;
import org.fa.jfs.sm.*;
import org.fa.jfs.sm.events.RemoteRepositoryRes;
import org.fa.jfs.sm.events.RemoteVersionRes;
import org.fa.jfs.xmpp.NotificationType;
import org.fa.jfs.xmpp.packets.JFSInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;

import static org.junit.Assert.*;

public class StateMachineTest {

    SmContext context = new SmContext();
    StateMachine sm = new StateMachine(context);
    int currentRepVersion = 5;
    SmEvent ev1 = new RemoteVersionRes(new JFSInfo(NotificationType.UPDATE, "12"));
    SmEvent ev3 = new RemoteVersionRes(new JFSInfo(NotificationType.UPDATE, "1"));
    SmEvent ev2 = new RemoteRepositoryRes("test");
    SmEvent startEvent = new SmEvent("start");

    SmState state1 = new SmState(5000);
    SmState state2 = new SmState(5000);

    SmAction a1 = new SmAction(SmAction.ActionType.INTER, (event, context1, smAction) -> {
        RemoteVersionRes e = (RemoteVersionRes) event;
        if (Integer.parseInt(e.getJfsInfo().getRepositoryVersion()) > currentRepVersion) {
            // need to request remote repository here, then going to state for awaiting request result
            // remote repository request call
            return sm.next(smAction.getRoute(SmEvent._ok));
        }
        // repository is up to date, returning to state1
        return sm.next(smAction.getRoute(SmEvent._1));
    });

    SmAction a2 = new SmAction(SmAction.ActionType.INTER, (event, context1, smAction) -> {
        //ok remote repository has arrived, do compare, save to fs and exit SM
        return sm.next(smAction.getRoute(SmEvent._ok));
    });

    @Before
    public void startup() {

        context.put("from", "vasya@pupkin.ru");
        context.put("sid", "0xffff");

        SmTransition t1 = new SmTransition(sm.start, state1, startEvent.getId());
        // from state1 to action1 on ev1
        SmTransition t2 = new SmTransition(state1, a1, ev1.getId());
        //to end if timeout
        SmTransition t3 = new SmTransition(state1, sm.end, SmEvent._timeout.getId());
        // from action1 to the state2 to wait request remote repository result
        SmTransition t4 = new SmTransition(a1, state2, SmEvent._ok.getId());
        //from action1 to state1 if nothing to do, waiting for RemoteVersionRes again
        SmTransition t5 = new SmTransition(a1, state1, SmEvent._1.getId());
        SmTransition t6 = new SmTransition(state2, sm.end, SmEvent._timeout.getId());
        SmTransition t7 = new SmTransition(state2, a2, ev2.getId());
        SmTransition t8 = new SmTransition(a2, sm.end, SmEvent._ok.getId());
    }


    @Test
    public void test1() throws Throwable {

        XStream xs = new XStream();
        xs.alias("StateMachine", StateMachine.class);
        xs.alias("SmTransition", SmTransition.class);
        xs.alias("SmState", SmState.class);
        xs.alias("SmAction", SmAction.class);

        xs.toXML(sm, new FileOutputStream("./etc/sm_text.xml"));

        assertFalse(sm.isFinished());

        SmNode node = sm.getCurrentNode();
        assertEquals(node, sm.start);

        node = sm.proceedEvent(startEvent);
        assertEquals(node, state1);

        node = sm.proceedEvent(ev3);
        assertEquals(node, state1);

        node = sm.proceedEvent(ev1);
        assertEquals(node, state2);

        node = sm.proceedEvent(ev2);
        assertEquals(node, sm.end);

        assertTrue(sm.isFinished());
    }
}

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

package org.fa.jfs.sm;

public class StateMachine {

    public final SmAction start = new SmAction(SmAction.ActionType.START, new SmAction.Action() {
        @Override
        public SmNode execute(SmEvent event, SmContext context, SmAction smAction) {
            return smAction.getRoute(event).getToNode();
        }
    });

    public final SmAction end = new SmAction(SmAction.ActionType.END, new SmAction.Action() {
        @Override
        public SmNode execute(SmEvent event, SmContext context, SmAction smAction) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    });

    private SmNode currentNode = start;
    private SmContext context;

    public StateMachine(SmContext context) {
        this.context = context;
    }

    public SmNode proceedEvent(SmEvent event) {
        if (isFinished()) return end;
        do {
            currentNode = currentNode.processEvent(event, context);
        } while (!(currentNode instanceof SmState) && (currentNode != end));
        return currentNode;
    }

    public SmNode getCurrentNode() {
        return currentNode;
    }

    public boolean isFinished() {
        return currentNode == end;
    }

    public SmNode next(SmTransition transition) {
        return transition == null ? end : transition.getToNode();
    }
}

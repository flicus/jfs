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

public class SmTransition {
    private SmNode fromNode;
    private SmNode toNode;
    private String triggerEventClass;

    public SmTransition() {
    }

    public SmTransition(SmNode fromNode, SmNode toNode, String triggerEventClass) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.triggerEventClass = triggerEventClass;
    }

    public SmNode getFromNode() {
        return fromNode;
    }

    public SmTransition setFromNode(SmNode fromNode) {
        this.fromNode = fromNode;
        return this;
    }

    public SmNode getToNode() {
        return toNode;
    }

    public SmTransition setToNode(SmNode toNode) {
        this.toNode = toNode;
        return this;
    }

    public String getTriggerEventClass() {
        return triggerEventClass;
    }

    public void setTriggerEventClass(String triggerEventClass) {
        this.triggerEventClass = triggerEventClass;
    }

    @Override
    public String toString() {
        return "SmTransition{" +
                "fromNode=" + fromNode +
                ", toNode=" + toNode +
                ", triggerEventClass='" + triggerEventClass + '\'' +
                '}';
    }
}

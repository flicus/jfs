/*
 * The MIT License
 *
 * Copyright (c) 2013  Sergey Skoptsov (flicus@gmail.com), Alexey Marin (asmadews@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.fa.jfs.common;

public class Configuration {

    private String xmppServer;
    private String xmppLogin;
    private String xmppPassword;
    private String xmppResource;

    public Configuration() {
    }
    //

    public Configuration(String xmppServer, String xmppLogin, String xmppPassword, String xmppResource) {
        this.xmppServer = xmppServer;
        this.xmppLogin = xmppLogin;
        this.xmppPassword = xmppPassword;
        this.xmppResource = xmppResource;
    }

    public String getXmppServer() {
        return xmppServer;
    }

    public void setXmppServer(String xmppServer) {
        this.xmppServer = xmppServer;
    }

    public String getXmppLogin() {
        return xmppLogin;
    }

    public void setXmppLogin(String xmppLogin) {
        this.xmppLogin = xmppLogin;
    }

    public String getXmppPassword() {
        return xmppPassword;
    }

    public void setXmppPassword(String xmppPassword) {
        this.xmppPassword = xmppPassword;
    }

    public String getXmppResource() {
        return xmppResource;
    }

    public void setXmppResource(String xmppResource) {
        this.xmppResource = xmppResource;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "xmppServer='" + xmppServer + '\'' +
                ", xmppLogin='" + xmppLogin + '\'' +
                ", xmppPassword='" + xmppPassword + '\'' +
                ", xmppResource='" + xmppResource + '\'' +
                '}';
    }
}

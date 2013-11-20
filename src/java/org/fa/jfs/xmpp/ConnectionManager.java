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

package org.fa.jfs.xmpp;

import org.fa.jfs.common.Configuration;
import org.fa.jfs.common.GUIDGenerator;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceCapability;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.NodeInformationProvider;
import org.jivesoftware.smackx.PEPListener;
import org.jivesoftware.smackx.PEPManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.PEPEvent;
import org.jivesoftware.smackx.provider.PEPProvider;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConnectionManager {

    private Connection connection;
    private PEPManager pepManager;
    private String lastReceivedRevision;

    private static final String[] features = new String[] {
            "http://jabber.org/protocol/caps",
            "http://jabber.org/protocol/disco#info",
            "http://jabber.org/protocol/disco#items",
            JFSNotification.NAMESPACE,
            JFSNotification.NAMESPACE+"+notify"
    };

    public ConnectionManager() {
        Connection.DEBUG_ENABLED = true;
        lastReceivedRevision = "";
    }

    public boolean connect(Configuration cfg) {
        boolean result = true;
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }

        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);  //todo

        ConnectionConfiguration config = new ConnectionConfiguration(cfg.getXmppServer()/*, new ProxyInfo(ProxyInfo.ProxyType.HTTP, "10.50.8.19", 8080, "", "")*/);
        config.setCompressionEnabled(true);
        config.setReconnectionAllowed(true);
        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);

        ServiceDiscoveryManager.getIdentityName();

        connection = new XMPPConnection(config);
        try {
            connection.connect();
            connection.login(cfg.getXmppLogin(), cfg.getXmppPassword(), cfg.getXmppResource());

            PacketFilter filter = new PacketTypeFilter(Presence.class);
            PacketListener listener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    if (packet instanceof Presence) {
                        Presence presence = (Presence)packet;
                        System.out.println(presence.getFrom() +", " + presence.getStatus() + ", " +presence.getType());
                        String f = StringUtils.parseBareAddress(packet.getFrom());
                        if (presence.getType().equals(Presence.Type.subscribe) && !connection.getRoster().contains(presence.getFrom())) {
                            try {
                                connection.getRoster().createEntry(f, f, null);
                            } catch (XMPPException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            Presence subscribed = new Presence(Presence.Type.subscribed);
                            subscribed.setTo(f);
                            connection.sendPacket(subscribed);
                        }
                    }
                }
            };
            connection.addPacketListener(listener, filter);

            pepManager = new PEPManager(connection);
            pepManager.addPEPListener(new PEPListener() {
                @Override
                public void eventReceived(String from, PEPEvent event) {
                    lastReceivedRevision = ((JFSNotification)event.getItem()).getRepositoryVersion();
                    System.out.println(event.getItem());
                }
            });
            PEPProvider pepProvider = new PEPProvider();
            pepProvider.registerPEPParserExtension(JFSNotification.NAMESPACE, new JFSNotificationProvider());
            ProviderManager.getInstance().addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", pepProvider);

            for (RosterEntry entry : connection.getRoster().getEntries()) {
                System.out.println("Roster.entry: " + entry.toString());
            }

            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);

            //supported features
            for (String item : features) {
                discoManager.addFeature(item);
            }

            final DiscoverInfo.Identity idt = new DiscoverInfo.Identity("client", "JFS client", "pc");
            String ver = computeVersion(idt, features);
            discoManager.setNodeInformationProvider("http://0xffff.net/jfs#"+ver, new NodeInformationProvider() {
                @Override
                public List<DiscoverItems.Item> getNodeItems() {
                    List<DiscoverItems.Item> list = new ArrayList<DiscoverItems.Item>();
                    for (String item : features) {
                        list.add(new DiscoverItems.Item(item));
                    }
                    return list;
                }

                @Override
                public List<String> getNodeFeatures() {
                    return Arrays.asList(features);
                }

                @Override
                public List<DiscoverInfo.Identity> getNodeIdentities() {
                    return Arrays.asList(new DiscoverInfo.Identity[]{idt});
                }
            });

            Presence presence = new Presence(Presence.Type.available, "Ready", 10, Presence.Mode.available);
            presence.setCaps(new PresenceCapability("sha-1", "http://0xffff.net/jfs", ver));
            connection.sendPacket(presence);


            //look for server capabilities
            DiscoverItems discoItems = discoManager.discoverItems(cfg.getXmppServer());
            Iterator it = discoItems.getItems();

            System.out.println("Discovered items: ");
            while (it.hasNext()) {
                DiscoverItems.Item item = (DiscoverItems.Item) it.next();
                System.out.println(item.getEntityID());
                System.out.println(item.getNode());
                System.out.println(item.getName());
            }

            DiscoverInfo discoInfo = discoManager.discoverInfo(cfg.getXmppServer());
            it = discoInfo.getIdentities();
            System.out.println("Discovered info: ");
            while (it.hasNext()) {
                DiscoverInfo.Identity identity = (DiscoverInfo.Identity) it.next();
                System.out.println(identity.getName());
                System.out.println(identity.getType());
                System.out.println(identity.getCategory());
            }

        } catch (XMPPException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            result = false;
        }

        return result;
    }

    private String computeVersion(DiscoverInfo.Identity identity, String[] features) {
        StringBuilder s = new StringBuilder();
        Arrays.sort(features);

        s.append(identity.getCategory()).append("/").append(identity.getType()).append("/");
        //no language here
        s.append("/");
        s.append(identity.getName()).append("<");
        for (String feature : features) {
            s.append(feature).append("<");
        }

        byte[] res = null;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.toString().getBytes("UTF-8"));
            res = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Base64.encodeBytes(res);
    }

    public void publishRevision(String revision) {
        String newId =  newId();
        JFSNotification jfsn = new JFSNotification(newId, NotificationType.UPDATE, revision);
        pepManager.publish(jfsn);
    }

    public void subscribe(String to) {
        Presence subscribe = new Presence(Presence.Type.subscribe);
        subscribe.setTo(to);
        connection.sendPacket(subscribe);
    }

    private String newId() {
        return Long.toHexString(GUIDGenerator.getGUID());
    }

    public void disconnect() {
        connection.disconnect();
    }

    public String getLastReceivedRevision() {
        return lastReceivedRevision;
    }
}

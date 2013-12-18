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
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.NodeInformationProvider;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XMPPRemoteConnector implements RemoteRepConnector {

    private Connection connection;
    private ServiceDiscoveryManager discoManager;
    private String lastReceivedRevision;
    private RemoteRepListener remoteRepListener;

    private static final String NODE = "http://0xffff.net/jfs";
    private static final String[] features = new String[]{
            "http://jabber.org/protocol/caps",  //xep-0163
            "http://jabber.org/protocol/disco#info",
            "http://jabber.org/protocol/disco#items",
            "http://jabber.org/protocol/bytestreams",   //xep-0065
            "http://jabber.org/protocol/si",                    //xep-0095
            "http://jabber.org/protocol/si/profile/file-transfer",  //xep-0096
            "http://jabber.org/protocol/ibb",   //xep-0047
            JFSPacketExtension.NAMESPACE    // our megafeature!!!
    };

    private static final DiscoverInfo.Identity identity = new DiscoverInfo.Identity("client", "JFS client"/*, "bot"*/);

    public XMPPRemoteConnector() {

        Connection.DEBUG_ENABLED = true;
        lastReceivedRevision = "";

        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);  //todo
    }

    public boolean connect(Configuration cfg) {
        boolean result = true;
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }

        ConnectionConfiguration config = new ConnectionConfiguration(cfg.getXmppServer()/*, new ProxyInfo(ProxyInfo.ProxyType.HTTP, "10.50.8.19", 8080, "", "")*/);
        config.setCompressionEnabled(true);
        config.setReconnectionAllowed(true);
        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);

        connection = new XMPPConnection(config);

        try {
            connection.connect();
            connection.login(cfg.getXmppLogin(), cfg.getXmppPassword(), cfg.getXmppResource());
            connection.addPacketListener(new PresenceListener(), new PacketTypeFilter(Presence.class));
            ProviderManager.getInstance().addExtensionProvider(JFSPacketExtension.NAME, JFSPacketExtension.NAMESPACE, new JFSPacketExtProvider());

            //to workaround bug in smack
            ServiceDiscoveryManager.getIdentityName();
            discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
            //supported features
            for (String item : features) {
                discoManager.addFeature(item);
            }

            for (RosterEntry entry : connection.getRoster().getEntries()) {
                System.out.println("Roster.entry: " + entry.toString());
            }
        } catch (XMPPException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            result = false;
        }

        return result;
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



    @Override
    public void setListener(RemoteRepListener listener) {
        this.remoteRepListener = listener;
    }

    @Override
    public void updateLocalRevision(String revision) {
        Presence presence = new Presence(Presence.Type.available, "Ready", 10, Presence.Mode.available);
        presence.addExtension(new JFSPacketExtension(NotificationType.UPDATE, revision));
        connection.sendPacket(presence);
    }

    @Override
    public void requestRemoteRepository() {

    }

    @Override
    public void requestRemoteFileSet() {

    }

    @Override
    public void sendLocalFileSet() {

    }


    private class PresenceListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                System.out.println(presence.getFrom() + ", " + presence.getStatus() + ", " + presence.getType());
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
                } else {
                    PacketExtension pe = presence.getExtension(JFSPacketExtension.NAMESPACE);
                    if (pe != null) {
                        JFSPacketExtension jfsPacketExtension = (JFSPacketExtension)pe;
                        lastReceivedRevision = jfsPacketExtension.getRepositoryVersion();
                    }
                }
            }
        }
    }
}

/*
discoManager.setNodeInformationProvider(NODE, new NodeInformationProvider() {
@Override
public List<DiscoverItems.Item> getNodeItems() {
        List<DiscoverItems.Item> list = new ArrayList<DiscoverItems.Item>();
        //for (String item : features) {
        list.add(new DiscoverItems.Item(NODE));
        //}
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
*/

//presence.setCaps(new PresenceCapability("sha-1", NODE, ver));
//connection.sendPacket(presence);


//look for server capabilities
//            DiscoverItems discoItems = discoManager.discoverItems(cfg.getXmppServer());
//            Iterator it = discoItems.getItems();
//
//            System.out.println("Discovered items: ");
//            while (it.hasNext()) {
//                DiscoverItems.Item item = (DiscoverItems.Item) it.next();
//                System.out.println(item.getEntityID());
//                System.out.println(item.getNode());
//                System.out.println(item.getName());
//            }
//
//            DiscoverInfo discoInfo = discoManager.discoverInfo(cfg.getXmppServer());
//            it = discoInfo.getIdentities();
//            System.out.println("Discovered info: ");
//            while (it.hasNext()) {
//                DiscoverInfo.Identity identity = (DiscoverInfo.Identity) it.next();
//                System.out.println(identity.getName());
//                System.out.println(identity.getType());
//                System.out.println(identity.getCategory());
//            }

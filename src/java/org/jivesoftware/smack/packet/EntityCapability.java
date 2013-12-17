package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smackx.packet.DiscoverInfo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * XEP-0115 Entity Capabilities
 */

public class EntityCapability implements PacketExtension {
    public static final String XMLNS = "http://jabber.org/protocol/caps";

    private String hash;
    private String node;
    private String version;

    public EntityCapability() {
    }

    public EntityCapability(String hash, String node, String version) {
        this.hash = hash;
        this.node = node;
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public EntityCapability setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getNode() {
        return node;
    }

    public EntityCapability setNode(String node) {
        this.node = node;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public EntityCapability setVersion(String version) {
        this.version = version;
        return this;
    }

    public static final EntityCapability createCapabilities(String node, DiscoverInfo.Identity identity, String[] features) {
        return new EntityCapability("sha-1", node, computeVersion(identity, features));
    }

    private static String computeVersion(DiscoverInfo.Identity identity, String[] features) {
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

    @Override
    public String getElementName() {
        return "c"; //lol
    }

    @Override
    public String getNamespace() {
        return XMLNS;
    }

    @Override
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<c xmlns='").append(XMLNS).append("'")
                .append(" hash='").append(hash).append("'")
                .append(" node='").append(node).append("'")
                .append(" ver='").append(version).append("'")
                .append("/>");
        String res = builder.toString();
        System.out.println("Presence cap: " + res);
        return res;
    }
}

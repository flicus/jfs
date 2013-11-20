package org.jivesoftware.smack.packet;

public class PresenceCapability {
    public static final String XMLNS = "http://jabber.org/protocol/caps";

    private String hash;
    private String node;
    private String version;

    public PresenceCapability() {
    }

    public PresenceCapability(String hash, String node, String version) {
        this.hash = hash;
        this.node = node;
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public PresenceCapability setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getNode() {
        return node;
    }

    public PresenceCapability setNode(String node) {
        this.node = node;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public PresenceCapability setVersion(String version) {
        this.version = version;
        return this;
    }

    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<c xmlns='").append(XMLNS).append("'")
                .append(" hash='").append(hash).append("'")
                .append(" node='").append(node).append("'")
                .append(" ver='").append(version).append("'")
        .append("/>");
        String res = builder.toString();
        System.out.println("Presence cap: "+res);
        return res;
    }
}

/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Category(value={"Minecraft", "Network"})
@StackTrace(value=false)
@Enabled(value=false)
public abstract class PacketEvent
extends Event {
    @Name(value="protocolId")
    @Label(value="Protocol Id")
    public final int protocolId;
    @Name(value="packetId")
    @Label(value="Packet Id")
    public final int packetId;
    @Name(value="remoteAddress")
    @Label(value="Remote Address")
    public final String remoteAddress;
    @Name(value="bytes")
    @Label(value="Bytes")
    @DataAmount
    public final int bytes;

    public PacketEvent(int protocolId, int packetId, SocketAddress remoteAddress, int bytes) {
        this.protocolId = protocolId;
        this.packetId = packetId;
        this.remoteAddress = remoteAddress.toString();
        this.bytes = bytes;
    }

    public static final class Names {
        public static final String REMOTE_ADDRESS = "remoteAddress";
        public static final String PROTOCOL_ID = "protocolId";
        public static final String PACKET_ID = "packetId";
        public static final String BYTES = "bytes";

        private Names() {
        }
    }
}


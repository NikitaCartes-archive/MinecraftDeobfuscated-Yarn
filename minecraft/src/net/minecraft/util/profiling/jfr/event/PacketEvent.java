package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Category({"Minecraft", "Network"})
@StackTrace(false)
@Enabled(false)
public abstract class PacketEvent extends Event {
	@Name("protocolId")
	@Label("Protocol Id")
	public final int protocolId;
	@Name("packetId")
	@Label("Packet Id")
	public final int packetId;
	@Name("remoteAddress")
	@Label("Remote Address")
	public final String remoteAddress;
	@Name("bytes")
	@Label("Bytes")
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

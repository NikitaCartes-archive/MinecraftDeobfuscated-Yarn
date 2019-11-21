package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker MARKER = MarkerManager.getMarker("PACKET_SENT", ClientConnection.MARKER_NETWORK_PACKETS);
	private final NetworkSide side;

	public PacketEncoder(NetworkSide networkSide) {
		this.side = networkSide;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
		NetworkState networkState = (NetworkState)channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get();
		if (networkState == null) {
			throw new RuntimeException("ConnectionProtocol unknown: " + packet);
		} else {
			Integer integer = networkState.getPacketId(this.side, packet);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
					MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get(), integer, packet.getClass().getName()
				);
			}

			if (integer == null) {
				throw new IOException("Can't serialize unregistered packet");
			} else {
				PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
				packetByteBuf.writeVarInt(integer);

				try {
					packet.write(packetByteBuf);
				} catch (Throwable var8) {
					LOGGER.error(var8);
					if (packet.isErrorFatal()) {
						throw new PacketEncoderException(var8);
					} else {
						throw var8;
					}
				}
			}
		}
	}
}

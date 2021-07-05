package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker MARKER = MarkerManager.getMarker("PACKET_SENT", ClientConnection.NETWORK_PACKETS_MARKER);
	private final NetworkSide side;

	public PacketEncoder(NetworkSide side) {
		this.side = side;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
		NetworkState networkState = (NetworkState)channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get();
		if (networkState == null) {
			throw new RuntimeException("ConnectionProtocol unknown: " + packet);
		} else {
			Integer integer = networkState.getPacketId(this.side, packet);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
					MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), integer, packet.getClass().getName()
				);
			}

			if (integer == null) {
				throw new IOException("Can't serialize unregistered packet");
			} else {
				PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
				packetByteBuf.writeVarInt(integer);

				try {
					int i = packetByteBuf.writerIndex();
					packet.write(packetByteBuf);
					int j = packetByteBuf.writerIndex() - i;
					if (j > 8388608) {
						throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than 8388608): " + packet);
					}
				} catch (Throwable var9) {
					LOGGER.error(var9);
					if (packet.isWritingErrorSkippable()) {
						throw new PacketEncoderException(var9);
					} else {
						throw var9;
					}
				}
			}
		}
	}
}

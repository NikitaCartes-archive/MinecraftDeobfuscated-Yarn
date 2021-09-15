package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.util.profiling.jfr.event.network.PacketReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DecoderHandler extends ByteToMessageDecoder {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker MARKER = MarkerManager.getMarker("PACKET_RECEIVED", ClientConnection.NETWORK_PACKETS_MARKER);
	private final NetworkSide side;

	public DecoderHandler(NetworkSide side) {
		this.side = side;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		int i = byteBuf.readableBytes();
		if (i != 0) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
			int j = packetByteBuf.readVarInt();
			Packet<?> packet = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getPacketHandler(this.side, j, packetByteBuf);
			if (packet == null) {
				throw new IOException("Bad packet id " + j);
			} else {
				if (PacketReceivedEvent.TYPE.isEnabled()) {
					int k = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId();
					String string = "%d/%d (%s)".formatted(k, j, packet.getClass().getSimpleName());
					PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(string, channelHandlerContext.channel().remoteAddress(), i);
					packetReceivedEvent.commit();
				}

				if (packetByteBuf.readableBytes() > 0) {
					throw new IOException(
						"Packet "
							+ channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId()
							+ "/"
							+ j
							+ " ("
							+ packet.getClass().getSimpleName()
							+ ") was larger than I expected, found "
							+ packetByteBuf.readableBytes()
							+ " bytes extra whilst reading packet "
							+ j
					);
				} else {
					list.add(packet);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
							MARKER, " IN: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), j, packet.getClass().getName()
						);
					}
				}
			}
		}
	}
}

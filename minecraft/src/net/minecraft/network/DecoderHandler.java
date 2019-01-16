package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DecoderHandler extends ByteToMessageDecoder {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker MARKER = MarkerManager.getMarker("PACKET_RECEIVED", ClientConnection.field_11639);
	private final NetworkSide side;

	public DecoderHandler(NetworkSide networkSide) {
		this.side = networkSide;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() != 0) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
			int i = packetByteBuf.readVarInt();
			Packet<?> packet = channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get().getPacketHandler(this.side, i);
			if (packet == null) {
				throw new IOException("Bad packet id " + i);
			} else {
				packet.read(packetByteBuf);
				if (packetByteBuf.readableBytes() > 0) {
					throw new IOException(
						"Packet "
							+ channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get().getId()
							+ "/"
							+ i
							+ " ("
							+ packet.getClass().getSimpleName()
							+ ") was larger than I expected, found "
							+ packetByteBuf.readableBytes()
							+ " bytes extra whilst reading packet "
							+ i
					);
				} else {
					list.add(packet);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(MARKER, " IN: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.ATTR_KEY_PROTOCOL).get(), i, packet.getClass().getName());
					}
				}
			}
		}
	}
}

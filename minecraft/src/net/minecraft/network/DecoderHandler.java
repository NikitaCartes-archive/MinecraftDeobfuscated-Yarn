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
			int j = byteBuf.readerIndex();
			PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
			int k = packetByteBuf.readVarInt();
			Packet<?> packet = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getPacketHandler(this.side, k, packetByteBuf);
			int l = byteBuf.readerIndex() - j;
			if (packet == null) {
				throw new IOException("Bad packet id " + k);
			} else {
				PacketReceivedEvent packetReceivedEvent = (PacketReceivedEvent)PacketReceivedEvent.EVENT.get();
				if (packetReceivedEvent.isEnabled() && packetReceivedEvent.shouldCommit()) {
					packetReceivedEvent.packetName = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId()
						+ "/"
						+ k
						+ " ("
						+ packet.getClass().getSimpleName()
						+ ")";
					packetReceivedEvent.bytes = l;
					packetReceivedEvent.commit();
					packetReceivedEvent.reset();
				}

				if (packetByteBuf.readableBytes() > 0) {
					throw new IOException(
						"Packet "
							+ channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId()
							+ "/"
							+ k
							+ " ("
							+ packet.getClass().getSimpleName()
							+ ") was larger than I expected, found "
							+ packetByteBuf.readableBytes()
							+ " bytes extra whilst reading packet "
							+ k
					);
				} else {
					list.add(packet);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
							MARKER, " IN: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), k, packet.getClass().getName()
						);
					}
				}
			}
		}
	}
}

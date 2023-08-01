package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class DecoderHandler extends ByteToMessageDecoder implements NetworkStateTransitionHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final AttributeKey<NetworkState.PacketHandler<?>> protocolKey;

	public DecoderHandler(AttributeKey<NetworkState.PacketHandler<?>> protocolKey) {
		this.protocolKey = protocolKey;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) throws Exception {
		int i = buf.readableBytes();
		if (i != 0) {
			Attribute<NetworkState.PacketHandler<?>> attribute = ctx.channel().attr(this.protocolKey);
			NetworkState.PacketHandler<?> packetHandler = attribute.get();
			PacketByteBuf packetByteBuf = new PacketByteBuf(buf);
			int j = packetByteBuf.readVarInt();
			Packet<?> packet = packetHandler.createPacket(j, packetByteBuf);
			if (packet == null) {
				throw new IOException("Bad packet id " + j);
			} else {
				FlightProfiler.INSTANCE.onPacketReceived(packetHandler.getState(), j, ctx.channel().remoteAddress(), i);
				if (packetByteBuf.readableBytes() > 0) {
					throw new IOException(
						"Packet "
							+ packetHandler.getState().getId()
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
					objects.add(packet);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(ClientConnection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", packetHandler.getState().getId(), j, packet.getClass().getName());
					}

					NetworkStateTransitionHandler.handle(attribute, packet);
				}
			}
		}
	}
}

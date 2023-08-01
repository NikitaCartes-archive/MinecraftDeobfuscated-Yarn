package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final AttributeKey<NetworkState.PacketHandler<?>> protocolKey;

	public PacketEncoder(AttributeKey<NetworkState.PacketHandler<?>> protocolKey) {
		this.protocolKey = protocolKey;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
		Attribute<NetworkState.PacketHandler<?>> attribute = channelHandlerContext.channel().attr(this.protocolKey);
		NetworkState.PacketHandler<?> packetHandler = attribute.get();
		if (packetHandler == null) {
			throw new RuntimeException("ConnectionProtocol unknown: " + packet);
		} else {
			int i = packetHandler.getId(packet);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(ClientConnection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", packetHandler.getState().getId(), i, packet.getClass().getName());
			}

			if (i == -1) {
				throw new IOException("Can't serialize unregistered packet");
			} else {
				PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
				packetByteBuf.writeVarInt(i);

				try {
					int j = packetByteBuf.writerIndex();
					packet.write(packetByteBuf);
					int k = packetByteBuf.writerIndex() - j;
					if (k > 8388608) {
						throw new IllegalArgumentException("Packet too big (is " + k + ", should be less than 8388608): " + packet);
					}

					FlightProfiler.INSTANCE.onPacketSent(packetHandler.getState(), i, channelHandlerContext.channel().remoteAddress(), k);
				} catch (Throwable var13) {
					LOGGER.error("Error receiving packet {}", i, var13);
					if (packet.isWritingErrorSkippable()) {
						throw new PacketEncoderException(var13);
					}

					throw var13;
				} finally {
					NetworkStateTransitionHandler.handle(attribute, packet);
				}
			}
		}
	}
}

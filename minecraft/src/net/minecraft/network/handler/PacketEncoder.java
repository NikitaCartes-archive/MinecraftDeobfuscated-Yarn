package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.class_9127;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class PacketEncoder<T extends PacketListener> extends MessageToByteEncoder<Packet<T>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final class_9127<T> field_48537;

	public PacketEncoder(class_9127<T> arg) {
		this.field_48537 = arg;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<T> packet, ByteBuf byteBuf) throws Exception {
		PacketIdentifier<? extends Packet<? super T>> packetIdentifier = packet.getPacketId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(ClientConnection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", this.field_48537.id().getId(), packetIdentifier, packet.getClass().getName());
		}

		try {
			int i = byteBuf.writerIndex();
			this.field_48537.codec().encode(byteBuf, packet);
			int j = byteBuf.writerIndex() - i;
			if (j > 8388608) {
				throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than 8388608): " + packet);
			}

			FlightProfiler.INSTANCE.onPacketSent(this.field_48537.id(), packetIdentifier, channelHandlerContext.channel().remoteAddress(), j);
		} catch (Throwable var10) {
			LOGGER.error("Error receiving packet {}", packetIdentifier, var10);
			if (packet.isWritingErrorSkippable()) {
				throw new PacketEncoderException(var10);
			}

			throw var10;
		} finally {
			NetworkStateTransitionHandler.method_56348(channelHandlerContext, packet);
		}
	}
}

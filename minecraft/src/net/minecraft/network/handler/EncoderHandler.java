package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class EncoderHandler<T extends PacketListener> extends MessageToByteEncoder<Packet<T>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final NetworkState<T> state;

	public EncoderHandler(NetworkState<T> state) {
		this.state = state;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<T> packet, ByteBuf byteBuf) throws Exception {
		PacketType<? extends Packet<? super T>> packetType = packet.getPacketId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(ClientConnection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", this.state.id().getId(), packetType, packet.getClass().getName());
		}

		try {
			int i = byteBuf.writerIndex();
			this.state.codec().encode(byteBuf, packet);
			int j = byteBuf.writerIndex() - i;
			if (j > 8388608) {
				throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than 8388608): " + packet);
			}

			FlightProfiler.INSTANCE.onPacketSent(this.state.id(), packetType, channelHandlerContext.channel().remoteAddress(), j);
		} catch (Throwable var10) {
			LOGGER.error("Error sending packet {}", packetType, var10);
			if (packet.isWritingErrorSkippable()) {
				throw new PacketEncoderException(var10);
			}

			throw var10;
		} finally {
			NetworkStateTransitionHandler.onEncoded(channelHandlerContext, packet);
		}
	}
}

package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class DecoderHandler<T extends PacketListener> extends ByteToMessageDecoder implements NetworkStateTransitionHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final NetworkState<T> state;

	public DecoderHandler(NetworkState<T> state) {
		this.state = state;
	}

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> objects) throws Exception {
		int i = buf.readableBytes();
		if (i != 0) {
			Packet<? super T> packet = this.state.codec().decode(buf);
			PacketType<? extends Packet<? super T>> packetType = packet.getPacketId();
			FlightProfiler.INSTANCE.onPacketReceived(this.state.id(), packetType, context.channel().remoteAddress(), i);
			if (buf.readableBytes() > 0) {
				throw new IOException(
					"Packet "
						+ this.state.id().getId()
						+ "/"
						+ packetType
						+ " ("
						+ packet.getClass().getSimpleName()
						+ ") was larger than I expected, found "
						+ buf.readableBytes()
						+ " bytes extra whilst reading packet "
						+ packetType
				);
			} else {
				objects.add(packet);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(ClientConnection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {} -> {} bytes", this.state.id().getId(), packetType, packet.getClass().getName(), i);
				}

				NetworkStateTransitionHandler.onDecoded(context, packet);
			}
		}
	}
}

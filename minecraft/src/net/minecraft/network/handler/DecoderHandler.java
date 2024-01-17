package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.class_9127;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class DecoderHandler<T extends PacketListener> extends ByteToMessageDecoder implements NetworkStateTransitionHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final class_9127<T> field_48536;

	public DecoderHandler(class_9127<T> arg) {
		this.field_48536 = arg;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> objects) throws Exception {
		int i = buf.readableBytes();
		if (i != 0) {
			Packet<? super T> packet = this.field_48536.codec().decode(buf);
			PacketIdentifier<? extends Packet<? super T>> packetIdentifier = packet.getPacketId();
			FlightProfiler.INSTANCE.onPacketReceived(this.field_48536.id(), packetIdentifier, channelHandlerContext.channel().remoteAddress(), i);
			if (buf.readableBytes() > 0) {
				throw new IOException(
					"Packet "
						+ this.field_48536.id().getId()
						+ "/"
						+ packetIdentifier
						+ " ("
						+ packet.getClass().getSimpleName()
						+ ") was larger than I expected, found "
						+ buf.readableBytes()
						+ " bytes extra whilst reading packet "
						+ packetIdentifier
				);
			} else {
				objects.add(packet);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(ClientConnection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", this.field_48536.id().getId(), packetIdentifier, packet.getClass().getName());
				}

				NetworkStateTransitionHandler.method_56347(channelHandlerContext, packet);
			}
		}
	}
}

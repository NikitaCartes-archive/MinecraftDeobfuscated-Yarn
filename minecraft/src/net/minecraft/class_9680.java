package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.handler.PacketSizeLogger;

public class class_9680 extends ChannelInboundHandlerAdapter {
	private final PacketSizeLogger field_51500;

	public class_9680(PacketSizeLogger packetSizeLogger) {
		this.field_51500 = packetSizeLogger;
	}

	@Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) {
		if (object instanceof ByteBuf byteBuf) {
			this.field_51500.increment(byteBuf.readableBytes());
		}

		channelHandlerContext.fireChannelRead(object);
	}
}

package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.class_9130;
import net.minecraft.network.packet.Packet;

public interface NetworkStateTransitionHandler {
	static void method_56347(ChannelHandlerContext channelHandlerContext, Packet<?> packet) {
		if (packet.transitionsNetworkState()) {
			channelHandlerContext.channel().config().setAutoRead(false);
			channelHandlerContext.pipeline().addBefore(channelHandlerContext.name(), "inbound_config", new class_9130.class_9131());
			channelHandlerContext.pipeline().remove(channelHandlerContext.name());
		}
	}

	static void method_56348(ChannelHandlerContext channelHandlerContext, Packet<?> packet) {
		if (packet.transitionsNetworkState()) {
			channelHandlerContext.pipeline().addAfter(channelHandlerContext.name(), "outbound_config", new class_9130.class_9133());
			channelHandlerContext.pipeline().remove(channelHandlerContext.name());
		}
	}
}

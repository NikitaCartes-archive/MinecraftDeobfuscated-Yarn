package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AttributeKey;
import java.util.List;
import net.minecraft.network.packet.Packet;

public class PacketUnbundler extends MessageToMessageEncoder<Packet<?>> {
	private final AttributeKey<? extends PacketBundleHandler.BundlerGetter> protocolKey;

	public PacketUnbundler(AttributeKey<? extends PacketBundleHandler.BundlerGetter> protocolKey) {
		this.protocolKey = protocolKey;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
		PacketBundleHandler.BundlerGetter bundlerGetter = channelHandlerContext.channel().attr(this.protocolKey).get();
		if (bundlerGetter == null) {
			throw new EncoderException("Bundler not configured: " + packet);
		} else {
			bundlerGetter.getBundler().forEachPacket(packet, list::add);
		}
	}
}

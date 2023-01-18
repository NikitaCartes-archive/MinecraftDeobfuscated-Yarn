package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.network.packet.Packet;

public class PacketUnbundler extends MessageToMessageEncoder<Packet<?>> {
	private final NetworkSide side;

	public PacketUnbundler(NetworkSide side) {
		this.side = side;
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
		PacketBundleHandler.BundlerGetter bundlerGetter = channelHandlerContext.channel().attr(PacketBundleHandler.KEY).get();
		if (bundlerGetter == null) {
			throw new EncoderException("Bundler not configured: " + packet);
		} else {
			bundlerGetter.getBundler(this.side).forEachPacket(packet, list::add);
		}
	}
}

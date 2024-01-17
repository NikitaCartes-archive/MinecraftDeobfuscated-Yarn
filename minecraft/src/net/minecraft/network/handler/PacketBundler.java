package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.packet.Packet;

public class PacketBundler extends MessageToMessageDecoder<Packet<?>> {
	private final PacketBundleHandler handler;
	@Nullable
	private PacketBundleHandler.Bundler currentBundler;

	public PacketBundler(PacketBundleHandler handler) {
		this.handler = handler;
	}

	protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
		if (this.currentBundler != null) {
			method_56346(packet);
			Packet<?> packet2 = this.currentBundler.add(packet);
			if (packet2 != null) {
				this.currentBundler = null;
				list.add(packet2);
			}
		} else {
			PacketBundleHandler.Bundler bundler = this.handler.createBundler(packet);
			if (bundler != null) {
				method_56346(packet);
				this.currentBundler = bundler;
			} else {
				list.add(packet);
				if (packet.transitionsNetworkState()) {
					channelHandlerContext.pipeline().remove(channelHandlerContext.name());
				}
			}
		}
	}

	private static void method_56346(Packet<?> packet) {
		if (packet.transitionsNetworkState()) {
			throw new DecoderException("Terminal message received in bundle");
		}
	}
}

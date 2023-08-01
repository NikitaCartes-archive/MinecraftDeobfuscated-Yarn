package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;
import org.slf4j.Logger;

public class PacketValidator extends MessageToMessageCodec<Packet<?>, Packet<?>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final AttributeKey<NetworkState.PacketHandler<?>> receivingSideKey;
	private final AttributeKey<NetworkState.PacketHandler<?>> sendingSideKey;

	public PacketValidator(AttributeKey<NetworkState.PacketHandler<?>> receivingSideKey, AttributeKey<NetworkState.PacketHandler<?>> sendingSideKey) {
		this.receivingSideKey = receivingSideKey;
		this.sendingSideKey = sendingSideKey;
	}

	private static void handle(ChannelHandlerContext context, Packet<?> packet, List<Object> packets, AttributeKey<NetworkState.PacketHandler<?>> key) {
		Attribute<NetworkState.PacketHandler<?>> attribute = context.channel().attr(key);
		NetworkState.PacketHandler<?> packetHandler = attribute.get();
		if (!packetHandler.canHandle(packet)) {
			LOGGER.error("Unrecognized packet in pipeline {}:{} - {}", packetHandler.getState().getId(), packetHandler.getSide(), packet);
		}

		ReferenceCountUtil.retain(packet);
		packets.add(packet);
		NetworkStateTransitionHandler.handle(attribute, packet);
	}

	protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
		handle(channelHandlerContext, packet, list, this.receivingSideKey);
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
		handle(channelHandlerContext, packet, list, this.sendingSideKey);
	}
}

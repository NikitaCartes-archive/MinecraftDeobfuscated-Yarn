package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;

public record XpComponent(int value) implements TooltipAppender {
	public static final XpComponent DEFAULT = new XpComponent(10);
	public static final Codec<XpComponent> CODEC = Codec.INT.xmap(XpComponent::new, XpComponent::value);
	public static final PacketCodec<ByteBuf, XpComponent> PACKET_CODEC = PacketCodecs.INTEGER.xmap(XpComponent::new, XpComponent::value);

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		textConsumer.accept(Text.translatable("item.minecraft.potato_of_knowledge.amount", this.value));
	}
}

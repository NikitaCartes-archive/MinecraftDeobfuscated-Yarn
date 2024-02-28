package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;

public record UnbreakableComponent(boolean showInTooltip) implements TooltipAppender {
	public static final Codec<UnbreakableComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "show_in_tooltip", true).forGetter(UnbreakableComponent::showInTooltip))
				.apply(instance, UnbreakableComponent::new)
	);
	public static final PacketCodec<ByteBuf, UnbreakableComponent> PACKET_CODEC = PacketCodecs.BOOL
		.xmap(UnbreakableComponent::new, UnbreakableComponent::showInTooltip);
	private static final Text TOOLTIP_TEXT = Text.translatable("item.unbreakable").formatted(Formatting.BLUE);

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		if (this.showInTooltip) {
			textConsumer.accept(TOOLTIP_TEXT);
		}
	}
}

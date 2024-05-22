package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record UnbreakableComponent(boolean showInTooltip) implements TooltipAppender {
	public static final Codec<UnbreakableComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(UnbreakableComponent::showInTooltip))
				.apply(instance, UnbreakableComponent::new)
	);
	public static final PacketCodec<ByteBuf, UnbreakableComponent> PACKET_CODEC = PacketCodecs.BOOL
		.xmap(UnbreakableComponent::new, UnbreakableComponent::showInTooltip);
	private static final Text TOOLTIP_TEXT = Text.translatable("item.unbreakable").formatted(Formatting.BLUE);

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		if (this.showInTooltip) {
			tooltip.accept(TOOLTIP_TEXT);
		}
	}

	public UnbreakableComponent withShowInTooltip(boolean showInTooltip) {
		return new UnbreakableComponent(showInTooltip);
	}
}

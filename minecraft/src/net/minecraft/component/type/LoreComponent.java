package net.minecraft.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public record LoreComponent(List<Text> lines, List<Text> styledLines) implements TooltipAppender {
	public static final LoreComponent DEFAULT = new LoreComponent(List.of());
	public static final int MAX_LORES = 256;
	private static final Style STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
	public static final Codec<LoreComponent> CODEC = TextCodecs.STRINGIFIED_CODEC.sizeLimitedListOf(256).xmap(LoreComponent::new, LoreComponent::lines);
	public static final PacketCodec<RegistryByteBuf, LoreComponent> PACKET_CODEC = TextCodecs.REGISTRY_PACKET_CODEC
		.collect(PacketCodecs.toList(256))
		.xmap(LoreComponent::new, LoreComponent::lines);

	public LoreComponent(List<Text> lines) {
		this(lines, Lists.transform(lines, style -> Texts.setStyleIfAbsent(style.copy(), STYLE)));
	}

	public LoreComponent(List<Text> lines, List<Text> styledLines) {
		if (lines.size() > 256) {
			throw new IllegalArgumentException("Got " + lines.size() + " lines, but maximum is 256");
		} else {
			this.lines = lines;
			this.styledLines = styledLines;
		}
	}

	public LoreComponent with(Text line) {
		return new LoreComponent(Util.withAppended(this.lines, line));
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		this.styledLines.forEach(tooltip);
	}
}

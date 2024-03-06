package net.minecraft.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public record LoreComponent(List<Text> lines, List<Text> styledLines) implements TooltipAppender {
	public static final LoreComponent DEFAULT = new LoreComponent(List.of());
	private static final int MAX_LORES = 256;
	private static final Style STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
	public static final Codec<LoreComponent> CODEC = Codecs.list(TextCodecs.STRINGIFIED_CODEC.listOf(), 256).xmap(LoreComponent::new, LoreComponent::lines);
	public static final PacketCodec<RegistryByteBuf, LoreComponent> PACKET_CODEC = TextCodecs.REGISTRY_PACKET_CODEC
		.collect(PacketCodecs.toList(256))
		.xmap(LoreComponent::new, LoreComponent::lines);

	public LoreComponent(List<Text> lines) {
		this(lines, Lists.transform(lines, style -> Texts.setStyleIfAbsent(style.copy(), STYLE)));
	}

	public LoreComponent of(Text line) {
		return new LoreComponent(Util.listWith(this.lines, line));
	}

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		this.styledLines.forEach(textConsumer);
	}
}

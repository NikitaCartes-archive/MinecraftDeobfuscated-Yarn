package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.slf4j.Logger;

public record BannerPatternsComponent(List<BannerPatternsComponent.Layer> layers) {
	static final Logger LOGGER = LogUtils.getLogger();
	public static final BannerPatternsComponent DEFAULT = new BannerPatternsComponent(List.of());
	public static final Codec<BannerPatternsComponent> CODEC = BannerPatternsComponent.Layer.CODEC
		.listOf()
		.xmap(BannerPatternsComponent::new, BannerPatternsComponent::layers);
	public static final PacketCodec<RegistryByteBuf, BannerPatternsComponent> PACKET_CODEC = BannerPatternsComponent.Layer.PACKET_CODEC
		.collect(PacketCodecs.toList())
		.xmap(BannerPatternsComponent::new, BannerPatternsComponent::layers);

	public BannerPatternsComponent withoutTopLayer() {
		return new BannerPatternsComponent(List.copyOf(this.layers.subList(0, this.layers.size() - 1)));
	}

	public static class Builder {
		private final ImmutableList.Builder<BannerPatternsComponent.Layer> entries = ImmutableList.builder();

		@Deprecated
		public BannerPatternsComponent.Builder add(RegistryEntryLookup<BannerPattern> patternLookup, RegistryKey<BannerPattern> pattern, DyeColor color) {
			Optional<RegistryEntry.Reference<BannerPattern>> optional = patternLookup.getOptional(pattern);
			if (optional.isEmpty()) {
				BannerPatternsComponent.LOGGER.warn("Unable to find banner pattern with id: '{}'", pattern.getValue());
				return this;
			} else {
				return this.add((RegistryEntry<BannerPattern>)optional.get(), color);
			}
		}

		public BannerPatternsComponent.Builder add(RegistryEntry<BannerPattern> pattern, DyeColor color) {
			return this.add(new BannerPatternsComponent.Layer(pattern, color));
		}

		public BannerPatternsComponent.Builder add(BannerPatternsComponent.Layer layer) {
			this.entries.add(layer);
			return this;
		}

		public BannerPatternsComponent.Builder addAll(BannerPatternsComponent patterns) {
			this.entries.addAll(patterns.layers);
			return this;
		}

		public BannerPatternsComponent build() {
			return new BannerPatternsComponent(this.entries.build());
		}
	}

	public static record Layer(RegistryEntry<BannerPattern> pattern, DyeColor color) {
		public static final Codec<BannerPatternsComponent.Layer> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						BannerPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(BannerPatternsComponent.Layer::pattern),
						DyeColor.CODEC.fieldOf("color").forGetter(BannerPatternsComponent.Layer::color)
					)
					.apply(instance, BannerPatternsComponent.Layer::new)
		);
		public static final PacketCodec<RegistryByteBuf, BannerPatternsComponent.Layer> PACKET_CODEC = PacketCodec.tuple(
			BannerPattern.ENTRY_PACKET_CODEC,
			BannerPatternsComponent.Layer::pattern,
			DyeColor.PACKET_CODEC,
			BannerPatternsComponent.Layer::color,
			BannerPatternsComponent.Layer::new
		);

		public MutableText getTooltipText() {
			String string = this.pattern.value().translationKey();
			return Text.translatable(string + "." + this.color.getName());
		}
	}
}

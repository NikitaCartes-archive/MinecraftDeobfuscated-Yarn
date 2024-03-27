package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;

public record ToolComponent(List<ToolComponent.Rule> rules, float defaultMiningSpeed, int damagePerBlock) {
	public static final Codec<ToolComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ToolComponent.Rule.CODEC.listOf().fieldOf("rules").forGetter(ToolComponent::rules),
					Codec.FLOAT.optionalFieldOf("default_mining_speed", Float.valueOf(1.0F)).forGetter(ToolComponent::defaultMiningSpeed),
					Codecs.NONNEGATIVE_INT.optionalFieldOf("damage_per_block", 1).forGetter(ToolComponent::damagePerBlock)
				)
				.apply(instance, ToolComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, ToolComponent> PACKET_CODEC = PacketCodec.tuple(
		ToolComponent.Rule.PACKET_CODEC.collect(PacketCodecs.toList()),
		ToolComponent::rules,
		PacketCodecs.FLOAT,
		ToolComponent::defaultMiningSpeed,
		PacketCodecs.VAR_INT,
		ToolComponent::damagePerBlock,
		ToolComponent::new
	);

	public float getSpeed(BlockState state) {
		for (ToolComponent.Rule rule : this.rules) {
			if (rule.speed.isPresent() && state.isIn(rule.blocks)) {
				return (Float)rule.speed.get();
			}
		}

		return this.defaultMiningSpeed;
	}

	public boolean isCorrectForDrops(BlockState state) {
		for (ToolComponent.Rule rule : this.rules) {
			if (rule.correctForDrops.isPresent() && state.isIn(rule.blocks)) {
				return (Boolean)rule.correctForDrops.get();
			}
		}

		return false;
	}

	public static record Rule(RegistryEntryList<Block> blocks, Optional<Float> speed, Optional<Boolean> correctForDrops) {
		public static final Codec<ToolComponent.Rule> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("blocks").forGetter(ToolComponent.Rule::blocks),
						Codecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(ToolComponent.Rule::speed),
						Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(ToolComponent.Rule::correctForDrops)
					)
					.apply(instance, ToolComponent.Rule::new)
		);
		public static final PacketCodec<RegistryByteBuf, ToolComponent.Rule> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.registryEntryList(RegistryKeys.BLOCK),
			ToolComponent.Rule::blocks,
			PacketCodecs.FLOAT.collect(PacketCodecs::optional),
			ToolComponent.Rule::speed,
			PacketCodecs.BOOL.collect(PacketCodecs::optional),
			ToolComponent.Rule::correctForDrops,
			ToolComponent.Rule::new
		);

		public static ToolComponent.Rule ofAlwaysDropping(List<Block> blocks, float speed) {
			return of(blocks, Optional.of(speed), Optional.of(true));
		}

		public static ToolComponent.Rule ofAlwaysDropping(TagKey<Block> blocks, float speed) {
			return of(blocks, Optional.of(speed), Optional.of(true));
		}

		public static ToolComponent.Rule ofNeverDropping(TagKey<Block> tag) {
			return of(tag, Optional.empty(), Optional.of(false));
		}

		public static ToolComponent.Rule of(TagKey<Block> tag, float speed) {
			return of(tag, Optional.of(speed), Optional.empty());
		}

		public static ToolComponent.Rule of(List<Block> blocks, float speed) {
			return of(blocks, Optional.of(speed), Optional.empty());
		}

		private static ToolComponent.Rule of(TagKey<Block> tag, Optional<Float> speed, Optional<Boolean> correctForDrops) {
			return new ToolComponent.Rule(Registries.BLOCK.getOrCreateEntryList(tag), speed, correctForDrops);
		}

		private static ToolComponent.Rule of(List<Block> blocks, Optional<Float> speed, Optional<Boolean> correctForDrops) {
			return new ToolComponent.Rule(
				RegistryEntryList.of((List<? extends RegistryEntry<Block>>)blocks.stream().map(Block::getRegistryEntry).collect(Collectors.toList())),
				speed,
				correctForDrops
			);
		}
	}
}

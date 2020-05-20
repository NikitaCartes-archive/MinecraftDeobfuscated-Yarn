package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.util.StringIdentifiable;

public class OreFeatureConfig implements FeatureConfig {
	public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					OreFeatureConfig.Target.field_24898.fieldOf("target").forGetter(oreFeatureConfig -> oreFeatureConfig.target),
					BlockState.field_24734.fieldOf("state").forGetter(oreFeatureConfig -> oreFeatureConfig.state),
					Codec.INT.fieldOf("size").withDefault(0).forGetter(oreFeatureConfig -> oreFeatureConfig.size)
				)
				.apply(instance, OreFeatureConfig::new)
	);
	public final OreFeatureConfig.Target target;
	public final int size;
	public final BlockState state;

	public OreFeatureConfig(OreFeatureConfig.Target target, BlockState state, int size) {
		this.size = size;
		this.state = state;
		this.target = target;
	}

	public static enum Target implements StringIdentifiable {
		NATURAL_STONE(
			"natural_stone",
			blockState -> blockState == null
					? false
					: blockState.isOf(Blocks.STONE) || blockState.isOf(Blocks.GRANITE) || blockState.isOf(Blocks.DIORITE) || blockState.isOf(Blocks.ANDESITE)
		),
		NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK)),
		NETHER_ORE_REPLACEABLES(
			"nether_ore_replaceables",
			blockState -> blockState == null ? false : blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.BASALT) || blockState.isOf(Blocks.BLACKSTONE)
		);

		public static final Codec<OreFeatureConfig.Target> field_24898 = StringIdentifiable.method_28140(
			OreFeatureConfig.Target::values, OreFeatureConfig.Target::byName
		);
		private static final Map<String, OreFeatureConfig.Target> nameMap = (Map<String, OreFeatureConfig.Target>)Arrays.stream(values())
			.collect(Collectors.toMap(OreFeatureConfig.Target::getName, target -> target));
		private final String name;
		private final Predicate<BlockState> predicate;

		private Target(String name, Predicate<BlockState> predicate) {
			this.name = name;
			this.predicate = predicate;
		}

		public String getName() {
			return this.name;
		}

		public static OreFeatureConfig.Target byName(String name) {
			return (OreFeatureConfig.Target)nameMap.get(name);
		}

		public Predicate<BlockState> getCondition() {
			return this.predicate;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}

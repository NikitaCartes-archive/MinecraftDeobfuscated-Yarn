package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;

public class OreFeatureConfig implements FeatureConfig {
	public final OreFeatureConfig.Target target;
	public final int size;
	public final BlockState state;

	public OreFeatureConfig(OreFeatureConfig.Target target, BlockState blockState, int i) {
		this.size = i;
		this.state = blockState;
		this.target = target;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("size"),
					dynamicOps.createInt(this.size),
					dynamicOps.createString("target"),
					dynamicOps.createString(this.target.getName()),
					dynamicOps.createString("state"),
					BlockState.serialize(dynamicOps, this.state).getValue()
				)
			)
		);
	}

	public static OreFeatureConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("size").asInt(0);
		OreFeatureConfig.Target target = OreFeatureConfig.Target.byName(dynamic.get("target").asString(""));
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.field_10124.method_9564());
		return new OreFeatureConfig(target, blockState, i);
	}

	public static enum Target {
		NATURAL_STONE("natural_stone", blockState -> {
			if (blockState == null) {
				return false;
			} else {
				Block block = blockState.getBlock();
				return block == Blocks.field_10340 || block == Blocks.field_10474 || block == Blocks.field_10508 || block == Blocks.field_10115;
			}
		}),
		NETHERRACK("netherrack", new BlockPredicate(Blocks.field_10515));

		private static final Map<String, OreFeatureConfig.Target> nameMap = (Map<String, OreFeatureConfig.Target>)Arrays.stream(values())
			.collect(Collectors.toMap(OreFeatureConfig.Target::getName, target -> target));
		private final String name;
		private final Predicate<BlockState> predicate;

		private Target(String string2, Predicate<BlockState> predicate) {
			this.name = string2;
			this.predicate = predicate;
		}

		public String getName() {
			return this.name;
		}

		public static OreFeatureConfig.Target byName(String string) {
			return (OreFeatureConfig.Target)nameMap.get(string);
		}

		public Predicate<BlockState> getCondition() {
			return this.predicate;
		}
	}
}

package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class OreFeatureConfig implements FeatureConfig {
	public final OreFeatureConfig.Target target;
	public final int size;
	public final BlockState state;

	public OreFeatureConfig(OreFeatureConfig.Target target, BlockState state, int size) {
		this.size = size;
		this.state = state;
		this.target = target;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("size"),
					ops.createInt(this.size),
					ops.createString("target"),
					ops.createString(this.target.getName()),
					ops.createString("state"),
					BlockState.serialize(ops, this.state).getValue()
				)
			)
		);
	}

	public static OreFeatureConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("size").asInt(0);
		OreFeatureConfig.Target target = OreFeatureConfig.Target.byName(dynamic.get("target").asString(""));
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		return new OreFeatureConfig(target, blockState, i);
	}

	public static OreFeatureConfig method_26621(Random random) {
		return new OreFeatureConfig(Util.method_26715(OreFeatureConfig.Target.class, random), Registry.BLOCK.getRandom(random).getDefaultState(), random.nextInt(15));
	}

	public static enum Target {
		NATURAL_STONE("natural_stone", blockState -> {
			if (blockState == null) {
				return false;
			} else {
				Block block = blockState.getBlock();
				return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
			}
		}),
		NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK)),
		ANY("any", blockState -> true);

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
	}
}

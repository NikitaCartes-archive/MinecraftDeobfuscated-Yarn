package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpringFeatureConfig implements FeatureConfig {
	public final FluidState state;
	public final boolean requiresBlockBelow;
	public final int rockCount;
	public final int holeCount;
	public final Set<Block> validBlocks;

	public SpringFeatureConfig(FluidState state, boolean requiresBlockBelow, int rockCount, int holeCount, Set<Block> validBlocks) {
		this.state = state;
		this.requiresBlockBelow = requiresBlockBelow;
		this.rockCount = rockCount;
		this.holeCount = holeCount;
		this.validBlocks = validBlocks;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("state"),
					FluidState.serialize(ops, this.state).getValue(),
					ops.createString("requires_block_below"),
					ops.createBoolean(this.requiresBlockBelow),
					ops.createString("rock_count"),
					ops.createInt(this.rockCount),
					ops.createString("hole_count"),
					ops.createInt(this.holeCount),
					ops.createString("valid_blocks"),
					ops.createList(this.validBlocks.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(ops::createString))
				)
			)
		);
	}

	public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic) {
		return new SpringFeatureConfig(
			(FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()),
			dynamic.get("requires_block_below").asBoolean(true),
			dynamic.get("rock_count").asInt(4),
			dynamic.get("hole_count").asInt(1),
			ImmutableSet.copyOf(dynamic.get("valid_blocks").asList(dynamicx -> Registry.BLOCK.get(new Identifier(dynamicx.asString("minecraft:air")))))
		);
	}
}

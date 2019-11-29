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
	public final FluidState field_21283;
	public final boolean field_21284;
	public final int field_21285;
	public final int field_21286;
	public final Set<Block> field_21287;

	public SpringFeatureConfig(FluidState fluidState, boolean bl, int i, int j, Set<Block> set) {
		this.field_21283 = fluidState;
		this.field_21284 = bl;
		this.field_21285 = i;
		this.field_21286 = j;
		this.field_21287 = set;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("state"),
					FluidState.serialize(ops, this.field_21283).getValue(),
					ops.createString("requires_block_below"),
					ops.createBoolean(this.field_21284),
					ops.createString("rock_count"),
					ops.createInt(this.field_21285),
					ops.createString("hole_count"),
					ops.createInt(this.field_21286),
					ops.createString("valid_blocks"),
					ops.createList(this.field_21287.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(ops::createString))
				)
			)
		);
	}

	public static <T> SpringFeatureConfig method_23440(Dynamic<T> dynamic) {
		return new SpringFeatureConfig(
			(FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()),
			dynamic.get("requires_block_below").asBoolean(true),
			dynamic.get("rock_count").asInt(4),
			dynamic.get("hole_count").asInt(1),
			ImmutableSet.copyOf(dynamic.get("valid_blocks").asList(dynamicx -> Registry.BLOCK.get(new Identifier(dynamicx.asString("minecraft:air")))))
		);
	}
}

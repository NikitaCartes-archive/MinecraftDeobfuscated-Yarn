package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class TernarySurfaceConfig implements SurfaceConfig {
	private final BlockState topMaterial;
	private final BlockState underMaterial;
	private final BlockState underwaterMaterial;

	public TernarySurfaceConfig(BlockState topMaterial, BlockState underMaterial, BlockState underwaterMaterial) {
		this.topMaterial = topMaterial;
		this.underMaterial = underMaterial;
		this.underwaterMaterial = underwaterMaterial;
	}

	@Override
	public BlockState getTopMaterial() {
		return this.topMaterial;
	}

	@Override
	public BlockState getUnderMaterial() {
		return this.underMaterial;
	}

	public BlockState getUnderwaterMaterial() {
		return this.underwaterMaterial;
	}

	@Override
	public <T> Dynamic<T> method_26681(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("top_material"),
					BlockState.serialize(dynamicOps, this.topMaterial).getValue(),
					dynamicOps.createString("under_material"),
					BlockState.serialize(dynamicOps, this.underMaterial).getValue(),
					dynamicOps.createString("underwater_material"),
					BlockState.serialize(dynamicOps, this.underwaterMaterial).getValue()
				)
			)
		);
	}

	public static TernarySurfaceConfig deserialize(Dynamic<?> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState3 = (BlockState)dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		return new TernarySurfaceConfig(blockState, blockState2, blockState3);
	}

	public static TernarySurfaceConfig method_26680(Random random) {
		BlockState blockState = Util.method_26719(random, OverworldChunkGeneratorConfig.field_23567);
		BlockState blockState2 = Util.method_26719(random, OverworldChunkGeneratorConfig.field_23567);
		BlockState blockState3 = Util.method_26719(random, OverworldChunkGeneratorConfig.field_23567);
		return new TernarySurfaceConfig(blockState, blockState2, blockState3);
	}
}

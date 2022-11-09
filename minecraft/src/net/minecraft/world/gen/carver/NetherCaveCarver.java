package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherCaveCarver extends CaveCarver {
	public NetherCaveCarver(Codec<CaveCarverConfig> codec) {
		super(codec);
		this.carvableFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
	}

	@Override
	protected int getMaxCaveCount() {
		return 10;
	}

	@Override
	protected float getTunnelSystemWidth(Random random) {
		return (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
	}

	@Override
	protected double getTunnelSystemHeightWidthRatio() {
		return 5.0;
	}

	protected boolean carveAtPoint(
		CarverContext carverContext,
		CaveCarverConfig caveCarverConfig,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> function,
		CarvingMask carvingMask,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		AquiferSampler aquiferSampler,
		MutableBoolean mutableBoolean
	) {
		if (this.canAlwaysCarveBlock(caveCarverConfig, chunk.getBlockState(mutable))) {
			BlockState blockState;
			if (mutable.getY() <= carverContext.getMinY() + 31) {
				blockState = LAVA.getBlockState();
			} else {
				blockState = CAVE_AIR;
			}

			chunk.setBlockState(mutable, blockState, false);
			return true;
		} else {
			return false;
		}
	}
}

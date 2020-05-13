package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IcePatchFeature extends Feature<IcePatchFeatureConfig> {
	private final Block ICE = Blocks.PACKED_ICE;

	public IcePatchFeature(Function<Dynamic<?>, ? extends IcePatchFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		IcePatchFeatureConfig icePatchFeatureConfig
	) {
		while (serverWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!serverWorldAccess.getBlockState(blockPos).isOf(Blocks.SNOW_BLOCK)) {
			return false;
		} else {
			int i = random.nextInt(icePatchFeatureConfig.radius) + 2;
			int j = 1;

			for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; k++) {
				for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= i * i) {
						for (int o = blockPos.getY() - 1; o <= blockPos.getY() + 1; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = serverWorldAccess.getBlockState(blockPos2).getBlock();
							if (isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
								serverWorldAccess.setBlockState(blockPos2, this.ICE.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}

package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.loot.LootTables;

public class BonusChestFeature extends Feature<DefaultFeatureConfig> {
	public BonusChestFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12817(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockState blockState = iWorld.method_8320(blockPos);
			(blockState.isAir() || blockState.method_11602(BlockTags.field_15503)) && blockPos.getY() > 1;
			blockState = iWorld.method_8320(blockPos)
		) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() < 1) {
			return false;
		} else {
			blockPos = blockPos.up();

			for (int i = 0; i < 4; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				BlockPos blockPos3 = blockPos2.down();
				if (iWorld.method_8623(blockPos2) && iWorld.method_8320(blockPos3).method_11631(iWorld, blockPos3)) {
					iWorld.method_8652(blockPos2, Blocks.field_10034.method_9564(), 2);
					LootableContainerBlockEntity.method_11287(iWorld, random, blockPos2, LootTables.field_850);
					BlockPos blockPos4 = blockPos2.east();
					BlockPos blockPos5 = blockPos2.west();
					BlockPos blockPos6 = blockPos2.north();
					BlockPos blockPos7 = blockPos2.south();
					BlockPos blockPos8 = blockPos5.down();
					if (iWorld.method_8623(blockPos5) && iWorld.method_8320(blockPos8).method_11631(iWorld, blockPos8)) {
						iWorld.method_8652(blockPos5, Blocks.field_10336.method_9564(), 2);
					}

					BlockPos blockPos9 = blockPos4.down();
					if (iWorld.method_8623(blockPos4) && iWorld.method_8320(blockPos9).method_11631(iWorld, blockPos9)) {
						iWorld.method_8652(blockPos4, Blocks.field_10336.method_9564(), 2);
					}

					BlockPos blockPos10 = blockPos6.down();
					if (iWorld.method_8623(blockPos6) && iWorld.method_8320(blockPos10).method_11631(iWorld, blockPos10)) {
						iWorld.method_8652(blockPos6, Blocks.field_10336.method_9564(), 2);
					}

					BlockPos blockPos11 = blockPos7.down();
					if (iWorld.method_8623(blockPos7) && iWorld.method_8320(blockPos11).method_11631(iWorld, blockPos11)) {
						iWorld.method_8652(blockPos7, Blocks.field_10336.method_9564(), 2);
					}

					return true;
				}
			}

			return false;
		}
	}
}

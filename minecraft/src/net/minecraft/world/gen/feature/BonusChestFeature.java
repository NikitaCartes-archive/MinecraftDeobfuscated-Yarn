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
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.matches(BlockTags.field_15503)) && blockPos.getY() > 1;
			blockState = iWorld.getBlockState(blockPos)
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
				if (iWorld.isAir(blockPos2) && iWorld.getBlockState(blockPos3).hasSolidTopSurface(iWorld, blockPos3)) {
					iWorld.setBlockState(blockPos2, Blocks.field_10034.getDefaultState(), 2);
					LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos2, LootTables.CHEST_SPAWN_BONUS);
					BlockPos blockPos4 = blockPos2.east();
					BlockPos blockPos5 = blockPos2.west();
					BlockPos blockPos6 = blockPos2.north();
					BlockPos blockPos7 = blockPos2.south();
					BlockPos blockPos8 = blockPos5.down();
					if (iWorld.isAir(blockPos5) && iWorld.getBlockState(blockPos8).hasSolidTopSurface(iWorld, blockPos8)) {
						iWorld.setBlockState(blockPos5, Blocks.field_10336.getDefaultState(), 2);
					}

					BlockPos blockPos9 = blockPos4.down();
					if (iWorld.isAir(blockPos4) && iWorld.getBlockState(blockPos9).hasSolidTopSurface(iWorld, blockPos9)) {
						iWorld.setBlockState(blockPos4, Blocks.field_10336.getDefaultState(), 2);
					}

					BlockPos blockPos10 = blockPos6.down();
					if (iWorld.isAir(blockPos6) && iWorld.getBlockState(blockPos10).hasSolidTopSurface(iWorld, blockPos10)) {
						iWorld.setBlockState(blockPos6, Blocks.field_10336.getDefaultState(), 2);
					}

					BlockPos blockPos11 = blockPos7.down();
					if (iWorld.isAir(blockPos7) && iWorld.getBlockState(blockPos11).hasSolidTopSurface(iWorld, blockPos11)) {
						iWorld.setBlockState(blockPos7, Blocks.field_10336.getDefaultState(), 2);
					}

					return true;
				}
			}

			return false;
		}
	}
}

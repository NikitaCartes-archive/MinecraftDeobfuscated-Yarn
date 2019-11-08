package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BonusChestFeature extends Feature<DefaultFeatureConfig> {
	public BonusChestFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	public boolean method_12817(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.matches(BlockTags.LEAVES)) && blockPos.getY() > 1;
			blockState = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.method_10074();
		}

		if (blockPos.getY() < 1) {
			return false;
		} else {
			blockPos = blockPos.up();

			for (int i = 0; i < 4; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				if (iWorld.isAir(blockPos2)) {
					iWorld.setBlockState(blockPos2, Blocks.CHEST.getDefaultState(), 2);
					LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos2, LootTables.SPAWN_BONUS_CHEST);
					BlockState blockState2 = Blocks.TORCH.getDefaultState();

					for (Direction direction : Direction.Type.HORIZONTAL) {
						BlockPos blockPos3 = blockPos2.offset(direction);
						if (blockState2.canPlaceAt(iWorld, blockPos3)) {
							iWorld.setBlockState(blockPos3, blockState2, 2);
						}
					}

					return true;
				}
			}

			return false;
		}
	}
}

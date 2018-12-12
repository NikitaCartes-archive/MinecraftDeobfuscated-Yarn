package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;

public class EndGatewayFeature extends Feature<EndGatewayFeatureConfig> {
	public EndGatewayFeature(Function<Dynamic<?>, ? extends EndGatewayFeatureConfig> function) {
		super(function);
	}

	public boolean method_13142(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		EndGatewayFeatureConfig endGatewayFeatureConfig
	) {
		for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
			boolean bl = mutable.getX() == blockPos.getX();
			boolean bl2 = mutable.getY() == blockPos.getY();
			boolean bl3 = mutable.getZ() == blockPos.getZ();
			boolean bl4 = Math.abs(mutable.getY() - blockPos.getY()) == 2;
			if (bl && bl2 && bl3) {
				BlockPos blockPos2 = mutable.toImmutable();
				this.setBlockState(iWorld, blockPos2, Blocks.field_10613.getDefaultState());
				if (endGatewayFeatureConfig.exitsAtSpawn()) {
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
					if (blockEntity instanceof EndGatewayBlockEntity) {
						EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
						endGatewayBlockEntity.method_11418(TheEndDimension.field_13103);
					}
				}
			} else if (bl2) {
				this.setBlockState(iWorld, mutable, Blocks.field_10124.getDefaultState());
			} else if (bl4 && bl && bl3) {
				this.setBlockState(iWorld, mutable, Blocks.field_9987.getDefaultState());
			} else if ((bl || bl3) && !bl4) {
				this.setBlockState(iWorld, mutable, Blocks.field_9987.getDefaultState());
			} else {
				this.setBlockState(iWorld, mutable, Blocks.field_10124.getDefaultState());
			}
		}

		BlockEntity blockEntity2 = iWorld.getBlockEntity(blockPos);
		if (blockEntity2 instanceof EndGatewayBlockEntity) {
			EndGatewayBlockEntity endGatewayBlockEntity2 = (EndGatewayBlockEntity)blockEntity2;
			endGatewayBlockEntity2.method_11418(((FloatingIslandsChunkGenerator)chunkGenerator).getCenter());
		}

		return true;
	}
}

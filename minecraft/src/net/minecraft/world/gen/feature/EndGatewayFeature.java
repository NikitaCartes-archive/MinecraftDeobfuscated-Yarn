package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EndGatewayFeature extends Feature<EndGatewayFeatureConfig> {
	public EndGatewayFeature(Codec<EndGatewayFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_13142(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, EndGatewayFeatureConfig endGatewayFeatureConfig
	) {
		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
			boolean bl = blockPos2.getX() == blockPos.getX();
			boolean bl2 = blockPos2.getY() == blockPos.getY();
			boolean bl3 = blockPos2.getZ() == blockPos.getZ();
			boolean bl4 = Math.abs(blockPos2.getY() - blockPos.getY()) == 2;
			if (bl && bl2 && bl3) {
				BlockPos blockPos3 = blockPos2.toImmutable();
				this.setBlockState(structureWorldAccess, blockPos3, Blocks.field_10613.getDefaultState());
				endGatewayFeatureConfig.getExitPos().ifPresent(blockPos2x -> {
					BlockEntity blockEntity = structureWorldAccess.getBlockEntity(blockPos3);
					if (blockEntity instanceof EndGatewayBlockEntity) {
						EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
						endGatewayBlockEntity.setExitPortalPos(blockPos2x, endGatewayFeatureConfig.isExact());
						blockEntity.markDirty();
					}
				});
			} else if (bl2) {
				this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_10124.getDefaultState());
			} else if (bl4 && bl && bl3) {
				this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_9987.getDefaultState());
			} else if ((bl || bl3) && !bl4) {
				this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_9987.getDefaultState());
			} else {
				this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_10124.getDefaultState());
			}
		}

		return true;
	}
}

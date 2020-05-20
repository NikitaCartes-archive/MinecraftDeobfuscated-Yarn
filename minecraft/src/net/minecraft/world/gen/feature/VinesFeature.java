package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class VinesFeature extends Feature<DefaultFeatureConfig> {
	private static final Direction[] DIRECTIONS = Direction.values();

	public VinesFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int i = blockPos.getY(); i < 256; i++) {
			mutable.set(blockPos);
			mutable.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			mutable.setY(i);
			if (serverWorldAccess.isAir(mutable)) {
				for (Direction direction : DIRECTIONS) {
					if (direction != Direction.DOWN && VineBlock.shouldConnectTo(serverWorldAccess, mutable, direction)) {
						serverWorldAccess.setBlockState(mutable, Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), Boolean.valueOf(true)), 2);
						break;
					}
				}
			}
		}

		return true;
	}
}

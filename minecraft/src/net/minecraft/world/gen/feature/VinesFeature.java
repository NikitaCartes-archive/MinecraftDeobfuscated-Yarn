package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class VinesFeature extends Feature<DefaultFeatureConfig> {
	private static final Direction[] DIRECTIONS = Direction.values();

	public VinesFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 64; i < 384; i++) {
			mutable.set(blockPos);
			mutable.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			mutable.setY(i);
			if (structureWorldAccess.isAir(mutable)) {
				for (Direction direction : DIRECTIONS) {
					if (direction != Direction.DOWN) {
						mutable2.set(mutable, direction);
						if (VineBlock.shouldConnectTo(structureWorldAccess, mutable2, direction)) {
							structureWorldAccess.setBlockState(mutable, Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), Boolean.valueOf(true)), 2);
							break;
						}
					}
				}
			}
		}

		return true;
	}
}

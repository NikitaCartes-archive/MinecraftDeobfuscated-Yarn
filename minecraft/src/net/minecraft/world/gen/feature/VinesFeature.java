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
	public boolean generate(FeatureContext<DefaultFeatureConfig> featureContext) {
		Random random = featureContext.getRandom();
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockPos blockPos = featureContext.getPos();
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int i = 64; i < 256; i++) {
			mutable.set(blockPos);
			mutable.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			mutable.setY(i);
			if (structureWorldAccess.isAir(mutable)) {
				for (Direction direction : DIRECTIONS) {
					if (direction != Direction.DOWN && VineBlock.shouldConnectTo(structureWorldAccess, mutable, direction)) {
						structureWorldAccess.setBlockState(mutable, Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), Boolean.valueOf(true)), 2);
						break;
					}
				}
			}
		}

		return true;
	}
}

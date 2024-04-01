package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ParkLaneFeature extends Feature<DefaultFeatureConfig> {
	public ParkLaneFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		if (structureWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, blockPos).getY() <= blockPos.getY() + 2) {
			return false;
		} else if (!this.method_59243(structureWorldAccess, blockPos)) {
			return false;
		} else {
			Direction direction = Direction.fromHorizontal(random.nextInt(4));
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			structureWorldAccess.setBlockState(blockPos.down(), Blocks.POISON_PATH.getDefaultState(), Block.NOTIFY_LISTENERS);
			int i = random.nextBetweenExclusive(6, 12);
			List<BlockPos> list = new ArrayList();

			for (int j = 0; j < 2; j++) {
				mutable.set(blockPos);
				float f = 1.0F;
				int k = 0;
				direction = direction.getOpposite();

				while (random.nextFloat() < f) {
					if (++k > 2) {
						break;
					}

					mutable.move(direction);
					if (!this.method_59243(structureWorldAccess, mutable)) {
						break;
					}

					f *= 0.8F;
					Function<BlockPos, BlockState> function = blockPosx -> {
						list.add(blockPosx.toImmutable());
						return Blocks.AIR.getDefaultState();
					};
					Function<BlockPos, BlockState> function2 = blockPosx -> Blocks.POISON_PATH.getDefaultState();
					this.method_59239(structureWorldAccess, random, mutable, direction.rotateYClockwise(), function2, function, i);
					Direction direction2 = direction.rotateYCounterclockwise();
					this.method_59239(structureWorldAccess, random, mutable.offset(direction2), direction2, function2, function, i);
				}

				if (random.nextInt(2) == 0) {
					mutable.move(direction);
					if (this.method_59243(structureWorldAccess, mutable)) {
						Function<BlockPos, BlockState> function = blockPosx -> {
							list.add(blockPosx.toImmutable());
							if (random.nextInt(10) == 0) {
								structureWorldAccess.setBlockState(blockPosx.up(), Blocks.LANTERN.getDefaultState(), Block.NOTIFY_ALL);
							}

							return Blocks.POTATO_FENCE.getDefaultState();
						};
						this.method_59239(structureWorldAccess, random, mutable, direction.rotateYClockwise(), blockPosx -> Blocks.PEELGRASS_BLOCK.getDefaultState(), function, i);
						Direction direction3 = direction.rotateYCounterclockwise();
						this.method_59239(
							structureWorldAccess, random, mutable.offset(direction3), direction3, blockPosx -> Blocks.PEELGRASS_BLOCK.getDefaultState(), function, i
						);
					}
				}
			}

			for (BlockPos blockPos2 : list) {
				structureWorldAccess.getChunk(blockPos2).markBlockForPostProcessing(blockPos2);
			}

			Direction direction4 = direction.rotateYCounterclockwise();
			this.method_59239(
				structureWorldAccess,
				random,
				blockPos.offset(direction4),
				direction4,
				blockPosx -> Blocks.POISON_PATH.getDefaultState(),
				blockPosx -> Blocks.AIR.getDefaultState(),
				i
			);
			direction4 = direction4.getOpposite();
			this.method_59239(
				structureWorldAccess,
				random,
				blockPos.offset(direction4),
				direction4,
				blockPosx -> Blocks.POISON_PATH.getDefaultState(),
				blockPosx -> Blocks.AIR.getDefaultState(),
				i
			);
			return false;
		}
	}

	private void method_59239(
		StructureWorldAccess structureWorldAccess,
		Random random,
		BlockPos blockPos,
		Direction direction,
		Function<BlockPos, BlockState> function,
		Function<BlockPos, BlockState> function2,
		int i
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		int j = i + random.nextInt(3);

		for (int k = 0; k < j; k++) {
			if (!this.method_59243(structureWorldAccess, mutable)) {
				mutable.move(Direction.UP);
				if (!this.method_59243(structureWorldAccess, mutable)) {
					mutable.move(Direction.DOWN, 2);
					if (!this.method_59243(structureWorldAccess, mutable)) {
						break;
					}
				}
			}

			structureWorldAccess.setBlockState(mutable.down(), (BlockState)function.apply(mutable.down()), Block.NOTIFY_ALL);
			structureWorldAccess.setBlockState(mutable, (BlockState)function2.apply(mutable), Block.NOTIFY_ALL);
			mutable.move(direction);
		}
	}

	private boolean method_59243(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return (structureWorldAccess.isAir(blockPos) || structureWorldAccess.getBlockState(blockPos).isOf(Blocks.POTATO_FENCE))
			&& structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.PEELGRASS_BLOCK);
	}
}

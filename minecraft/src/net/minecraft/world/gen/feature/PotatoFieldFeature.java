package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class PotatoFieldFeature extends Feature<DefaultFeatureConfig> {
	public PotatoFieldFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		if (!this.canPlaceAt(structureWorldAccess, blockPos)) {
			return false;
		} else {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (!this.canPlaceAt(structureWorldAccess, blockPos.offset(direction))) {
					return false;
				}
			}

			double d = structureWorldAccess.toServerWorld()
				.getChunkManager()
				.getNoiseConfig()
				.getNoiseRouter()
				.continents()
				.sample(new DensityFunction.UnblendedNoisePos(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
			Direction direction2 = Direction.fromHorizontal((int)((d + 1.0) * 5.0));
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			if (random.nextInt(8) == 0) {
				structureWorldAccess.setBlockState(
					blockPos.down(), Blocks.POTATO_FENCE.getDefaultState().with(Properties.WATERLOGGED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS
				);
				structureWorldAccess.setBlockState(blockPos, Blocks.POTATO_FENCE.getDefaultState(), Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.up(), Blocks.LANTERN.getDefaultState(), Block.NOTIFY_LISTENERS);
			} else {
				structureWorldAccess.setBlockState(blockPos.down(), Blocks.WATER.getDefaultState(), Block.NOTIFY_LISTENERS);
			}

			structureWorldAccess.setBlockState(blockPos.down(2), Blocks.TERRE_DE_POMME.getDefaultState(), Block.NOTIFY_LISTENERS);
			BlockBox blockBox = new BlockBox(blockPos.down()).method_59292(4, 1, 4);

			for (int i = 0; i < 2; i++) {
				mutable.set(blockPos);
				float f = 1.0F;
				int j = 0;
				direction2 = direction2.getOpposite();

				while (random.nextFloat() < f) {
					if (++j > 6) {
						break;
					}

					mutable.move(direction2);
					if (!this.canPlaceAt(structureWorldAccess, mutable)) {
						break;
					}

					f *= 0.8F;
					Function<BlockPos, BlockState> function = blockPosx -> Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(random.nextInt(7)));
					Function<BlockPos, BlockState> function2 = blockPosx -> {
						BlockState blockState = Blocks.POISON_FARMLAND.getDefaultState();
						return blockBox.contains(blockPosx) ? blockState.with(FarmlandBlock.MOISTURE, Integer.valueOf(7)) : blockState;
					};
					this.method_59262(structureWorldAccess, random, mutable, direction2.rotateYClockwise(), function2, function);
					Direction direction3 = direction2.rotateYCounterclockwise();
					this.method_59262(structureWorldAccess, random, mutable.offset(direction3), direction3, function2, function);
				}

				if (random.nextInt(10) == 0) {
					mutable.move(direction2);
					if (!this.canPlaceAt(structureWorldAccess, mutable)) {
						break;
					}

					List<BlockPos> list = new ArrayList();
					Function<BlockPos, BlockState> function2 = blockPosx -> {
						list.add(blockPosx.toImmutable());
						return Blocks.POTATO_FENCE.getDefaultState();
					};
					this.method_59262(structureWorldAccess, random, mutable, direction2.rotateYClockwise(), blockPosx -> Blocks.PEELGRASS_BLOCK.getDefaultState(), function2);
					Direction direction3 = direction2.rotateYCounterclockwise();
					this.method_59262(structureWorldAccess, random, mutable.offset(direction3), direction3, blockPosx -> Blocks.PEELGRASS_BLOCK.getDefaultState(), function2);

					for (BlockPos blockPos2 : list) {
						structureWorldAccess.getChunk(blockPos2).markBlockForPostProcessing(blockPos2);
					}
				}
			}

			Direction direction4 = direction2.rotateYCounterclockwise();
			this.method_59262(
				structureWorldAccess,
				random,
				blockPos.offset(direction4),
				direction4,
				blockPosx -> Blocks.POISON_PATH.getDefaultState(),
				blockPosx -> Blocks.AIR.getDefaultState()
			);
			direction4 = direction4.getOpposite();
			this.method_59262(
				structureWorldAccess,
				random,
				blockPos.offset(direction4),
				direction4,
				blockPosx -> Blocks.POISON_PATH.getDefaultState(),
				blockPosx -> Blocks.AIR.getDefaultState()
			);
			return false;
		}
	}

	private void method_59262(
		StructureWorldAccess structureWorldAccess,
		Random random,
		BlockPos blockPos,
		Direction direction,
		Function<BlockPos, BlockState> function,
		Function<BlockPos, BlockState> function2
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		int i = random.nextBetweenExclusive(3, 15);

		for (int j = 0; j < i; j++) {
			if (!this.canPlaceAt(structureWorldAccess, mutable)) {
				mutable.move(Direction.UP);
				if (!this.canPlaceAt(structureWorldAccess, mutable)) {
					mutable.move(Direction.DOWN, 2);
					if (!this.canPlaceAt(structureWorldAccess, mutable)) {
						break;
					}
				}
			}

			structureWorldAccess.setBlockState(mutable.down(), (BlockState)function.apply(mutable.down()), Block.NOTIFY_ALL);
			structureWorldAccess.setBlockState(mutable, (BlockState)function2.apply(mutable), Block.NOTIFY_ALL);
			mutable.move(direction);
		}
	}

	private boolean canPlaceAt(StructureWorldAccess world, BlockPos pos) {
		return world.isAir(pos) && world.getBlockState(pos.down()).isOf(Blocks.PEELGRASS_BLOCK);
	}
}

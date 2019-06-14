package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class BoneMealItem extends Item {
	public BoneMealItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(itemUsageContext.getSide());
		if (method_7720(itemUsageContext.getStack(), world, blockPos)) {
			if (!world.isClient) {
				world.playLevelEvent(2005, blockPos, 0);
			}

			return ActionResult.field_5812;
		} else {
			BlockState blockState = world.method_8320(blockPos);
			boolean bl = Block.method_20045(blockState, world, blockPos, itemUsageContext.getSide());
			if (bl && method_7719(itemUsageContext.getStack(), world, blockPos2, itemUsageContext.getSide())) {
				if (!world.isClient) {
					world.playLevelEvent(2005, blockPos2, 0);
				}

				return ActionResult.field_5812;
			} else {
				return ActionResult.field_5811;
			}
		}
	}

	public static boolean method_7720(ItemStack itemStack, World world, BlockPos blockPos) {
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() instanceof Fertilizable) {
			Fertilizable fertilizable = (Fertilizable)blockState.getBlock();
			if (fertilizable.method_9651(world, blockPos, blockState, world.isClient)) {
				if (!world.isClient) {
					if (fertilizable.method_9650(world, world.random, blockPos, blockState)) {
						fertilizable.method_9652(world, world.random, blockPos, blockState);
					}

					itemStack.decrement(1);
				}

				return true;
			}
		}

		return false;
	}

	public static boolean method_7719(ItemStack itemStack, World world, BlockPos blockPos, @Nullable Direction direction) {
		if (world.method_8320(blockPos).getBlock() == Blocks.field_10382 && world.method_8316(blockPos).getLevel() == 8) {
			if (!world.isClient) {
				label79:
				for (int i = 0; i < 128; i++) {
					BlockPos blockPos2 = blockPos;
					Biome biome = world.method_8310(blockPos);
					BlockState blockState = Blocks.field_10376.method_9564();

					for (int j = 0; j < i / 16; j++) {
						blockPos2 = blockPos2.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1);
						biome = world.method_8310(blockPos2);
						if (Block.method_9614(world.method_8320(blockPos2).method_11628(world, blockPos2))) {
							continue label79;
						}
					}

					if (biome == Biomes.field_9408 || biome == Biomes.field_9448) {
						if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
							blockState = BlockTags.field_15476.getRandom(world.random).method_9564().method_11657(DeadCoralWallFanBlock.field_9933, direction);
						} else if (RANDOM.nextInt(4) == 0) {
							blockState = BlockTags.field_15496.getRandom(RANDOM).method_9564();
						}
					}

					if (blockState.getBlock().matches(BlockTags.field_15476)) {
						for (int jx = 0; !blockState.canPlaceAt(world, blockPos2) && jx < 4; jx++) {
							blockState = blockState.method_11657(DeadCoralWallFanBlock.field_9933, Direction.Type.field_11062.random(RANDOM));
						}
					}

					if (blockState.canPlaceAt(world, blockPos2)) {
						BlockState blockState2 = world.method_8320(blockPos2);
						if (blockState2.getBlock() == Blocks.field_10382 && world.method_8316(blockPos2).getLevel() == 8) {
							world.method_8652(blockPos2, blockState, 3);
						} else if (blockState2.getBlock() == Blocks.field_10376 && RANDOM.nextInt(10) == 0) {
							((Fertilizable)Blocks.field_10376).method_9652(world, RANDOM, blockPos2, blockState2);
						}
					}
				}

				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_7721(IWorld iWorld, BlockPos blockPos, int i) {
		if (i == 0) {
			i = 15;
		}

		BlockState blockState = iWorld.method_8320(blockPos);
		if (!blockState.isAir()) {
			for (int j = 0; j < i; j++) {
				double d = RANDOM.nextGaussian() * 0.02;
				double e = RANDOM.nextGaussian() * 0.02;
				double f = RANDOM.nextGaussian() * 0.02;
				iWorld.addParticle(
					ParticleTypes.field_11211,
					(double)((float)blockPos.getX() + RANDOM.nextFloat()),
					(double)blockPos.getY() + (double)RANDOM.nextFloat() * blockState.method_17770(iWorld, blockPos).getMaximum(Direction.Axis.Y),
					(double)((float)blockPos.getZ() + RANDOM.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}
}

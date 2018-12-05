package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CoralDeadWallFanBlock;
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
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getPos();
		BlockPos blockPos2 = blockPos.method_10093(itemUsageContext.method_8038());
		if (method_7720(itemUsageContext.getItemStack(), world, blockPos)) {
			if (!world.isRemote) {
				world.fireWorldEvent(2005, blockPos, 0);
			}

			return ActionResult.SUCCESS;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			boolean bl = Block.method_9501(blockState.method_11628(world, blockPos), itemUsageContext.method_8038());
			if (bl && method_7719(itemUsageContext.getItemStack(), world, blockPos2, itemUsageContext.method_8038())) {
				if (!world.isRemote) {
					world.fireWorldEvent(2005, blockPos2, 0);
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public static boolean method_7720(ItemStack itemStack, World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof Fertilizable) {
			Fertilizable fertilizable = (Fertilizable)blockState.getBlock();
			if (fertilizable.isFertilizable(world, blockPos, blockState, world.isRemote)) {
				if (!world.isRemote) {
					if (fertilizable.canGrow(world, world.random, blockPos, blockState)) {
						fertilizable.grow(world, world.random, blockPos, blockState);
					}

					itemStack.subtractAmount(1);
				}

				return true;
			}
		}

		return false;
	}

	public static boolean method_7719(ItemStack itemStack, World world, BlockPos blockPos, @Nullable Direction direction) {
		if (world.getBlockState(blockPos).getBlock() == Blocks.field_10382 && world.getFluidState(blockPos).method_15761() == 8) {
			if (!world.isRemote) {
				label79:
				for (int i = 0; i < 128; i++) {
					BlockPos blockPos2 = blockPos;
					Biome biome = world.getBiome(blockPos);
					BlockState blockState = Blocks.field_10376.getDefaultState();

					for (int j = 0; j < i / 16; j++) {
						blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
						biome = world.getBiome(blockPos2);
						if (world.getBlockState(blockPos2).blocksLight(world, blockPos2)) {
							continue label79;
						}
					}

					if (biome == Biomes.field_9408 || biome == Biomes.field_9448) {
						if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
							blockState = BlockTags.field_15476.getRandom(world.random).getDefaultState().with(CoralDeadWallFanBlock.field_9933, direction);
						} else if (random.nextInt(4) == 0) {
							blockState = BlockTags.field_15496.getRandom(random).getDefaultState();
						}
					}

					if (blockState.getBlock().matches(BlockTags.field_15476)) {
						for (int jx = 0; !blockState.canPlaceAt(world, blockPos2) && jx < 4; jx++) {
							blockState = blockState.with(CoralDeadWallFanBlock.field_9933, Direction.class_2353.HORIZONTAL.random(random));
						}
					}

					if (blockState.canPlaceAt(world, blockPos2)) {
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.getBlock() == Blocks.field_10382 && world.getFluidState(blockPos2).method_15761() == 8) {
							world.setBlockState(blockPos2, blockState, 3);
						} else if (blockState2.getBlock() == Blocks.field_10376 && random.nextInt(10) == 0) {
							((Fertilizable)Blocks.field_10376).grow(world, random, blockPos2, blockState2);
						}
					}
				}

				itemStack.subtractAmount(1);
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

		BlockState blockState = iWorld.getBlockState(blockPos);
		if (!blockState.isAir()) {
			for (int j = 0; j < i; j++) {
				double d = random.nextGaussian() * 0.02;
				double e = random.nextGaussian() * 0.02;
				double f = random.nextGaussian() * 0.02;
				iWorld.method_8406(
					ParticleTypes.field_11211,
					(double)((float)blockPos.getX() + random.nextFloat()),
					(double)blockPos.getY() + (double)random.nextFloat() * blockState.getBoundingShape(iWorld, blockPos).method_1105(Direction.Axis.Y),
					(double)((float)blockPos.getZ() + random.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}
}

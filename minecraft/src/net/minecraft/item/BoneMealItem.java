package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(itemUsageContext.getSide());
		if (useOnFertilizable(itemUsageContext.getStack(), world, blockPos)) {
			if (!world.isClient) {
				world.playLevelEvent(2005, blockPos, 0);
			}

			return ActionResult.SUCCESS;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			boolean bl = blockState.isSideSolidFullSquare(world, blockPos, itemUsageContext.getSide());
			if (bl && useOnGround(itemUsageContext.getStack(), world, blockPos2, itemUsageContext.getSide())) {
				if (!world.isClient) {
					world.playLevelEvent(2005, blockPos2, 0);
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public static boolean useOnFertilizable(ItemStack itemStack, World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof Fertilizable) {
			Fertilizable fertilizable = (Fertilizable)blockState.getBlock();
			if (fertilizable.isFertilizable(world, blockPos, blockState, world.isClient)) {
				if (world instanceof ServerWorld) {
					if (fertilizable.canGrow(world, world.random, blockPos, blockState)) {
						fertilizable.grow((ServerWorld)world, world.random, blockPos, blockState);
					}

					itemStack.decrement(1);
				}

				return true;
			}
		}

		return false;
	}

	public static boolean useOnGround(ItemStack itemStack, World world, BlockPos blockPos, @Nullable Direction direction) {
		if (world.getBlockState(blockPos).getBlock() == Blocks.WATER && world.getFluidState(blockPos).getLevel() == 8) {
			if (!(world instanceof ServerWorld)) {
				return true;
			} else {
				label80:
				for (int i = 0; i < 128; i++) {
					BlockPos blockPos2 = blockPos;
					Biome biome = world.getBiome(blockPos);
					BlockState blockState = Blocks.SEAGRASS.getDefaultState();

					for (int j = 0; j < i / 16; j++) {
						blockPos2 = blockPos2.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1);
						biome = world.getBiome(blockPos2);
						if (world.getBlockState(blockPos2).method_21743(world, blockPos2)) {
							continue label80;
						}
					}

					if (biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN) {
						if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
							blockState = BlockTags.WALL_CORALS.getRandom(world.random).getDefaultState().with(DeadCoralWallFanBlock.FACING, direction);
						} else if (RANDOM.nextInt(4) == 0) {
							blockState = BlockTags.UNDERWATER_BONEMEALS.getRandom(RANDOM).getDefaultState();
						}
					}

					if (blockState.getBlock().matches(BlockTags.WALL_CORALS)) {
						for (int jx = 0; !blockState.canPlaceAt(world, blockPos2) && jx < 4; jx++) {
							blockState = blockState.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(RANDOM));
						}
					}

					if (blockState.canPlaceAt(world, blockPos2)) {
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.getBlock() == Blocks.WATER && world.getFluidState(blockPos2).getLevel() == 8) {
							world.setBlockState(blockPos2, blockState, 3);
						} else if (blockState2.getBlock() == Blocks.SEAGRASS && RANDOM.nextInt(10) == 0) {
							((Fertilizable)Blocks.SEAGRASS).grow((ServerWorld)world, RANDOM, blockPos2, blockState2);
						}
					}
				}

				itemStack.decrement(1);
				return true;
			}
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public static void createParticles(IWorld iWorld, BlockPos blockPos, int i) {
		if (i == 0) {
			i = 15;
		}

		BlockState blockState = iWorld.getBlockState(blockPos);
		if (!blockState.isAir()) {
			for (int j = 0; j < i; j++) {
				double d = RANDOM.nextGaussian() * 0.02;
				double e = RANDOM.nextGaussian() * 0.02;
				double f = RANDOM.nextGaussian() * 0.02;
				iWorld.addParticle(
					ParticleTypes.HAPPY_VILLAGER,
					(double)((float)blockPos.getX() + RANDOM.nextFloat()),
					(double)blockPos.getY() + (double)RANDOM.nextFloat() * blockState.getOutlineShape(iWorld, blockPos).getMaximum(Direction.Axis.Y),
					(double)((float)blockPos.getZ() + RANDOM.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}
}

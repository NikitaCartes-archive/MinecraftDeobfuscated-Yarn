package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class BoneMealItem extends Item {
	public static final int field_30851 = 3;
	public static final int field_30852 = 1;
	public static final int field_30853 = 3;

	public BoneMealItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(context.getSide());
		if (useOnFertilizable(context.getStack(), world, blockPos)) {
			if (!world.isClient) {
				context.getPlayer().emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
				world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 15);
			}

			return ActionResult.success(world.isClient);
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			boolean bl = blockState.isSideSolidFullSquare(world, blockPos, context.getSide());
			if (bl && useOnGround(context.getStack(), world, blockPos2, context.getSide())) {
				if (!world.isClient) {
					context.getPlayer().emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
					world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos2, 15);
				}

				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof Fertilizable fertilizable && fertilizable.isFertilizable(world, pos, blockState)) {
			if (world instanceof ServerWorld) {
				if (fertilizable.canGrow(world, world.random, pos, blockState)) {
					fertilizable.grow((ServerWorld)world, world.random, pos, blockState);
				}

				stack.decrement(1);
			}

			return true;
		}

		return false;
	}

	public static boolean useOnGround(ItemStack stack, World world, BlockPos blockPos, @Nullable Direction facing) {
		if (world.getBlockState(blockPos).isOf(Blocks.WATER) && world.getFluidState(blockPos).getLevel() == 8) {
			if (!(world instanceof ServerWorld)) {
				return true;
			} else {
				Random random = world.getRandom();

				label78:
				for (int i = 0; i < 128; i++) {
					BlockPos blockPos2 = blockPos;
					BlockState blockState = Blocks.SEAGRASS.getDefaultState();

					for (int j = 0; j < i / 16; j++) {
						blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
						if (world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
							continue label78;
						}
					}

					RegistryEntry<Biome> registryEntry = world.getBiome(blockPos2);
					if (registryEntry.isIn(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
						if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
							blockState = (BlockState)Registries.BLOCK
								.getEntryList(BlockTags.WALL_CORALS)
								.flatMap(blocks -> blocks.getRandom(world.random))
								.map(blockEntry -> ((Block)blockEntry.value()).getDefaultState())
								.orElse(blockState);
							if (blockState.contains(DeadCoralWallFanBlock.FACING)) {
								blockState = blockState.with(DeadCoralWallFanBlock.FACING, facing);
							}
						} else if (random.nextInt(4) == 0) {
							blockState = (BlockState)Registries.BLOCK
								.getEntryList(BlockTags.UNDERWATER_BONEMEALS)
								.flatMap(blocks -> blocks.getRandom(world.random))
								.map(blockEntry -> ((Block)blockEntry.value()).getDefaultState())
								.orElse(blockState);
						}
					}

					if (blockState.isIn(BlockTags.WALL_CORALS, state -> state.contains(DeadCoralWallFanBlock.FACING))) {
						for (int k = 0; !blockState.canPlaceAt(world, blockPos2) && k < 4; k++) {
							blockState = blockState.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(random));
						}
					}

					if (blockState.canPlaceAt(world, blockPos2)) {
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.isOf(Blocks.WATER) && world.getFluidState(blockPos2).getLevel() == 8) {
							world.setBlockState(blockPos2, blockState, Block.NOTIFY_ALL);
						} else if (blockState2.isOf(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
							((Fertilizable)Blocks.SEAGRASS).grow((ServerWorld)world, random, blockPos2, blockState2);
						}
					}
				}

				stack.decrement(1);
				return true;
			}
		} else {
			return false;
		}
	}

	public static void createParticles(WorldAccess world, BlockPos pos, int count) {
		if (world.getBlockState(pos).getBlock() instanceof Fertilizable fertilizable) {
			BlockPos blockPos = fertilizable.method_55769(pos);
			switch (fertilizable.method_55770()) {
				case NEIGHBOR_SPREADER:
					ParticleUtil.method_55635(world, blockPos, count * 3, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
					break;
				case GROWER:
					ParticleUtil.method_55636(world, blockPos, count, ParticleTypes.HAPPY_VILLAGER);
			}
		}
	}
}

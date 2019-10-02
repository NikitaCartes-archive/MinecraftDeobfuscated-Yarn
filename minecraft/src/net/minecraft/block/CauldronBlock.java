package net.minecraft.block;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CauldronBlock extends Block {
	public static final IntProperty LEVEL = Properties.LEVEL_3;
	private static final VoxelShape RAY_TRACE_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
	protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
		VoxelShapes.fullCube(),
		VoxelShapes.union(
			createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
			createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
			createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
			RAY_TRACE_SHAPE
		),
		BooleanBiFunction.ONLY_FIRST
	);

	public CauldronBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return OUTLINE_SHAPE;
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return RAY_TRACE_SHAPE;
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		int i = (Integer)blockState.get(LEVEL);
		float f = (float)blockPos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
		if (!world.isClient && entity.isOnFire() && i > 0 && entity.getBoundingBox().minY <= (double)f) {
			entity.extinguish();
			this.setLevel(world, blockPos, blockState, i - 1);
		}
	}

	@Override
	public boolean onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.isEmpty()) {
			return true;
		} else {
			int i = (Integer)blockState.get(LEVEL);
			Item item = itemStack.getItem();
			if (item == Items.WATER_BUCKET) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.setStackInHand(hand, new ItemStack(Items.BUCKET));
					}

					playerEntity.incrementStat(Stats.FILL_CAULDRON);
					this.setLevel(world, blockPos, blockState, 3);
					world.playSound(null, blockPos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.BUCKET) {
				if (i == 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
						} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
							playerEntity.dropItem(new ItemStack(Items.WATER_BUCKET), false);
						}
					}

					playerEntity.incrementStat(Stats.USE_CAULDRON);
					this.setLevel(world, blockPos, blockState, 0);
					world.playSound(null, blockPos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.GLASS_BOTTLE) {
				if (i > 0 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
						playerEntity.incrementStat(Stats.USE_CAULDRON);
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, itemStack2);
						} else if (!playerEntity.inventory.insertStack(itemStack2)) {
							playerEntity.dropItem(itemStack2, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
						}
					}

					world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setLevel(world, blockPos, blockState, i - 1);
				}

				return true;
			} else if (item == Items.POTION && PotionUtil.getPotion(itemStack) == Potions.WATER) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = new ItemStack(Items.GLASS_BOTTLE);
						playerEntity.incrementStat(Stats.USE_CAULDRON);
						playerEntity.setStackInHand(hand, itemStack2);
						if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
						}
					}

					world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setLevel(world, blockPos, blockState, i + 1);
				}

				return true;
			} else {
				if (i > 0 && item instanceof DyeableItem) {
					DyeableItem dyeableItem = (DyeableItem)item;
					if (dyeableItem.hasColor(itemStack) && !world.isClient) {
						dyeableItem.removeColor(itemStack);
						this.setLevel(world, blockPos, blockState, i - 1);
						playerEntity.incrementStat(Stats.CLEAN_ARMOR);
						return true;
					}
				}

				if (i > 0 && item instanceof BannerItem) {
					if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
						ItemStack itemStack2 = itemStack.copy();
						itemStack2.setCount(1);
						BannerBlockEntity.loadFromItemStack(itemStack2);
						playerEntity.incrementStat(Stats.CLEAN_BANNER);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.decrement(1);
							this.setLevel(world, blockPos, blockState, i - 1);
						}

						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, itemStack2);
						} else if (!playerEntity.inventory.insertStack(itemStack2)) {
							playerEntity.dropItem(itemStack2, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
						}
					}

					return true;
				} else if (i > 0 && item instanceof BlockItem) {
					Block block = ((BlockItem)item).getBlock();
					if (block instanceof ShulkerBoxBlock && !world.isClient()) {
						ItemStack itemStack3 = new ItemStack(Blocks.SHULKER_BOX, 1);
						if (itemStack.hasTag()) {
							itemStack3.setTag(itemStack.getTag().method_10553());
						}

						playerEntity.setStackInHand(hand, itemStack3);
						this.setLevel(world, blockPos, blockState, i - 1);
						playerEntity.incrementStat(Stats.CLEAN_SHULKER_BOX);
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void setLevel(World world, BlockPos blockPos, BlockState blockState, int i) {
		world.setBlockState(blockPos, blockState.with(LEVEL, Integer.valueOf(MathHelper.clamp(i, 0, 3))), 2);
		world.updateHorizontalAdjacent(blockPos, this);
	}

	@Override
	public void rainTick(World world, BlockPos blockPos) {
		if (world.random.nextInt(20) == 1) {
			float f = world.getBiome(blockPos).getTemperature(blockPos);
			if (!(f < 0.15F)) {
				BlockState blockState = world.getBlockState(blockPos);
				if ((Integer)blockState.get(LEVEL) < 3) {
					world.setBlockState(blockPos, blockState.cycle(LEVEL), 2);
				}
			}
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return (Integer)blockState.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}

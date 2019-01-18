package net.minecraft.block;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.block.BannerItem;
import net.minecraft.item.block.BlockItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
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
	public static final IntegerProperty LEVEL = Properties.CAULDRON_LEVEL;
	protected static final VoxelShape RAY_TRACE_SHAPE = Block.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
	protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), RAY_TRACE_SHAPE, BooleanBiFunction.ONLY_FIRST);

	public CauldronBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return OUTLINE_SHAPE;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
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
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.isEmpty()) {
			return true;
		} else {
			int i = (Integer)blockState.get(LEVEL);
			Item item = itemStack.getItem();
			if (item == Items.field_8705) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.setStackInHand(hand, new ItemStack(Items.field_8550));
					}

					playerEntity.increaseStat(Stats.field_15430);
					this.setLevel(world, blockPos, blockState, 3);
					world.playSound(null, blockPos, SoundEvents.field_14834, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.field_8550) {
				if (i == 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, new ItemStack(Items.field_8705));
						} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.field_8705))) {
							playerEntity.dropItem(new ItemStack(Items.field_8705), false);
						}
					}

					playerEntity.increaseStat(Stats.field_15373);
					this.setLevel(world, blockPos, blockState, 0);
					world.playSound(null, blockPos, SoundEvents.field_15126, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.field_8469) {
				if (i > 0 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8991);
						playerEntity.increaseStat(Stats.field_15373);
						itemStack.subtractAmount(1);
						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, itemStack2);
						} else if (!playerEntity.inventory.insertStack(itemStack2)) {
							playerEntity.dropItem(itemStack2, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).method_14204(playerEntity.containerPlayer);
						}
					}

					world.playSound(null, blockPos, SoundEvents.field_14779, SoundCategory.field_15245, 1.0F, 1.0F);
					this.setLevel(world, blockPos, blockState, i - 1);
				}

				return true;
			} else if (item == Items.field_8574 && PotionUtil.getPotion(itemStack) == Potions.field_8991) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = new ItemStack(Items.field_8469);
						playerEntity.increaseStat(Stats.field_15373);
						playerEntity.setStackInHand(hand, itemStack2);
						if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).method_14204(playerEntity.containerPlayer);
						}
					}

					world.playSound(null, blockPos, SoundEvents.field_14826, SoundCategory.field_15245, 1.0F, 1.0F);
					this.setLevel(world, blockPos, blockState, i + 1);
				}

				return true;
			} else {
				if (i > 0 && item instanceof DyeableArmorItem) {
					DyeableArmorItem dyeableArmorItem = (DyeableArmorItem)item;
					if (dyeableArmorItem.hasColor(itemStack) && !world.isClient) {
						dyeableArmorItem.removeColor(itemStack);
						this.setLevel(world, blockPos, blockState, i - 1);
						playerEntity.increaseStat(Stats.field_15382);
						return true;
					}
				}

				if (i > 0 && item instanceof BannerItem) {
					if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
						ItemStack itemStack2 = itemStack.copy();
						itemStack2.setAmount(1);
						BannerBlockEntity.loadFromItemStack(itemStack2);
						playerEntity.increaseStat(Stats.field_15390);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.subtractAmount(1);
							this.setLevel(world, blockPos, blockState, i - 1);
						}

						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, itemStack2);
						} else if (!playerEntity.inventory.insertStack(itemStack2)) {
							playerEntity.dropItem(itemStack2, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).method_14204(playerEntity.containerPlayer);
						}
					}

					return true;
				} else if (i > 0 && item instanceof BlockItem) {
					Block block = ((BlockItem)item).getBlock();
					if (block instanceof ShulkerBoxBlock && !world.isClient()) {
						ItemStack itemStack3 = new ItemStack(Blocks.field_10603, 1);
						if (itemStack.hasTag()) {
							itemStack3.setTag(itemStack.getTag().copy());
						}

						playerEntity.setStackInHand(hand, itemStack3);
						this.setLevel(world, blockPos, blockState, i - 1);
						playerEntity.increaseStat(Stats.field_15398);
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
	public void onRainTick(World world, BlockPos blockPos) {
		if (world.random.nextInt(20) == 1) {
			float f = world.getBiome(blockPos).getTemperature(blockPos);
			if (!(f < 0.15F)) {
				BlockState blockState = world.getBlockState(blockPos);
				if ((Integer)blockState.get(LEVEL) < 3) {
					world.setBlockState(blockPos, blockState.method_11572(LEVEL), 2);
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
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(LEVEL);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}

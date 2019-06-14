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
import net.minecraft.state.StateFactory;
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
	public static final IntProperty field_10745 = Properties.field_12513;
	private static final VoxelShape field_10747 = method_9541(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
	protected static final VoxelShape field_10746 = VoxelShapes.method_1072(
		VoxelShapes.method_1077(),
		VoxelShapes.method_17786(
			method_9541(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), method_9541(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), method_9541(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), field_10747
		),
		BooleanBiFunction.ONLY_FIRST
	);

	public CauldronBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10745, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10746;
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return false;
	}

	@Override
	public VoxelShape method_9584(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10747;
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		int i = (Integer)blockState.method_11654(field_10745);
		float f = (float)blockPos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
		if (!world.isClient && entity.isOnFire() && i > 0 && entity.method_5829().minY <= (double)f) {
			entity.extinguish();
			this.method_9726(world, blockPos, blockState, i - 1);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.isEmpty()) {
			return true;
		} else {
			int i = (Integer)blockState.method_11654(field_10745);
			Item item = itemStack.getItem();
			if (item == Items.field_8705) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.setStackInHand(hand, new ItemStack(Items.field_8550));
					}

					playerEntity.incrementStat(Stats.field_15430);
					this.method_9726(world, blockPos, blockState, 3);
					world.playSound(null, blockPos, SoundEvents.field_14834, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.field_8550) {
				if (i == 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, new ItemStack(Items.field_8705));
						} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.field_8705))) {
							playerEntity.dropItem(new ItemStack(Items.field_8705), false);
						}
					}

					playerEntity.incrementStat(Stats.field_15373);
					this.method_9726(world, blockPos, blockState, 0);
					world.playSound(null, blockPos, SoundEvents.field_15126, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.field_8469) {
				if (i > 0 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8991);
						playerEntity.incrementStat(Stats.field_15373);
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							playerEntity.setStackInHand(hand, itemStack2);
						} else if (!playerEntity.inventory.insertStack(itemStack2)) {
							playerEntity.dropItem(itemStack2, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
						}
					}

					world.playSound(null, blockPos, SoundEvents.field_14779, SoundCategory.field_15245, 1.0F, 1.0F);
					this.method_9726(world, blockPos, blockState, i - 1);
				}

				return true;
			} else if (item == Items.field_8574 && PotionUtil.getPotion(itemStack) == Potions.field_8991) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = new ItemStack(Items.field_8469);
						playerEntity.incrementStat(Stats.field_15373);
						playerEntity.setStackInHand(hand, itemStack2);
						if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
						}
					}

					world.playSound(null, blockPos, SoundEvents.field_14826, SoundCategory.field_15245, 1.0F, 1.0F);
					this.method_9726(world, blockPos, blockState, i + 1);
				}

				return true;
			} else {
				if (i > 0 && item instanceof DyeableItem) {
					DyeableItem dyeableItem = (DyeableItem)item;
					if (dyeableItem.hasColor(itemStack) && !world.isClient) {
						dyeableItem.removeColor(itemStack);
						this.method_9726(world, blockPos, blockState, i - 1);
						playerEntity.incrementStat(Stats.field_15382);
						return true;
					}
				}

				if (i > 0 && item instanceof BannerItem) {
					if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
						ItemStack itemStack2 = itemStack.copy();
						itemStack2.setCount(1);
						BannerBlockEntity.loadFromItemStack(itemStack2);
						playerEntity.incrementStat(Stats.field_15390);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.decrement(1);
							this.method_9726(world, blockPos, blockState, i - 1);
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
					Block block = ((BlockItem)item).method_7711();
					if (block instanceof ShulkerBoxBlock && !world.isClient()) {
						ItemStack itemStack3 = new ItemStack(Blocks.field_10603, 1);
						if (itemStack.hasTag()) {
							itemStack3.setTag(itemStack.getTag().method_10553());
						}

						playerEntity.setStackInHand(hand, itemStack3);
						this.method_9726(world, blockPos, blockState, i - 1);
						playerEntity.incrementStat(Stats.field_15398);
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void method_9726(World world, BlockPos blockPos, BlockState blockState, int i) {
		world.method_8652(blockPos, blockState.method_11657(field_10745, Integer.valueOf(MathHelper.clamp(i, 0, 3))), 2);
		world.method_8455(blockPos, this);
	}

	@Override
	public void onRainTick(World world, BlockPos blockPos) {
		if (world.random.nextInt(20) == 1) {
			float f = world.method_8310(blockPos).getTemperature(blockPos);
			if (!(f < 0.15F)) {
				BlockState blockState = world.method_8320(blockPos);
				if ((Integer)blockState.method_11654(field_10745) < 3) {
					world.method_8652(blockPos, blockState.method_11572(field_10745), 2);
				}
			}
		}
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return (Integer)blockState.method_11654(field_10745);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10745);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}

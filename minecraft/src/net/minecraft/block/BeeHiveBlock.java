package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BeeHiveBlock extends BlockWithEntity {
	public static final Direction[] field_20418 = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;

	public BeeHiveBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(HONEY_LEVEL, Integer.valueOf(0)).with(FACING, Direction.NORTH));
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(HONEY_LEVEL);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state, blockEntity, stack);
		if (!world.isClient) {
			if (blockEntity instanceof BeeHiveBlockEntity && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
				((BeeHiveBlockEntity)blockEntity).angerBees(player, state, BeeHiveBlockEntity.BeeState.EMERGENCY);
				world.updateHorizontalAdjacent(pos, this);
			}

			List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, new Box(pos).expand(8.0, 6.0, 8.0));
			if (!list.isEmpty()) {
				List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, new Box(pos).expand(8.0, 6.0, 8.0));
				int i = list2.size();

				for (BeeEntity beeEntity : list) {
					if (beeEntity.getTarget() == null) {
						beeEntity.setBeeAttacker((Entity)list2.get(world.random.nextInt(i)));
					}
				}
			}
		}
	}

	public static void dropHoneycomb(World world, BlockPos blockPos) {
		for (int i = 0; i < 3; i++) {
			dropStack(world, blockPos, new ItemStack(Items.HONEYCOMB, 1));
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		int i = (Integer)state.get(HONEY_LEVEL);
		boolean bl = false;
		if (i >= 5) {
			if (itemStack.getItem() == Items.SHEARS) {
				world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				dropHoneycomb(world, pos);
				itemStack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
				bl = true;
			} else if (itemStack.getItem() == Items.GLASS_BOTTLE) {
				itemStack.decrement(1);
				world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				if (itemStack.isEmpty()) {
					player.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
				} else if (!player.inventory.insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
					player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
				}

				bl = true;
			}
		}

		if (bl) {
			this.emptyHoney(world, state, pos, player, BeeHiveBlockEntity.BeeState.EMERGENCY);
			return ActionResult.SUCCESS;
		} else {
			return super.onUse(state, world, pos, player, hand, hit);
		}
	}

	public void emptyHoney(World world, BlockState blockState, BlockPos blockPos, @Nullable PlayerEntity playerEntity, BeeHiveBlockEntity.BeeState beeState) {
		world.setBlockState(blockPos, blockState.with(HONEY_LEVEL, Integer.valueOf(0)), 3);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof BeeHiveBlockEntity) {
			BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
			beeHiveBlockEntity.angerBees(playerEntity, blockState, beeState);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Integer)state.get(HONEY_LEVEL) >= 5) {
			for (int i = 0; i < random.nextInt(1) + 1; i++) {
				this.method_21843(world, pos, state);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_21843(World world, BlockPos blockPos, BlockState blockState) {
		if (blockState.getFluidState().isEmpty() && !(world.random.nextFloat() < 0.3F)) {
			VoxelShape voxelShape = blockState.getCollisionShape(world, blockPos);
			double d = voxelShape.getMaximum(Direction.Axis.Y);
			if (d >= 1.0 && !blockState.matches(BlockTags.IMPERMEABLE)) {
				double e = voxelShape.getMinimum(Direction.Axis.Y);
				if (e > 0.0) {
					this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() + e - 0.05);
				} else {
					BlockPos blockPos2 = blockPos.method_10074();
					BlockState blockState2 = world.getBlockState(blockPos2);
					VoxelShape voxelShape2 = blockState2.getCollisionShape(world, blockPos2);
					double f = voxelShape2.getMaximum(Direction.Axis.Y);
					if ((f < 1.0 || !blockState2.isFullCube(world, blockPos2)) && blockState2.getFluidState().isEmpty()) {
						this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() - 0.05);
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private void addHoneyParticle(World world, BlockPos blockPos, VoxelShape voxelShape, double d) {
		this.addHoneyParticle(
			world,
			(double)blockPos.getX() + voxelShape.getMinimum(Direction.Axis.X),
			(double)blockPos.getX() + voxelShape.getMaximum(Direction.Axis.X),
			(double)blockPos.getZ() + voxelShape.getMinimum(Direction.Axis.Z),
			(double)blockPos.getZ() + voxelShape.getMaximum(Direction.Axis.Z),
			d
		);
	}

	@Environment(EnvType.CLIENT)
	private void addHoneyParticle(World world, double d, double e, double f, double g, double h) {
		world.addParticle(
			ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), d, e), h, MathHelper.lerp(world.random.nextDouble(), f, g), 0.0, 0.0, 0.0
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HONEY_LEVEL, FACING);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new BeeHiveBlockEntity();
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && player.isCreative()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BeeHiveBlockEntity) {
				ItemStack itemStack = new ItemStack(this);
				BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
				if (!beeHiveBlockEntity.hasNoBees()) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.put("Bees", beeHiveBlockEntity.getBees());
					itemStack.putSubTag("BlockEntityTag", compoundTag);
				}

				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putInt("honey_level", (Integer)state.get(HONEY_LEVEL));
				itemStack.putSubTag("BlockStateTag", compoundTag);
				ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}
		}

		super.onBreak(world, pos, state, player);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		Entity entity = builder.getNullable(LootContextParameters.THIS_ENTITY);
		if (entity instanceof TntEntity
			|| entity instanceof CreeperEntity
			|| entity instanceof WitherSkullEntity
			|| entity instanceof WitherEntity
			|| entity instanceof TntMinecartEntity) {
			BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
			if (blockEntity instanceof BeeHiveBlockEntity) {
				BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
				beeHiveBlockEntity.angerBees(null, state, BeeHiveBlockEntity.BeeState.EMERGENCY);
			}
		}

		return super.getDroppedStacks(state, builder);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (world.getBlockState(neighborPos).getBlock() instanceof FireBlock) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BeeHiveBlockEntity) {
				BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
				beeHiveBlockEntity.angerBees(null, state, BeeHiveBlockEntity.BeeState.EMERGENCY);
			}
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}
}

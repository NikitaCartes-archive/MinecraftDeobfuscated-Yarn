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
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
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
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return (Integer)blockState.get(HONEY_LEVEL);
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
		if (!world.isClient) {
			if (blockEntity instanceof BeeHiveBlockEntity && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
				((BeeHiveBlockEntity)blockEntity).angerBees(playerEntity, BeeHiveBlockEntity.BeeState.BEE_RELEASED);
				world.updateHorizontalAdjacent(blockPos, this);
			}

			List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
			if (!list.isEmpty()) {
				List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
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
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		int i = (Integer)blockState.get(HONEY_LEVEL);
		boolean bl = false;
		if (i >= 5) {
			if (itemStack.getItem() == Items.SHEARS) {
				world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				dropHoneycomb(world, blockPos);
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
				bl = true;
			} else if (itemStack.getItem() == Items.GLASS_BOTTLE) {
				itemStack.decrement(1);
				world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				if (itemStack.isEmpty()) {
					playerEntity.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
				} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
					playerEntity.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
				}

				bl = true;
			}
		}

		if (bl) {
			this.emptyHoney(world, blockState, blockPos, playerEntity);
			return true;
		} else {
			return super.activate(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		}
	}

	public void emptyHoney(World world, BlockState blockState, BlockPos blockPos, @Nullable PlayerEntity playerEntity) {
		world.setBlockState(blockPos, blockState.with(HONEY_LEVEL, Integer.valueOf(0)), 3);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof BeeHiveBlockEntity) {
			BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
			beeHiveBlockEntity.angerBees(playerEntity, BeeHiveBlockEntity.BeeState.BEE_RELEASED);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Integer)blockState.get(HONEY_LEVEL) >= 5) {
			for (int i = 0; i < random.nextInt(1) + 1; i++) {
				this.method_21843(world, blockPos, blockState);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_21843(World world, BlockPos blockPos, BlockState blockState) {
		if (blockState.getFluidState().isEmpty()) {
			VoxelShape voxelShape = blockState.getCollisionShape(world, blockPos);
			double d = voxelShape.getMaximum(Direction.Axis.Y);
			if (d >= 1.0 && !blockState.matches(BlockTags.IMPERMEABLE)) {
				double e = voxelShape.getMinimum(Direction.Axis.Y);
				if (e > 0.0) {
					this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() + e - 0.05);
				} else {
					BlockPos blockPos2 = blockPos.down();
					BlockState blockState2 = world.getBlockState(blockPos2);
					VoxelShape voxelShape2 = blockState2.getCollisionShape(world, blockPos2);
					double f = voxelShape2.getMaximum(Direction.Axis.Y);
					if ((f < 1.0 || !blockState2.method_21743(world, blockPos2)) && blockState2.getFluidState().isEmpty()) {
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
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(HONEY_LEVEL, FACING);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BeeHiveBlockEntity();
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && playerEntity.isCreative()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BeeHiveBlockEntity) {
				ItemStack itemStack = new ItemStack(this);
				BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
				if (!beeHiveBlockEntity.hasNoBees()) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.put("Bees", beeHiveBlockEntity.getBees());
					itemStack.putSubTag("BlockEntityTag", compoundTag);
				}

				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putInt("honey_level", (Integer)blockState.get(HONEY_LEVEL));
				itemStack.putSubTag("BlockStateTag", compoundTag);
				ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}
}

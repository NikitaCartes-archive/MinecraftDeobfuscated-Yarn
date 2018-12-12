package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractButtonBlock extends WallMountedBlock {
	public static final BooleanProperty field_10729 = Properties.POWERED;
	protected static final VoxelShape field_10721 = Block.createCubeShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape field_10727 = Block.createCubeShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape field_10723 = Block.createCubeShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
	protected static final VoxelShape field_10716 = Block.createCubeShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
	protected static final VoxelShape field_10728 = Block.createCubeShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_10715 = Block.createCubeShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
	protected static final VoxelShape field_10731 = Block.createCubeShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_10720 = Block.createCubeShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
	protected static final VoxelShape field_10717 = Block.createCubeShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape field_10726 = Block.createCubeShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape field_10722 = Block.createCubeShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
	protected static final VoxelShape field_10730 = Block.createCubeShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
	protected static final VoxelShape field_10719 = Block.createCubeShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_10724 = Block.createCubeShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
	protected static final VoxelShape field_10732 = Block.createCubeShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_10718 = Block.createCubeShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
	private final boolean field_10725;

	protected AbstractButtonBlock(boolean bl, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11177, Direction.NORTH)
				.with(field_10729, Boolean.valueOf(false))
				.with(field_11007, WallMountLocation.field_12471)
		);
		this.field_10725 = bl;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.field_10725 ? 30 : 20;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.get(field_11177);
		boolean bl = (Boolean)blockState.get(field_10729);
		switch ((WallMountLocation)blockState.get(field_11007)) {
			case field_12475:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? field_10722 : field_10723;
				}

				return bl ? field_10730 : field_10716;
			case field_12471:
				switch (direction) {
					case EAST:
						return bl ? field_10718 : field_10720;
					case WEST:
						return bl ? field_10732 : field_10731;
					case SOUTH:
						return bl ? field_10724 : field_10715;
					case NORTH:
					default:
						return bl ? field_10719 : field_10728;
				}
			case field_12473:
			default:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? field_10717 : field_10721;
				} else {
					return bl ? field_10726 : field_10727;
				}
		}
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if ((Boolean)blockState.get(field_10729)) {
			return true;
		} else {
			world.setBlockState(blockPos, blockState.with(field_10729, Boolean.valueOf(true)), 3);
			this.method_9714(playerEntity, world, blockPos, true);
			this.method_9713(blockState, world, blockPos);
			world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
			return true;
		}
	}

	protected void method_9714(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos, boolean bl) {
		iWorld.playSound(bl ? playerEntity : null, blockPos, this.method_9712(bl), SoundCategory.field_15245, 0.3F, bl ? 0.6F : 0.5F);
	}

	protected abstract SoundEvent method_9712(boolean bl);

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.get(field_10729)) {
				this.method_9713(blockState, world, blockPos);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_10729) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_10729) && method_10119(blockState) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient && (Boolean)blockState.get(field_10729)) {
			if (this.field_10725) {
				this.method_9715(blockState, world, blockPos);
			} else {
				world.setBlockState(blockPos, blockState.with(field_10729, Boolean.valueOf(false)), 3);
				this.method_9713(blockState, world, blockPos);
				this.method_9714(null, world, blockPos, false);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient && this.field_10725 && !(Boolean)blockState.get(field_10729)) {
			this.method_9715(blockState, world, blockPos);
		}
	}

	private void method_9715(BlockState blockState, World world, BlockPos blockPos) {
		List<? extends Entity> list = world.getVisibleEntities(ProjectileEntity.class, blockState.getBoundingShape(world, blockPos).getBoundingBox().offset(blockPos));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)blockState.get(field_10729);
		if (bl != bl2) {
			world.setBlockState(blockPos, blockState.with(field_10729, Boolean.valueOf(bl)), 3);
			this.method_9713(blockState, world, blockPos);
			this.method_9714(null, world, blockPos, bl);
		}

		if (bl) {
			world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	private void method_9713(BlockState blockState, World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.offset(method_10119(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_10729, field_11007);
	}
}

package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractButtonBlock extends WallMountedBlock {
	public static final BooleanProperty field_10729 = Properties.field_12484;
	protected static final VoxelShape field_10721 = Block.method_9541(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape field_10727 = Block.method_9541(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape field_10723 = Block.method_9541(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
	protected static final VoxelShape field_10716 = Block.method_9541(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
	protected static final VoxelShape field_10728 = Block.method_9541(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_10715 = Block.method_9541(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
	protected static final VoxelShape field_10731 = Block.method_9541(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_10720 = Block.method_9541(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
	protected static final VoxelShape field_10717 = Block.method_9541(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape field_10726 = Block.method_9541(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape field_10722 = Block.method_9541(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
	protected static final VoxelShape field_10730 = Block.method_9541(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
	protected static final VoxelShape field_10719 = Block.method_9541(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_10724 = Block.method_9541(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
	protected static final VoxelShape field_10732 = Block.method_9541(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_10718 = Block.method_9541(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
	private final boolean wooden;

	protected AbstractButtonBlock(boolean bl, Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, Direction.field_11043)
				.method_11657(field_10729, Boolean.valueOf(false))
				.method_11657(field_11007, WallMountLocation.field_12471)
		);
		this.wooden = bl;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.wooden ? 30 : 20;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		Direction direction = blockState.method_11654(field_11177);
		boolean bl = (Boolean)blockState.method_11654(field_10729);
		switch ((WallMountLocation)blockState.method_11654(field_11007)) {
			case field_12475:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? field_10722 : field_10723;
				}

				return bl ? field_10730 : field_10716;
			case field_12471:
				switch (direction) {
					case field_11034:
						return bl ? field_10718 : field_10720;
					case field_11039:
						return bl ? field_10732 : field_10731;
					case field_11035:
						return bl ? field_10724 : field_10715;
					case field_11043:
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
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.method_11654(field_10729)) {
			return true;
		} else {
			world.method_8652(blockPos, blockState.method_11657(field_10729, Boolean.valueOf(true)), 3);
			this.playClickSound(playerEntity, world, blockPos, true);
			this.method_9713(blockState, world, blockPos);
			world.method_8397().schedule(blockPos, this, this.getTickRate(world));
			return true;
		}
	}

	protected void playClickSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos, boolean bl) {
		iWorld.playSound(bl ? playerEntity : null, blockPos, this.getClickSound(bl), SoundCategory.field_15245, 0.3F, bl ? 0.6F : 0.5F);
	}

	protected abstract SoundEvent getClickSound(boolean bl);

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.method_11654(field_10729)) {
				this.method_9713(blockState, world, blockPos);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_10729) ? 15 : 0;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_10729) && method_10119(blockState) == direction ? 15 : 0;
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient && (Boolean)blockState.method_11654(field_10729)) {
			if (this.wooden) {
				this.method_9715(blockState, world, blockPos);
			} else {
				world.method_8652(blockPos, blockState.method_11657(field_10729, Boolean.valueOf(false)), 3);
				this.method_9713(blockState, world, blockPos);
				this.playClickSound(null, world, blockPos, false);
			}
		}
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient && this.wooden && !(Boolean)blockState.method_11654(field_10729)) {
			this.method_9715(blockState, world, blockPos);
		}
	}

	private void method_9715(BlockState blockState, World world, BlockPos blockPos) {
		List<? extends Entity> list = world.method_18467(ProjectileEntity.class, blockState.method_17770(world, blockPos).getBoundingBox().offset(blockPos));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)blockState.method_11654(field_10729);
		if (bl != bl2) {
			world.method_8652(blockPos, blockState.method_11657(field_10729, Boolean.valueOf(bl)), 3);
			this.method_9713(blockState, world, blockPos);
			this.playClickSound(null, world, blockPos, bl);
		}

		if (bl) {
			world.method_8397().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	private void method_9713(BlockState blockState, World world, BlockPos blockPos) {
		world.method_8452(blockPos, this);
		world.method_8452(blockPos.offset(method_10119(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_10729, field_11007);
	}
}

package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ComparatorBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {
	public static final EnumProperty<ComparatorMode> MODE = Properties.COMPARATOR_MODE;

	public ComparatorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(POWERED, Boolean.valueOf(false)).with(MODE, ComparatorMode.field_12576)
		);
	}

	@Override
	protected int getUpdateDelayInternal(BlockState blockState) {
		return 2;
	}

	@Override
	protected int getOutputLevel(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		return blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
	}

	private int calculateOutputSignal(World world, BlockPos blockPos, BlockState blockState) {
		return blockState.get(MODE) == ComparatorMode.field_12578
			? Math.max(this.getPower(world, blockPos, blockState) - this.getMaxInputLevelSides(world, blockPos, blockState), 0)
			: this.getPower(world, blockPos, blockState);
	}

	@Override
	protected boolean hasPower(World world, BlockPos blockPos, BlockState blockState) {
		int i = this.getPower(world, blockPos, blockState);
		if (i >= 15) {
			return true;
		} else {
			return i == 0 ? false : i >= this.getMaxInputLevelSides(world, blockPos, blockState);
		}
	}

	@Override
	protected int getPower(World world, BlockPos blockPos, BlockState blockState) {
		int i = super.getPower(world, blockPos, blockState);
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.hasComparatorOutput()) {
			i = blockState2.getComparatorOutput(world, blockPos2);
		} else if (i < 15 && blockState2.isSimpleFullBlock(world, blockPos2)) {
			blockPos2 = blockPos2.offset(direction);
			blockState2 = world.getBlockState(blockPos2);
			if (blockState2.hasComparatorOutput()) {
				i = blockState2.getComparatorOutput(world, blockPos2);
			} else if (blockState2.isAir()) {
				ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos2);
				if (itemFrameEntity != null) {
					i = itemFrameEntity.getComparatorPower();
				}
			}
		}

		return i;
	}

	@Nullable
	private ItemFrameEntity getAttachedItemFrame(World world, Direction direction, BlockPos blockPos) {
		List<ItemFrameEntity> list = world.getEntities(
			ItemFrameEntity.class,
			new Box(
				(double)blockPos.getX(),
				(double)blockPos.getY(),
				(double)blockPos.getZ(),
				(double)(blockPos.getX() + 1),
				(double)(blockPos.getY() + 1),
				(double)(blockPos.getZ() + 1)
			),
			itemFrameEntity -> itemFrameEntity != null && itemFrameEntity.getHorizontalFacing() == direction
		);
		return list.size() == 1 ? (ItemFrameEntity)list.get(0) : null;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!playerEntity.abilities.allowModifyWorld) {
			return false;
		} else {
			blockState = blockState.cycle(MODE);
			float f = blockState.get(MODE) == ComparatorMode.field_12578 ? 0.55F : 0.5F;
			world.playSound(playerEntity, blockPos, SoundEvents.field_14762, SoundCategory.field_15245, 0.3F, f);
			world.setBlockState(blockPos, blockState, 2);
			this.update(world, blockPos, blockState);
			return true;
		}
	}

	@Override
	protected void updatePowered(World world, BlockPos blockPos, BlockState blockState) {
		if (!world.getBlockTickScheduler().isTicking(blockPos, this)) {
			int i = this.calculateOutputSignal(world, blockPos, blockState);
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			int j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
			if (i != j || (Boolean)blockState.get(POWERED) != this.hasPower(world, blockPos, blockState)) {
				TaskPriority taskPriority = this.isTargetNotAligned(world, blockPos, blockState) ? TaskPriority.field_9310 : TaskPriority.field_9314;
				world.getBlockTickScheduler().schedule(blockPos, this, 2, taskPriority);
			}
		}
	}

	private void update(World world, BlockPos blockPos, BlockState blockState) {
		int i = this.calculateOutputSignal(world, blockPos, blockState);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		int j = 0;
		if (blockEntity instanceof ComparatorBlockEntity) {
			ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
			j = comparatorBlockEntity.getOutputSignal();
			comparatorBlockEntity.setOutputSignal(i);
		}

		if (j != i || blockState.get(MODE) == ComparatorMode.field_12576) {
			boolean bl = this.hasPower(world, blockPos, blockState);
			boolean bl2 = (Boolean)blockState.get(POWERED);
			if (bl2 && !bl) {
				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(false)), 2);
			} else if (!bl2 && bl) {
				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 2);
			}

			this.updateTarget(world, blockPos, blockState);
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.update(world, blockPos, blockState);
	}

	@Override
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		super.onBlockAction(blockState, world, blockPos, i, j);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		return blockEntity != null && blockEntity.onBlockAction(i, j);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new ComparatorBlockEntity();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, MODE, POWERED);
	}
}

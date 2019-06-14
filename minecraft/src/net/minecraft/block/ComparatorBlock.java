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
	public static final EnumProperty<ComparatorMode> field_10789 = Properties.field_12534;

	public ComparatorBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, Direction.field_11043)
				.method_11657(field_10911, Boolean.valueOf(false))
				.method_11657(field_10789, ComparatorMode.field_12576)
		);
	}

	@Override
	protected int method_9992(BlockState blockState) {
		return 2;
	}

	@Override
	protected int method_9993(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = blockView.method_8321(blockPos);
		return blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
	}

	private int method_9773(World world, BlockPos blockPos, BlockState blockState) {
		return blockState.method_11654(field_10789) == ComparatorMode.field_12578
			? Math.max(this.method_9991(world, blockPos, blockState) - this.method_10000(world, blockPos, blockState), 0)
			: this.method_9991(world, blockPos, blockState);
	}

	@Override
	protected boolean method_9990(World world, BlockPos blockPos, BlockState blockState) {
		int i = this.method_9991(world, blockPos, blockState);
		if (i >= 15) {
			return true;
		} else {
			return i == 0 ? false : i >= this.method_10000(world, blockPos, blockState);
		}
	}

	@Override
	protected int method_9991(World world, BlockPos blockPos, BlockState blockState) {
		int i = super.method_9991(world, blockPos, blockState);
		Direction direction = blockState.method_11654(field_11177);
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = world.method_8320(blockPos2);
		if (blockState2.hasComparatorOutput()) {
			i = blockState2.getComparatorOutput(world, blockPos2);
		} else if (i < 15 && blockState2.isSimpleFullBlock(world, blockPos2)) {
			blockPos2 = blockPos2.offset(direction);
			blockState2 = world.method_8320(blockPos2);
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
		List<ItemFrameEntity> list = world.method_8390(
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
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!playerEntity.abilities.allowModifyWorld) {
			return false;
		} else {
			blockState = blockState.method_11572(field_10789);
			float f = blockState.method_11654(field_10789) == ComparatorMode.field_12578 ? 0.55F : 0.5F;
			world.playSound(playerEntity, blockPos, SoundEvents.field_14762, SoundCategory.field_15245, 0.3F, f);
			world.method_8652(blockPos, blockState, 2);
			this.method_9775(world, blockPos, blockState);
			return true;
		}
	}

	@Override
	protected void method_9998(World world, BlockPos blockPos, BlockState blockState) {
		if (!world.method_8397().isTicking(blockPos, this)) {
			int i = this.method_9773(world, blockPos, blockState);
			BlockEntity blockEntity = world.method_8321(blockPos);
			int j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
			if (i != j || (Boolean)blockState.method_11654(field_10911) != this.method_9990(world, blockPos, blockState)) {
				TaskPriority taskPriority = this.method_9988(world, blockPos, blockState) ? TaskPriority.field_9310 : TaskPriority.field_9314;
				world.method_8397().method_8675(blockPos, this, 2, taskPriority);
			}
		}
	}

	private void method_9775(World world, BlockPos blockPos, BlockState blockState) {
		int i = this.method_9773(world, blockPos, blockState);
		BlockEntity blockEntity = world.method_8321(blockPos);
		int j = 0;
		if (blockEntity instanceof ComparatorBlockEntity) {
			ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
			j = comparatorBlockEntity.getOutputSignal();
			comparatorBlockEntity.setOutputSignal(i);
		}

		if (j != i || blockState.method_11654(field_10789) == ComparatorMode.field_12576) {
			boolean bl = this.method_9990(world, blockPos, blockState);
			boolean bl2 = (Boolean)blockState.method_11654(field_10911);
			if (bl2 && !bl) {
				world.method_8652(blockPos, blockState.method_11657(field_10911, Boolean.valueOf(false)), 2);
			} else if (!bl2 && bl) {
				world.method_8652(blockPos, blockState.method_11657(field_10911, Boolean.valueOf(true)), 2);
			}

			this.method_9997(world, blockPos, blockState);
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.method_9775(world, blockPos, blockState);
	}

	@Override
	public boolean method_9592(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		super.method_9592(blockState, world, blockPos, i, j);
		BlockEntity blockEntity = world.method_8321(blockPos);
		return blockEntity != null && blockEntity.onBlockAction(i, j);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new ComparatorBlockEntity();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_10789, field_10911);
	}
}

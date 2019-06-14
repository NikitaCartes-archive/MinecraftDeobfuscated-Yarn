package net.minecraft.block;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class AbstractFurnaceBlock extends BlockWithEntity {
	public static final DirectionProperty field_11104 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_11105 = RedstoneTorchBlock.field_11446;

	protected AbstractFurnaceBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11104, Direction.field_11043).method_11657(field_11105, Boolean.valueOf(false)));
	}

	@Override
	public int method_9593(BlockState blockState) {
		return blockState.method_11654(field_11105) ? super.method_9593(blockState) : 0;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient) {
			this.openContainer(world, blockPos, playerEntity);
		}

		return true;
	}

	protected abstract void openContainer(World world, BlockPos blockPos, PlayerEntity playerEntity);

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11104, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof AbstractFurnaceBlockEntity) {
				((AbstractFurnaceBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof AbstractFurnaceBlockEntity) {
				ItemScatterer.method_5451(world, blockPos, (AbstractFurnaceBlockEntity)blockEntity);
				world.method_8455(blockPos, this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.method_7608(world.method_8321(blockPos));
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11104, blockRotation.rotate(blockState.method_11654(field_11104)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11104)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11104, field_11105);
	}
}

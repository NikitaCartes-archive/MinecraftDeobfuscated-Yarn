package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class SignBlock extends BlockWithEntity implements Waterloggable {
	public static final BooleanProperty field_11491 = Properties.WATERLOGGED;
	protected static final VoxelShape field_11492 = Block.createCubeShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	protected SignBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.get(field_11491)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11492;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return true;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new SignBlockEntity();
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isRemote) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof SignBlockEntity) {
				SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
				ItemStack itemStack = playerEntity.getStackInHand(hand);
				if (itemStack.getItem() instanceof DyeItem) {
					boolean bl = signBlockEntity.method_16127(((DyeItem)itemStack.getItem()).getColor());
					if (bl && !playerEntity.isCreative()) {
						itemStack.subtractAmount(1);
					}
				}

				return signBlockEntity.onActivate(playerEntity);
			} else {
				return false;
			}
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_11491) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}
}

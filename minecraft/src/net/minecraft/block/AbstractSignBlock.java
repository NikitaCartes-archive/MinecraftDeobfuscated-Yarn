package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractSignBlock extends BlockWithEntity implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	protected AbstractSignBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
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
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = itemStack.getItem() instanceof DyeItem && playerEntity.abilities.allowModifyWorld;
		if (world.isClient) {
			return bl ? ActionResult.SUCCESS : ActionResult.CONSUME;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof SignBlockEntity) {
				SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
				if (bl) {
					boolean bl2 = signBlockEntity.setTextColor(((DyeItem)itemStack.getItem()).getColor());
					if (bl2 && !playerEntity.isCreative()) {
						itemStack.decrement(1);
					}
				}

				return signBlockEntity.onActivate(playerEntity) ? ActionResult.SUCCESS : ActionResult.PASS;
			} else {
				return ActionResult.PASS;
			}
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}
}

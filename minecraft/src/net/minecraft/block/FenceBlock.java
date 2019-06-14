package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceBlock extends HorizontalConnectedBlock {
	private final VoxelShape[] field_11066;

	public FenceBlock(Block.Settings settings) {
		super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
		this.field_11066 = this.method_9984(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.field_11066[this.method_9987(blockState)];
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	public boolean method_10184(BlockState blockState, boolean bl, Direction direction) {
		Block block = blockState.getBlock();
		boolean bl2 = block.matches(BlockTags.field_16584) && blockState.method_11620() == this.field_10635;
		boolean bl3 = block instanceof FenceGateBlock && FenceGateBlock.method_16703(blockState, direction);
		return !canConnect(block) && bl || bl2 || bl3;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient) {
			return LeadItem.method_7994(playerEntity, world, blockPos);
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			return itemStack.getItem() == Items.field_8719 || itemStack.isEmpty();
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = blockView.method_8320(blockPos2);
		BlockState blockState2 = blockView.method_8320(blockPos3);
		BlockState blockState3 = blockView.method_8320(blockPos4);
		BlockState blockState4 = blockView.method_8320(blockPos5);
		return super.method_9605(itemPlacementContext)
			.method_11657(
				field_10905,
				Boolean.valueOf(this.method_10184(blockState, Block.method_20045(blockState, blockView, blockPos2, Direction.field_11035), Direction.field_11035))
			)
			.method_11657(
				field_10907,
				Boolean.valueOf(this.method_10184(blockState2, Block.method_20045(blockState2, blockView, blockPos3, Direction.field_11039), Direction.field_11039))
			)
			.method_11657(
				field_10904,
				Boolean.valueOf(this.method_10184(blockState3, Block.method_20045(blockState3, blockView, blockPos4, Direction.field_11043), Direction.field_11043))
			)
			.method_11657(
				field_10903,
				Boolean.valueOf(this.method_10184(blockState4, Block.method_20045(blockState4, blockView, blockPos5, Direction.field_11034), Direction.field_11034))
			)
			.method_11657(field_10900, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_10900)) {
			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getAxis().getType() == Direction.Type.field_11062
			? blockState.method_11657(
				(Property)FACING_PROPERTIES.get(direction),
				Boolean.valueOf(this.method_10184(blockState2, Block.method_20045(blockState2, iWorld, blockPos2, direction.getOpposite()), direction.getOpposite()))
			)
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10905, field_10907, field_10903, field_10904, field_10900);
	}
}

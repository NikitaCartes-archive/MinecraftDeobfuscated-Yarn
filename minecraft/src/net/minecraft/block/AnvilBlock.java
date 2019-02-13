package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.ContainerWorldContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AnvilBlock extends FallingBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.field_11177;
	private static final VoxelShape field_9882 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final VoxelShape field_9885 = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
	private static final VoxelShape field_9888 = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
	private static final VoxelShape field_9884 = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
	private static final VoxelShape field_9891 = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
	private static final VoxelShape field_9889 = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
	private static final VoxelShape field_9886 = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
	private static final VoxelShape X_AXIS_SHAPE = VoxelShapes.union(field_9882, field_9885, field_9888, field_9884);
	private static final VoxelShape Z_AXIS_SHAPE = VoxelShapes.union(field_9882, field_9891, field_9889, field_9886);
	private static final TranslatableTextComponent CONTAINER_NAME = new TranslatableTextComponent("container.repair");

	public AnvilBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerHorizontalFacing().rotateYClockwise());
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		playerEntity.openContainer(blockState.createContainerProvider(world, blockPos));
		return true;
	}

	@Nullable
	@Override
	public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
		return new ClientDummyContainerProvider(
			(i, playerInventory, playerEntity) -> new AnvilContainer(i, playerInventory, ContainerWorldContext.create(world, blockPos)), CONTAINER_NAME
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.get(FACING);
		return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity fallingBlockEntity) {
		fallingBlockEntity.setHurtEntities(true);
	}

	@Override
	public void onLanding(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		world.playEvent(1031, blockPos, 0);
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos blockPos) {
		world.playEvent(1029, blockPos, 0);
	}

	@Nullable
	public static BlockState getLandingState(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10535) {
			return Blocks.field_10105.getDefaultState().with(FACING, blockState.get(FACING));
		} else {
			return block == Blocks.field_10105 ? Blocks.field_10414.getDefaultState().with(FACING, blockState.get(FACING)) : null;
		}
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}

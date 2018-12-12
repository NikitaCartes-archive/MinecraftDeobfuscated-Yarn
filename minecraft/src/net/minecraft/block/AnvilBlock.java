package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilBlock extends FallingBlock {
	private static final Logger LOGGER_ANVIL = LogManager.getLogger();
	public static final DirectionProperty FACING = HorizontalFacingBlock.field_11177;
	private static final VoxelShape field_9882 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final VoxelShape field_9885 = Block.createCubeShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
	private static final VoxelShape field_9888 = Block.createCubeShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
	private static final VoxelShape field_9884 = Block.createCubeShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
	private static final VoxelShape field_9891 = Block.createCubeShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
	private static final VoxelShape field_9889 = Block.createCubeShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
	private static final VoxelShape field_9886 = Block.createCubeShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
	private static final VoxelShape field_9887 = VoxelShapes.union(field_9882, VoxelShapes.union(field_9885, VoxelShapes.union(field_9888, field_9884)));
	private static final VoxelShape field_9892 = VoxelShapes.union(field_9882, VoxelShapes.union(field_9891, VoxelShapes.union(field_9889, field_9886)));

	public AnvilBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerHorizontalFacing().rotateYClockwise());
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!world.isClient) {
			playerEntity.openContainer(new AnvilBlock.ContainerProvider(world, blockPos));
		}

		return true;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.get(FACING);
		return direction.getAxis() == Direction.Axis.X ? field_9887 : field_9892;
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity fallingBlockEntity) {
		fallingBlockEntity.setHurtEntities(true);
	}

	@Override
	public void onLanding(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		world.fireWorldEvent(1031, blockPos, 0);
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos blockPos) {
		world.fireWorldEvent(1029, blockPos, 0);
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
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.method_10503(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}

	public static class ContainerProvider implements net.minecraft.container.ContainerProvider {
		private final World world;
		private final BlockPos pos;

		public ContainerProvider(World world, BlockPos blockPos) {
			this.world = world;
			this.pos = blockPos;
		}

		@Override
		public TextComponent getName() {
			return new TranslatableTextComponent(Blocks.field_10535.getTranslationKey());
		}

		@Override
		public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new AnvilContainer(playerInventory, this.world, this.pos, playerEntity);
		}

		@Override
		public String getContainerId() {
			return "minecraft:anvil";
		}
	}
}

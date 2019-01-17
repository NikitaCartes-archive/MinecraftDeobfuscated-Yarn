package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class BellBlock extends BlockWithEntity {
	public static final DirectionProperty field_16324 = HorizontalFacingBlock.field_11177;
	private static final EnumProperty<Attachment> field_16326 = Properties.ATTACHMENT;
	private static final VoxelShape field_16325 = Block.createCubeShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	private static final VoxelShape field_16322 = Block.createCubeShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	private static final VoxelShape field_17087 = Block.createCubeShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
	private static final VoxelShape field_17088 = Block.createCubeShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
	private static final VoxelShape field_17089 = VoxelShapes.union(field_17088, field_17087);
	private static final VoxelShape field_17090 = VoxelShapes.union(field_17089, Block.createCubeShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
	private static final VoxelShape field_16321 = VoxelShapes.union(field_17089, Block.createCubeShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape field_17091 = VoxelShapes.union(field_17089, Block.createCubeShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
	private static final VoxelShape field_17092 = VoxelShapes.union(field_17089, Block.createCubeShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape field_16323 = VoxelShapes.union(field_17089, Block.createCubeShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
	private static final VoxelShape field_17093 = VoxelShapes.union(field_17089, Block.createCubeShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
	private static final VoxelShape field_17094 = VoxelShapes.union(field_17089, Block.createCubeShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

	public BellBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_16324, Direction.NORTH).with(field_16326, Attachment.field_17098));
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		Direction direction = blockHitResult.getSide();
		if (blockEntity instanceof BellBlockEntity
			&& this.method_17028(blockState, direction, blockHitResult.getPos().y - (double)blockHitResult.getBlockPos().getY())) {
			((BellBlockEntity)blockEntity).activate(direction);
			if (!world.isClient) {
				this.method_17026(world, blockPos);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean method_17028(BlockState blockState, Direction direction, double d) {
		if (direction.getAxis() != Direction.Axis.Y && !(d > 0.8124F)) {
			Direction direction2 = blockState.get(field_16324);
			Attachment attachment = blockState.get(field_16326);
			switch (attachment) {
				case field_17098:
					return direction2.getAxis() == direction.getAxis();
				case field_17100:
				case field_17101:
					return direction2.getAxis() != direction.getAxis();
				case field_17099:
					return true;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	private void method_17026(World world, BlockPos blockPos) {
		world.playSound(null, blockPos, SoundEvents.field_17265, SoundCategory.field_15245, 1.0F, 1.0F);
	}

	private VoxelShape method_16116(BlockState blockState) {
		Direction direction = blockState.get(field_16324);
		Attachment attachment = blockState.get(field_16326);
		if (attachment == Attachment.field_17098) {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? field_16322 : field_16325;
		} else if (attachment == Attachment.field_17099) {
			return field_17094;
		} else if (attachment == Attachment.field_17101) {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? field_16321 : field_17090;
		} else if (direction == Direction.NORTH) {
			return field_16323;
		} else if (direction == Direction.SOUTH) {
			return field_17093;
		} else {
			return direction == Direction.EAST ? field_17092 : field_17091;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_16116(blockState);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_16116(blockState);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getFacing();
		BlockPos blockPos = itemPlacementContext.getPos();
		World world = itemPlacementContext.getWorld();
		Direction.Axis axis = direction.getAxis();
		if (axis == Direction.Axis.Y) {
			BlockState blockState = this.getDefaultState()
				.with(field_16326, direction == Direction.DOWN ? Attachment.field_17099 : Attachment.field_17098)
				.with(field_16324, itemPlacementContext.getPlayerHorizontalFacing());
			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), blockPos)) {
				return blockState;
			}
		} else {
			boolean bl = axis == Direction.Axis.X
					&& this.method_17027(world.getBlockState(blockPos.west()), world, blockPos.west(), Direction.EAST)
					&& this.method_17027(world.getBlockState(blockPos.east()), world, blockPos.east(), Direction.WEST)
				|| axis == Direction.Axis.Z
					&& this.method_17027(world.getBlockState(blockPos.north()), world, blockPos.north(), Direction.SOUTH)
					&& this.method_17027(world.getBlockState(blockPos.south()), world, blockPos.south(), Direction.NORTH);
			BlockState blockState = this.getDefaultState()
				.with(field_16324, direction.getOpposite())
				.with(field_16326, bl ? Attachment.field_17101 : Attachment.field_17100);
			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getPos())) {
				return blockState;
			}
		}

		return null;
	}

	private boolean method_17027(BlockState blockState, IWorld iWorld, BlockPos blockPos, Direction direction) {
		return isFaceFullCube(blockState.getCollisionShape(iWorld, blockPos), direction) && !method_9553(blockState.getBlock());
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		Attachment attachment = blockState.get(field_16326);
		Direction direction2 = method_16115(blockState).getOpposite();
		if (direction2 == direction && !blockState.canPlaceAt(iWorld, blockPos) && attachment != Attachment.field_17101) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if (direction.getAxis() == ((Direction)blockState.get(field_16324)).getAxis()) {
				if (attachment == Attachment.field_17101 && !this.method_17027(blockState2, iWorld, blockPos2, direction)) {
					return blockState.with(field_16326, Attachment.field_17100).with(field_16324, direction.getOpposite());
				}

				if (attachment == Attachment.field_17100
					&& direction2.getOpposite() == direction
					&& this.method_17027(blockState2, iWorld, blockPos2, blockState.get(field_16324))) {
					return blockState.with(field_16326, Attachment.field_17101);
				}
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = method_16115(blockState).getOpposite();
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		if (method_9553(block)) {
			return false;
		} else {
			boolean bl = Block.isFaceFullCube(blockState2.getCollisionShape(viewableWorld, blockPos2), direction.getOpposite());
			return direction == Direction.UP ? block == Blocks.field_10312 || bl : !method_9581(block) && bl;
		}
	}

	private static Direction method_16115(BlockState blockState) {
		switch ((Attachment)blockState.get(field_16326)) {
			case field_17098:
				return Direction.UP;
			case field_17099:
				return Direction.DOWN;
			default:
				return ((Direction)blockState.get(field_16324)).getOpposite();
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16324, field_16326);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BellBlockEntity();
	}
}

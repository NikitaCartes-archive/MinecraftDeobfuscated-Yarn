package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
	private static final EnumProperty<Attachment> field_16326 = Properties.field_17104;
	private static final VoxelShape field_16325 = Block.method_9541(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	private static final VoxelShape field_16322 = Block.method_9541(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	private static final VoxelShape field_17087 = Block.method_9541(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
	private static final VoxelShape field_17088 = Block.method_9541(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
	private static final VoxelShape field_17089 = VoxelShapes.method_1084(field_17088, field_17087);
	private static final VoxelShape field_17090 = VoxelShapes.method_1084(field_17089, Block.method_9541(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
	private static final VoxelShape field_16321 = VoxelShapes.method_1084(field_17089, Block.method_9541(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape field_17091 = VoxelShapes.method_1084(field_17089, Block.method_9541(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
	private static final VoxelShape field_17092 = VoxelShapes.method_1084(field_17089, Block.method_9541(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape field_16323 = VoxelShapes.method_1084(field_17089, Block.method_9541(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
	private static final VoxelShape field_17093 = VoxelShapes.method_1084(field_17089, Block.method_9541(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
	private static final VoxelShape field_17094 = VoxelShapes.method_1084(field_17089, Block.method_9541(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

	public BellBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_16324, Direction.NORTH).method_11657(field_16326, Attachment.field_17098));
	}

	@Override
	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (entity instanceof ProjectileEntity) {
			this.method_19285(world, blockState, world.method_8321(blockHitResult.method_17777()), blockHitResult);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.method_19285(world, blockState, world.method_8321(blockPos), blockHitResult);
	}

	private boolean method_19285(World world, BlockState blockState, @Nullable BlockEntity blockEntity, BlockHitResult blockHitResult) {
		Direction direction = blockHitResult.method_17780();
		if (!world.isClient
			&& blockEntity instanceof BellBlockEntity
			&& this.method_17028(blockState, direction, blockHitResult.method_17784().y - (double)blockHitResult.method_17777().getY())) {
			((BellBlockEntity)blockEntity).method_17031(direction);
			this.method_17026(world, blockHitResult.method_17777());
			return true;
		} else {
			return false;
		}
	}

	private boolean method_17028(BlockState blockState, Direction direction, double d) {
		if (direction.getAxis() != Direction.Axis.Y && !(d > 0.8124F)) {
			Direction direction2 = blockState.method_11654(field_16324);
			Attachment attachment = blockState.method_11654(field_16326);
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
		world.method_8396(null, blockPos, SoundEvents.field_17265, SoundCategory.field_15245, 1.0F, 1.0F);
	}

	private VoxelShape method_16116(BlockState blockState) {
		Direction direction = blockState.method_11654(field_16324);
		Attachment attachment = blockState.method_11654(field_16326);
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
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_16116(blockState);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_16116(blockState);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.method_8038();
		BlockPos blockPos = itemPlacementContext.method_8037();
		World world = itemPlacementContext.method_8045();
		Direction.Axis axis = direction.getAxis();
		if (axis == Direction.Axis.Y) {
			BlockState blockState = this.method_9564()
				.method_11657(field_16326, direction == Direction.DOWN ? Attachment.field_17099 : Attachment.field_17098)
				.method_11657(field_16324, itemPlacementContext.method_8042());
			if (blockState.method_11591(itemPlacementContext.method_8045(), blockPos)) {
				return blockState;
			}
		} else {
			boolean bl = axis == Direction.Axis.X
					&& this.method_17027(world.method_8320(blockPos.west()), world, blockPos.west(), Direction.EAST)
					&& this.method_17027(world.method_8320(blockPos.east()), world, blockPos.east(), Direction.WEST)
				|| axis == Direction.Axis.Z
					&& this.method_17027(world.method_8320(blockPos.north()), world, blockPos.north(), Direction.SOUTH)
					&& this.method_17027(world.method_8320(blockPos.south()), world, blockPos.south(), Direction.NORTH);
			BlockState blockState = this.method_9564()
				.method_11657(field_16324, direction.getOpposite())
				.method_11657(field_16326, bl ? Attachment.field_17101 : Attachment.field_17100);
			if (blockState.method_11591(itemPlacementContext.method_8045(), itemPlacementContext.method_8037())) {
				return blockState;
			}
		}

		return null;
	}

	private boolean method_17027(BlockState blockState, IWorld iWorld, BlockPos blockPos, Direction direction) {
		return method_9501(blockState.method_11628(iWorld, blockPos), direction) && !method_9553(blockState.getBlock());
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		Attachment attachment = blockState.method_11654(field_16326);
		Direction direction2 = method_16115(blockState).getOpposite();
		if (direction2 == direction && !blockState.method_11591(iWorld, blockPos) && attachment != Attachment.field_17101) {
			return Blocks.field_10124.method_9564();
		} else {
			if (direction.getAxis() == ((Direction)blockState.method_11654(field_16324)).getAxis()) {
				if (attachment == Attachment.field_17101 && !this.method_17027(blockState2, iWorld, blockPos2, direction)) {
					return blockState.method_11657(field_16326, Attachment.field_17100).method_11657(field_16324, direction.getOpposite());
				}

				if (attachment == Attachment.field_17100
					&& direction2.getOpposite() == direction
					&& this.method_17027(blockState2, iWorld, blockPos2, blockState.method_11654(field_16324))) {
					return blockState.method_11657(field_16326, Attachment.field_17101);
				}
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = method_16115(blockState).getOpposite();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		Block block = blockState2.getBlock();
		if (method_9553(block)) {
			return false;
		} else {
			boolean bl = Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), direction.getOpposite());
			return direction == Direction.UP ? block == Blocks.field_10312 || bl : !method_9581(block) && bl;
		}
	}

	private static Direction method_16115(BlockState blockState) {
		switch ((Attachment)blockState.method_11654(field_16326)) {
			case field_17098:
				return Direction.UP;
			case field_17099:
				return Direction.DOWN;
			default:
				return ((Direction)blockState.method_11654(field_16324)).getOpposite();
		}
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_16324, field_16326);
	}

	@Nullable
	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new BellBlockEntity();
	}
}

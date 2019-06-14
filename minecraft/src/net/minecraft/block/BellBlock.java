package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
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
		this.method_9590(this.field_10647.method_11664().method_11657(field_16324, Direction.field_11043).method_11657(field_16326, Attachment.field_17098));
	}

	@Override
	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (entity instanceof ProjectileEntity) {
			Entity entity2 = ((ProjectileEntity)entity).getOwner();
			PlayerEntity playerEntity = entity2 instanceof PlayerEntity ? (PlayerEntity)entity2 : null;
			this.method_19285(world, blockState, world.method_8321(blockHitResult.getBlockPos()), blockHitResult, playerEntity, true);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.method_19285(world, blockState, world.method_8321(blockPos), blockHitResult, playerEntity, true);
	}

	public boolean method_19285(
		World world, BlockState blockState, @Nullable BlockEntity blockEntity, BlockHitResult blockHitResult, @Nullable PlayerEntity playerEntity, boolean bl
	) {
		Direction direction = blockHitResult.getSide();
		BlockPos blockPos = blockHitResult.getBlockPos();
		boolean bl2 = !bl || this.method_17028(blockState, direction, blockHitResult.method_17784().y - (double)blockPos.getY());
		if (!world.isClient && blockEntity instanceof BellBlockEntity && bl2) {
			((BellBlockEntity)blockEntity).activate(direction);
			this.ring(world, blockPos);
			if (playerEntity != null) {
				playerEntity.incrementStat(Stats.field_19255);
			}

			return true;
		} else {
			return true;
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

	private void ring(World world, BlockPos blockPos) {
		world.playSound(null, blockPos, SoundEvents.field_17265, SoundCategory.field_15245, 2.0F, 1.0F);
	}

	private VoxelShape method_16116(BlockState blockState) {
		Direction direction = blockState.method_11654(field_16324);
		Attachment attachment = blockState.method_11654(field_16326);
		if (attachment == Attachment.field_17098) {
			return direction != Direction.field_11043 && direction != Direction.field_11035 ? field_16322 : field_16325;
		} else if (attachment == Attachment.field_17099) {
			return field_17094;
		} else if (attachment == Attachment.field_17101) {
			return direction != Direction.field_11043 && direction != Direction.field_11035 ? field_16321 : field_17090;
		} else if (direction == Direction.field_11043) {
			return field_16323;
		} else if (direction == Direction.field_11035) {
			return field_17093;
		} else {
			return direction == Direction.field_11034 ? field_17092 : field_17091;
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.method_16116(blockState);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.method_16116(blockState);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getSide();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		World world = itemPlacementContext.method_8045();
		Direction.Axis axis = direction.getAxis();
		if (axis == Direction.Axis.Y) {
			BlockState blockState = this.method_9564()
				.method_11657(field_16326, direction == Direction.field_11033 ? Attachment.field_17099 : Attachment.field_17098)
				.method_11657(field_16324, itemPlacementContext.getPlayerFacing());
			if (blockState.canPlaceAt(itemPlacementContext.method_8045(), blockPos)) {
				return blockState;
			}
		} else {
			boolean bl = axis == Direction.Axis.X
					&& method_20045(world.method_8320(blockPos.west()), world, blockPos.west(), Direction.field_11034)
					&& method_20045(world.method_8320(blockPos.east()), world, blockPos.east(), Direction.field_11039)
				|| axis == Direction.Axis.Z
					&& method_20045(world.method_8320(blockPos.north()), world, blockPos.north(), Direction.field_11035)
					&& method_20045(world.method_8320(blockPos.south()), world, blockPos.south(), Direction.field_11043);
			BlockState blockState = this.method_9564()
				.method_11657(field_16324, direction.getOpposite())
				.method_11657(field_16326, bl ? Attachment.field_17101 : Attachment.field_17100);
			if (blockState.canPlaceAt(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos())) {
				return blockState;
			}

			boolean bl2 = method_20045(world.method_8320(blockPos.down()), world, blockPos.down(), Direction.field_11036);
			blockState = blockState.method_11657(field_16326, bl2 ? Attachment.field_17098 : Attachment.field_17099);
			if (blockState.canPlaceAt(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		Attachment attachment = blockState.method_11654(field_16326);
		Direction direction2 = method_16115(blockState).getOpposite();
		if (direction2 == direction && !blockState.canPlaceAt(iWorld, blockPos) && attachment != Attachment.field_17101) {
			return Blocks.field_10124.method_9564();
		} else {
			if (direction.getAxis() == ((Direction)blockState.method_11654(field_16324)).getAxis()) {
				if (attachment == Attachment.field_17101 && !method_20045(blockState2, iWorld, blockPos2, direction)) {
					return blockState.method_11657(field_16326, Attachment.field_17100).method_11657(field_16324, direction.getOpposite());
				}

				if (attachment == Attachment.field_17100
					&& direction2.getOpposite() == direction
					&& method_20045(blockState2, iWorld, blockPos2, blockState.method_11654(field_16324))) {
					return blockState.method_11657(field_16326, Attachment.field_17101);
				}
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return WallMountedBlock.canPlaceAt(viewableWorld, blockPos, method_16115(blockState).getOpposite());
	}

	private static Direction method_16115(BlockState blockState) {
		switch ((Attachment)blockState.method_11654(field_16326)) {
			case field_17098:
				return Direction.field_11036;
			case field_17099:
				return Direction.field_11033;
			default:
				return ((Direction)blockState.method_11654(field_16324)).getOpposite();
		}
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_16324, field_16326);
	}

	@Nullable
	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new BellBlockEntity();
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}

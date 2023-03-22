package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HangingSignBlock extends AbstractSignBlock {
	public static final IntProperty ROTATION = Properties.ROTATION;
	public static final BooleanProperty ATTACHED = Properties.ATTACHED;
	protected static final float field_40302 = 5.0F;
	protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	private static final Map<Integer, VoxelShape> SHAPES_FOR_ROTATION = Maps.<Integer, VoxelShape>newHashMap(
		ImmutableMap.of(
			0,
			Block.createCuboidShape(1.0, 0.0, 7.0, 15.0, 10.0, 9.0),
			4,
			Block.createCuboidShape(7.0, 0.0, 1.0, 9.0, 10.0, 15.0),
			8,
			Block.createCuboidShape(1.0, 0.0, 7.0, 15.0, 10.0, 9.0),
			12,
			Block.createCuboidShape(7.0, 0.0, 1.0, 9.0, 10.0, 15.0)
		)
	);

	public HangingSignBlock(AbstractBlock.Settings settings, WoodType woodType) {
		super(settings.sounds(woodType.hangingSignSoundType()), woodType);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(ROTATION, Integer.valueOf(0)).with(ATTACHED, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
			ItemStack itemStack = player.getStackInHand(hand);
			SignText signText = signBlockEntity.getTextFacing(player);
			if (!signText.hasRunCommandClickEvent(player) && itemStack.getItem() instanceof BlockItem) {
				return ActionResult.PASS;
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.up()).isSideSolid(world, pos.up(), Direction.DOWN, SideShapeType.CENTER);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		FluidState fluidState = world.getFluidState(ctx.getBlockPos());
		BlockPos blockPos = ctx.getBlockPos().up();
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = blockState.isIn(BlockTags.ALL_HANGING_SIGNS);
		Direction direction = Direction.fromRotation((double)ctx.getPlayerYaw());
		boolean bl2 = !Block.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.DOWN) || ctx.shouldCancelInteraction();
		if (bl && !ctx.shouldCancelInteraction()) {
			if (blockState.contains(WallHangingSignBlock.FACING)) {
				Direction direction2 = blockState.get(WallHangingSignBlock.FACING);
				if (direction2.getAxis().test(direction)) {
					bl2 = false;
				}
			} else if (blockState.contains(ROTATION)) {
				Optional<Direction> optional = RotationPropertyHelper.toDirection((Integer)blockState.get(ROTATION));
				if (optional.isPresent() && ((Direction)optional.get()).getAxis().test(direction)) {
					bl2 = false;
				}
			}
		}

		int i = !bl2 ? RotationPropertyHelper.fromDirection(direction.getOpposite()) : RotationPropertyHelper.fromYaw(ctx.getPlayerYaw() + 180.0F);
		return this.getDefaultState()
			.with(ATTACHED, Boolean.valueOf(bl2))
			.with(ROTATION, Integer.valueOf(i))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		VoxelShape voxelShape = (VoxelShape)SHAPES_FOR_ROTATION.get(state.get(ROTATION));
		return voxelShape == null ? DEFAULT_SHAPE : voxelShape;
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return this.getOutlineShape(state, world, pos, ShapeContext.absent());
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.UP && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public float getRotationDegrees(BlockState state) {
		return RotationPropertyHelper.toDegrees((Integer)state.get(ROTATION));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, Integer.valueOf(rotation.rotate((Integer)state.get(ROTATION), 16)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, Integer.valueOf(mirror.mirror((Integer)state.get(ROTATION), 16)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ROTATION, ATTACHED, WATERLOGGED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new HangingSignBlockEntity(pos, state);
	}
}

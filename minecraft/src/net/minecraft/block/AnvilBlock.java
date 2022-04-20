package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class AnvilBlock extends FallingBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final VoxelShape X_STEP_SHAPE = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
	private static final VoxelShape X_STEM_SHAPE = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
	private static final VoxelShape X_FACE_SHAPE = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
	private static final VoxelShape Z_STEP_SHAPE = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
	private static final VoxelShape Z_STEM_SHAPE = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
	private static final VoxelShape Z_FACE_SHAPE = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
	private static final VoxelShape X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE);
	private static final VoxelShape Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE);
	private static final Text TITLE = Text.method_43471("container.repair");
	private static final float FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER = 2.0F;
	private static final int FALLING_BLOCK_ENTITY_MAX_DAMAGE = 40;

	public AnvilBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().rotateYClockwise());
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_ANVIL);
			return ActionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, inventory, player) -> new AnvilScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
		entity.setHurtEntities(2.0F, 40);
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
		if (!fallingBlockEntity.isSilent()) {
			world.syncWorldEvent(WorldEvents.ANVIL_LANDS, pos, 0);
		}
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
		if (!fallingBlockEntity.isSilent()) {
			world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
		}
	}

	@Override
	public DamageSource getDamageSource() {
		return DamageSource.ANVIL;
	}

	@Nullable
	public static BlockState getLandingState(BlockState fallingState) {
		if (fallingState.isOf(Blocks.ANVIL)) {
			return Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, (Direction)fallingState.get(FACING));
		} else {
			return fallingState.isOf(Blocks.CHIPPED_ANVIL) ? Blocks.DAMAGED_ANVIL.getDefaultState().with(FACING, (Direction)fallingState.get(FACING)) : null;
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return state.getMapColor(world, pos).color;
	}
}

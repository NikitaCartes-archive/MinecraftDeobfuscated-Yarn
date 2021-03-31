package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class EnderChestBlock extends AbstractChestBlock<EnderChestBlockEntity> implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	private static final Text CONTAINER_NAME = new TranslatableText("container.enderchest");

	protected EnderChestBlock(AbstractBlock.Settings settings) {
		super(settings, () -> BlockEntityType.ENDER_CHEST);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(
		BlockState state, World world, BlockPos pos, boolean ignoreBlocked
	) {
		return DoubleBlockProperties.PropertyRetriever::getFallback;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		EnderChestInventory enderChestInventory = player.getEnderChestInventory();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (enderChestInventory != null && blockEntity instanceof EnderChestBlockEntity) {
			BlockPos blockPos = pos.up();
			if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
				return ActionResult.success(world.isClient);
			} else if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				EnderChestBlockEntity enderChestBlockEntity = (EnderChestBlockEntity)blockEntity;
				enderChestInventory.setActiveBlockEntity(enderChestBlockEntity);
				player.openHandledScreen(
					new SimpleNamedScreenHandlerFactory(
						(syncId, inventory, playerx) -> GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, enderChestInventory), CONTAINER_NAME
					)
				);
				player.incrementStat(Stats.OPEN_ENDERCHEST);
				PiglinBrain.onGuardedBlockInteracted(player, true);
				return ActionResult.CONSUME;
			}
		} else {
			return ActionResult.success(world.isClient);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EnderChestBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? checkType(type, BlockEntityType.ENDER_CHEST, EnderChestBlockEntity::clientTick) : null;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		for (int i = 0; i < 3; i++) {
			int j = random.nextInt(2) * 2 - 1;
			int k = random.nextInt(2) * 2 - 1;
			double d = (double)pos.getX() + 0.5 + 0.25 * (double)j;
			double e = (double)((float)pos.getY() + random.nextFloat());
			double f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
			double g = (double)(random.nextFloat() * (float)j);
			double h = ((double)random.nextFloat() - 0.5) * 0.125;
			double l = (double)(random.nextFloat() * (float)k);
			world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, l);
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof EnderChestBlockEntity) {
			((EnderChestBlockEntity)blockEntity).onScheduledTick();
		}
	}
}

package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CandleCakeBlock extends AbstractCandleBlock {
	public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
	protected static final VoxelShape CAKE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);
	protected static final VoxelShape CANDLE_SHAPE = Block.createCuboidShape(7.0, 8.0, 7.0, 9.0, 14.0, 9.0);
	protected static final VoxelShape SHAPE = VoxelShapes.union(CAKE_SHAPE, CANDLE_SHAPE);
	private static final Map<Block, CandleCakeBlock> CANDLES_TO_CANDLE_CAKES = Maps.<Block, CandleCakeBlock>newHashMap();
	private static final Iterable<Vec3d> PARTICLE_OFFSETS = ImmutableList.<Vec3d>of(new Vec3d(0.5, 1.0, 0.5));

	protected CandleCakeBlock(Block candle, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LIT, Boolean.valueOf(false)));
		CANDLES_TO_CANDLE_CAKES.put(candle, this);
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
		return PARTICLE_OFFSETS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE)) {
			return ActionResult.PASS;
		} else if (isHittingCandle(hit) && player.getStackInHand(hand).isEmpty() && (Boolean)state.get(LIT)) {
			extinguish(state, world, pos);
			return ActionResult.success(world.isClient);
		} else {
			dropStacks(state, world, pos);
			return CakeBlock.tryEat(world, pos, Blocks.CAKE.getDefaultState(), player);
		}
	}

	private static boolean isHittingCandle(BlockHitResult hitResult) {
		return hitResult.getPos().y - (double)hitResult.getBlockPos().getY() > 0.5;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.CAKE);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return CakeBlock.DEFAULT_COMPARATOR_OUTPUT;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	public static BlockState getCandleCakeFromCandle(Block candle) {
		return ((CandleCakeBlock)CANDLES_TO_CANDLE_CAKES.get(candle)).getDefaultState();
	}

	public static boolean canBeLit(BlockState state) {
		return state.isIn(BlockTags.CANDLE_CAKES, statex -> statex.contains(LIT) && !(Boolean)state.get(LIT));
	}
}

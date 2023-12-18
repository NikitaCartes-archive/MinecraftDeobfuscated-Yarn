package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
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
	public static final MapCodec<CandleCakeBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Registries.BLOCK.getCodec().fieldOf("candle").forGetter(block -> block.candle), createSettingsCodec())
				.apply(instance, CandleCakeBlock::new)
	);
	public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
	protected static final float field_31052 = 1.0F;
	protected static final VoxelShape CAKE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);
	protected static final VoxelShape CANDLE_SHAPE = Block.createCuboidShape(7.0, 8.0, 7.0, 9.0, 14.0, 9.0);
	protected static final VoxelShape SHAPE = VoxelShapes.union(CAKE_SHAPE, CANDLE_SHAPE);
	private static final Map<CandleBlock, CandleCakeBlock> CANDLES_TO_CANDLE_CAKES = Maps.<CandleBlock, CandleCakeBlock>newHashMap();
	private static final Iterable<Vec3d> PARTICLE_OFFSETS = ImmutableList.<Vec3d>of(new Vec3d(0.5, 1.0, 0.5));
	private final CandleBlock candle;

	@Override
	public MapCodec<CandleCakeBlock> getCodec() {
		return CODEC;
	}

	protected CandleCakeBlock(Block candle, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LIT, Boolean.valueOf(false)));
		if (candle instanceof CandleBlock candleBlock) {
			CANDLES_TO_CANDLE_CAKES.put(candleBlock, this);
			this.candle = candleBlock;
		} else {
			throw new IllegalArgumentException("Expected block to be of " + CandleBlock.class + " was " + candle.getClass());
		}
	}

	@Override
	protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
		return PARTICLE_OFFSETS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE)) {
			return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		} else if (isHittingCandle(hit) && stack.isEmpty() && (Boolean)state.get(LIT)) {
			extinguish(player, state, world, pos);
			return ItemActionResult.success(world.isClient);
		} else {
			return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		ActionResult actionResult = CakeBlock.tryEat(world, pos, Blocks.CAKE.getDefaultState(), player);
		if (actionResult.isAccepted()) {
			dropStacks(state, world, pos);
		}

		return actionResult;
	}

	private static boolean isHittingCandle(BlockHitResult hitResult) {
		return hitResult.getPos().y - (double)hitResult.getBlockPos().getY() > 0.5;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.CAKE);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.down()).isSolid();
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

	public static BlockState getCandleCakeFromCandle(CandleBlock candle) {
		return ((CandleCakeBlock)CANDLES_TO_CANDLE_CAKES.get(candle)).getDefaultState();
	}

	public static boolean canBeLit(BlockState state) {
		return state.isIn(BlockTags.CANDLE_CAKES, statex -> statex.contains(LIT) && !(Boolean)state.get(LIT));
	}
}

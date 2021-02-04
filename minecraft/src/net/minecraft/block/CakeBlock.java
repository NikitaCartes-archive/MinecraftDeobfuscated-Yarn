package net.minecraft.block;

import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class CakeBlock extends Block {
	public static final IntProperty BITES = Properties.BITES;
	public static final int DEFAULT_COMPARATOR_OUTPUT = getComparatorOutput(0);
	protected static final VoxelShape[] BITES_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
	};

	protected CakeBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(BITES, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BITES_TO_SHAPE[state.get(BITES)];
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (itemStack.isIn(ItemTags.CANDLES) && (Integer)state.get(BITES) == 0) {
			Block block = Block.getBlockFromItem(item);
			if (block instanceof CandleBlock) {
				if (!player.isCreative()) {
					itemStack.decrement(1);
				}

				world.playSound(null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.setBlockState(pos, CandleCakeBlock.getCandleCakeFromCandle(block));
				world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
				return ActionResult.SUCCESS;
			}
		}

		if (world.isClient) {
			if (tryEat(world, pos, state, player).isAccepted()) {
				return ActionResult.SUCCESS;
			}

			if (itemStack.isEmpty()) {
				return ActionResult.CONSUME;
			}
		}

		return tryEat(world, pos, state, player);
	}

	protected static ActionResult tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!player.canConsume(false)) {
			return ActionResult.PASS;
		} else {
			player.incrementStat(Stats.EAT_CAKE_SLICE);
			player.getHungerManager().add(2, 0.1F);
			int i = (Integer)state.get(BITES);
			world.emitGameEvent(player, GameEvent.EAT, pos);
			if (i < 6) {
				world.setBlockState(pos, state.with(BITES, Integer.valueOf(i + 1)), 3);
			} else {
				world.removeBlock(pos, false);
				world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
			}

			return ActionResult.SUCCESS;
		}
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
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(BITES);
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return getComparatorOutput((Integer)state.get(BITES));
	}

	public static int getComparatorOutput(int bites) {
		return (7 - bites) * 2;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}

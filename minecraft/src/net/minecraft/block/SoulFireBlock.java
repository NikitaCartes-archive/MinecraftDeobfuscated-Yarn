package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SoulFireBlock extends AbstractFireBlock {
	public static final int field_38600 = 25;
	public static final IntProperty field_38601 = Properties.AGE_25;
	private final Supplier<BiMap<Block, Pair<Double, Block>>> field_38602 = Suppliers.memoize(
		() -> ImmutableBiMap.<Block, Pair<Double, Block>>builder()
				.put(Blocks.IRON_ORE, new Pair<>(0.1, Blocks.IRON_BLOCK))
				.put(Blocks.FIRE, new Pair<>(0.5, Blocks.SOUL_FIRE))
				.build()
	);

	public SoulFireBlock(AbstractBlock.Settings settings) {
		super(settings, 2.0F);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_38601, Integer.valueOf(0)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_38601);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.createAndScheduleBlockTick(pos, this, method_42955(world.random));
		int i = (Integer)state.get(field_38601);
		if (random.nextFloat() < 0.2F + (float)i * 0.03F) {
			world.removeBlock(pos, false);
		} else {
			int j = Math.min(25, i + random.nextInt(3) / 2);
			if (i != j) {
				state = state.with(field_38601, Integer.valueOf(j));
				world.setBlockState(pos, state, Block.NO_REDRAW);

				for (Direction direction : Direction.values()) {
					BlockPos blockPos = pos.offset(direction);
					Block block = world.getBlockState(blockPos).getBlock();
					if (((BiMap)this.field_38602.get()).containsKey(block)) {
						Pair<Double, Block> pair = (Pair<Double, Block>)((BiMap)this.field_38602.get()).get(block);
						if (world.random.nextDouble() > pair.getLeft()) {
							world.setBlockState(blockPos, pair.getRight().getDefaultState(), Block.NOTIFY_ALL);
						}
					}
				}
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.createAndScheduleBlockTick(pos, this, method_42955(world.random));
	}

	private static int method_42955(Random random) {
		return 30 + random.nextInt(10);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return isSoulBase(world.getBlockState(pos.down()));
	}

	public static boolean isSoulBase(BlockState state) {
		return true;
	}

	@Override
	protected boolean isFlammable(BlockState state) {
		return true;
	}
}

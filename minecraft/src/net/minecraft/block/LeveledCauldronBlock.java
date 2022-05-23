package net.minecraft.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class LeveledCauldronBlock extends AbstractCauldronBlock {
	public static final int field_31107 = 1;
	public static final int field_31108 = 3;
	public static final IntProperty LEVEL = Properties.LEVEL_3;
	private static final int field_31109 = 6;
	private static final double field_31110 = 3.0;
	public static final Predicate<Biome.Precipitation> RAIN_PREDICATE = precipitation -> precipitation == Biome.Precipitation.RAIN;
	public static final Predicate<Biome.Precipitation> SNOW_PREDICATE = precipitation -> precipitation == Biome.Precipitation.SNOW;
	private final Predicate<Biome.Precipitation> precipitationPredicate;

	public LeveledCauldronBlock(AbstractBlock.Settings settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CauldronBehavior> behaviorMap) {
		super(settings, behaviorMap);
		this.precipitationPredicate = precipitationPredicate;
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, Integer.valueOf(1)));
	}

	@Override
	public boolean isFull(BlockState state) {
		return (Integer)state.get(LEVEL) == 3;
	}

	@Override
	protected boolean canBeFilledByDripstone(Fluid fluid) {
		return fluid == Fluids.WATER && this.precipitationPredicate == RAIN_PREDICATE;
	}

	@Override
	protected double getFluidHeight(BlockState state) {
		return (6.0 + (double)((Integer)state.get(LEVEL)).intValue() * 3.0) / 16.0;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
			entity.extinguish();
			if (entity.canModifyAt(world, pos)) {
				this.onFireCollision(state, world, pos);
			}
		}
	}

	protected void onFireCollision(BlockState state, World world, BlockPos pos) {
		decrementFluidLevel(state, world, pos);
	}

	public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(LEVEL) - 1;
		BlockState blockState = i == 0 ? Blocks.CAULDRON.getDefaultState() : state.with(LEVEL, Integer.valueOf(i));
		world.setBlockState(pos, blockState);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
	}

	@Override
	public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
		if (CauldronBlock.canFillWithPrecipitation(world, precipitation) && (Integer)state.get(LEVEL) != 3 && this.precipitationPredicate.test(precipitation)) {
			BlockState blockState = state.cycle(LEVEL);
			world.setBlockState(pos, blockState);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
		}
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
		if (!this.isFull(state)) {
			BlockState blockState = state.with(LEVEL, Integer.valueOf((Integer)state.get(LEVEL) + 1));
			world.setBlockState(pos, blockState);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
			world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON, pos, 0);
		}
	}
}

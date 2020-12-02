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
import net.minecraft.world.biome.Biome;

public class LeveledCauldronBlock extends AbstractCauldronBlock {
	public static final IntProperty LEVEL = Properties.LEVEL_3;
	public static final Predicate<Biome.Precipitation> RAIN_PREDICATE = precipitation -> precipitation == Biome.Precipitation.RAIN;
	public static final Predicate<Biome.Precipitation> SNOW_PREDICATE = precipitation -> precipitation == Biome.Precipitation.SNOW;
	private final Predicate<Biome.Precipitation> precipitationPredicate;

	public LeveledCauldronBlock(AbstractBlock.Settings settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CauldronBehavior> behaviorMap) {
		super(settings, behaviorMap);
		this.precipitationPredicate = precipitationPredicate;
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, Integer.valueOf(1)));
	}

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
			decrementFluidLevel(state, world, pos);
		}
	}

	public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(LEVEL) - 1;
		world.setBlockState(pos, i == 0 ? Blocks.CAULDRON.getDefaultState() : state.with(LEVEL, Integer.valueOf(i)));
	}

	@Override
	public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
		if (CauldronBlock.canFillWithPrecipitation(world) && (Integer)state.get(LEVEL) != 3 && this.precipitationPredicate.test(precipitation)) {
			world.setBlockState(pos, state.cycle(LEVEL));
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
			world.setBlockState(pos, state.with(LEVEL, Integer.valueOf((Integer)state.get(LEVEL) + 1)));
			world.syncWorldEvent(1047, pos, 0);
		}
	}
}

package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

/**
 * Constructs a leveled cauldron block.
 */
public class LeveledCauldronBlock extends AbstractCauldronBlock {
	public static final MapCodec<LeveledCauldronBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(block -> block.precipitation),
					CauldronBehavior.CODEC.fieldOf("interactions").forGetter(block -> block.behaviorMap),
					createSettingsCodec()
				)
				.apply(instance, LeveledCauldronBlock::new)
	);
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 3;
	public static final IntProperty LEVEL = Properties.LEVEL_3;
	private static final int BASE_FLUID_HEIGHT = 6;
	private static final double FLUID_HEIGHT_PER_LEVEL = 3.0;
	private final Biome.Precipitation precipitation;

	@Override
	public MapCodec<LeveledCauldronBlock> getCodec() {
		return CODEC;
	}

	/**
	 * Constructs a leveled cauldron block.
	 */
	public LeveledCauldronBlock(Biome.Precipitation precipitation, CauldronBehavior.CauldronBehaviorMap behaviorMap, AbstractBlock.Settings settings) {
		super(settings, behaviorMap);
		this.precipitation = precipitation;
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, Integer.valueOf(1)));
	}

	@Override
	public boolean isFull(BlockState state) {
		return (Integer)state.get(LEVEL) == 3;
	}

	@Override
	protected boolean canBeFilledByDripstone(Fluid fluid) {
		return fluid == Fluids.WATER && this.precipitation == Biome.Precipitation.RAIN;
	}

	@Override
	protected double getFluidHeight(BlockState state) {
		return (6.0 + (double)((Integer)state.get(LEVEL)).intValue() * 3.0) / 16.0;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
			entity.extinguish();
			if (entity.canModifyAt(world, pos)) {
				this.onFireCollision(state, world, pos);
			}
		}
	}

	private void onFireCollision(BlockState state, World world, BlockPos pos) {
		if (this.precipitation == Biome.Precipitation.SNOW) {
			decrementFluidLevel(Blocks.WATER_CAULDRON.getDefaultState().with(LEVEL, (Integer)state.get(LEVEL)), world, pos);
		} else {
			decrementFluidLevel(state, world, pos);
		}
	}

	public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(LEVEL) - 1;
		BlockState blockState = i == 0 ? Blocks.CAULDRON.getDefaultState() : state.with(LEVEL, Integer.valueOf(i));
		world.setBlockState(pos, blockState);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
	}

	@Override
	public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
		if (CauldronBlock.canFillWithPrecipitation(world, precipitation) && (Integer)state.get(LEVEL) != 3 && precipitation == this.precipitation) {
			BlockState blockState = state.cycle(LEVEL);
			world.setBlockState(pos, blockState);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
		}
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
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

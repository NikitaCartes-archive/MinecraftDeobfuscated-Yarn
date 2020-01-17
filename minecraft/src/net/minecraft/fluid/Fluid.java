package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class Fluid {
	public static final IdList<FluidState> STATE_IDS = new IdList<>();
	protected final StateManager<Fluid, FluidState> stateManager;
	private FluidState defaultState;

	protected Fluid() {
		StateManager.Builder<Fluid, FluidState> builder = new StateManager.Builder<>(this);
		this.appendProperties(builder);
		this.stateManager = builder.build(FluidStateImpl::new);
		this.setDefaultState(this.stateManager.getDefaultState());
	}

	protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
	}

	public StateManager<Fluid, FluidState> getStateManager() {
		return this.stateManager;
	}

	protected final void setDefaultState(FluidState state) {
		this.defaultState = state;
	}

	public final FluidState getDefaultState() {
		return this.defaultState;
	}

	public abstract Item getBucketItem();

	@Environment(EnvType.CLIENT)
	protected void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
	}

	protected void onScheduledTick(World world, BlockPos pos, FluidState state) {
	}

	protected void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected ParticleEffect getParticle() {
		return null;
	}

	protected abstract boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction);

	protected abstract Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state);

	public abstract int getTickRate(WorldView world);

	protected boolean hasRandomTicks() {
		return false;
	}

	protected boolean isEmpty() {
		return false;
	}

	protected abstract float getBlastResistance();

	public abstract float getHeight(FluidState state, BlockView world, BlockPos pos);

	public abstract float getHeight(FluidState state);

	protected abstract BlockState toBlockState(FluidState state);

	public abstract boolean isStill(FluidState state);

	public abstract int getLevel(FluidState state);

	public boolean matchesType(Fluid fluid) {
		return fluid == this;
	}

	public boolean matches(Tag<Fluid> tag) {
		return tag.contains(this);
	}

	public abstract VoxelShape getShape(FluidState state, BlockView world, BlockPos pos);
}

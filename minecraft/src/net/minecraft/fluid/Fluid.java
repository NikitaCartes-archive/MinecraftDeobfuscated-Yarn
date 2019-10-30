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
	protected final StateManager<Fluid, FluidState> stateFactory;
	private FluidState defaultState;

	protected Fluid() {
		StateManager.Builder<Fluid, FluidState> builder = new StateManager.Builder<>(this);
		this.appendProperties(builder);
		this.stateFactory = builder.build(FluidStateImpl::new);
		this.setDefaultState(this.stateFactory.getDefaultState());
	}

	protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
	}

	public StateManager<Fluid, FluidState> getStateFactory() {
		return this.stateFactory;
	}

	protected final void setDefaultState(FluidState fluidState) {
		this.defaultState = fluidState;
	}

	public final FluidState getDefaultState() {
		return this.defaultState;
	}

	public abstract Item getBucketItem();

	@Environment(EnvType.CLIENT)
	protected void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	protected void onScheduledTick(World world, BlockPos pos, FluidState state) {
	}

	protected void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected ParticleEffect getParticle() {
		return null;
	}

	protected abstract boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction);

	protected abstract Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state);

	public abstract int getTickRate(WorldView worldView);

	protected boolean hasRandomTicks() {
		return false;
	}

	protected boolean isEmpty() {
		return false;
	}

	protected abstract float getBlastResistance();

	public abstract float getHeight(FluidState fluidState, BlockView blockView, BlockPos blockPos);

	public abstract float method_20784(FluidState fluidState);

	protected abstract BlockState toBlockState(FluidState fluidState);

	public abstract boolean isStill(FluidState fluidState);

	public abstract int getLevel(FluidState fluidState);

	public boolean matchesType(Fluid fluid) {
		return fluid == this;
	}

	public boolean matches(Tag<Fluid> tag) {
		return tag.contains(this);
	}

	public abstract VoxelShape getShape(FluidState fluidState, BlockView blockView, BlockPos blockPos);
}

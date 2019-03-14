package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.Tag;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class Fluid {
	public static final IdList<FluidState> STATE_IDS = new IdList<>();
	protected final StateFactory<Fluid, FluidState> stateFactory;
	private FluidState defaultState;

	protected Fluid() {
		StateFactory.Builder<Fluid, FluidState> builder = new StateFactory.Builder<>(this);
		this.appendProperties(builder);
		this.stateFactory = builder.build(FluidStateImpl::new);
		this.setDefaultState(this.stateFactory.getDefaultState());
	}

	protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
	}

	public StateFactory<Fluid, FluidState> getStateFactory() {
		return this.stateFactory;
	}

	protected final void setDefaultState(FluidState fluidState) {
		this.defaultState = fluidState;
	}

	public final FluidState getDefaultState() {
		return this.defaultState;
	}

	@Environment(EnvType.CLIENT)
	protected abstract BlockRenderLayer getRenderLayer();

	public abstract Item getBucketItem();

	@Environment(EnvType.CLIENT)
	protected void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	protected void onScheduledTick(World world, BlockPos blockPos, FluidState fluidState) {
	}

	protected void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected ParticleParameters getParticle() {
		return null;
	}

	protected abstract boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction);

	protected abstract Vec3d method_15782(BlockView blockView, BlockPos blockPos, FluidState fluidState);

	public abstract int getTickRate(ViewableWorld viewableWorld);

	protected boolean hasRandomTicks() {
		return false;
	}

	protected boolean isEmpty() {
		return false;
	}

	protected abstract float getBlastResistance();

	public abstract float getHeight(FluidState fluidState, BlockView blockView, BlockPos blockPos);

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

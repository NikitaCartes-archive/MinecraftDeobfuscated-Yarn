package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.item.Item;
import net.minecraft.particle.Particle;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.Tag;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
	protected void method_15776(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	protected void method_15778(World world, BlockPos blockPos, FluidState fluidState) {
	}

	protected void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected Particle getParticle() {
		return null;
	}

	protected abstract boolean method_15777(FluidState fluidState, Fluid fluid, Direction direction);

	protected abstract Vec3d method_15782(BlockView blockView, BlockPos blockPos, FluidState fluidState);

	public abstract int method_15789(ViewableWorld viewableWorld);

	protected boolean hasRandomTicks() {
		return false;
	}

	protected boolean isEmpty() {
		return false;
	}

	protected abstract float getBlastResistance();

	public abstract float method_15788(FluidState fluidState);

	protected abstract BlockState toBlockState(FluidState fluidState);

	public abstract boolean isStill(FluidState fluidState);

	public abstract int method_15779(FluidState fluidState);

	public boolean matchesType(Fluid fluid) {
		return fluid == this;
	}

	public boolean matches(Tag<Fluid> tag) {
		return tag.contains(this);
	}
}

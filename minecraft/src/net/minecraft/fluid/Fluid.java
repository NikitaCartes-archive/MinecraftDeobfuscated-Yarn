package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
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
	private FluidState field_15903;

	protected Fluid() {
		StateFactory.Builder<Fluid, FluidState> builder = new StateFactory.Builder<>(this);
		this.appendProperties(builder);
		this.stateFactory = builder.build(FluidStateImpl::new);
		this.method_15781(this.stateFactory.method_11664());
	}

	protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
	}

	public StateFactory<Fluid, FluidState> getStateFactory() {
		return this.stateFactory;
	}

	protected final void method_15781(FluidState fluidState) {
		this.field_15903 = fluidState;
	}

	public final FluidState method_15785() {
		return this.field_15903;
	}

	@Environment(EnvType.CLIENT)
	protected abstract BlockRenderLayer getRenderLayer();

	public abstract Item getBucketItem();

	@Environment(EnvType.CLIENT)
	protected void method_15776(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	protected void method_15778(World world, BlockPos blockPos, FluidState fluidState) {
	}

	protected void method_15792(World world, BlockPos blockPos, FluidState fluidState, Random random) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected ParticleEffect getParticle() {
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

	public abstract float method_15788(FluidState fluidState, BlockView blockView, BlockPos blockPos);

	public abstract float method_20784(FluidState fluidState);

	protected abstract BlockState method_15790(FluidState fluidState);

	public abstract boolean method_15793(FluidState fluidState);

	public abstract int method_15779(FluidState fluidState);

	public boolean matchesType(Fluid fluid) {
		return fluid == this;
	}

	public boolean matches(Tag<Fluid> tag) {
		return tag.contains(this);
	}

	public abstract VoxelShape method_17775(FluidState fluidState, BlockView blockView, BlockPos blockPos);
}

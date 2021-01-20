/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class Fluid {
    public static final IdList<FluidState> STATE_IDS = new IdList();
    protected final StateManager<Fluid, FluidState> stateManager;
    private FluidState defaultState;

    protected Fluid() {
        StateManager.Builder<Fluid, FluidState> builder = new StateManager.Builder<Fluid, FluidState>(this);
        this.appendProperties(builder);
        this.stateManager = builder.build(Fluid::getDefaultState, FluidState::new);
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

    @Environment(value=EnvType.CLIENT)
    protected void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
    }

    protected void onScheduledTick(World world, BlockPos pos, FluidState state) {
    }

    protected void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    protected ParticleEffect getParticle() {
        return null;
    }

    protected abstract boolean canBeReplacedWith(FluidState var1, BlockView var2, BlockPos var3, Fluid var4, Direction var5);

    protected abstract Vec3d getVelocity(BlockView var1, BlockPos var2, FluidState var3);

    public abstract int getTickRate(WorldView var1);

    protected boolean hasRandomTicks() {
        return false;
    }

    protected boolean isEmpty() {
        return false;
    }

    protected abstract float getBlastResistance();

    public abstract float getHeight(FluidState var1, BlockView var2, BlockPos var3);

    public abstract float getHeight(FluidState var1);

    protected abstract BlockState toBlockState(FluidState var1);

    public abstract boolean isStill(FluidState var1);

    public abstract int getLevel(FluidState var1);

    public boolean matchesType(Fluid fluid) {
        return fluid == this;
    }

    public boolean isIn(Tag<Fluid> tag) {
        return tag.contains(this);
    }

    public abstract VoxelShape getShape(FluidState var1, BlockView var2, BlockPos var3);

    /**
     * Returns the sound played when filling a bucket with this fluid.
     */
    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.empty();
    }
}


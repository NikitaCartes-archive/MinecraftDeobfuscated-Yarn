/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface FluidState
extends State<FluidState> {
    public Fluid getFluid();

    default public boolean isStill() {
        return this.getFluid().isStill(this);
    }

    default public boolean isEmpty() {
        return this.getFluid().isEmpty();
    }

    default public float getHeight(BlockView world, BlockPos pos) {
        return this.getFluid().getHeight(this, world, pos);
    }

    default public float getHeight() {
        return this.getFluid().getHeight(this);
    }

    default public int getLevel() {
        return this.getFluid().getLevel(this);
    }

    @Environment(value=EnvType.CLIENT)
    default public boolean method_15756(BlockView world, BlockPos pos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos blockPos = pos.add(i, 0, j);
                FluidState fluidState = world.getFluidState(blockPos);
                if (fluidState.getFluid().matchesType(this.getFluid()) || world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) continue;
                return true;
            }
        }
        return false;
    }

    default public void onScheduledTick(World world, BlockPos pos) {
        this.getFluid().onScheduledTick(world, pos, this);
    }

    @Environment(value=EnvType.CLIENT)
    default public void randomDisplayTick(World world, BlockPos pos, Random random) {
        this.getFluid().randomDisplayTick(world, pos, this, random);
    }

    default public boolean hasRandomTicks() {
        return this.getFluid().hasRandomTicks();
    }

    default public void onRandomTick(World world, BlockPos pos, Random random) {
        this.getFluid().onRandomTick(world, pos, this, random);
    }

    default public Vec3d getVelocity(BlockView world, BlockPos pos) {
        return this.getFluid().getVelocity(world, pos, this);
    }

    default public BlockState getBlockState() {
        return this.getFluid().toBlockState(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    default public ParticleEffect getParticle() {
        return this.getFluid().getParticle();
    }

    default public boolean matches(Tag<Fluid> tag) {
        return this.getFluid().isIn(tag);
    }

    default public float getBlastResistance() {
        return this.getFluid().getBlastResistance();
    }

    default public boolean method_15764(BlockView world, BlockPos blockPos, Fluid fluid, Direction direction) {
        return this.getFluid().canBeReplacedWith(this, world, blockPos, fluid, direction);
    }

    public static <T> Dynamic<T> serialize(DynamicOps<T> ops, FluidState state) {
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
        Object object = immutableMap.isEmpty() ? ops.createMap(ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.FLUID.getId(state.getFluid()).toString()))) : ops.createMap(ImmutableMap.of(ops.createString("Name"), ops.createString(Registry.FLUID.getId(state.getFluid()).toString()), ops.createString("Properties"), ops.createMap(immutableMap.entrySet().stream().map(entry -> Pair.of(ops.createString(((Property)entry.getKey()).getName()), ops.createString(State.nameValue((Property)entry.getKey(), (Comparable)entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        return new Dynamic<T>(ops, object);
    }

    public static <T> FluidState deserialize(Dynamic<T> dynamic2) {
        Fluid fluid = Registry.FLUID.get(new Identifier(dynamic2.getElement("Name").flatMap(dynamic2.getOps()::getStringValue).orElse("minecraft:empty")));
        Map<String, String> map = dynamic2.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        FluidState fluidState = fluid.getDefaultState();
        StateManager<Fluid, FluidState> stateManager = fluid.getStateManager();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            Property<?> property = stateManager.getProperty(string);
            if (property == null) continue;
            fluidState = State.tryRead(fluidState, property, string, dynamic2.toString(), entry.getValue());
        }
        return fluidState;
    }

    default public VoxelShape getShape(BlockView world, BlockPos pos) {
        return this.getFluid().getShape(this, world, pos);
    }
}


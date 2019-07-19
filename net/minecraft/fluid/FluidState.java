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
import net.minecraft.client.render.RenderLayer;
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

    default public float getHeight(BlockView blockView, BlockPos blockPos) {
        return this.getFluid().getHeight(this, blockView, blockPos);
    }

    default public float method_20785() {
        return this.getFluid().method_20784(this);
    }

    default public int getLevel() {
        return this.getFluid().getLevel(this);
    }

    @Environment(value=EnvType.CLIENT)
    default public boolean method_15756(BlockView blockView, BlockPos blockPos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos blockPos2 = blockPos.add(i, 0, j);
                FluidState fluidState = blockView.getFluidState(blockPos2);
                if (fluidState.getFluid().matchesType(this.getFluid()) || blockView.getBlockState(blockPos2).isFullOpaque(blockView, blockPos2)) continue;
                return true;
            }
        }
        return false;
    }

    default public void onScheduledTick(World world, BlockPos blockPos) {
        this.getFluid().onScheduledTick(world, blockPos, this);
    }

    @Environment(value=EnvType.CLIENT)
    default public void randomDisplayTick(World world, BlockPos blockPos, Random random) {
        this.getFluid().randomDisplayTick(world, blockPos, this, random);
    }

    default public boolean hasRandomTicks() {
        return this.getFluid().hasRandomTicks();
    }

    default public void onRandomTick(World world, BlockPos blockPos, Random random) {
        this.getFluid().onRandomTick(world, blockPos, this, random);
    }

    default public Vec3d getVelocity(BlockView blockView, BlockPos blockPos) {
        return this.getFluid().getVelocity(blockView, blockPos, this);
    }

    default public BlockState getBlockState() {
        return this.getFluid().toBlockState(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    default public ParticleEffect getParticle() {
        return this.getFluid().getParticle();
    }

    @Environment(value=EnvType.CLIENT)
    default public RenderLayer getRenderLayer() {
        return this.getFluid().getRenderLayer();
    }

    default public boolean matches(Tag<Fluid> tag) {
        return this.getFluid().matches(tag);
    }

    default public float getBlastResistance() {
        return this.getFluid().getBlastResistance();
    }

    default public boolean method_15764(BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return this.getFluid().canBeReplacedWith(this, blockView, blockPos, fluid, direction);
    }

    public static <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps, FluidState fluidState) {
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = fluidState.getEntries();
        Object object = immutableMap.isEmpty() ? dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getId(fluidState.getFluid()).toString()))) : dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getId(fluidState.getFluid()).toString()), dynamicOps.createString("Properties"), dynamicOps.createMap(immutableMap.entrySet().stream().map(entry -> Pair.of(dynamicOps.createString(((Property)entry.getKey()).getName()), dynamicOps.createString(State.nameValue((Property)entry.getKey(), (Comparable)entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        return new Dynamic<T>(dynamicOps, object);
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

    default public VoxelShape getShape(BlockView blockView, BlockPos blockPos) {
        return this.getFluid().getShape(this, blockView, blockPos);
    }
}


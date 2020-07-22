/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePoolElement {
    public static final Codec<StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT.dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
    @Nullable
    private volatile StructurePool.Projection projection;

    protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> method_28883() {
        return ((MapCodec)StructurePool.Projection.field_24956.fieldOf("projection")).forGetter(StructurePoolElement::getProjection);
    }

    protected StructurePoolElement(StructurePool.Projection projection) {
        this.projection = projection;
    }

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager var1, BlockPos var2, BlockRotation var3, Random var4);

    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    public abstract boolean generate(StructureManager var1, StructureWorldAccess var2, StructureAccessor var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, BlockRotation var7, BlockBox var8, Random var9, boolean var10);

    public abstract StructurePoolElementType<?> getType();

    public void method_16756(WorldAccess worldAccess, Structure.StructureBlockInfo structureBlockInfo, BlockPos blockPos, BlockRotation blockRotation, Random random, BlockBox blockBox) {
    }

    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        this.projection = projection;
        return this;
    }

    public StructurePool.Projection getProjection() {
        StructurePool.Projection projection = this.projection;
        if (projection == null) {
            throw new IllegalStateException();
        }
        return projection;
    }

    public int getGroundLevelDelta() {
        return 1;
    }

    public static Function<StructurePool.Projection, EmptyPoolElement> method_30438() {
        return projection -> EmptyPoolElement.INSTANCE;
    }

    public static Function<StructurePool.Projection, LegacySinglePoolElement> method_30425(String string) {
        return projection -> new LegacySinglePoolElement(Either.left(new Identifier(string)), ImmutableList::of, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, LegacySinglePoolElement> method_30426(String string, ImmutableList<StructureProcessor> immutableList) {
        return projection -> new LegacySinglePoolElement(Either.left(new Identifier(string)), () -> immutableList, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, SinglePoolElement> method_30434(String string) {
        return projection -> new SinglePoolElement(Either.left(new Identifier(string)), ImmutableList::of, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, SinglePoolElement> method_30435(String string, ImmutableList<StructureProcessor> immutableList) {
        return projection -> new SinglePoolElement(Either.left(new Identifier(string)), () -> immutableList, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, FeaturePoolElement> method_30421(ConfiguredFeature<?, ?> configuredFeature) {
        return projection -> new FeaturePoolElement(() -> configuredFeature, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, ListPoolElement> method_30429(List<Function<StructurePool.Projection, ? extends StructurePoolElement>> list) {
        return projection -> new ListPoolElement(list.stream().map(function -> (StructurePoolElement)function.apply(projection)).collect(Collectors.toList()), (StructurePool.Projection)projection);
    }
}


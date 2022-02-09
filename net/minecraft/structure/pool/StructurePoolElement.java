/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

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
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePoolElement {
    public static final Codec<StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT.getCodec().dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
    @Nullable
    private volatile StructurePool.Projection projection;

    protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> method_28883() {
        return ((MapCodec)StructurePool.Projection.CODEC.fieldOf("projection")).forGetter(StructurePoolElement::getProjection);
    }

    protected StructurePoolElement(StructurePool.Projection projection) {
        this.projection = projection;
    }

    public abstract Vec3i getStart(StructureManager var1, BlockRotation var2);

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager var1, BlockPos var2, BlockRotation var3, Random var4);

    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    public abstract boolean generate(StructureManager var1, StructureWorldAccess var2, StructureAccessor var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, BlockRotation var7, BlockBox var8, Random var9, boolean var10);

    public abstract StructurePoolElementType<?> getType();

    public void method_16756(WorldAccess world, Structure.StructureBlockInfo structureBlockInfo, BlockPos pos, BlockRotation rotation, Random random, BlockBox box) {
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

    public static Function<StructurePool.Projection, EmptyPoolElement> ofEmpty() {
        return projection -> EmptyPoolElement.INSTANCE;
    }

    public static Function<StructurePool.Projection, LegacySinglePoolElement> ofLegacySingle(String id) {
        return projection -> new LegacySinglePoolElement(Either.left(new Identifier(id)), StructureProcessorLists.EMPTY, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, LegacySinglePoolElement> ofProcessedLegacySingle(String id, RegistryEntry<StructureProcessorList> registryEntry) {
        return projection -> new LegacySinglePoolElement(Either.left(new Identifier(id)), registryEntry, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, SinglePoolElement> ofSingle(String id) {
        return projection -> new SinglePoolElement(Either.left(new Identifier(id)), StructureProcessorLists.EMPTY, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, SinglePoolElement> ofProcessedSingle(String id, RegistryEntry<StructureProcessorList> registryEntry) {
        return projection -> new SinglePoolElement(Either.left(new Identifier(id)), registryEntry, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, FeaturePoolElement> ofFeature(RegistryEntry<PlacedFeature> registryEntry) {
        return projection -> new FeaturePoolElement(registryEntry, (StructurePool.Projection)projection);
    }

    public static Function<StructurePool.Projection, ListPoolElement> ofList(List<Function<StructurePool.Projection, ? extends StructurePoolElement>> list) {
        return projection -> new ListPoolElement(list.stream().map(function -> (StructurePoolElement)function.apply(projection)).collect(Collectors.toList()), (StructurePool.Projection)projection);
    }
}


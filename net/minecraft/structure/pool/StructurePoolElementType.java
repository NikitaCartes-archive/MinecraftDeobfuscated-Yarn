/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;

public interface StructurePoolElementType<P extends StructurePoolElement> {
    public static final StructurePoolElementType<SinglePoolElement> SINGLE_POOL_ELEMENT = StructurePoolElementType.register("single_pool_element", SinglePoolElement.CODEC);
    public static final StructurePoolElementType<ListPoolElement> LIST_POOL_ELEMENT = StructurePoolElementType.register("list_pool_element", ListPoolElement.CODEC);
    public static final StructurePoolElementType<FeaturePoolElement> FEATURE_POOL_ELEMENT = StructurePoolElementType.register("feature_pool_element", FeaturePoolElement.CODEC);
    public static final StructurePoolElementType<EmptyPoolElement> EMPTY_POOL_ELEMENT = StructurePoolElementType.register("empty_pool_element", EmptyPoolElement.CODEC);
    public static final StructurePoolElementType<LegacySinglePoolElement> LEGACY_SINGLE_POOL_ELEMENT = StructurePoolElementType.register("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

    public Codec<P> codec();

    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.STRUCTURE_POOL_ELEMENT, id, () -> codec);
    }
}


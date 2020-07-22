/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElementType<P extends StructurePoolElement> {
    public static final StructurePoolElementType<SinglePoolElement> SINGLE_POOL_ELEMENT = StructurePoolElementType.method_28885("single_pool_element", SinglePoolElement.field_24952);
    public static final StructurePoolElementType<ListPoolElement> LIST_POOL_ELEMENT = StructurePoolElementType.method_28885("list_pool_element", ListPoolElement.CODEC);
    public static final StructurePoolElementType<FeaturePoolElement> FEATURE_POOL_ELEMENT = StructurePoolElementType.method_28885("feature_pool_element", FeaturePoolElement.CODEC);
    public static final StructurePoolElementType<EmptyPoolElement> EMPTY_POOL_ELEMENT = StructurePoolElementType.method_28885("empty_pool_element", EmptyPoolElement.CODEC);
    public static final StructurePoolElementType<LegacySinglePoolElement> LEGACY_SINGLE_POOL_ELEMENT = StructurePoolElementType.method_28885("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

    public Codec<P> codec();

    public static <P extends StructurePoolElement> StructurePoolElementType<P> method_28885(String string, Codec<P> codec) {
        return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, () -> codec);
    }
}


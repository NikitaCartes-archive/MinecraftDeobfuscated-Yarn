/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElementType
extends DynamicDeserializer<StructurePoolElement> {
    public static final StructurePoolElementType SINGLE_POOL_ELEMENT = StructurePoolElementType.register("single_pool_element", SinglePoolElement::new);
    public static final StructurePoolElementType LIST_POOL_ELEMENT = StructurePoolElementType.register("list_pool_element", ListPoolElement::new);
    public static final StructurePoolElementType FEATURE_POOL_ELEMENT = StructurePoolElementType.register("feature_pool_element", FeaturePoolElement::new);
    public static final StructurePoolElementType EMPTY_POOL_ELEMENT = StructurePoolElementType.register("empty_pool_element", dynamic -> EmptyPoolElement.INSTANCE);
    public static final StructurePoolElementType LEGACY_SINGLE_POOL_ELEMENT = StructurePoolElementType.register("legacy_single_pool_element", LegacySinglePoolElement::new);

    public static StructurePoolElementType register(String string, StructurePoolElementType structurePoolElementType) {
        return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, structurePoolElementType);
    }
}


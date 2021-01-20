/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.BastionBridgeData;
import net.minecraft.structure.BastionData;
import net.minecraft.structure.BastionTreasureData;
import net.minecraft.structure.BastionUnitsData;
import net.minecraft.structure.HoglinStableData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class BastionRemnantGenerator {
    public static final StructurePool STRUCTURE_POOLS = StructurePools.register(new StructurePool(new Identifier("bastion/starts"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/air_base", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/air_base", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(StructurePoolElement.method_30435("bastion/treasure/big_air_full", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance_base", StructureProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), StructurePool.Projection.RIGID));

    public static void init() {
        BastionUnitsData.init();
        HoglinStableData.init();
        BastionTreasureData.init();
        BastionBridgeData.init();
        BastionData.init();
    }
}


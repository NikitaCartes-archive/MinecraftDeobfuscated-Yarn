/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.class_4538;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class NopStructureProcessor
extends StructureProcessor {
    public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

    private NopStructureProcessor() {
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(class_4538 arg, BlockPos blockPos, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.NOP;
    }

    @Override
    protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.emptyMap());
    }
}


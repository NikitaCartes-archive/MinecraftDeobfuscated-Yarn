/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class StructureProcessor {
    @Nullable
    public abstract Structure.StructureBlockInfo process(WorldView var1, BlockPos var2, Structure.StructureBlockInfo var3, Structure.StructureBlockInfo var4, StructurePlacementData var5);

    protected abstract StructureProcessorType getType();

    protected abstract <T> Dynamic<T> rawToDynamic(DynamicOps<T> var1);

    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.mergeInto(this.rawToDynamic(dynamicOps).getValue(), dynamicOps.createString("processor_type"), dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getType()).toString())));
    }
}


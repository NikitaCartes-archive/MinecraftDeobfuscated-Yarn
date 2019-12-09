/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType
extends DynamicDeserializer<StructureProcessor> {
    public static final StructureProcessorType BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreStructureProcessor::new);
    public static final StructureProcessorType BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotStructureProcessor::new);
    public static final StructureProcessorType GRAVITY = StructureProcessorType.register("gravity", GravityStructureProcessor::new);
    public static final StructureProcessorType JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", dynamic -> JigsawReplacementStructureProcessor.INSTANCE);
    public static final StructureProcessorType RULE = StructureProcessorType.register("rule", RuleStructureProcessor::new);
    public static final StructureProcessorType NOP = StructureProcessorType.register("nop", dynamic -> NopStructureProcessor.INSTANCE);

    public static StructureProcessorType register(String id, StructureProcessorType processor) {
        return Registry.register(Registry.STRUCTURE_PROCESSOR, id, processor);
    }
}


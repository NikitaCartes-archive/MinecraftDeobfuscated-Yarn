/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.class_5399;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType<P extends StructureProcessor> {
    public static final StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreStructureProcessor.field_24998);
    public static final StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotStructureProcessor.field_25000);
    public static final StructureProcessorType<GravityStructureProcessor> GRAVITY = StructureProcessorType.register("gravity", GravityStructureProcessor.CODEC);
    public static final StructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", JigsawReplacementStructureProcessor.field_25003);
    public static final StructureProcessorType<RuleStructureProcessor> RULE = StructureProcessorType.register("rule", RuleStructureProcessor.field_25011);
    public static final StructureProcessorType<NopStructureProcessor> NOP = StructureProcessorType.register("nop", NopStructureProcessor.field_25005);
    public static final StructureProcessorType<BlockAgeStructureProcessor> BLOCK_AGE = StructureProcessorType.register("block_age", BlockAgeStructureProcessor.field_24997);
    public static final StructureProcessorType<BlackstoneReplacementStructureProcessor> BLACKSTONE_REPLACE = StructureProcessorType.register("blackstone_replace", BlackstoneReplacementStructureProcessor.field_24996);
    public static final StructureProcessorType<class_5399> LAVA_SUBMERGED_BLOCK = StructureProcessorType.register("lava_submerged_block", class_5399.field_25618);
    public static final Codec<StructureProcessor> field_25013 = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);

    public Codec<P> codec();

    public static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
    }
}


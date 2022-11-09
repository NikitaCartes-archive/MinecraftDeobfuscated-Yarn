/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.LavaSubmergedBlockStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.ProtectedBlocksStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;

public interface StructureProcessorType<P extends StructureProcessor> {
    public static final StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
    public static final StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotStructureProcessor.CODEC);
    public static final StructureProcessorType<GravityStructureProcessor> GRAVITY = StructureProcessorType.register("gravity", GravityStructureProcessor.CODEC);
    public static final StructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", JigsawReplacementStructureProcessor.CODEC);
    public static final StructureProcessorType<RuleStructureProcessor> RULE = StructureProcessorType.register("rule", RuleStructureProcessor.CODEC);
    public static final StructureProcessorType<NopStructureProcessor> NOP = StructureProcessorType.register("nop", NopStructureProcessor.CODEC);
    public static final StructureProcessorType<BlockAgeStructureProcessor> BLOCK_AGE = StructureProcessorType.register("block_age", BlockAgeStructureProcessor.CODEC);
    public static final StructureProcessorType<BlackstoneReplacementStructureProcessor> BLACKSTONE_REPLACE = StructureProcessorType.register("blackstone_replace", BlackstoneReplacementStructureProcessor.CODEC);
    public static final StructureProcessorType<LavaSubmergedBlockStructureProcessor> LAVA_SUBMERGED_BLOCK = StructureProcessorType.register("lava_submerged_block", LavaSubmergedBlockStructureProcessor.CODEC);
    public static final StructureProcessorType<ProtectedBlocksStructureProcessor> PROTECTED_BLOCKS = StructureProcessorType.register("protected_blocks", ProtectedBlocksStructureProcessor.CODEC);
    public static final Codec<StructureProcessor> CODEC = Registries.STRUCTURE_PROCESSOR.getCodec().dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
    public static final Codec<StructureProcessorList> LIST_CODEC = CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::getList);
    public static final Codec<StructureProcessorList> PROCESSORS_CODEC = Codec.either(((MapCodec)LIST_CODEC.fieldOf("processors")).codec(), LIST_CODEC).xmap(either -> either.map(structureProcessorList -> structureProcessorList, structureProcessorList -> structureProcessorList), Either::left);
    public static final Codec<RegistryEntry<StructureProcessorList>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PROCESSOR_LIST_WORLDGEN, PROCESSORS_CODEC);

    public Codec<P> codec();

    public static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.STRUCTURE_PROCESSOR, id, () -> codec);
    }
}


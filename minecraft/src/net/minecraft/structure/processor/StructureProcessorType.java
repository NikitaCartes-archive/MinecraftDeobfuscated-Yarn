package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType<P extends StructureProcessor> {
	StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = register("block_ignore", BlockIgnoreStructureProcessor.field_24998);
	StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = register("block_rot", BlockRotStructureProcessor.field_25000);
	StructureProcessorType<GravityStructureProcessor> GRAVITY = register("gravity", GravityStructureProcessor.CODEC);
	StructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = register(
		"jigsaw_replacement", JigsawReplacementStructureProcessor.field_25003
	);
	StructureProcessorType<RuleStructureProcessor> RULE = register("rule", RuleStructureProcessor.field_25011);
	StructureProcessorType<NopStructureProcessor> NOP = register("nop", NopStructureProcessor.field_25005);
	StructureProcessorType<BlockAgeStructureProcessor> BLOCK_AGE = register("block_age", BlockAgeStructureProcessor.field_24997);
	StructureProcessorType<BlackstoneReplacementStructureProcessor> BLACKSTONE_REPLACE = register(
		"blackstone_replace", BlackstoneReplacementStructureProcessor.field_24996
	);
	Codec<StructureProcessor> field_25013 = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);

	Codec<P> codec();

	static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
	}
}

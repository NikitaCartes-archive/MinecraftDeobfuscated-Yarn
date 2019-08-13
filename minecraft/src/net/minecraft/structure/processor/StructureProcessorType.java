package net.minecraft.structure.processor;

import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType extends DynamicDeserializer<StructureProcessor> {
	StructureProcessorType field_16986 = register("block_ignore", BlockIgnoreStructureProcessor::new);
	StructureProcessorType field_16988 = register("block_rot", BlockRotStructureProcessor::new);
	StructureProcessorType field_16989 = register("gravity", GravityStructureProcessor::new);
	StructureProcessorType field_16991 = register("jigsaw_replacement", dynamic -> JigsawReplacementStructureProcessor.INSTANCE);
	StructureProcessorType field_16990 = register("rule", RuleStructureProcessor::new);
	StructureProcessorType field_16987 = register("nop", dynamic -> NopStructureProcessor.INSTANCE);

	static StructureProcessorType register(String string, StructureProcessorType structureProcessorType) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, string, structureProcessorType);
	}
}

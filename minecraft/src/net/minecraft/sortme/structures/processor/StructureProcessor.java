package net.minecraft.sortme.structures.processor;

import net.minecraft.class_3817;
import net.minecraft.util.registry.Registry;

public interface StructureProcessor extends class_3817<AbstractStructureProcessor> {
	StructureProcessor field_16986 = register("block_ignore", BlockIgnoreStructureProcessor::new);
	StructureProcessor field_16988 = register("block_rot", BlockRotStructureProcessor::new);
	StructureProcessor field_16989 = register("gravity", GravityStructureProcessor::new);
	StructureProcessor field_16991 = register("jigsaw_replacement", dynamic -> JigsawReplacementStructureProcessor.INSTANCE);
	StructureProcessor field_16990 = register("rule", RuleStructureProcessor::new);
	StructureProcessor field_16987 = register("nop", dynamic -> NopStructureProcessor.INSTANCE);

	static StructureProcessor register(String string, StructureProcessor structureProcessor) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, string, structureProcessor);
	}
}

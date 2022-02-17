package net.minecraft.structure.processor;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType<P extends StructureProcessor> {
	StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
	StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = register("block_rot", BlockRotStructureProcessor.CODEC);
	StructureProcessorType<ReplaceInTagStructureProcessor> REPLACE_IN_TAG = register("replace_in_tag", ReplaceInTagStructureProcessor.CODEC);
	StructureProcessorType<GravityStructureProcessor> GRAVITY = register("gravity", GravityStructureProcessor.CODEC);
	StructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = register("jigsaw_replacement", JigsawReplacementStructureProcessor.CODEC);
	StructureProcessorType<RuleStructureProcessor> RULE = register("rule", RuleStructureProcessor.CODEC);
	StructureProcessorType<NopStructureProcessor> NOP = register("nop", NopStructureProcessor.CODEC);
	StructureProcessorType<BlockAgeStructureProcessor> BLOCK_AGE = register("block_age", BlockAgeStructureProcessor.CODEC);
	StructureProcessorType<BlackstoneReplacementStructureProcessor> BLACKSTONE_REPLACE = register(
		"blackstone_replace", BlackstoneReplacementStructureProcessor.CODEC
	);
	StructureProcessorType<LavaSubmergedBlockStructureProcessor> LAVA_SUBMERGED_BLOCK = register(
		"lava_submerged_block", LavaSubmergedBlockStructureProcessor.CODEC
	);
	StructureProcessorType<ProtectedBlocksStructureProcessor> PROTECTED_BLOCKS = register("protected_blocks", ProtectedBlocksStructureProcessor.CODEC);
	Codec<StructureProcessor> CODEC = Registry.STRUCTURE_PROCESSOR
		.getCodec()
		.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
	Codec<StructureProcessorList> field_26663 = CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::getList);
	Codec<StructureProcessorList> field_25876 = Codec.either(field_26663.fieldOf("processors").codec(), field_26663)
		.xmap(either -> either.map(structureProcessorList -> structureProcessorList, structureProcessorList -> structureProcessorList), Either::left);
	Codec<Supplier<StructureProcessorList>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.STRUCTURE_PROCESSOR_LIST_KEY, field_25876);

	Codec<P> codec();

	static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
	}
}

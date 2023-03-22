package net.minecraft.structure.processor;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;

public interface StructureProcessorType<P extends StructureProcessor> {
	Codec<StructureProcessor> CODEC = Registries.STRUCTURE_PROCESSOR
		.getCodec()
		.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
	Codec<StructureProcessorList> LIST_CODEC = CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::getList);
	Codec<StructureProcessorList> PROCESSORS_CODEC = Codec.either(LIST_CODEC.fieldOf("processors").codec(), LIST_CODEC)
		.xmap(either -> either.map(structureProcessorList -> structureProcessorList, structureProcessorList -> structureProcessorList), Either::left);
	Codec<RegistryEntry<StructureProcessorList>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PROCESSOR_LIST, PROCESSORS_CODEC);
	StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
	StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = register("block_rot", BlockRotStructureProcessor.CODEC);
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
	StructureProcessorType<CappedStructureProcessor> CAPPED = register("capped", CappedStructureProcessor.CODEC);

	Codec<P> codec();

	static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.STRUCTURE_PROCESSOR, id, () -> codec);
	}
}

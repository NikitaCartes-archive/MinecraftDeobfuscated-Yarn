package net.minecraft.structure.processor;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType<P extends StructureProcessor> {
	StructureProcessorType<BlockIgnoreStructureProcessor> field_16986 = register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
	StructureProcessorType<BlockRotStructureProcessor> field_16988 = register("block_rot", BlockRotStructureProcessor.CODEC);
	StructureProcessorType<GravityStructureProcessor> field_16989 = register("gravity", GravityStructureProcessor.CODEC);
	StructureProcessorType<JigsawReplacementStructureProcessor> field_16991 = register("jigsaw_replacement", JigsawReplacementStructureProcessor.CODEC);
	StructureProcessorType<RuleStructureProcessor> field_16990 = register("rule", RuleStructureProcessor.CODEC);
	StructureProcessorType<NopStructureProcessor> field_16987 = register("nop", NopStructureProcessor.CODEC);
	StructureProcessorType<BlockAgeStructureProcessor> field_24044 = register("block_age", BlockAgeStructureProcessor.CODEC);
	StructureProcessorType<BlackstoneReplacementStructureProcessor> field_24045 = register("blackstone_replace", BlackstoneReplacementStructureProcessor.CODEC);
	StructureProcessorType<LavaSubmergedBlockStructureProcessor> field_25620 = register("lava_submerged_block", LavaSubmergedBlockStructureProcessor.CODEC);
	Codec<StructureProcessor> CODEC = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
	Codec<StructureProcessorList> field_26663 = CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::getList);
	Codec<StructureProcessorList> field_25876 = Codec.either(field_26663.fieldOf("processors").codec(), field_26663)
		.xmap(either -> either.map(structureProcessorList -> structureProcessorList, structureProcessorList -> structureProcessorList), Either::left);
	Codec<Supplier<StructureProcessorList>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.field_25916, field_25876);

	Codec<P> codec();

	static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
	}
}

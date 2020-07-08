package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;

public interface StructureProcessorType<P extends StructureProcessor> {
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
	Codec<StructureProcessor> CODEC = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
	MapCodec<ImmutableList<StructureProcessor>> field_25876 = CODEC.listOf()
		.<ImmutableList<StructureProcessor>>xmap(ImmutableList::copyOf, Function.identity())
		.fieldOf("processors");
	Codec<Supplier<ImmutableList<StructureProcessor>>> field_25877 = RegistryElementCodec.of(Registry.PROCESSOR_LIST_WORLDGEN, field_25876);

	Codec<P> codec();

	static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
	}
}

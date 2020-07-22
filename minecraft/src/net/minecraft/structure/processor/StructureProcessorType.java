package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
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
	MapCodec<ImmutableList<StructureProcessor>> PROCESSORS = method_30652("processors", CODEC.listOf().xmap(ImmutableList::copyOf, Function.identity()));
	Codec<Supplier<ImmutableList<StructureProcessor>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.PROCESSOR_LIST_WORLDGEN, PROCESSORS);

	Codec<P> codec();

	static <E> MapCodec<E> method_30652(String string, Codec<E> codec) {
		final MapCodec<E> mapCodec = codec.fieldOf(string);
		return new MapCodec<E>() {
			@Override
			public <O> Stream<O> keys(DynamicOps<O> dynamicOps) {
				return mapCodec.keys(dynamicOps);
			}

			@Override
			public <O> DataResult<E> decode(DynamicOps<O> dynamicOps, MapLike<O> mapLike) {
				return mapCodec.decode(dynamicOps, mapLike);
			}

			@Override
			public <O> RecordBuilder<O> encode(E object, DynamicOps<O> dynamicOps, RecordBuilder<O> recordBuilder) {
				return mapCodec.encode(object, dynamicOps, recordBuilder);
			}

			@Override
			public Codec<E> codec() {
				final Codec<E> codec = super.codec();
				return new Codec<E>() {
					@Override
					public <O> DataResult<Pair<E, O>> decode(DynamicOps<O> dynamicOps, O object) {
						if (dynamicOps.compressMaps()) {
							return codec.decode(dynamicOps, object);
						} else {
							DataResult<MapLike<O>> dataResult = dynamicOps.getMap(object);
							MapLike<O> mapLike = dataResult.get()
								.map(mapLikex -> mapLikex, partialResult -> MapLike.forMap(ImmutableMap.of(dynamicOps.createString(string), object), dynamicOps));
							return mapCodec.decode(dynamicOps, mapLike).map(object2 -> Pair.of(object2, object));
						}
					}

					@Override
					public <O> DataResult<O> encode(E object, DynamicOps<O> dynamicOps, O object2) {
						return codec.encode(object, dynamicOps, object2);
					}
				};
			}
		};
	}

	static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, id, () -> codec);
	}
}

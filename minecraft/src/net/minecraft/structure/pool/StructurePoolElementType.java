package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface StructurePoolElementType<P extends StructurePoolElement> {
	StructurePoolElementType<SinglePoolElement> SINGLE_POOL_ELEMENT = register("single_pool_element", SinglePoolElement.CODEC);
	StructurePoolElementType<ListPoolElement> LIST_POOL_ELEMENT = register("list_pool_element", ListPoolElement.CODEC);
	StructurePoolElementType<FeaturePoolElement> FEATURE_POOL_ELEMENT = register("feature_pool_element", FeaturePoolElement.CODEC);
	StructurePoolElementType<EmptyPoolElement> EMPTY_POOL_ELEMENT = register("empty_pool_element", EmptyPoolElement.CODEC);
	StructurePoolElementType<LegacySinglePoolElement> LEGACY_SINGLE_POOL_ELEMENT = register("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

	Codec<P> codec();

	static <P extends StructurePoolElement> StructurePoolElementType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.STRUCTURE_POOL_ELEMENT, id, () -> codec);
	}
}

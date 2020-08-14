package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElementType<P extends StructurePoolElement> {
	StructurePoolElementType<SinglePoolElement> SINGLE_POOL_ELEMENT = method_28885("single_pool_element", SinglePoolElement.field_24952);
	StructurePoolElementType<ListPoolElement> LIST_POOL_ELEMENT = method_28885("list_pool_element", ListPoolElement.CODEC);
	StructurePoolElementType<FeaturePoolElement> FEATURE_POOL_ELEMENT = method_28885("feature_pool_element", FeaturePoolElement.CODEC);
	StructurePoolElementType<EmptyPoolElement> EMPTY_POOL_ELEMENT = method_28885("empty_pool_element", EmptyPoolElement.CODEC);
	StructurePoolElementType<LegacySinglePoolElement> LEGACY_SINGLE_POOL_ELEMENT = method_28885("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

	Codec<P> codec();

	static <P extends StructurePoolElement> StructurePoolElementType<P> method_28885(String string, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, () -> codec);
	}
}

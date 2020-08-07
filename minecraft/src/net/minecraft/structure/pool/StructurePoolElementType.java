package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElementType<P extends StructurePoolElement> {
	StructurePoolElementType<SinglePoolElement> field_16973 = method_28885("single_pool_element", SinglePoolElement.field_24952);
	StructurePoolElementType<ListPoolElement> field_16974 = method_28885("list_pool_element", ListPoolElement.CODEC);
	StructurePoolElementType<FeaturePoolElement> field_16971 = method_28885("feature_pool_element", FeaturePoolElement.CODEC);
	StructurePoolElementType<EmptyPoolElement> field_16972 = method_28885("empty_pool_element", EmptyPoolElement.CODEC);
	StructurePoolElementType<LegacySinglePoolElement> field_24016 = method_28885("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

	Codec<P> codec();

	static <P extends StructurePoolElement> StructurePoolElementType<P> method_28885(String string, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, () -> codec);
	}
}

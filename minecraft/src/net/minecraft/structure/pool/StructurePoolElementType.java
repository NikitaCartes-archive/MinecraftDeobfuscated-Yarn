package net.minecraft.structure.pool;

import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElementType extends DynamicDeserializer<StructurePoolElement> {
	StructurePoolElementType field_16973 = register("single_pool_element", SinglePoolElement::new);
	StructurePoolElementType field_16974 = register("list_pool_element", ListPoolElement::new);
	StructurePoolElementType field_16971 = register("feature_pool_element", FeaturePoolElement::new);
	StructurePoolElementType field_16972 = register("empty_pool_element", dynamic -> EmptyPoolElement.INSTANCE);

	static StructurePoolElementType register(String string, StructurePoolElementType structurePoolElementType) {
		return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, structurePoolElementType);
	}
}

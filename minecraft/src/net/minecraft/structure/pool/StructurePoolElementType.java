package net.minecraft.structure.pool;

import net.minecraft.class_3817;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElementType extends class_3817<StructurePoolElement> {
	StructurePoolElementType SINGLE_POOL_ELEMENT = register("single_pool_element", SinglePoolElement::new);
	StructurePoolElementType LIST_POOL_ELEMENT = register("list_pool_element", ListPoolElement::new);
	StructurePoolElementType FEATURE_POOL_ELEMENT = register("feature_pool_element", FeaturePoolElement::new);
	StructurePoolElementType EMPTY_POOL_ELEMENT = register("empty_pool_element", dynamic -> EmptyPoolElement.INSTANCE);

	static StructurePoolElementType register(String string, StructurePoolElementType structurePoolElementType) {
		return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, structurePoolElementType);
	}
}

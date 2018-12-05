package net.minecraft.sortme;

import net.minecraft.class_3776;
import net.minecraft.class_3777;
import net.minecraft.class_3781;
import net.minecraft.class_3782;
import net.minecraft.class_3784;
import net.minecraft.class_3817;
import net.minecraft.util.registry.Registry;

public interface StructurePoolElement extends class_3817<class_3784> {
	StructurePoolElement field_16973 = register("single_pool_element", class_3781::new);
	StructurePoolElement field_16974 = register("list_pool_element", class_3782::new);
	StructurePoolElement field_16971 = register("feature_pool_element", class_3776::new);
	StructurePoolElement field_16972 = register("empty_pool_element", dynamic -> class_3777.field_16663);

	static StructurePoolElement register(String string, StructurePoolElement structurePoolElement) {
		return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, string, structurePoolElement);
	}
}

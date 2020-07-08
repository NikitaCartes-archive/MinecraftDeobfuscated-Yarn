package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;

public class TemplatePools {
	public static final StructurePool EMPTY = register(
		new StructurePool(new Identifier("empty"), new Identifier("empty"), ImmutableList.of(), StructurePool.Projection.RIGID)
	);
	public static final StructurePool INVALID = register(
		new StructurePool(new Identifier("invalid"), new Identifier("invalid"), ImmutableList.of(), StructurePool.Projection.RIGID)
	);

	public static StructurePool register(StructurePool templatePool) {
		return BuiltinRegistries.add(BuiltinRegistries.TEMPLATE_POOL, templatePool.getId(), templatePool);
	}

	public static void method_30599() {
		BastionRemnantGenerator.init();
		PillagerOutpostGenerator.init();
		VillageGenerator.init();
	}

	static {
		method_30599();
	}
}

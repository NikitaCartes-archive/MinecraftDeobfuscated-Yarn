package net.minecraft.structure.pool;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.TrialChamberData;
import net.minecraft.util.Identifier;

public class OneTwentyOneStructurePools {
	public static final RegistryKey<StructurePool> EMPTY = of("empty");

	public static RegistryKey<StructurePool> of(String id) {
		return RegistryKey.of(RegistryKeys.TEMPLATE_POOL, new Identifier(id));
	}

	public static void register(Registerable<StructurePool> structurePoolsRegisterable, String id, StructurePool pool) {
		StructurePools.register(structurePoolsRegisterable, id, pool);
	}

	public static void bootstrap(Registerable<StructurePool> structurePoolsRegisterable) {
		TrialChamberData.bootstrap(structurePoolsRegisterable);
	}
}

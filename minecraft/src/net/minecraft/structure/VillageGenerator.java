package net.minecraft.structure;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.registry.Registerable;

public class VillageGenerator {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		PlainsVillageData.bootstrap(poolRegisterable);
		SnowyVillageData.bootstrap(poolRegisterable);
		SavannaVillageData.bootstrap(poolRegisterable);
		DesertVillageData.bootstrap(poolRegisterable);
		TaigaVillageData.bootstrap(poolRegisterable);
	}
}

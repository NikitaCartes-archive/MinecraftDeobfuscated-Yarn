package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.AncientCityGenerator;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class StructurePools {
	public static final RegistryKey<StructurePool> EMPTY = RegistryKey.of(Registry.STRUCTURE_POOL_KEY, new Identifier("empty"));
	private static final RegistryEntry<StructurePool> INVALID = register(
		new StructurePool(EMPTY.getValue(), EMPTY.getValue(), ImmutableList.of(), StructurePool.Projection.RIGID)
	);

	public static RegistryEntry<StructurePool> register(StructurePool templatePool) {
		return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_POOL, templatePool.getId(), templatePool);
	}

	@Deprecated
	public static void method_44111() {
		initDefaultPools(BuiltinRegistries.STRUCTURE_POOL);
	}

	public static RegistryEntry<StructurePool> initDefaultPools(Registry<StructurePool> registry) {
		BastionRemnantGenerator.init();
		PillagerOutpostGenerator.init();
		VillageGenerator.init();
		AncientCityGenerator.init();
		return INVALID;
	}
}

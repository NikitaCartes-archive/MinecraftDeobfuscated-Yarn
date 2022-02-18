package net.minecraft;

import java.util.List;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

public interface class_7072 {
	RegistryEntry<class_7059> field_37233 = method_41183(
		class_7057.VILLAGES,
		new class_7059(
			List.of(
				class_7059.method_41145(ConfiguredStructureFeatures.field_26311),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26312),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26313),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26314),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26315)
			),
			new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 10387312)
		)
	);
	RegistryEntry<class_7059> field_37234 = method_41184(
		class_7057.DESERT_PYRAMIDS, ConfiguredStructureFeatures.field_26297, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357617)
	);
	RegistryEntry<class_7059> field_37235 = method_41184(
		class_7057.IGLOOS, ConfiguredStructureFeatures.field_26298, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357618)
	);
	RegistryEntry<class_7059> field_37236 = method_41184(
		class_7057.JUNGLE_TEMPLES, ConfiguredStructureFeatures.field_26296, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357619)
	);
	RegistryEntry<class_7059> field_37237 = method_41184(
		class_7057.SWAMP_HUTS, ConfiguredStructureFeatures.field_26301, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357620)
	);
	RegistryEntry<class_7059> field_37238 = method_41184(
		class_7057.PILLAGER_OUTPOSTS, ConfiguredStructureFeatures.field_26292, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 165745296)
	);
	RegistryEntry<class_7059> field_37239 = method_41184(
		class_7057.OCEAN_MONUMENTS, ConfiguredStructureFeatures.field_26303, new RandomSpreadStructurePlacement(32, 5, SpreadType.TRIANGULAR, 10387313)
	);
	RegistryEntry<class_7059> field_37240 = method_41184(
		class_7057.WOODLAND_MANSIONS, ConfiguredStructureFeatures.field_26295, new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 10387319)
	);
	RegistryEntry<class_7059> field_37241 = method_41184(
		class_7057.BURIED_TREASURES, ConfiguredStructureFeatures.field_26309, new RandomSpreadStructurePlacement(1, 0, SpreadType.LINEAR, 0, new Vec3i(9, 0, 9))
	);
	RegistryEntry<class_7059> field_37242 = method_41183(
		class_7057.MINESHAFTS,
		new class_7059(
			List.of(class_7059.method_41145(ConfiguredStructureFeatures.field_26293), class_7059.method_41145(ConfiguredStructureFeatures.field_26294)),
			new RandomSpreadStructurePlacement(1, 0, SpreadType.LINEAR, 0)
		)
	);
	RegistryEntry<class_7059> field_37243 = method_41183(
		class_7057.RUINED_PORTALS,
		new class_7059(
			List.of(
				class_7059.method_41145(ConfiguredStructureFeatures.field_26316),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26317),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26287),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26288),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26289),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26290),
				class_7059.method_41145(ConfiguredStructureFeatures.field_26291)
			),
			new RandomSpreadStructurePlacement(40, 15, SpreadType.LINEAR, 34222645)
		)
	);
	RegistryEntry<class_7059> field_37244 = method_41183(
		class_7057.SHIPWRECKS,
		new class_7059(
			List.of(class_7059.method_41145(ConfiguredStructureFeatures.field_26299), class_7059.method_41145(ConfiguredStructureFeatures.field_26300)),
			new RandomSpreadStructurePlacement(24, 4, SpreadType.LINEAR, 165745295)
		)
	);
	RegistryEntry<class_7059> field_37245 = method_41183(
		class_7057.OCEAN_RUINS,
		new class_7059(
			List.of(class_7059.method_41145(ConfiguredStructureFeatures.field_26304), class_7059.method_41145(ConfiguredStructureFeatures.field_26305)),
			new RandomSpreadStructurePlacement(20, 8, SpreadType.LINEAR, 14357621)
		)
	);
	RegistryEntry<class_7059> field_37246 = method_41183(
		class_7057.NETHER_COMPLEXES,
		new class_7059(
			List.of(class_7059.method_41146(ConfiguredStructureFeatures.field_26306, 2), class_7059.method_41146(ConfiguredStructureFeatures.field_26310, 3)),
			new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232)
		)
	);
	RegistryEntry<class_7059> field_37247 = method_41184(
		class_7057.NETHER_FOSSILS, ConfiguredStructureFeatures.field_26307, new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357921)
	);
	RegistryEntry<class_7059> field_37248 = method_41184(
		class_7057.END_CITIES, ConfiguredStructureFeatures.field_26308, new RandomSpreadStructurePlacement(20, 11, SpreadType.TRIANGULAR, 10387313)
	);
	RegistryEntry<class_7059> field_37249 = method_41184(
		class_7057.STRONGHOLDS, ConfiguredStructureFeatures.field_26302, new ConcentricRingsStructurePlacement(32, 3, 128)
	);

	static RegistryEntry<class_7059> method_41182() {
		return (RegistryEntry<class_7059>)BuiltinRegistries.field_37231.streamEntries().iterator().next();
	}

	static RegistryEntry<class_7059> method_41183(RegistryKey<class_7059> registryKey, class_7059 arg) {
		return BuiltinRegistries.add(BuiltinRegistries.field_37231, registryKey, arg);
	}

	static RegistryEntry<class_7059> method_41184(
		RegistryKey<class_7059> registryKey, RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry, StructurePlacement structurePlacement
	) {
		return method_41183(registryKey, new class_7059(registryEntry, structurePlacement));
	}
}

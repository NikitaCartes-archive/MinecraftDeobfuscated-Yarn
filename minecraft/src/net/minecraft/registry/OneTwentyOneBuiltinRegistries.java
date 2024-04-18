package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.entity.OneTwentyOneBannerPatterns;
import net.minecraft.entity.damage.OneTwentyOneDamageTypes;
import net.minecraft.item.trim.OneTwentyOneArmorTrimPatterns;
import net.minecraft.structure.OneTwentyOneStructureSets;
import net.minecraft.structure.pool.OneTwentyOneStructurePools;
import net.minecraft.structure.processor.OneTwentyOneStructureProcessorLists;
import net.minecraft.world.biome.OneTwentyOneBiomes;
import net.minecraft.world.gen.structure.OneTwentyOneStructures;

public class OneTwentyOneBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.BIOME, OneTwentyOneBiomes::bootstrap)
		.addRegistry(RegistryKeys.TEMPLATE_POOL, OneTwentyOneStructurePools::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE, OneTwentyOneStructures::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE_SET, OneTwentyOneStructureSets::bootstrap)
		.addRegistry(RegistryKeys.PROCESSOR_LIST, OneTwentyOneStructureProcessorLists::bootstrap)
		.addRegistry(RegistryKeys.DAMAGE_TYPE, OneTwentyOneDamageTypes::bootstrap)
		.addRegistry(RegistryKeys.BANNER_PATTERN, OneTwentyOneBannerPatterns::bootstrap)
		.addRegistry(RegistryKeys.TRIM_PATTERN, OneTwentyOneArmorTrimPatterns::bootstrap);

	public static CompletableFuture<RegistryBuilder.FullPatchesRegistriesPair> createWrapperLookup(
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
	) {
		return ExperimentalRegistriesValidator.validate(registriesFuture, REGISTRY_BUILDER);
	}
}

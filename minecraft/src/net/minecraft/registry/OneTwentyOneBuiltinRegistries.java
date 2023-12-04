package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.structure.OneTwentyOneStructureSets;
import net.minecraft.structure.pool.OneTwentyOneStructurePools;
import net.minecraft.structure.processor.OneTwentyOneStructureProcessorLists;
import net.minecraft.world.gen.structure.OneTwentyOneStructures;

public class OneTwentyOneBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.TEMPLATE_POOL, OneTwentyOneStructurePools::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE, OneTwentyOneStructures::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE_SET, OneTwentyOneStructureSets::bootstrap)
		.addRegistry(RegistryKeys.PROCESSOR_LIST, OneTwentyOneStructureProcessorLists::bootstrap);

	public static CompletableFuture<RegistryBuilder.FullPatchesRegistriesPair> createWrapperLookup(
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
	) {
		return ExperimentalRegistriesValidator.validate(registriesFuture, REGISTRY_BUILDER);
	}
}
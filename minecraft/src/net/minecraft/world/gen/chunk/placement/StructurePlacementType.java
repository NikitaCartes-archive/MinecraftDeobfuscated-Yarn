package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface StructurePlacementType<SP extends StructurePlacement> {
	StructurePlacementType<RandomSpreadStructurePlacement> RANDOM_SPREAD = register("random_spread", RandomSpreadStructurePlacement.CODEC);
	StructurePlacementType<ConcentricRingsStructurePlacement> CONCENTRIC_RINGS = register("concentric_rings", ConcentricRingsStructurePlacement.CODEC);

	Codec<SP> codec();

	private static <SP extends StructurePlacement> StructurePlacementType<SP> register(String id, Codec<SP> codec) {
		return Registry.register(Registries.STRUCTURE_PLACEMENT, id, () -> codec);
	}
}

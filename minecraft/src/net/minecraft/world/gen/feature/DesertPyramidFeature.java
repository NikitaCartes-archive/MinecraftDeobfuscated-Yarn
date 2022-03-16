package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureType;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class DesertPyramidFeature extends BasicTempleStructureFeature {
	public static final Codec<DesertPyramidFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance).apply(instance, DesertPyramidFeature::new)
	);

	public DesertPyramidFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(DesertTempleGenerator::new, 21, 21, registryEntryList, map, feature, bl);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.DESERT_PYRAMID;
	}
}

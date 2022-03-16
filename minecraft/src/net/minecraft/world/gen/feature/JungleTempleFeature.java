package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructureType;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class JungleTempleFeature extends BasicTempleStructureFeature {
	public static final Codec<JungleTempleFeature> CODEC = RecordCodecBuilder.create(instance -> method_41608(instance).apply(instance, JungleTempleFeature::new));

	public JungleTempleFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(JungleTempleGenerator::new, 12, 15, registryEntryList, map, feature, bl);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.JUNGLE_TEMPLE;
	}
}

package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class OceanRuinFeature extends StructureFeature {
	public static final Codec<OceanRuinFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance)
				.<OceanRuinFeature.BiomeType, float, float>and(
					instance.group(
						OceanRuinFeature.BiomeType.CODEC.fieldOf("biome_temp").forGetter(oceanRuinFeature -> oceanRuinFeature.field_37808),
						Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter(oceanRuinFeature -> oceanRuinFeature.field_37809),
						Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter(oceanRuinFeature -> oceanRuinFeature.field_37810)
					)
				)
				.apply(instance, OceanRuinFeature::new)
	);
	public final OceanRuinFeature.BiomeType field_37808;
	public final float field_37809;
	public final float field_37810;

	public OceanRuinFeature(
		RegistryEntryList<Biome> registryEntryList,
		Map<SpawnGroup, StructureSpawns> map,
		GenerationStep.Feature feature,
		boolean bl,
		OceanRuinFeature.BiomeType biomeType,
		float f,
		float g
	) {
		super(registryEntryList, map, feature, bl);
		this.field_37808 = biomeType;
		this.field_37809 = f;
		this.field_37810 = g;
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		return method_41612(arg, Heightmap.Type.OCEAN_FLOOR_WG, structurePiecesCollector -> this.addPieces(structurePiecesCollector, arg));
	}

	private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg) {
		BlockPos blockPos = new BlockPos(arg.chunkPos().getStartX(), 90, arg.chunkPos().getStartZ());
		BlockRotation blockRotation = BlockRotation.random(arg.random());
		OceanRuinGenerator.addPieces(arg.structureTemplateManager(), blockPos, blockRotation, structurePiecesCollector, arg.random(), this);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.OCEAN_RUIN;
	}

	public static enum BiomeType implements StringIdentifiable {
		WARM("warm"),
		COLD("cold");

		public static final Codec<OceanRuinFeature.BiomeType> CODEC = StringIdentifiable.createCodec(
			OceanRuinFeature.BiomeType::values, OceanRuinFeature.BiomeType::byName
		);
		private static final Map<String, OceanRuinFeature.BiomeType> BY_NAME = (Map<String, OceanRuinFeature.BiomeType>)Arrays.stream(values())
			.collect(Collectors.toMap(OceanRuinFeature.BiomeType::getName, biomeType -> biomeType));
		private final String name;

		private BiomeType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Nullable
		public static OceanRuinFeature.BiomeType byName(String name) {
			return (OceanRuinFeature.BiomeType)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}

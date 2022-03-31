package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class OceanRuinFeature extends StructureFeature {
	public static final Codec<OceanRuinFeature> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					configCodecBuilder(instance),
					OceanRuinFeature.BiomeType.CODEC.fieldOf("biome_temp").forGetter(oceanRuinFeature -> oceanRuinFeature.field_37808),
					Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter(oceanRuinFeature -> oceanRuinFeature.field_37809),
					Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter(oceanRuinFeature -> oceanRuinFeature.field_37810)
				)
				.apply(instance, OceanRuinFeature::new)
	);
	public final OceanRuinFeature.BiomeType field_37808;
	public final float field_37809;
	public final float field_37810;

	public OceanRuinFeature(StructureFeature.Config config, OceanRuinFeature.BiomeType biomeType, float f, float g) {
		super(config);
		this.field_37808 = biomeType;
		this.field_37809 = f;
		this.field_37810 = g;
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		return getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, structurePiecesCollector -> this.addPieces(structurePiecesCollector, context));
	}

	private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
		BlockRotation blockRotation = BlockRotation.random(context.random());
		OceanRuinGenerator.addPieces(context.structureManager(), blockPos, blockRotation, structurePiecesCollector, context.random(), this);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.OCEAN_RUIN;
	}

	public static enum BiomeType implements StringIdentifiable {
		WARM("warm"),
		COLD("cold");

		public static final com.mojang.serialization.Codec<OceanRuinFeature.BiomeType> CODEC = StringIdentifiable.createCodec(OceanRuinFeature.BiomeType::values);
		private final String name;

		private BiomeType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}

package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

public class EndCityFeature extends StructureFeature {
	public static final Codec<EndCityFeature> CODEC = RecordCodecBuilder.create(instance -> method_41608(instance).apply(instance, EndCityFeature::new));
	private static final int Z_SEED_MULTIPLIER = 10387313;

	public EndCityFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	private static int getGenerationHeight(ChunkPos pos, ChunkGenerator chunkGenerator, HeightLimitView world, NoiseConfig noiseConfig) {
		Random random = new Random((long)(pos.x + pos.z * 10387313));
		BlockRotation blockRotation = BlockRotation.random(random);
		int i = 5;
		int j = 5;
		if (blockRotation == BlockRotation.CLOCKWISE_90) {
			i = -5;
		} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
			i = -5;
			j = -5;
		} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
			j = -5;
		}

		int k = pos.getOffsetX(7);
		int l = pos.getOffsetZ(7);
		int m = chunkGenerator.getHeightInGround(k, l, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
		int n = chunkGenerator.getHeightInGround(k, l + j, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
		int o = chunkGenerator.getHeightInGround(k + i, l, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
		int p = chunkGenerator.getHeightInGround(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		int i = getGenerationHeight(arg.chunkPos(), arg.chunkGenerator(), arg.heightAccessor(), arg.randomState());
		if (i < 60) {
			return Optional.empty();
		} else {
			BlockPos blockPos = arg.chunkPos().getCenterAtY(i);
			return Optional.of(new StructureFeature.class_7150(blockPos, structurePiecesCollector -> this.method_39817(structurePiecesCollector, blockPos, arg)));
		}
	}

	private void method_39817(StructurePiecesCollector collector, BlockPos blockPos, StructureFeature.class_7149 arg) {
		BlockRotation blockRotation = BlockRotation.random(arg.random());
		List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
		EndCityGenerator.addPieces(arg.structureTemplateManager(), blockPos, blockRotation, list, arg.random());
		list.forEach(collector::addPiece);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.END_CITY;
	}
}

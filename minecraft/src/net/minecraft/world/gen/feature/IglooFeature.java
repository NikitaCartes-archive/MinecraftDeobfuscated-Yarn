package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.random.ChunkRandom;

public class IglooFeature extends StructureFeature {
	public static final Codec<IglooFeature> CODEC = RecordCodecBuilder.create(instance -> method_41608(instance).apply(instance, IglooFeature::new));

	public IglooFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		return method_41612(arg, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> this.addPieces(structurePiecesCollector, arg));
	}

	private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg) {
		ChunkPos chunkPos = arg.chunkPos();
		ChunkRandom chunkRandom = arg.random();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 90, chunkPos.getStartZ());
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
		IglooGenerator.addPieces(arg.structureTemplateManager(), blockPos, blockRotation, structurePiecesCollector, chunkRandom);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.IGLOO;
	}
}

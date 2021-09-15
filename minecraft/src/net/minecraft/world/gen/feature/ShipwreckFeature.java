package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ShipwreckFeature extends StructureFeature<ShipwreckFeatureConfig> {
	public ShipwreckFeature(Codec<ShipwreckFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<ShipwreckFeatureConfig> getStructureStartFactory() {
		return ShipwreckFeature.Start::new;
	}

	public static class Start extends StructureStart<ShipwreckFeatureConfig> {
		public Start(StructureFeature<ShipwreckFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			ShipwreckFeatureConfig shipwreckFeatureConfig,
			HeightLimitView heightLimitView,
			Predicate<Biome> predicate
		) {
			if (StructureFeature.checkBiome(
				chunkGenerator,
				heightLimitView,
				predicate,
				shipwreckFeatureConfig.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG,
				chunkPos.getCenterX(),
				chunkPos.getCenterZ()
			)) {
				BlockRotation blockRotation = BlockRotation.random(this.random);
				BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 90, chunkPos.getStartZ());
				ShipwreckGenerator.addParts(structureManager, blockPos, blockRotation, this, this.random, shipwreckFeatureConfig);
			}
		}
	}
}

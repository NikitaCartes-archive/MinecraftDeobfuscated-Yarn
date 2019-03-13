package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.ShipwreckGenerator;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ShipwreckFeature extends AbstractTempleFeature<ShipwreckFeatureConfig> {
	public ShipwreckFeature(Function<Dynamic<?>, ? extends ShipwreckFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Shipwreck";
	}

	@Override
	public int getRadius() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return ShipwreckFeature.Start::new;
	}

	@Override
	protected int getSeedModifier() {
		return 165745295;
	}

	@Override
	protected int getSpacing(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.method_12109().getShipwreckSpacing();
	}

	@Override
	protected int getSeparation(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.method_12109().getShipwreckSeparation();
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			ShipwreckFeatureConfig shipwreckFeatureConfig = chunkGenerator.method_12105(biome, Feature.field_13589);
			Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			ShipwreckGenerator.method_14834(structureManager, blockPos, rotation, this.children, this.random, shipwreckFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}

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
	public int method_14021() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return ShipwreckFeature.class_3171::new;
	}

	@Override
	protected int method_13774() {
		return 165745295;
	}

	@Override
	protected int method_13773(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getSettings().method_12566();
	}

	@Override
	protected int method_13775(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getSettings().method_12562();
	}

	public static class class_3171 extends StructureStart {
		public class_3171(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			ShipwreckFeatureConfig shipwreckFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.SHIPWRECK);
			Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			ShipwreckGenerator.addParts(structureManager, blockPos, rotation, this.children, this.random, shipwreckFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}

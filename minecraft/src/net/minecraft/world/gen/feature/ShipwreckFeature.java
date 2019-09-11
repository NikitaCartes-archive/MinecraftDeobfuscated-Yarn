package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
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
		return chunkGenerator.getConfig().getShipwreckSpacing();
	}

	@Override
	protected int getSeparation(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getConfig().getShipwreckSeparation();
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			ShipwreckFeatureConfig shipwreckFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.SHIPWRECK);
			BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			ShipwreckGenerator.addParts(structureManager, blockPos, blockRotation, this.children, this.random, shipwreckFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}

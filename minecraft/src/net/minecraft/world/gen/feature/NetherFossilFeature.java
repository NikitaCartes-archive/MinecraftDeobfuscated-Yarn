package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFossilFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public NetherFossilFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSeedModifier() {
		return 14357921;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return NetherFossilFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Nether_Fossil";
	}

	@Override
	protected int getSpacing(ChunkGenerator<?> chunkGenerator) {
		return 2;
	}

	@Override
	protected int getSeparation(ChunkGenerator<?> chunkGenerator) {
		return 1;
	}

	@Override
	public int getRadius() {
		return 3;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			int i = x * 16;
			int j = z * 16;
			int k = chunkGenerator.getSeaLevel() + this.random.nextInt(126 - chunkGenerator.getSeaLevel());
			NetherFossilGenerator.addPieces(structureManager, this.children, this.random, new BlockPos(i + this.random.nextInt(16), k, j + this.random.nextInt(16)));
			this.setBoundingBoxFromChildren();
		}
	}
}

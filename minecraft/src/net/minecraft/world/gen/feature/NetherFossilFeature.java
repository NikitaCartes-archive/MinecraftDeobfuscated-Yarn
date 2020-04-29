package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NetherFossilFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public NetherFossilFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
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
	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return 2;
	}

	@Override
	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
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
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			ChunkPos chunkPos = new ChunkPos(x, z);
			int i = chunkPos.getStartX() + this.random.nextInt(16);
			int j = chunkPos.getStartZ() + this.random.nextInt(16);
			int k = chunkGenerator.getSeaLevel();
			int l = k + this.random.nextInt(chunkGenerator.getMaxY() - 2 - k);
			BlockView blockView = chunkGenerator.getColumnSample(i, j);

			for (BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j); l > k; l--) {
				BlockState blockState = blockView.getBlockState(mutable);
				mutable.move(Direction.DOWN);
				BlockState blockState2 = blockView.getBlockState(mutable);
				if (blockState.isAir() && (blockState2.isOf(Blocks.SOUL_SAND) || blockState2.isSideSolidFullSquare(blockView, mutable, Direction.UP))) {
					break;
				}
			}

			if (l > k) {
				NetherFossilGenerator.addPieces(structureManager, this.children, this.random, new BlockPos(i, l, j));
				this.setBoundingBoxFromChildren();
			}
		}
	}
}

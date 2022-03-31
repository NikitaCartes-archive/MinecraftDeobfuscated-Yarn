package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherFossilFeature extends StructureFeature {
	public static final Codec<NetherFossilFeature> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					configCodecBuilder(instance), HeightProvider.CODEC.fieldOf("height").forGetter(netherFossilFeature -> netherFossilFeature.field_37805)
				)
				.apply(instance, NetherFossilFeature::new)
	);
	public final HeightProvider field_37805;

	public NetherFossilFeature(StructureFeature.Config config, HeightProvider heightProvider) {
		super(config);
		this.field_37805 = heightProvider;
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		ChunkRandom chunkRandom = context.random();
		int i = context.chunkPos().getStartX() + chunkRandom.nextInt(16);
		int j = context.chunkPos().getStartZ() + chunkRandom.nextInt(16);
		int k = context.chunkGenerator().getSeaLevel();
		HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.world());
		int l = this.field_37805.get(chunkRandom, heightContext);
		VerticalBlockSample verticalBlockSample = context.chunkGenerator().getColumnSample(i, j, context.world(), context.noiseConfig());
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j);

		while (l > k) {
			BlockState blockState = verticalBlockSample.getState(l);
			BlockState blockState2 = verticalBlockSample.getState(--l);
			if (blockState.isAir() && (blockState2.isOf(Blocks.SOUL_SAND) || blockState2.isSideSolidFullSquare(EmptyBlockView.INSTANCE, mutable.setY(l), Direction.UP))) {
				break;
			}
		}

		if (l <= k) {
			return Optional.empty();
		} else {
			BlockPos blockPos = new BlockPos(i, l, j);
			return Optional.of(
				new StructureFeature.StructurePosition(
					blockPos, structurePiecesCollector -> NetherFossilGenerator.addPieces(context.structureManager(), structurePiecesCollector, chunkRandom, blockPos)
				)
			);
		}
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.NETHER_FOSSIL;
	}
}

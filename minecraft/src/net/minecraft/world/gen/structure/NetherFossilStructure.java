package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class NetherFossilStructure extends StructureType {
	public static final Codec<NetherFossilStructure> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(configCodecBuilder(instance), HeightProvider.CODEC.fieldOf("height").forGetter(structure -> structure.height))
				.apply(instance, NetherFossilStructure::new)
	);
	public final HeightProvider height;

	public NetherFossilStructure(StructureType.Config config, HeightProvider height) {
		super(config);
		this.height = height;
	}

	@Override
	public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
		ChunkRandom chunkRandom = context.random();
		int i = context.chunkPos().getStartX() + chunkRandom.nextInt(16);
		int j = context.chunkPos().getStartZ() + chunkRandom.nextInt(16);
		int k = context.chunkGenerator().getSeaLevel();
		HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.world());
		int l = this.height.get(chunkRandom, heightContext);
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
				new StructureType.StructurePosition(
					blockPos, structurePiecesCollector -> NetherFossilGenerator.addPieces(context.structureManager(), structurePiecesCollector, chunkRandom, blockPos)
				)
			);
		}
	}

	@Override
	public net.minecraft.structure.StructureType<?> getType() {
		return net.minecraft.structure.StructureType.NETHER_FOSSIL;
	}
}

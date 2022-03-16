package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherFossilFeature extends StructureFeature {
	public static final Codec<NetherFossilFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance)
				.and(HeightProvider.CODEC.fieldOf("height").forGetter(netherFossilFeature -> netherFossilFeature.field_37805))
				.apply(instance, NetherFossilFeature::new)
	);
	public final HeightProvider field_37805;

	public NetherFossilFeature(
		RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl, HeightProvider heightProvider
	) {
		super(registryEntryList, map, feature, bl);
		this.field_37805 = heightProvider;
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		ChunkRandom chunkRandom = arg.random();
		int i = arg.chunkPos().getStartX() + chunkRandom.nextInt(16);
		int j = arg.chunkPos().getStartZ() + chunkRandom.nextInt(16);
		int k = arg.chunkGenerator().getSeaLevel();
		HeightContext heightContext = new HeightContext(arg.chunkGenerator(), arg.heightAccessor());
		int l = this.field_37805.get(chunkRandom, heightContext);
		VerticalBlockSample verticalBlockSample = arg.chunkGenerator().getColumnSample(i, j, arg.heightAccessor(), arg.randomState());
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
				new StructureFeature.class_7150(
					blockPos, structurePiecesCollector -> NetherFossilGenerator.addPieces(arg.structureTemplateManager(), structurePiecesCollector, chunkRandom, blockPos)
				)
			);
		}
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.NETHER_FOSSIL;
	}
}

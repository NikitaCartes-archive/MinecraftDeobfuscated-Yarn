package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public final class JigsawStructure extends StructureType {
	public static final int MAX_SIZE = 128;
	public static final Codec<JigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						configCodecBuilder(instance),
						StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(feature -> feature.startPool),
						Codec.intRange(0, 7).fieldOf("size").forGetter(feature -> feature.size),
						HeightProvider.CODEC.fieldOf("start_height").forGetter(feature -> feature.startHeight),
						Codec.BOOL.fieldOf("use_expansion_hack").forGetter(feature -> feature.useExpansionHack),
						Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(feature -> feature.projectStartToHeightmap),
						Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(feature -> feature.maxDistanceFromCenter)
					)
					.apply(instance, JigsawStructure::new)
		)
		.<JigsawStructure>flatXmap(createValidator(), createValidator())
		.codec();
	private final RegistryEntry<StructurePool> startPool;
	private final int size;
	private final HeightProvider startHeight;
	private final boolean useExpansionHack;
	private final Optional<Heightmap.Type> projectStartToHeightmap;
	private final int maxDistanceFromCenter;

	private static Function<JigsawStructure, DataResult<JigsawStructure>> createValidator() {
		return feature -> {
			int i = switch (feature.getTerrainAdaptation()) {
				case NONE -> 0;
				case BURY, BEARD_THIN, BEARD_BOX -> 12;
			};
			return feature.maxDistanceFromCenter + i > 128
				? DataResult.error("Structure size including terrain adaptation must not exceed 128")
				: DataResult.success(feature);
		};
	}

	public JigsawStructure(
		StructureType.Config config,
		RegistryEntry<StructurePool> startPool,
		int size,
		HeightProvider startHeight,
		boolean useExpansionHack,
		Optional<Heightmap.Type> projectStartToHeightmap,
		int maxDistanceFromCenter
	) {
		super(config);
		this.startPool = startPool;
		this.size = size;
		this.startHeight = startHeight;
		this.useExpansionHack = useExpansionHack;
		this.projectStartToHeightmap = projectStartToHeightmap;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
	}

	public JigsawStructure(
		StructureType.Config config,
		RegistryEntry<StructurePool> startPool,
		int size,
		HeightProvider startHeight,
		boolean useExpansionHack,
		Heightmap.Type projectStartToHeightmap
	) {
		this(config, startPool, size, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), 80);
	}

	public JigsawStructure(StructureType.Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack) {
		this(config, startPool, size, startHeight, useExpansionHack, Optional.empty(), 80);
	}

	@Override
	public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
		ChunkPos chunkPos = context.chunkPos();
		int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());
		StructurePools.initDefaultPools();
		return StructurePoolBasedGenerator.generate(
			context, this.startPool, this.size, PoolStructurePiece::new, blockPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter
		);
	}

	@Override
	public net.minecraft.structure.StructureType<?> getType() {
		return net.minecraft.structure.StructureType.JIGSAW;
	}
}

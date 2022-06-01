package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public final class JigsawStructure extends Structure {
	public static final int MAX_SIZE = 128;
	public static final Codec<JigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						configCodecBuilder(instance),
						StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
						Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
						Codec.intRange(0, 7).fieldOf("size").forGetter(structure -> structure.size),
						HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
						Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
						Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
						Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
					)
					.apply(instance, JigsawStructure::new)
		)
		.<JigsawStructure>flatXmap(createValidator(), createValidator())
		.codec();
	private final RegistryEntry<StructurePool> startPool;
	private final Optional<Identifier> startJigsawName;
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
		Structure.Config config,
		RegistryEntry<StructurePool> startPool,
		Optional<Identifier> startJigsawName,
		int size,
		HeightProvider startHeight,
		boolean useExpansionHack,
		Optional<Heightmap.Type> projectStartToHeightmap,
		int maxDistanceFromCenter
	) {
		super(config);
		this.startPool = startPool;
		this.startJigsawName = startJigsawName;
		this.size = size;
		this.startHeight = startHeight;
		this.useExpansionHack = useExpansionHack;
		this.projectStartToHeightmap = projectStartToHeightmap;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
	}

	public JigsawStructure(
		Structure.Config config,
		RegistryEntry<StructurePool> startPool,
		int size,
		HeightProvider startHeight,
		boolean useExpansionHack,
		Heightmap.Type projectStartToHeightmap
	) {
		this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), 80);
	}

	public JigsawStructure(Structure.Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack) {
		this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.empty(), 80);
	}

	@Override
	public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
		ChunkPos chunkPos = context.chunkPos();
		int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());
		StructurePools.method_44111();
		return StructurePoolBasedGenerator.generate(
			context, this.startPool, this.startJigsawName, this.size, blockPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter
		);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.JIGSAW;
	}
}

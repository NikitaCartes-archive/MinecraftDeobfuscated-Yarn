package net.minecraft.world.chunk;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.Blender;

public class ChunkGenerating {
	private static boolean isLightOn(Chunk chunk) {
		return chunk.getStatus().isAtLeast(ChunkStatus.LIGHT) && chunk.isLightOn();
	}

	static CompletableFuture<Chunk> noop(ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk) {
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> generateStructures(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerWorld serverWorld = context.world();
		if (serverWorld.getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
			context.generator()
				.setStructureStarts(
					serverWorld.getRegistryManager(),
					serverWorld.getChunkManager().getStructurePlacementCalculator(),
					serverWorld.getStructureAccessor(),
					chunk,
					context.structureManager()
				);
		}

		serverWorld.cacheStructures(chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> loadStructures(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		context.world().cacheStructures(chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> generateStructureReferences(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, step, chunk);
		context.generator().addStructureReferences(chunkRegion, serverWorld.getStructureAccessor().forRegion(chunkRegion), chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> populateBiomes(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, step, chunk);
		return context.generator()
			.populateBiomes(
				serverWorld.getChunkManager().getNoiseConfig(), Blender.getBlender(chunkRegion), serverWorld.getStructureAccessor().forRegion(chunkRegion), chunk
			);
	}

	static CompletableFuture<Chunk> populateNoise(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, step, chunk);
		return context.generator()
			.populateNoise(
				Blender.getBlender(chunkRegion), serverWorld.getChunkManager().getNoiseConfig(), serverWorld.getStructureAccessor().forRegion(chunkRegion), chunk
			)
			.thenApply(populated -> {
				if (populated instanceof ProtoChunk protoChunk) {
					BelowZeroRetrogen belowZeroRetrogen = protoChunk.getBelowZeroRetrogen();
					if (belowZeroRetrogen != null) {
						BelowZeroRetrogen.replaceOldBedrock(protoChunk);
						if (belowZeroRetrogen.hasMissingBedrock()) {
							belowZeroRetrogen.fillColumnsWithAirIfMissingBedrock(protoChunk);
						}
					}
				}

				return populated;
			});
	}

	static CompletableFuture<Chunk> buildSurface(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, step, chunk);
		context.generator()
			.buildSurface(chunkRegion, serverWorld.getStructureAccessor().forRegion(chunkRegion), serverWorld.getChunkManager().getNoiseConfig(), chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> carve(ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, step, chunk);
		if (chunk instanceof ProtoChunk protoChunk) {
			Blender.createCarvingMasks(chunkRegion, protoChunk);
		}

		context.generator()
			.carve(
				chunkRegion,
				serverWorld.getSeed(),
				serverWorld.getChunkManager().getNoiseConfig(),
				serverWorld.getBiomeAccess(),
				serverWorld.getStructureAccessor().forRegion(chunkRegion),
				chunk,
				GenerationStep.Carver.AIR
			);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> generateFeatures(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerWorld serverWorld = context.world();
		Heightmap.populateHeightmaps(
			chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
		);
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, chunks, step, chunk);
		context.generator().generateFeatures(chunkRegion, chunk, serverWorld.getStructureAccessor().forRegion(chunkRegion));
		Blender.tickLeavesAndFluids(chunkRegion, chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> initializeLight(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ServerLightingProvider serverLightingProvider = context.lightingProvider();
		chunk.refreshSurfaceY();
		((ProtoChunk)chunk).setLightingProvider(serverLightingProvider);
		boolean bl = isLightOn(chunk);
		return serverLightingProvider.initializeLight(chunk, bl);
	}

	static CompletableFuture<Chunk> light(ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk) {
		boolean bl = isLightOn(chunk);
		return context.lightingProvider().light(chunk, bl);
	}

	static CompletableFuture<Chunk> generateEntities(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		if (!chunk.hasBelowZeroRetrogen()) {
			context.generator().populateEntities(new ChunkRegion(context.world(), chunks, step, chunk));
		}

		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> convertToFullChunk(
		ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk
	) {
		ChunkPos chunkPos = chunk.getPos();
		AbstractChunkHolder abstractChunkHolder = chunks.get(chunkPos.x, chunkPos.z);
		return CompletableFuture.supplyAsync(() -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			ServerWorld serverWorld = context.world();
			WorldChunk worldChunk;
			if (protoChunk instanceof WrapperProtoChunk) {
				worldChunk = ((WrapperProtoChunk)protoChunk).getWrappedChunk();
			} else {
				worldChunk = new WorldChunk(serverWorld, protoChunk, worldChunkx -> addEntities(serverWorld, protoChunk.getEntities()));
				abstractChunkHolder.replaceWith(new WrapperProtoChunk(worldChunk, false));
			}

			worldChunk.setLevelTypeProvider(abstractChunkHolder::getLevelType);
			worldChunk.loadEntities();
			worldChunk.setLoadedToWorld(true);
			worldChunk.updateAllBlockEntities();
			worldChunk.addChunkTickSchedulers(serverWorld);
			return worldChunk;
		}, runnable -> context.mainThreadMailBox().send(ChunkTaskPrioritySystem.createMessage(runnable, chunkPos.toLong(), abstractChunkHolder::getLevel)));
	}

	private static void addEntities(ServerWorld world, List<NbtCompound> entities) {
		if (!entities.isEmpty()) {
			world.addEntities(EntityType.streamFromNbt(entities, world));
		}
	}
}

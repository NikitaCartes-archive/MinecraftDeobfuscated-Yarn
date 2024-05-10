package net.minecraft.world.chunk;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_9761;
import net.minecraft.class_9762;
import net.minecraft.class_9770;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.Blender;

public class ChunkGenerating {
	private static boolean isLightOn(Chunk chunk) {
		return chunk.getStatus().isAtLeast(ChunkStatus.LIGHT) && chunk.isLightOn();
	}

	static CompletableFuture<Chunk> noop(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> fullChunkConverter, Chunk chunk) {
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> generateStructures(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
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

	static CompletableFuture<Chunk> loadStructures(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> fullChunkConverter, Chunk chunk) {
		context.world().cacheStructures(chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> generateStructureReferences(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, arg2, arg, chunk);
		context.generator().addStructureReferences(chunkRegion, serverWorld.getStructureAccessor().forRegion(chunkRegion), chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> populateBiomes(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, arg2, arg, chunk);
		return context.generator()
			.populateBiomes(
				serverWorld.getChunkManager().getNoiseConfig(), Blender.getBlender(chunkRegion), serverWorld.getStructureAccessor().forRegion(chunkRegion), chunk
			);
	}

	static CompletableFuture<Chunk> populateNoise(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, arg2, arg, chunk);
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

	static CompletableFuture<Chunk> buildSurface(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, arg2, arg, chunk);
		context.generator()
			.buildSurface(chunkRegion, serverWorld.getStructureAccessor().forRegion(chunkRegion), serverWorld.getChunkManager().getNoiseConfig(), chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> carve(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, arg2, arg, chunk);
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

	static CompletableFuture<Chunk> generateFeatures(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerWorld serverWorld = context.world();
		Heightmap.populateHeightmaps(
			chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
		);
		ChunkRegion chunkRegion = new ChunkRegion(serverWorld, arg2, arg, chunk);
		context.generator().generateFeatures(chunkRegion, chunk, serverWorld.getStructureAccessor().forRegion(chunkRegion));
		Blender.tickLeavesAndFluids(chunkRegion, chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> initializeLight(ChunkGenerationContext chunkGenerationContext, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ServerLightingProvider serverLightingProvider = chunkGenerationContext.lightingProvider();
		chunk.refreshSurfaceY();
		((ProtoChunk)chunk).setLightingProvider(serverLightingProvider);
		boolean bl = isLightOn(chunk);
		return serverLightingProvider.initializeLight(chunk, bl);
	}

	static CompletableFuture<Chunk> method_60555(ChunkGenerationContext chunkGenerationContext, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		boolean bl = isLightOn(chunk);
		return chunkGenerationContext.lightingProvider().light(chunk, bl);
	}

	static CompletableFuture<Chunk> generateEntities(ChunkGenerationContext context, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		if (!chunk.hasBelowZeroRetrogen()) {
			context.generator().populateEntities(new ChunkRegion(context.world(), arg2, arg, chunk));
		}

		return CompletableFuture.completedFuture(chunk);
	}

	static CompletableFuture<Chunk> method_60556(ChunkGenerationContext chunkGenerationContext, class_9770 arg, class_9762<class_9761> arg2, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		class_9761 lv = arg2.method_60482(chunkPos.x, chunkPos.z);
		return CompletableFuture.supplyAsync(() -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			ServerWorld serverWorld = chunkGenerationContext.world();
			WorldChunk worldChunk;
			if (protoChunk instanceof WrapperProtoChunk) {
				worldChunk = ((WrapperProtoChunk)protoChunk).getWrappedChunk();
			} else {
				worldChunk = new WorldChunk(serverWorld, protoChunk, worldChunkx -> method_60552(serverWorld, protoChunk.getEntities()));
				lv.method_60456(new WrapperProtoChunk(worldChunk, false));
			}

			worldChunk.setLevelTypeProvider(lv::method_60474);
			worldChunk.loadEntities();
			worldChunk.setLoadedToWorld(true);
			worldChunk.updateAllBlockEntities();
			worldChunk.addChunkTickSchedulers(serverWorld);
			return worldChunk;
		}, runnable -> chunkGenerationContext.mainThreadMailBox().send(ChunkTaskPrioritySystem.createMessage(runnable, chunkPos.toLong(), lv::getLevel)));
	}

	private static void method_60552(ServerWorld serverWorld, List<NbtCompound> list) {
		if (!list.isEmpty()) {
			serverWorld.addEntities(EntityType.streamFromNbt(list, serverWorld));
		}
	}
}

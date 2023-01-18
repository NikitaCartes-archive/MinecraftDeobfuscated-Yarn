package net.minecraft.world.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import org.slf4j.Logger;

public class EntityChunkDataAccess implements ChunkDataAccess<Entity> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String ENTITIES_KEY = "Entities";
	private static final String POSITION_KEY = "Position";
	private final ServerWorld world;
	private final StorageIoWorker dataLoadWorker;
	private final LongSet emptyChunks = new LongOpenHashSet();
	private final TaskExecutor<Runnable> taskExecutor;
	protected final DataFixer dataFixer;

	public EntityChunkDataAccess(ServerWorld world, Path path, DataFixer dataFixer, boolean dsync, Executor executor) {
		this.world = world;
		this.dataFixer = dataFixer;
		this.taskExecutor = TaskExecutor.create(executor, "entity-deserializer");
		this.dataLoadWorker = new StorageIoWorker(path, dsync, "entities");
	}

	@Override
	public CompletableFuture<ChunkDataList<Entity>> readChunkData(ChunkPos pos) {
		return this.emptyChunks.contains(pos.toLong())
			? CompletableFuture.completedFuture(emptyDataList(pos))
			: this.dataLoadWorker.readChunkData(pos).thenApplyAsync(nbt -> {
				if (nbt.isEmpty()) {
					this.emptyChunks.add(pos.toLong());
					return emptyDataList(pos);
				} else {
					try {
						ChunkPos chunkPos2 = getChunkPos((NbtCompound)nbt.get());
						if (!Objects.equals(pos, chunkPos2)) {
							LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", pos, pos, chunkPos2);
						}
					} catch (Exception var6) {
						LOGGER.warn("Failed to parse chunk {} position info", pos, var6);
					}

					NbtCompound nbtCompound = this.fixChunkData((NbtCompound)nbt.get());
					NbtList nbtList = nbtCompound.getList("Entities", NbtElement.COMPOUND_TYPE);
					List<Entity> list = (List<Entity>)EntityType.streamFromNbt(nbtList, this.world).collect(ImmutableList.toImmutableList());
					return new ChunkDataList(pos, list);
				}
			}, this.taskExecutor::send);
	}

	private static ChunkPos getChunkPos(NbtCompound chunkNbt) {
		int[] is = chunkNbt.getIntArray("Position");
		return new ChunkPos(is[0], is[1]);
	}

	private static void putChunkPos(NbtCompound chunkNbt, ChunkPos pos) {
		chunkNbt.put("Position", new NbtIntArray(new int[]{pos.x, pos.z}));
	}

	private static ChunkDataList<Entity> emptyDataList(ChunkPos pos) {
		return new ChunkDataList<>(pos, ImmutableList.of());
	}

	@Override
	public void writeChunkData(ChunkDataList<Entity> dataList) {
		ChunkPos chunkPos = dataList.getChunkPos();
		if (dataList.isEmpty()) {
			if (this.emptyChunks.add(chunkPos.toLong())) {
				this.dataLoadWorker.setResult(chunkPos, null);
			}
		} else {
			NbtList nbtList = new NbtList();
			dataList.stream().forEach(entity -> {
				NbtCompound nbtCompoundx = new NbtCompound();
				if (entity.saveNbt(nbtCompoundx)) {
					nbtList.add(nbtCompoundx);
				}
			});
			NbtCompound nbtCompound = NbtHelper.putDataVersion(new NbtCompound());
			nbtCompound.put("Entities", nbtList);
			putChunkPos(nbtCompound, chunkPos);
			this.dataLoadWorker.setResult(chunkPos, nbtCompound).exceptionally(ex -> {
				LOGGER.error("Failed to store chunk {}", chunkPos, ex);
				return null;
			});
			this.emptyChunks.remove(chunkPos.toLong());
		}
	}

	@Override
	public void awaitAll(boolean sync) {
		this.dataLoadWorker.completeAll(sync).join();
		this.taskExecutor.awaitAll();
	}

	private NbtCompound fixChunkData(NbtCompound chunkNbt) {
		int i = NbtHelper.getDataVersion(chunkNbt, -1);
		return DataFixTypes.ENTITY_CHUNK.update(this.dataFixer, chunkNbt, i);
	}

	@Override
	public void close() throws IOException {
		this.dataLoadWorker.close();
	}
}

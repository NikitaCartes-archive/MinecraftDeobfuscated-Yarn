package net.minecraft.server.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4153;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.ThreadTaskQueue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public class ServerChunkManager extends ChunkManager {
	private static final int CHUNKS_ELIGIBLE_FOR_SPAWNING = (int)Math.pow(17.0, 2.0);
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private final ChunkTicketManager ticketManager;
	private final ChunkGenerator<?> chunkGenerator;
	private final ServerWorld field_13945;
	private final Thread serverThread;
	private final ServerLightingProvider field_13921;
	private final ServerChunkManager.class_4212 field_18809;
	public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	private final PersistentStateManager persistentStateManager;
	private long lastMobSpawningTime;
	private boolean spawnMonsters = true;
	private boolean spawnAnimals = true;

	public ServerChunkManager(
		ServerWorld serverWorld,
		File file,
		DataFixer dataFixer,
		StructureManager structureManager,
		Executor executor,
		ChunkGenerator<?> chunkGenerator,
		int i,
		int j,
		WorldGenerationProgressListener worldGenerationProgressListener,
		Supplier<PersistentStateManager> supplier
	) {
		this.field_13945 = serverWorld;
		this.field_18809 = new ServerChunkManager.class_4212(serverWorld);
		this.chunkGenerator = chunkGenerator;
		this.serverThread = Thread.currentThread();
		File file2 = serverWorld.method_8597().method_12460().getFile(file);
		File file3 = new File(file2, "data");
		file3.mkdirs();
		this.persistentStateManager = new PersistentStateManager(file3, dataFixer);
		this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(
			serverWorld, file, dataFixer, structureManager, executor, this.field_18809, this, this.getChunkGenerator(), worldGenerationProgressListener, supplier, i, j
		);
		this.field_13921 = this.threadedAnvilChunkStorage.method_17212();
		this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
	}

	public ServerLightingProvider method_17293() {
		return this.field_13921;
	}

	@Nullable
	private ChunkHolder getChunkHolder(long l) {
		return this.threadedAnvilChunkStorage.getCopiedChunkHolder(l);
	}

	public int getTotalChunksLoadedCount() {
		return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
	}

	@Nullable
	@Override
	public Chunk method_12121(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkSyncIfServerThread(i, j, chunkStatus, bl);
		return ((Either)completableFuture.join()).map(chunk -> chunk, unloaded -> {
			if (bl) {
				throw new IllegalStateException("Chunk not there when requested: " + unloaded);
			} else {
				return null;
			}
		});
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkSyncIfServerThread(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		boolean bl2 = Thread.currentThread() == this.serverThread;
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture;
		if (bl2) {
			completableFuture = this.getChunkAsync(i, j, chunkStatus, bl);
			this.field_18809.method_18857(completableFuture::isDone);
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.getChunkAsync(i, j, chunkStatus, bl), this.field_18809)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return completableFuture;
	}

	private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkAsync(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(i, j);
		long l = chunkPos.toLong();
		int k = 33 + ChunkStatus.getTargetGenerationRadius(chunkStatus);
		ChunkHolder chunkHolder = this.getChunkHolder(l);
		if (bl) {
			this.ticketManager.method_17290(ChunkTicketType.UNKNOWN, chunkPos, k, chunkPos);
			if (this.method_18752(chunkHolder, k)) {
				this.update();
				chunkHolder = this.getChunkHolder(l);
				if (this.method_18752(chunkHolder, k)) {
					throw new IllegalStateException("No chunk holder after ticket has been added");
				}
			}
		}

		return this.method_18752(chunkHolder, k) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : chunkHolder.getChunkMinimumStatus(chunkStatus);
	}

	private boolean method_18752(@Nullable ChunkHolder chunkHolder, int i) {
		return chunkHolder == null || chunkHolder.getLevel() > i;
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(i, j).toLong());
		int k = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL);
		return chunkHolder != null && chunkHolder.getLevel() <= k
			? ((Either)chunkHolder.getChunkMinimumStatus(ChunkStatus.FULL).getNow(ChunkHolder.UNLOADED_CHUNK)).left().isPresent()
			: false;
	}

	@Override
	public BlockView getChunk(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		ChunkHolder chunkHolder = this.getChunkHolder(l);
		if (chunkHolder == null) {
			return null;
		} else {
			int k = CHUNK_STATUSES.size() - 1;

			while (true) {
				ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(k);
				Optional<Chunk> optional = ((Either)chunkHolder.getChunkForStatus(chunkStatus).getNow(ChunkHolder.UNLOADED_CHUNK)).left();
				if (optional.isPresent()) {
					return (BlockView)optional.get();
				}

				if (chunkStatus == ChunkStatus.LIGHT.getPrevious()) {
					return null;
				}

				k--;
			}
		}
	}

	public World method_16434() {
		return this.field_13945;
	}

	public boolean method_19492() {
		return this.field_18809.executeQueuedTask();
	}

	private boolean update() {
		if (this.ticketManager.update(this.threadedAnvilChunkStorage)) {
			this.threadedAnvilChunkStorage.updateHolderMap();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isEntityInLoadedChunk(Entity entity) {
		if (!this.ticketManager.method_18739(ChunkSectionPos.from(entity))) {
			return false;
		} else {
			ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4));
			if (chunkHolder == null) {
				return false;
			} else {
				Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)chunkHolder.method_14003()
					.getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
				return either.left().isPresent();
			}
		}
	}

	public void save(boolean bl) {
		this.threadedAnvilChunkStorage.save(bl);
	}

	@Override
	public void close() throws IOException {
		this.field_13921.close();
		this.threadedAnvilChunkStorage.close();
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.field_13945.getProfiler().push("purge");
		this.ticketManager.purge();
		this.update();
		this.field_13945.getProfiler().swap("chunks");
		this.tickChunks();
		this.field_13945.getProfiler().swap("unload");
		this.threadedAnvilChunkStorage.unload(booleanSupplier);
		this.field_13945.getProfiler().pop();
	}

	private void tickChunks() {
		long l = this.field_13945.getTime();
		long m = l - this.lastMobSpawningTime;
		this.lastMobSpawningTime = l;
		LevelProperties levelProperties = this.field_13945.method_8401();
		boolean bl = levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
		boolean bl2 = this.field_13945.getGameRules().getBoolean("doMobSpawning");
		if (!bl) {
			this.field_13945.getProfiler().push("pollingChunks");
			int i = this.ticketManager.getLevelCount();
			int j = this.field_13945.getGameRules().getInteger("randomTickSpeed");
			BlockPos blockPos = this.field_13945.method_8395();
			boolean bl3 = levelProperties.getTime() % 400L == 0L;
			EntityCategory[] entityCategorys = EntityCategory.values();
			Object2IntMap<EntityCategory> object2IntMap = this.field_13945.method_18219();
			ObjectBidirectionalIterator<Entry<ChunkHolder>> objectBidirectionalIterator = this.threadedAnvilChunkStorage.entryIterator();

			while (objectBidirectionalIterator.hasNext()) {
				Entry<ChunkHolder> entry = (Entry<ChunkHolder>)objectBidirectionalIterator.next();
				ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
				WorldChunk worldChunk = chunkHolder.getWorldChunk();
				if (worldChunk != null) {
					chunkHolder.flushUpdates(worldChunk);
					ChunkPos chunkPos = chunkHolder.getPos();
					if (!this.threadedAnvilChunkStorage.method_18724(chunkPos)) {
						worldChunk.setInhabitedTime(worldChunk.getInhabitedTime() + m);
						if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.field_13945.method_8621().contains(worldChunk.getPos())) {
							this.field_13945.getProfiler().push("spawner");

							for (EntityCategory entityCategory : entityCategorys) {
								if (entityCategory != EntityCategory.field_17715
									&& (!entityCategory.isPeaceful() || this.spawnAnimals)
									&& (entityCategory.isPeaceful() || this.spawnMonsters)
									&& (!entityCategory.isAnimal() || bl3)) {
									int k = entityCategory.getSpawnCap() * i / CHUNKS_ELIGIBLE_FOR_SPAWNING;
									if (object2IntMap.getInt(entityCategory) <= k) {
										SpawnHelper.method_8663(entityCategory, this.field_13945, worldChunk, blockPos);
									}
								}
							}

							this.field_13945.getProfiler().pop();
						}

						this.field_13945.tickChunk(worldChunk, j);
					}
				}
			}

			this.field_13945.getProfiler().pop();
			if (bl2) {
				this.chunkGenerator.spawnEntities(this.field_13945, this.spawnMonsters, this.spawnAnimals);
			}
		}

		this.threadedAnvilChunkStorage.method_18727();
	}

	@Override
	public String getStatus() {
		return "ServerChunkCache: " + this.getLoadedChunkCount();
	}

	@Override
	public ChunkGenerator<?> getChunkGenerator() {
		return this.chunkGenerator;
	}

	public int getLoadedChunkCount() {
		return this.threadedAnvilChunkStorage.getLoadedChunkCount();
	}

	public void markForUpdate(BlockPos blockPos) {
		int i = blockPos.getX() >> 4;
		int j = blockPos.getZ() >> 4;
		ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(i, j));
		if (chunkHolder != null) {
			chunkHolder.markForUpdate(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15);
		}
	}

	@Override
	public void method_12247(LightType lightType, ChunkSectionPos chunkSectionPos) {
		this.field_18809.execute(() -> {
			ChunkHolder chunkHolder = this.getChunkHolder(chunkSectionPos.toChunkPos().toLong());
			if (chunkHolder != null) {
				chunkHolder.method_14012(lightType, chunkSectionPos.getChunkY());
			}
		});
	}

	public <T> void method_17297(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.ticketManager.method_17291(chunkTicketType, chunkPos, i, object);
	}

	public <T> void method_17300(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.ticketManager.method_17292(chunkTicketType, chunkPos, i, object);
	}

	@Override
	public void setChunkForced(ChunkPos chunkPos, boolean bl) {
		this.ticketManager.setChunkForced(chunkPos, bl);
	}

	public void method_14096(ServerPlayerEntity serverPlayerEntity) {
		this.threadedAnvilChunkStorage.method_18713(serverPlayerEntity);
	}

	public void method_18753(Entity entity) {
		this.threadedAnvilChunkStorage.method_18716(entity);
	}

	public void method_18755(Entity entity) {
		this.threadedAnvilChunkStorage.method_18701(entity);
	}

	public void method_18751(Entity entity, Packet<?> packet) {
		this.threadedAnvilChunkStorage.method_18717(entity, packet);
	}

	public void method_18754(Entity entity, Packet<?> packet) {
		this.threadedAnvilChunkStorage.method_18702(entity, packet);
	}

	public void applyViewDistance(int i, int j) {
		this.threadedAnvilChunkStorage.applyViewDistance(i, j);
	}

	@Override
	public void setMobSpawnOptions(boolean bl, boolean bl2) {
		this.spawnMonsters = bl;
		this.spawnAnimals = bl2;
	}

	@Environment(EnvType.CLIENT)
	public String getDebugString(ChunkPos chunkPos) {
		return this.threadedAnvilChunkStorage.getDebugString(chunkPos);
	}

	public PersistentStateManager getPersistentStateManager() {
		return this.persistentStateManager;
	}

	public class_4153 method_19493() {
		return this.threadedAnvilChunkStorage.method_19488();
	}

	final class class_4212 extends ThreadTaskQueue<Runnable> {
		private class_4212(World world) {
			super("Chunk source main thread executor for " + Registry.DIMENSION.method_10221(world.method_8597().method_12460()));
		}

		@Override
		protected Runnable method_16211(Runnable runnable) {
			return runnable;
		}

		@Override
		protected boolean method_18856(Runnable runnable) {
			return true;
		}

		@Override
		protected boolean isOffThread() {
			return true;
		}

		@Override
		protected Thread method_3777() {
			return ServerChunkManager.this.serverThread;
		}

		@Override
		protected boolean executeQueuedTask() {
			if (ServerChunkManager.this.update()) {
				return true;
			} else {
				ServerChunkManager.this.field_13921.tick();
				return super.executeQueuedTask();
			}
		}
	}
}

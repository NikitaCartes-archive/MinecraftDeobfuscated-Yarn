package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnDensityCapper;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.NbtScannable;

public class ServerChunkManager extends ChunkManager {
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private final ChunkTicketManager ticketManager;
	final ServerWorld world;
	final Thread serverThread;
	final ServerLightingProvider lightingProvider;
	private final ServerChunkManager.MainThreadExecutor mainThreadExecutor;
	public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	private final PersistentStateManager persistentStateManager;
	private long lastMobSpawningTime;
	private boolean spawnMonsters = true;
	private boolean spawnAnimals = true;
	private static final int CACHE_SIZE = 4;
	private final long[] chunkPosCache = new long[4];
	private final ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
	private final Chunk[] chunkCache = new Chunk[4];
	@Nullable
	@Debug
	private SpawnHelper.Info spawnInfo;

	public ServerChunkManager(
		ServerWorld world,
		LevelStorage.Session session,
		DataFixer dataFixer,
		StructureTemplateManager structureTemplateManager,
		Executor workerExecutor,
		ChunkGenerator chunkGenerator,
		int viewDistance,
		int simulationDistance,
		boolean dsync,
		WorldGenerationProgressListener worldGenerationProgressListener,
		ChunkStatusChangeListener chunkStatusChangeListener,
		Supplier<PersistentStateManager> persistentStateManagerFactory
	) {
		this.world = world;
		this.mainThreadExecutor = new ServerChunkManager.MainThreadExecutor(world);
		this.serverThread = Thread.currentThread();
		File file = session.getWorldDirectory(world.getRegistryKey()).resolve("data").toFile();
		file.mkdirs();
		this.persistentStateManager = new PersistentStateManager(file, dataFixer, world.getRegistryManager());
		this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(
			world,
			session,
			dataFixer,
			structureTemplateManager,
			workerExecutor,
			this.mainThreadExecutor,
			this,
			chunkGenerator,
			worldGenerationProgressListener,
			chunkStatusChangeListener,
			persistentStateManagerFactory,
			viewDistance,
			dsync
		);
		this.lightingProvider = this.threadedAnvilChunkStorage.getLightingProvider();
		this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
		this.ticketManager.setSimulationDistance(simulationDistance);
		this.initChunkCaches();
	}

	public ServerLightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	@Nullable
	private ChunkHolder getChunkHolder(long pos) {
		return this.threadedAnvilChunkStorage.getChunkHolder(pos);
	}

	public int getTotalChunksLoadedCount() {
		return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
	}

	private void putInCache(long pos, @Nullable Chunk chunk, ChunkStatus status) {
		for (int i = 3; i > 0; i--) {
			this.chunkPosCache[i] = this.chunkPosCache[i - 1];
			this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
			this.chunkCache[i] = this.chunkCache[i - 1];
		}

		this.chunkPosCache[0] = pos;
		this.chunkStatusCache[0] = status;
		this.chunkCache[0] = chunk;
	}

	@Nullable
	@Override
	public Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
		if (Thread.currentThread() != this.serverThread) {
			return (Chunk)CompletableFuture.supplyAsync(() -> this.getChunk(x, z, leastStatus, create), this.mainThreadExecutor).join();
		} else {
			Profiler profiler = this.world.getProfiler();
			profiler.visit("getChunk");
			long l = ChunkPos.toLong(x, z);

			for (int i = 0; i < 4; i++) {
				if (l == this.chunkPosCache[i] && leastStatus == this.chunkStatusCache[i]) {
					Chunk chunk = this.chunkCache[i];
					if (chunk != null || !create) {
						return chunk;
					}
				}
			}

			profiler.visit("getChunkCacheMiss");
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
			this.mainThreadExecutor.runTasks(completableFuture::isDone);
			OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)completableFuture.join();
			Chunk chunk2 = optionalChunk.orElse(null);
			if (chunk2 == null && create) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + optionalChunk.getError()));
			} else {
				this.putInCache(l, chunk2, leastStatus);
				return chunk2;
			}
		}
	}

	@Nullable
	@Override
	public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
		if (Thread.currentThread() != this.serverThread) {
			return null;
		} else {
			this.world.getProfiler().visit("getChunkNow");
			long l = ChunkPos.toLong(chunkX, chunkZ);

			for (int i = 0; i < 4; i++) {
				if (l == this.chunkPosCache[i] && this.chunkStatusCache[i] == ChunkStatus.FULL) {
					Chunk chunk = this.chunkCache[i];
					return chunk instanceof WorldChunk ? (WorldChunk)chunk : null;
				}
			}

			ChunkHolder chunkHolder = this.getChunkHolder(l);
			if (chunkHolder == null) {
				return null;
			} else {
				OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)chunkHolder.getValidFutureFor(ChunkStatus.FULL).getNow(null);
				if (optionalChunk == null) {
					return null;
				} else {
					Chunk chunk2 = optionalChunk.orElse(null);
					if (chunk2 != null) {
						this.putInCache(l, chunk2, ChunkStatus.FULL);
						if (chunk2 instanceof WorldChunk) {
							return (WorldChunk)chunk2;
						}
					}

					return null;
				}
			}
		}
	}

	private void initChunkCaches() {
		Arrays.fill(this.chunkPosCache, ChunkPos.MARKER);
		Arrays.fill(this.chunkStatusCache, null);
		Arrays.fill(this.chunkCache, null);
	}

	public CompletableFuture<OptionalChunk<Chunk>> getChunkFutureSyncOnMainThread(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		boolean bl = Thread.currentThread() == this.serverThread;
		CompletableFuture<OptionalChunk<Chunk>> completableFuture;
		if (bl) {
			completableFuture = this.getChunkFuture(chunkX, chunkZ, leastStatus, create);
			this.mainThreadExecutor.runTasks(completableFuture::isDone);
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.getChunkFuture(chunkX, chunkZ, leastStatus, create), this.mainThreadExecutor)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return completableFuture;
	}

	private CompletableFuture<OptionalChunk<Chunk>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
		ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
		long l = chunkPos.toLong();
		int i = ChunkLevels.getLevelFromStatus(leastStatus);
		ChunkHolder chunkHolder = this.getChunkHolder(l);
		if (create) {
			this.ticketManager.addTicketWithLevel(ChunkTicketType.UNKNOWN, chunkPos, i, chunkPos);
			if (this.isMissingForLevel(chunkHolder, i)) {
				Profiler profiler = this.world.getProfiler();
				profiler.push("chunkLoad");
				this.updateChunks();
				chunkHolder = this.getChunkHolder(l);
				profiler.pop();
				if (this.isMissingForLevel(chunkHolder, i)) {
					throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("No chunk holder after ticket has been added"));
				}
			}
		}

		return this.isMissingForLevel(chunkHolder, i) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : chunkHolder.getChunkAt(leastStatus, this.threadedAnvilChunkStorage);
	}

	private boolean isMissingForLevel(@Nullable ChunkHolder holder, int maxLevel) {
		return holder == null || holder.getLevel() > maxLevel;
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(x, z).toLong());
		int i = ChunkLevels.getLevelFromStatus(ChunkStatus.FULL);
		return !this.isMissingForLevel(chunkHolder, i);
	}

	@Nullable
	@Override
	public LightSourceView getChunk(int chunkX, int chunkZ) {
		long l = ChunkPos.toLong(chunkX, chunkZ);
		ChunkHolder chunkHolder = this.getChunkHolder(l);
		if (chunkHolder == null) {
			return null;
		} else {
			int i = CHUNK_STATUSES.size() - 1;

			while (true) {
				ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
				Chunk chunk = (Chunk)((OptionalChunk)chunkHolder.getFutureFor(chunkStatus).getNow(ChunkHolder.UNLOADED_CHUNK)).orElse(null);
				if (chunk != null) {
					return chunk;
				}

				if (chunkStatus == ChunkStatus.INITIALIZE_LIGHT.getPrevious()) {
					return null;
				}

				i--;
			}
		}
	}

	public World getWorld() {
		return this.world;
	}

	public boolean executeQueuedTasks() {
		return this.mainThreadExecutor.runTask();
	}

	/**
	 * Update expected chunk loading states by updating {@code PLAYER} tickets and {@code Future}s.
	 */
	boolean updateChunks() {
		boolean bl = this.ticketManager.update(this.threadedAnvilChunkStorage);
		boolean bl2 = this.threadedAnvilChunkStorage.updateHolderMap();
		if (!bl && !bl2) {
			return false;
		} else {
			this.initChunkCaches();
			return true;
		}
	}

	public boolean isTickingFutureReady(long pos) {
		ChunkHolder chunkHolder = this.getChunkHolder(pos);
		if (chunkHolder == null) {
			return false;
		} else {
			return !this.world.shouldTickBlocksInChunk(pos)
				? false
				: ((OptionalChunk)chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK)).isPresent();
		}
	}

	public void save(boolean flush) {
		this.updateChunks();
		this.threadedAnvilChunkStorage.save(flush);
	}

	@Override
	public void close() throws IOException {
		this.save(true);
		this.lightingProvider.close();
		this.threadedAnvilChunkStorage.close();
	}

	@Override
	public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
		this.world.getProfiler().push("purge");
		if (this.world.getTickManager().shouldTick() || !tickChunks) {
			this.ticketManager.purge();
		}

		this.updateChunks();
		this.world.getProfiler().swap("chunks");
		if (tickChunks) {
			this.tickChunks();
			this.threadedAnvilChunkStorage.tickEntityMovement();
		}

		this.world.getProfiler().swap("unload");
		this.threadedAnvilChunkStorage.tick(shouldKeepTicking);
		this.world.getProfiler().pop();
		this.initChunkCaches();
	}

	private void tickChunks() {
		long l = this.world.getTime();
		long m = l - this.lastMobSpawningTime;
		this.lastMobSpawningTime = l;
		if (!this.world.isDebugWorld()) {
			Profiler profiler = this.world.getProfiler();
			profiler.push("pollingChunks");
			profiler.push("filteringLoadedChunks");
			List<ServerChunkManager.ChunkWithHolder> list = Lists.<ServerChunkManager.ChunkWithHolder>newArrayListWithCapacity(
				this.threadedAnvilChunkStorage.getLoadedChunkCount()
			);

			for (ChunkHolder chunkHolder : this.threadedAnvilChunkStorage.entryIterator()) {
				WorldChunk worldChunk = chunkHolder.getWorldChunk();
				if (worldChunk != null) {
					list.add(new ServerChunkManager.ChunkWithHolder(worldChunk, chunkHolder));
				}
			}

			if (this.world.getTickManager().shouldTick()) {
				profiler.swap("naturalSpawnCount");
				int i = this.ticketManager.getTickedChunkCount();
				SpawnHelper.Info info = SpawnHelper.setupSpawn(i, this.world.iterateEntities(), this::ifChunkLoaded, new SpawnDensityCapper(this.threadedAnvilChunkStorage));
				this.spawnInfo = info;
				profiler.swap("spawnAndTick");
				boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
				Util.shuffle(list, this.world.random);
				int j = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
				boolean bl2 = this.world.getLevelProperties().getTime() % 400L == 0L;

				for (ServerChunkManager.ChunkWithHolder chunkWithHolder : list) {
					WorldChunk worldChunk2 = chunkWithHolder.chunk;
					ChunkPos chunkPos = worldChunk2.getPos();
					if (this.world.shouldTick(chunkPos) && this.threadedAnvilChunkStorage.shouldTick(chunkPos)) {
						worldChunk2.increaseInhabitedTime(m);
						if (bl && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(chunkPos)) {
							SpawnHelper.spawn(this.world, worldChunk2, info, this.spawnAnimals, this.spawnMonsters, bl2);
						}

						if (this.world.shouldTickBlocksInChunk(chunkPos.toLong())) {
							this.world.tickChunk(worldChunk2, j);
						}
					}
				}

				profiler.swap("customSpawners");
				if (bl) {
					this.world.tickSpawners(this.spawnMonsters, this.spawnAnimals);
				}
			}

			profiler.swap("broadcast");
			list.forEach(chunk -> chunk.holder.flushUpdates(chunk.chunk));
			profiler.pop();
			profiler.pop();
		}
	}

	private void ifChunkLoaded(long pos, Consumer<WorldChunk> chunkConsumer) {
		ChunkHolder chunkHolder = this.getChunkHolder(pos);
		if (chunkHolder != null) {
			((OptionalChunk)chunkHolder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK)).ifPresent(chunkConsumer);
		}
	}

	@Override
	public String getDebugString() {
		return Integer.toString(this.getLoadedChunkCount());
	}

	@VisibleForTesting
	public int getPendingTasks() {
		return this.mainThreadExecutor.getTaskCount();
	}

	public ChunkGenerator getChunkGenerator() {
		return this.threadedAnvilChunkStorage.getChunkGenerator();
	}

	public StructurePlacementCalculator getStructurePlacementCalculator() {
		return this.threadedAnvilChunkStorage.getStructurePlacementCalculator();
	}

	public NoiseConfig getNoiseConfig() {
		return this.threadedAnvilChunkStorage.getNoiseConfig();
	}

	@Override
	public int getLoadedChunkCount() {
		return this.threadedAnvilChunkStorage.getLoadedChunkCount();
	}

	public void markForUpdate(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(i, j));
		if (chunkHolder != null) {
			chunkHolder.markForBlockUpdate(pos);
		}
	}

	@Override
	public void onLightUpdate(LightType type, ChunkSectionPos pos) {
		this.mainThreadExecutor.execute(() -> {
			ChunkHolder chunkHolder = this.getChunkHolder(pos.toChunkPos().toLong());
			if (chunkHolder != null) {
				chunkHolder.markForLightUpdate(type, pos.getSectionY());
			}
		});
	}

	/**
	 * Adds a chunk ticket to the ticket manager.
	 * 
	 * <p>Addition of a ticket may load chunk(s) at some point in the future depending on the loading level in the ticket's vicinity.
	 */
	public <T> void addTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
		this.ticketManager.addTicket(ticketType, pos, radius, argument);
	}

	/**
	 * Removes a chunk ticket from the ticket manager.
	 * 
	 * <p>Removal of a ticket may unload chunk(s) at some point in the future depending on the loading levels in the ticket's vicinity after removal.
	 */
	public <T> void removeTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
		this.ticketManager.removeTicket(ticketType, pos, radius, argument);
	}

	@Override
	public void setChunkForced(ChunkPos pos, boolean forced) {
		this.ticketManager.setChunkForced(pos, forced);
	}

	/**
	 * Updates the chunk section position of the {@code player}. This can either be a
	 * result of the player's movement or its camera entity's movement.
	 * 
	 * <p>This updates the section position player's client is currently watching and
	 * the player's position in its entity tracker.
	 */
	public void updatePosition(ServerPlayerEntity player) {
		if (!player.isRemoved()) {
			this.threadedAnvilChunkStorage.updatePosition(player);
		}
	}

	public void unloadEntity(Entity entity) {
		this.threadedAnvilChunkStorage.unloadEntity(entity);
	}

	public void loadEntity(Entity entity) {
		this.threadedAnvilChunkStorage.loadEntity(entity);
	}

	public void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
		this.threadedAnvilChunkStorage.sendToNearbyPlayers(entity, packet);
	}

	public void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
		this.threadedAnvilChunkStorage.sendToOtherNearbyPlayers(entity, packet);
	}

	public void applyViewDistance(int watchDistance) {
		this.threadedAnvilChunkStorage.setViewDistance(watchDistance);
	}

	public void applySimulationDistance(int simulationDistance) {
		this.ticketManager.setSimulationDistance(simulationDistance);
	}

	@Override
	public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
		this.spawnMonsters = spawnMonsters;
		this.spawnAnimals = spawnAnimals;
	}

	public String getChunkLoadingDebugInfo(ChunkPos pos) {
		return this.threadedAnvilChunkStorage.getChunkLoadingDebugInfo(pos);
	}

	public PersistentStateManager getPersistentStateManager() {
		return this.persistentStateManager;
	}

	public PointOfInterestStorage getPointOfInterestStorage() {
		return this.threadedAnvilChunkStorage.getPointOfInterestStorage();
	}

	public NbtScannable getChunkIoWorker() {
		return this.threadedAnvilChunkStorage.getWorker();
	}

	@Nullable
	@Debug
	public SpawnHelper.Info getSpawnInfo() {
		return this.spawnInfo;
	}

	public void removePersistentTickets() {
		this.ticketManager.removePersistentTickets();
	}

	static record ChunkWithHolder(WorldChunk chunk, ChunkHolder holder) {
	}

	final class MainThreadExecutor extends ThreadExecutor<Runnable> {
		MainThreadExecutor(final World world) {
			super("Chunk source main thread executor for " + world.getRegistryKey().getValue());
		}

		@Override
		protected Runnable createTask(Runnable runnable) {
			return runnable;
		}

		@Override
		protected boolean canExecute(Runnable task) {
			return true;
		}

		@Override
		protected boolean shouldExecuteAsync() {
			return true;
		}

		@Override
		protected Thread getThread() {
			return ServerChunkManager.this.serverThread;
		}

		@Override
		protected void executeTask(Runnable task) {
			ServerChunkManager.this.world.getProfiler().visit("runTask");
			super.executeTask(task);
		}

		@Override
		protected boolean runTask() {
			if (ServerChunkManager.this.updateChunks()) {
				return true;
			} else {
				ServerChunkManager.this.lightingProvider.tick();
				return super.runTask();
			}
		}
	}
}

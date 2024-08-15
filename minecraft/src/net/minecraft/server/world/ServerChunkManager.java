package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.PathUtil;
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
import net.minecraft.world.chunk.AbstractChunkHolder;
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
import org.slf4j.Logger;

public class ServerChunkManager extends ChunkManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ChunkTicketManager ticketManager;
	final ServerWorld world;
	final Thread serverThread;
	final ServerLightingProvider lightingProvider;
	private final ServerChunkManager.MainThreadExecutor mainThreadExecutor;
	public final ServerChunkLoadingManager chunkLoadingManager;
	private final PersistentStateManager persistentStateManager;
	private long lastTickTime;
	private boolean spawnMonsters = true;
	private boolean spawnAnimals = true;
	private static final int CACHE_SIZE = 4;
	private final long[] chunkPosCache = new long[4];
	private final ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
	private final Chunk[] chunkCache = new Chunk[4];
	private final List<WorldChunk> chunks = new ArrayList();
	private final Set<ChunkHolder> chunksToBroadcastUpdate = new ReferenceOpenHashSet<>();
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
		Path path = session.getWorldDirectory(world.getRegistryKey()).resolve("data");

		try {
			PathUtil.createDirectories(path);
		} catch (IOException var15) {
			LOGGER.error("Failed to create dimension data storage directory", (Throwable)var15);
		}

		this.persistentStateManager = new PersistentStateManager(path, dataFixer, world.getRegistryManager());
		this.chunkLoadingManager = new ServerChunkLoadingManager(
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
		this.lightingProvider = this.chunkLoadingManager.getLightingProvider();
		this.ticketManager = this.chunkLoadingManager.getTicketManager();
		this.ticketManager.setSimulationDistance(simulationDistance);
		this.initChunkCaches();
	}

	public ServerLightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	@Nullable
	private ChunkHolder getChunkHolder(long pos) {
		return this.chunkLoadingManager.getChunkHolder(pos);
	}

	public int getTotalChunksLoadedCount() {
		return this.chunkLoadingManager.getTotalChunksLoadedCount();
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
				throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("Chunk not there when requested: " + optionalChunk.getError()));
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
				Chunk chunk = chunkHolder.getOrNull(ChunkStatus.FULL);
				if (chunk != null) {
					this.putInCache(l, chunk, ChunkStatus.FULL);
					if (chunk instanceof WorldChunk) {
						return (WorldChunk)chunk;
					}
				}

				return null;
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
				.thenCompose(future -> future);
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
					throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("No chunk holder after ticket has been added"));
				}
			}
		}

		return this.isMissingForLevel(chunkHolder, i) ? AbstractChunkHolder.UNLOADED_FUTURE : chunkHolder.load(leastStatus, this.chunkLoadingManager);
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
		return chunkHolder == null ? null : chunkHolder.getUncheckedOrNull(ChunkStatus.INITIALIZE_LIGHT.getPrevious());
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
		boolean bl = this.ticketManager.update(this.chunkLoadingManager);
		boolean bl2 = this.chunkLoadingManager.updateHolderMap();
		this.chunkLoadingManager.updateChunks();
		if (!bl && !bl2) {
			return false;
		} else {
			this.initChunkCaches();
			return true;
		}
	}

	public boolean isTickingFutureReady(long pos) {
		if (!this.world.shouldTickBlocksInChunk(pos)) {
			return false;
		} else {
			ChunkHolder chunkHolder = this.getChunkHolder(pos);
			return chunkHolder == null ? false : ((OptionalChunk)chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK)).isPresent();
		}
	}

	public void save(boolean flush) {
		this.updateChunks();
		this.chunkLoadingManager.save(flush);
	}

	@Override
	public void close() throws IOException {
		this.save(true);
		this.persistentStateManager.close();
		this.lightingProvider.close();
		this.chunkLoadingManager.close();
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
			this.chunkLoadingManager.tickEntityMovement();
		}

		this.world.getProfiler().swap("unload");
		this.chunkLoadingManager.tick(shouldKeepTicking);
		this.world.getProfiler().pop();
		this.initChunkCaches();
	}

	private void tickChunks() {
		long l = this.world.getTime();
		long m = l - this.lastTickTime;
		this.lastTickTime = l;
		if (!this.world.isDebugWorld()) {
			Profiler profiler = this.world.getProfiler();
			profiler.push("pollingChunks");
			if (this.world.getTickManager().shouldTick()) {
				List<WorldChunk> list = this.chunks;

				try {
					profiler.push("filteringTickingChunks");
					this.addChunksToTick(list);
					profiler.swap("shuffleChunks");
					Util.shuffle(list, this.world.random);
					this.tickChunks(profiler, m, list);
					profiler.pop();
				} finally {
					list.clear();
				}
			}

			this.broadcastUpdates(profiler);
			profiler.pop();
		}
	}

	private void broadcastUpdates(Profiler profiler) {
		profiler.push("broadcast");

		for (ChunkHolder chunkHolder : this.chunksToBroadcastUpdate) {
			WorldChunk worldChunk = chunkHolder.getWorldChunk();
			if (worldChunk != null) {
				chunkHolder.flushUpdates(worldChunk);
			}
		}

		this.chunksToBroadcastUpdate.clear();
		profiler.pop();
	}

	private void addChunksToTick(List<WorldChunk> chunks) {
		this.chunkLoadingManager.forEachTickedChunk(chunk -> {
			WorldChunk worldChunk = chunk.getWorldChunk();
			if (worldChunk != null && this.world.shouldTick(chunk.getPos())) {
				chunks.add(worldChunk);
			}
		});
	}

	private void tickChunks(Profiler profiler, long timeDelta, List<WorldChunk> chunks) {
		profiler.swap("naturalSpawnCount");
		int i = this.ticketManager.getTickedChunkCount();
		SpawnHelper.Info info = SpawnHelper.setupSpawn(i, this.world.iterateEntities(), this::ifChunkLoaded, new SpawnDensityCapper(this.chunkLoadingManager));
		this.spawnInfo = info;
		profiler.swap("spawnAndTick");
		boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
		int j = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
		List<SpawnGroup> list;
		if (bl && (this.spawnMonsters || this.spawnAnimals)) {
			boolean bl2 = this.world.getLevelProperties().getTime() % 400L == 0L;
			list = SpawnHelper.collectSpawnableGroups(info, this.spawnAnimals, this.spawnMonsters, bl2);
		} else {
			list = List.of();
		}

		for (WorldChunk worldChunk : chunks) {
			ChunkPos chunkPos = worldChunk.getPos();
			worldChunk.increaseInhabitedTime(timeDelta);
			if (!list.isEmpty() && this.world.getWorldBorder().contains(chunkPos)) {
				SpawnHelper.spawn(this.world, worldChunk, info, list);
			}

			if (this.world.shouldTickBlocksInChunk(chunkPos.toLong())) {
				this.world.tickChunk(worldChunk, j);
			}
		}

		profiler.swap("customSpawners");
		if (bl) {
			this.world.tickSpawners(this.spawnMonsters, this.spawnAnimals);
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
		return this.chunkLoadingManager.getChunkGenerator();
	}

	public StructurePlacementCalculator getStructurePlacementCalculator() {
		return this.chunkLoadingManager.getStructurePlacementCalculator();
	}

	public NoiseConfig getNoiseConfig() {
		return this.chunkLoadingManager.getNoiseConfig();
	}

	@Override
	public int getLoadedChunkCount() {
		return this.chunkLoadingManager.getLoadedChunkCount();
	}

	public void markForUpdate(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(i, j));
		if (chunkHolder != null && chunkHolder.markForBlockUpdate(pos)) {
			this.chunksToBroadcastUpdate.add(chunkHolder);
		}
	}

	@Override
	public void onLightUpdate(LightType type, ChunkSectionPos pos) {
		this.mainThreadExecutor.execute(() -> {
			ChunkHolder chunkHolder = this.getChunkHolder(pos.toChunkPos().toLong());
			if (chunkHolder != null && chunkHolder.markForLightUpdate(type, pos.getSectionY())) {
				this.chunksToBroadcastUpdate.add(chunkHolder);
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
			this.chunkLoadingManager.updatePosition(player);
		}
	}

	public void unloadEntity(Entity entity) {
		this.chunkLoadingManager.unloadEntity(entity);
	}

	public void loadEntity(Entity entity) {
		this.chunkLoadingManager.loadEntity(entity);
	}

	public void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
		this.chunkLoadingManager.sendToNearbyPlayers(entity, packet);
	}

	public void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
		this.chunkLoadingManager.sendToOtherNearbyPlayers(entity, packet);
	}

	public void applyViewDistance(int watchDistance) {
		this.chunkLoadingManager.setViewDistance(watchDistance);
	}

	public void applySimulationDistance(int simulationDistance) {
		this.ticketManager.setSimulationDistance(simulationDistance);
	}

	@Override
	public void setMobSpawnOptions(boolean spawnMonsters) {
		this.spawnMonsters = spawnMonsters;
		this.spawnAnimals = this.spawnAnimals;
	}

	public String getChunkLoadingDebugInfo(ChunkPos pos) {
		return this.chunkLoadingManager.getChunkLoadingDebugInfo(pos);
	}

	public PersistentStateManager getPersistentStateManager() {
		return this.persistentStateManager;
	}

	public PointOfInterestStorage getPointOfInterestStorage() {
		return this.chunkLoadingManager.getPointOfInterestStorage();
	}

	public NbtScannable getChunkIoWorker() {
		return this.chunkLoadingManager.getWorker();
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
		public void runTasks(BooleanSupplier stopCondition) {
			super.runTasks(() -> MinecraftServer.checkWorldGenException() && stopCondition.getAsBoolean());
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

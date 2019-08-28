package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
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
	private final ServerWorld world;
	private final Thread serverThread;
	private final ServerLightingProvider lightProvider;
	private final ServerChunkManager.MainThreadExecutor mainThreadExecutor;
	public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	private final PersistentStateManager persistentStateManager;
	private long lastMobSpawningTime;
	private boolean spawnMonsters = true;
	private boolean spawnAnimals = true;
	private final long[] chunkPosCache = new long[4];
	private final ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
	private final Chunk[] chunkCache = new Chunk[4];

	public ServerChunkManager(
		ServerWorld serverWorld,
		File file,
		DataFixer dataFixer,
		StructureManager structureManager,
		Executor executor,
		ChunkGenerator<?> chunkGenerator,
		int i,
		WorldGenerationProgressListener worldGenerationProgressListener,
		Supplier<PersistentStateManager> supplier
	) {
		this.world = serverWorld;
		this.mainThreadExecutor = new ServerChunkManager.MainThreadExecutor(serverWorld);
		this.chunkGenerator = chunkGenerator;
		this.serverThread = Thread.currentThread();
		File file2 = serverWorld.getDimension().getType().getFile(file);
		File file3 = new File(file2, "data");
		file3.mkdirs();
		this.persistentStateManager = new PersistentStateManager(file3, dataFixer);
		this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(
			serverWorld,
			file,
			dataFixer,
			structureManager,
			executor,
			this.mainThreadExecutor,
			this,
			this.getChunkGenerator(),
			worldGenerationProgressListener,
			supplier,
			i
		);
		this.lightProvider = this.threadedAnvilChunkStorage.getLightProvider();
		this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
		this.initChunkCaches();
	}

	public ServerLightingProvider method_17293() {
		return this.lightProvider;
	}

	@Nullable
	private ChunkHolder getChunkHolder(long l) {
		return this.threadedAnvilChunkStorage.getChunkHolder(l);
	}

	public int getTotalChunksLoadedCount() {
		return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
	}

	private void method_21738(long l, Chunk chunk, ChunkStatus chunkStatus) {
		for (int i = 3; i > 0; i--) {
			this.chunkPosCache[i] = this.chunkPosCache[i - 1];
			this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
			this.chunkCache[i] = this.chunkCache[i - 1];
		}

		this.chunkPosCache[0] = l;
		this.chunkStatusCache[0] = chunkStatus;
		this.chunkCache[0] = chunk;
	}

	@Nullable
	@Override
	public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		if (Thread.currentThread() != this.serverThread) {
			return (Chunk)CompletableFuture.supplyAsync(() -> this.getChunk(i, j, chunkStatus, bl), this.mainThreadExecutor).join();
		} else {
			long l = ChunkPos.toLong(i, j);

			for (int k = 0; k < 4; k++) {
				if (l == this.chunkPosCache[k] && chunkStatus == this.chunkStatusCache[k]) {
					Chunk chunk = this.chunkCache[k];
					if (chunk != null || !bl) {
						return chunk;
					}
				}
			}

			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(i, j, chunkStatus, bl);
			this.mainThreadExecutor.waitFor(completableFuture::isDone);
			Chunk chunk = ((Either)completableFuture.join()).map(chunkx -> chunkx, unloaded -> {
				if (bl) {
					throw (IllegalStateException)SystemUtil.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
				} else {
					return null;
				}
			});
			this.method_21738(l, chunk, chunkStatus);
			return chunk;
		}
	}

	@Nullable
	@Override
	public WorldChunk method_21730(int i, int j) {
		if (Thread.currentThread() != this.serverThread) {
			return null;
		} else {
			long l = ChunkPos.toLong(i, j);

			for (int k = 0; k < 4; k++) {
				if (l == this.chunkPosCache[k] && this.chunkStatusCache[k] == ChunkStatus.FULL) {
					Chunk chunk = this.chunkCache[k];
					return chunk instanceof WorldChunk ? (WorldChunk)chunk : null;
				}
			}

			ChunkHolder chunkHolder = this.getChunkHolder(l);
			if (chunkHolder == null) {
				return null;
			} else {
				Either<Chunk, ChunkHolder.Unloaded> either = (Either<Chunk, ChunkHolder.Unloaded>)chunkHolder.method_21737(ChunkStatus.FULL).getNow(null);
				if (either == null) {
					return null;
				} else {
					Chunk chunk2 = (Chunk)either.left().orElse(null);
					if (chunk2 != null) {
						this.method_21738(l, chunk2, ChunkStatus.FULL);
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
		Arrays.fill(this.chunkPosCache, ChunkPos.INVALID);
		Arrays.fill(this.chunkStatusCache, null);
		Arrays.fill(this.chunkCache, null);
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFutureSyncOnMainThread(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		boolean bl2 = Thread.currentThread() == this.serverThread;
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture;
		if (bl2) {
			completableFuture = this.getChunkFuture(i, j, chunkStatus, bl);
			this.mainThreadExecutor.waitFor(completableFuture::isDone);
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.getChunkFuture(i, j, chunkStatus, bl), this.mainThreadExecutor)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return completableFuture;
	}

	private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(i, j);
		long l = chunkPos.toLong();
		int k = 33 + ChunkStatus.getTargetGenerationRadius(chunkStatus);
		ChunkHolder chunkHolder = this.getChunkHolder(l);
		if (bl) {
			this.ticketManager.addTicketWithLevel(ChunkTicketType.UNKNOWN, chunkPos, k, chunkPos);
			if (this.isMissingForLevel(chunkHolder, k)) {
				Profiler profiler = this.world.getProfiler();
				profiler.push("chunkLoad");
				this.tick();
				chunkHolder = this.getChunkHolder(l);
				profiler.pop();
				if (this.isMissingForLevel(chunkHolder, k)) {
					throw (IllegalStateException)SystemUtil.throwOrPause(new IllegalStateException("No chunk holder after ticket has been added"));
				}
			}
		}

		return this.isMissingForLevel(chunkHolder, k) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : chunkHolder.createFuture(chunkStatus, this.threadedAnvilChunkStorage);
	}

	private boolean isMissingForLevel(@Nullable ChunkHolder chunkHolder, int i) {
		return chunkHolder == null || chunkHolder.getLevel() > i;
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(i, j).toLong());
		int k = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL);
		return !this.isMissingForLevel(chunkHolder, k);
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
				Optional<Chunk> optional = ((Either)chunkHolder.getFuture(chunkStatus).getNow(ChunkHolder.UNLOADED_CHUNK)).left();
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
		return this.world;
	}

	public boolean executeQueuedTasks() {
		return this.mainThreadExecutor.executeQueuedTask();
	}

	private boolean tick() {
		boolean bl = this.ticketManager.tick(this.threadedAnvilChunkStorage);
		boolean bl2 = this.threadedAnvilChunkStorage.updateHolderMap();
		if (!bl && !bl2) {
			return false;
		} else {
			this.initChunkCaches();
			return true;
		}
	}

	@Override
	public boolean shouldTickEntity(Entity entity) {
		long l = ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4);
		return this.method_20585(l, ChunkHolder::getEntityTickingFuture);
	}

	@Override
	public boolean shouldTickChunk(ChunkPos chunkPos) {
		return this.method_20585(chunkPos.toLong(), ChunkHolder::getEntityTickingFuture);
	}

	@Override
	public boolean shouldTickBlock(BlockPos blockPos) {
		long l = ChunkPos.toLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
		return this.method_20585(l, ChunkHolder::getTickingFuture);
	}

	public boolean method_20727(Entity entity) {
		long l = ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4);
		return this.method_20585(l, ChunkHolder::method_20725);
	}

	private boolean method_20585(long l, Function<ChunkHolder, CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>>> function) {
		ChunkHolder chunkHolder = this.getChunkHolder(l);
		if (chunkHolder == null) {
			return false;
		} else {
			Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)((CompletableFuture)function.apply(chunkHolder))
				.getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
			return either.left().isPresent();
		}
	}

	public void save(boolean bl) {
		this.tick();
		this.threadedAnvilChunkStorage.save(bl);
	}

	@Override
	public void close() throws IOException {
		this.save(true);
		this.lightProvider.close();
		this.threadedAnvilChunkStorage.close();
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.world.getProfiler().push("purge");
		this.ticketManager.purge();
		this.tick();
		this.world.getProfiler().swap("chunks");
		this.tickChunks();
		this.world.getProfiler().swap("unload");
		this.threadedAnvilChunkStorage.tick(booleanSupplier);
		this.world.getProfiler().pop();
		this.initChunkCaches();
	}

	private void tickChunks() {
		long l = this.world.getTime();
		long m = l - this.lastMobSpawningTime;
		this.lastMobSpawningTime = l;
		LevelProperties levelProperties = this.world.getLevelProperties();
		boolean bl = levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
		boolean bl2 = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
		if (!bl) {
			this.world.getProfiler().push("pollingChunks");
			int i = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
			BlockPos blockPos = this.world.getSpawnPos();
			boolean bl3 = levelProperties.getTime() % 400L == 0L;
			this.world.getProfiler().push("naturalSpawnCount");
			int j = this.ticketManager.getLevelCount();
			EntityCategory[] entityCategorys = EntityCategory.values();
			Object2IntMap<EntityCategory> object2IntMap = this.world.getMobCountsByCategory();
			this.world.getProfiler().pop();
			this.threadedAnvilChunkStorage
				.entryIterator()
				.forEach(
					chunkHolder -> {
						Optional<WorldChunk> optional = ((Either)chunkHolder.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK)).left();
						if (optional.isPresent()) {
							WorldChunk worldChunk = (WorldChunk)optional.get();
							this.world.getProfiler().push("broadcast");
							chunkHolder.flushUpdates(worldChunk);
							this.world.getProfiler().pop();
							ChunkPos chunkPos = chunkHolder.getPos();
							if (!this.threadedAnvilChunkStorage.isTooFarFromPlayersToSpawnMobs(chunkPos)) {
								worldChunk.setInhabitedTime(worldChunk.getInhabitedTime() + m);
								if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk.getPos())) {
									this.world.getProfiler().push("spawner");

									for (EntityCategory entityCategory : entityCategorys) {
										if (entityCategory != EntityCategory.MISC
											&& (!entityCategory.isPeaceful() || this.spawnAnimals)
											&& (entityCategory.isPeaceful() || this.spawnMonsters)
											&& (!entityCategory.isAnimal() || bl3)) {
											int k = entityCategory.getSpawnCap() * j / CHUNKS_ELIGIBLE_FOR_SPAWNING;
											if (object2IntMap.getInt(entityCategory) <= k) {
												SpawnHelper.spawnEntitiesInChunk(entityCategory, this.world, worldChunk, blockPos);
											}
										}
									}

									this.world.getProfiler().pop();
								}

								this.world.tickChunk(worldChunk, i);
							}
						}
					}
				);
			this.world.getProfiler().push("customSpawners");
			if (bl2) {
				this.chunkGenerator.spawnEntities(this.world, this.spawnMonsters, this.spawnAnimals);
			}

			this.world.getProfiler().pop();
			this.world.getProfiler().pop();
		}

		this.threadedAnvilChunkStorage.tickPlayerMovement();
	}

	@Override
	public String getDebugString() {
		return "ServerChunkCache: " + this.getLoadedChunkCount();
	}

	@VisibleForTesting
	public int method_21694() {
		return this.mainThreadExecutor.method_21684();
	}

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
			chunkHolder.markForBlockUpdate(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15);
		}
	}

	@Override
	public void onLightUpdate(LightType lightType, ChunkSectionPos chunkSectionPos) {
		this.mainThreadExecutor.execute(() -> {
			ChunkHolder chunkHolder = this.getChunkHolder(chunkSectionPos.toChunkPos().toLong());
			if (chunkHolder != null) {
				chunkHolder.markForLightUpdate(lightType, chunkSectionPos.getChunkY());
			}
		});
	}

	public <T> void addTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.ticketManager.addTicket(chunkTicketType, chunkPos, i, object);
	}

	public <T> void removeTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.ticketManager.removeTicket(chunkTicketType, chunkPos, i, object);
	}

	@Override
	public void setChunkForced(ChunkPos chunkPos, boolean bl) {
		this.ticketManager.setChunkForced(chunkPos, bl);
	}

	public void updateCameraPosition(ServerPlayerEntity serverPlayerEntity) {
		this.threadedAnvilChunkStorage.updateCameraPosition(serverPlayerEntity);
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

	public void applyViewDistance(int i) {
		this.threadedAnvilChunkStorage.setViewDistance(i);
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

	public PointOfInterestStorage getPointOfInterestStorage() {
		return this.threadedAnvilChunkStorage.getPointOfInterestStorage();
	}

	final class MainThreadExecutor extends ThreadExecutor<Runnable> {
		private MainThreadExecutor(World world) {
			super("Chunk source main thread executor for " + Registry.DIMENSION_TYPE.getId(world.getDimension().getType()));
		}

		@Override
		protected Runnable prepareRunnable(Runnable runnable) {
			return runnable;
		}

		@Override
		protected boolean canRun(Runnable runnable) {
			return true;
		}

		@Override
		protected boolean shouldRunAsync() {
			return true;
		}

		@Override
		protected Thread getThread() {
			return ServerChunkManager.this.serverThread;
		}

		@Override
		protected boolean executeQueuedTask() {
			if (ServerChunkManager.this.tick()) {
				return true;
			} else {
				ServerChunkManager.this.lightProvider.tick();
				return super.executeQueuedTask();
			}
		}
	}
}

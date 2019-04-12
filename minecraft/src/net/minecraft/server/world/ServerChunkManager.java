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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnHelper;
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
	private final ServerWorld world;
	private final Thread serverThread;
	private final ServerLightingProvider lightProvider;
	private final ServerChunkManager.MainThreadExecutor mainThreadExecutor;
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
			i,
			j
		);
		this.lightProvider = this.threadedAnvilChunkStorage.getLightProvider();
		this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
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

	@Nullable
	@Override
	public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFutureSyncOnMainThread(i, j, chunkStatus, bl);
		return ((Either)completableFuture.join()).map(chunk -> chunk, unloaded -> {
			if (bl) {
				throw new IllegalStateException("Chunk not there when requested: " + unloaded);
			} else {
				return null;
			}
		});
	}

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
				this.tick();
				chunkHolder = this.getChunkHolder(l);
				if (this.isMissingForLevel(chunkHolder, k)) {
					throw new IllegalStateException("No chunk holder after ticket has been added");
				}
			}
		}

		return this.isMissingForLevel(chunkHolder, k) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : chunkHolder.getFutureChecked(chunkStatus);
	}

	private boolean isMissingForLevel(@Nullable ChunkHolder chunkHolder, int i) {
		return chunkHolder == null || chunkHolder.getLevel() > i;
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(i, j).toLong());
		int k = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL);
		return chunkHolder != null && chunkHolder.getLevel() <= k
			? ((Either)chunkHolder.getFutureChecked(ChunkStatus.FULL).getNow(ChunkHolder.UNLOADED_CHUNK)).left().isPresent()
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
		if (this.ticketManager.tick(this.threadedAnvilChunkStorage)) {
			this.threadedAnvilChunkStorage.updateHolderMap();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldTickEntity(Entity entity) {
		ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4));
		if (chunkHolder == null) {
			return false;
		} else {
			Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)chunkHolder.getEntityTickingFuture()
				.getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
			return either.left().isPresent();
		}
	}

	public void save(boolean bl) {
		this.threadedAnvilChunkStorage.save(bl);
	}

	@Override
	public void close() throws IOException {
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
	}

	private void tickChunks() {
		long l = this.world.getTime();
		long m = l - this.lastMobSpawningTime;
		this.lastMobSpawningTime = l;
		LevelProperties levelProperties = this.world.getLevelProperties();
		boolean bl = levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
		boolean bl2 = this.world.getGameRules().getBoolean("doMobSpawning");
		if (!bl) {
			this.world.getProfiler().push("pollingChunks");
			int i = this.ticketManager.getLevelCount();
			int j = this.world.getGameRules().getInteger("randomTickSpeed");
			BlockPos blockPos = this.world.getSpawnPos();
			boolean bl3 = levelProperties.getTime() % 400L == 0L;
			EntityCategory[] entityCategorys = EntityCategory.values();
			Object2IntMap<EntityCategory> object2IntMap = this.world.getMobCountsByCategory();
			ObjectBidirectionalIterator<Entry<ChunkHolder>> objectBidirectionalIterator = this.threadedAnvilChunkStorage.entryIterator();

			while (objectBidirectionalIterator.hasNext()) {
				Entry<ChunkHolder> entry = (Entry<ChunkHolder>)objectBidirectionalIterator.next();
				ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
				Optional<WorldChunk> optional = ((Either)chunkHolder.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK)).left();
				if (optional.isPresent()) {
					WorldChunk worldChunk = (WorldChunk)optional.get();
					chunkHolder.flushUpdates(worldChunk);
					ChunkPos chunkPos = chunkHolder.getPos();
					if (!this.threadedAnvilChunkStorage.isTooFarFromPlayersToSpawnMobs(chunkPos)) {
						worldChunk.setInhabitedTime(worldChunk.getInhabitedTime() + m);
						if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk.getPos())) {
							this.world.getProfiler().push("spawner");

							for (EntityCategory entityCategory : entityCategorys) {
								if (entityCategory != EntityCategory.field_17715
									&& (!entityCategory.isPeaceful() || this.spawnAnimals)
									&& (entityCategory.isPeaceful() || this.spawnMonsters)
									&& (!entityCategory.isAnimal() || bl3)) {
									int k = entityCategory.getSpawnCap() * i / CHUNKS_ELIGIBLE_FOR_SPAWNING;
									if (object2IntMap.getInt(entityCategory) <= k) {
										SpawnHelper.spawnEntitiesInChunk(entityCategory, this.world, worldChunk, blockPos);
									}
								}
							}

							this.world.getProfiler().pop();
						}

						this.world.tickChunk(worldChunk, j);
					}
				}
			}

			this.world.getProfiler().pop();
			if (bl2) {
				this.chunkGenerator.spawnEntities(this.world, this.spawnMonsters, this.spawnAnimals);
			}
		}

		this.threadedAnvilChunkStorage.tickPlayerMovement();
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

	public void applyViewDistance(int i, int j) {
		this.threadedAnvilChunkStorage.setViewDistance(i, j);
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
			super("Chunk source main thread executor for " + Registry.DIMENSION.getId(world.getDimension().getType()));
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
		protected boolean isOffThread() {
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

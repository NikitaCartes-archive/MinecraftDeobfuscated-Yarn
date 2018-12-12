package net.minecraft.server.world;

import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3898;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkSaveHandler;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public class ServerChunkManager extends ChunkManager implements ServerChunkManagerEntry.class_3897 {
	private static final int CHUNKS_ELIGIBLE_FOR_SPAWNING = (int)Math.pow(17.0, 2.0);
	public static final int FULL_CHUNK_LEVEL = 33 + ChunkStatus.getStatusCount();
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private final ChunkTicketManager ticketManager;
	private final ChunkGenerator<?> chunkGenerator;
	private final World world;
	private final Thread serverThread;
	private final ServerLightingProvider lightProvider;
	private final Queue<Runnable> genQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	private final PlayerChunkWatchingManager players = new PlayerChunkWatchingManager();
	private final class_3898 field_17254;
	private int maxWatchDistance;
	private long field_13928;
	private boolean spawnMonsters = true;
	private boolean spawnAnimals = true;

	public ServerChunkManager(World world, ChunkSaveHandler chunkSaveHandler, Executor executor, ChunkGenerator<?> chunkGenerator, int i) {
		this.world = world;
		this.chunkGenerator = chunkGenerator;
		this.serverThread = Thread.currentThread();
		this.field_17254 = new class_3898(executor, this, this.genQueue::add, chunkSaveHandler, this, this.getWorld(), this.getChunkGenerator());
		this.lightProvider = this.field_17254.getLightProvider();
		this.ticketManager = this.field_17254.getTicketManager();
		this.applyViewDistance(i);
	}

	public ServerLightingProvider getLightingProvider() {
		return this.lightProvider;
	}

	@Nullable
	private ServerChunkManagerEntry getEntry(long l) {
		return this.field_17254.getEntry(l);
	}

	private static double getSqDistance(ChunkPos chunkPos, Entity entity) {
		double d = (double)(chunkPos.x * 16 + 8);
		double e = (double)(chunkPos.z * 16 + 8);
		double f = d - entity.x;
		double g = e - entity.z;
		return f * f + g * g;
	}

	private static int method_17295(ChunkPos chunkPos, ServerPlayerEntity serverPlayerEntity, boolean bl) {
		int i;
		int j;
		if (bl) {
			ChunkPos chunkPos2 = serverPlayerEntity.getChunkPos();
			i = chunkPos2.x;
			j = chunkPos2.z;
		} else {
			i = MathHelper.floor(serverPlayerEntity.x / 16.0);
			j = MathHelper.floor(serverPlayerEntity.z / 16.0);
		}

		return getWatchDistance(chunkPos, i, j);
	}

	public static int getWatchDistance(ChunkPos chunkPos, Entity entity) {
		return getWatchDistance(chunkPos, MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0));
	}

	private static int getWatchDistance(ChunkPos chunkPos, int i, int j) {
		int k = chunkPos.x - i;
		int l = chunkPos.z - j;
		return Math.max(Math.abs(k), Math.abs(l));
	}

	public void method_14142(int i) {
		this.field_17254.method_17213(i);
	}

	public String getProgressString() {
		return this.field_17254.method_17267();
	}

	public int method_17301() {
		return this.field_17254.method_17253();
	}

	private int getRemainingWatchDistance() {
		return 33 - this.maxWatchDistance;
	}

	@Nullable
	@Override
	public Chunk getChunkSync(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.getChunkSyncIfServerThread(i, j, chunkStatus, bl);
		return ((Either)completableFuture.join()).map(chunk -> chunk, unloaded -> {
			if (bl) {
				throw new IllegalStateException("Chunk not there when requested: " + unloaded);
			} else {
				return null;
			}
		});
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> getChunkSyncIfServerThread(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		boolean bl2 = Thread.currentThread() == this.serverThread;
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture;
		if (bl2) {
			completableFuture = this.getChunkAsync(i, j, chunkStatus, bl);

			while (!completableFuture.isDone()) {
				if (!this.method_17302()) {
					Thread.yield();
				}
			}
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.getChunkAsync(i, j, chunkStatus, bl), this.genQueue::add)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return completableFuture;
	}

	private CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> getChunkAsync(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(i, j);
		long l = chunkPos.toLong();
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(l);
		int k = 33 + ChunkStatus.getIndex(chunkStatus);
		if (bl && (serverChunkManagerEntry == null || serverChunkManagerEntry.getLevel() > k)) {
			this.update();
			serverChunkManagerEntry = this.getEntry(l);
			if (serverChunkManagerEntry == null || serverChunkManagerEntry.getLevel() > k) {
				this.ticketManager.addTicketAtLevel(ChunkTicketType.UNKNOWN, chunkPos, k, chunkPos);
				this.update();
				serverChunkManagerEntry = this.getEntry(l);
			}

			if (serverChunkManagerEntry == null || serverChunkManagerEntry.getLevel() > k) {
				throw new IllegalStateException("No chunk holder after ticket has been added");
			}
		}

		if (serverChunkManagerEntry != null && serverChunkManagerEntry.getLevel() <= k) {
			return serverChunkManagerEntry.getChunkMinimumStatus(chunkStatus);
		} else if (bl) {
			throw new IllegalStateException("No future after ticket has been added");
		} else {
			return ServerChunkManagerEntry.UNLOADED_CHUNK_FUTURE;
		}
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(new ChunkPos(i, j).toLong());
		int k = 33 + ChunkStatus.getIndex(ChunkStatus.FULL);
		return serverChunkManagerEntry != null && serverChunkManagerEntry.getLevel() <= k
			? ((Either)serverChunkManagerEntry.getChunkMinimumStatus(ChunkStatus.FULL).getNow(ServerChunkManagerEntry.UNLOADED_CHUNK)).left().isPresent()
			: false;
	}

	@Override
	public BlockView getChunk(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(l);
		if (serverChunkManagerEntry == null) {
			return null;
		} else {
			int k = CHUNK_STATUSES.size() - 1;

			while (true) {
				ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(k);
				Optional<Chunk> optional = ((Either)serverChunkManagerEntry.getChunkForStatus(chunkStatus).getNow(ServerChunkManagerEntry.UNLOADED_CHUNK)).left();
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

	public World getWorld() {
		return this.world;
	}

	public boolean method_17302() {
		if (this.update()) {
			return true;
		} else {
			Runnable runnable = (Runnable)this.genQueue.poll();
			if (runnable != null) {
				runnable.run();
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean update() {
		if (this.ticketManager.update(this.field_17254)) {
			this.field_17254.syncPosToEntryMap();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_12125(Entity entity) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4));
		if (serverChunkManagerEntry == null) {
			return false;
		} else {
			Either<WorldChunk, ServerChunkManagerEntry.Unloaded> either = (Either<WorldChunk, ServerChunkManagerEntry.Unloaded>)serverChunkManagerEntry.method_14003()
				.getNow(ServerChunkManagerEntry.UNLOADED_WORLD_CHUNK);
			return either.left().isPresent();
		}
	}

	public void save(boolean bl) {
		this.field_17254.save(bl);
	}

	@Override
	public void close() {
		this.lightProvider.close();
		this.field_17254.close();
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.world.getProfiler().push("purge");
		this.ticketManager.method_14045();
		this.update();
		this.world.getProfiler().swap("light");
		this.lightProvider.method_17303();
		this.world.getProfiler().swap("chunks");
		this.doMobSpawning();
		this.world.getProfiler().swap("unload");
		this.field_17254.method_17233(booleanSupplier);
		this.world.getProfiler().swap("promote_chunks");
		this.world.getProfiler().swap("storage");
		this.field_17254.method_17250(booleanSupplier);
		this.world.getProfiler().pop();
	}

	private void doMobSpawning() {
		long l = this.world.getTime();
		long m = l - this.field_13928;
		this.field_13928 = l;
		LevelProperties levelProperties = this.world.getLevelProperties();
		boolean bl = levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
		boolean bl2 = this.world.getGameRules().getBoolean("doMobSpawning");
		if (!bl) {
			this.world.getProfiler().push("pollingChunks");
			int i = this.ticketManager.method_14052();
			int j = this.world.getGameRules().getInteger("randomTickSpeed");
			BlockPos blockPos = this.world.getSpawnPos();
			boolean bl3 = levelProperties.getTime() % 400L == 0L;
			ObjectBidirectionalIterator<Entry<ServerChunkManagerEntry>> objectBidirectionalIterator = this.field_17254.method_17264();

			while (objectBidirectionalIterator.hasNext()) {
				Entry<ServerChunkManagerEntry> entry = (Entry<ServerChunkManagerEntry>)objectBidirectionalIterator.next();
				ServerChunkManagerEntry serverChunkManagerEntry = (ServerChunkManagerEntry)entry.getValue();
				WorldChunk worldChunk = serverChunkManagerEntry.getChunk();
				if (worldChunk != null) {
					serverChunkManagerEntry.flushUpdates(worldChunk);
					ChunkPos chunkPos = serverChunkManagerEntry.getPos();
					if (!this.players.getPlayersWatchingChunk(chunkPos.toLong()).noneMatch(serverPlayerEntity -> getSqDistance(chunkPos, serverPlayerEntity) < 16384.0)) {
						worldChunk.setInhabitedTime(worldChunk.getInhabitedTime() + m);
						if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk.getPos())) {
							this.world.getProfiler().push("spawner");

							for (EntityCategory entityCategory : EntityCategory.values()) {
								if ((!entityCategory.isPeaceful() || this.spawnAnimals) && (entityCategory.isPeaceful() || this.spawnMonsters) && (!entityCategory.isAnimal() || bl3)) {
									int k = entityCategory.getSpawnCap() * i / CHUNKS_ELIGIBLE_FOR_SPAWNING;
									int n = this.world.countTransientEntities(entityCategory.getEntityClass(), k);
									if (n <= k) {
										SpawnHelper.method_8663(entityCategory, this.world, worldChunk, blockPos);
									}
								}
							}

							this.world.getProfiler().pop();
						}

						this.world.method_8462(worldChunk, j);
					}
				}
			}

			this.world.getProfiler().pop();
			if (bl2) {
				this.chunkGenerator.spawnEntities(this.world, this.spawnMonsters, this.spawnAnimals);
			}
		}
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
		return this.field_17254.method_17260();
	}

	public void markForUpdate(BlockPos blockPos) {
		int i = blockPos.getX() >> 4;
		int j = blockPos.getZ() >> 4;
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(ChunkPos.toLong(i, j));
		if (serverChunkManagerEntry != null) {
			serverChunkManagerEntry.markForUpdate(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15);
		}
	}

	@Override
	public void onLightUpdate(LightType lightType, int i, int j, int k) {
		this.genQueue.add((Runnable)() -> {
			ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(ChunkPos.toLong(i, k));
			if (serverChunkManagerEntry != null) {
				serverChunkManagerEntry.method_14012(lightType, j);
			}
		});
	}

	public <T> void method_17297(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.ticketManager.addTicket(chunkTicketType, chunkPos, i, object);
	}

	public <T> void method_17300(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.ticketManager.removeTicket(chunkTicketType, chunkPos, i, object);
	}

	@Override
	public void method_12124(ChunkPos chunkPos, boolean bl) {
		this.ticketManager.setChunkForced(chunkPos, bl);
	}

	private boolean isWatchDisabled(ServerPlayerEntity serverPlayerEntity) {
		return serverPlayerEntity.isSpectator() && !this.world.getGameRules().getBoolean("spectatorsGenerateChunks");
	}

	public void addPlayer(ServerPlayerEntity serverPlayerEntity) {
		this.addOrRemovePlayer(serverPlayerEntity, true);
	}

	public void removePlayer(ServerPlayerEntity serverPlayerEntity) {
		this.addOrRemovePlayer(serverPlayerEntity, false);
	}

	private void addOrRemovePlayer(ServerPlayerEntity serverPlayerEntity, boolean bl) {
		boolean bl2 = this.isWatchDisabled(serverPlayerEntity);
		boolean bl3 = this.players.isWatchDisabled(serverPlayerEntity);
		int i = MathHelper.floor(serverPlayerEntity.x) >> 4;
		int j = MathHelper.floor(serverPlayerEntity.z) >> 4;
		long l = ChunkPos.toLong(i, j);
		if (bl) {
			this.players.add(l, serverPlayerEntity, bl2);
			if (!bl2) {
				this.ticketManager.method_14048(l, this.getRemainingWatchDistance(), serverPlayerEntity);
			}
		} else {
			long m = serverPlayerEntity.getChunkPos().toLong();
			this.players.remove(m, serverPlayerEntity);
			if (!bl2) {
				this.ticketManager.method_14051(m, serverPlayerEntity);
			}
		}

		for (int k = i - this.maxWatchDistance; k <= i + this.maxWatchDistance; k++) {
			for (int n = j - this.maxWatchDistance; n <= j + this.maxWatchDistance; n++) {
				ChunkPos chunkPos = new ChunkPos(k, n);
				this.field_17254.method_17241(serverPlayerEntity, chunkPos, new Packet[2], !bl && !bl3, bl && !bl2);
			}
		}
	}

	public void updateChunkWatchingForPlayer(ServerPlayerEntity serverPlayerEntity) {
		int i = MathHelper.floor(serverPlayerEntity.x) >> 4;
		int j = MathHelper.floor(serverPlayerEntity.z) >> 4;
		int k = serverPlayerEntity.getChunkPos().x;
		int l = serverPlayerEntity.getChunkPos().z;
		long m = ChunkPos.toLong(k, l);
		long n = ChunkPos.toLong(i, j);
		boolean bl = this.players.isWatchDisabled(serverPlayerEntity);
		boolean bl2 = this.isWatchDisabled(serverPlayerEntity);
		boolean bl3 = m != n;
		if (bl3 || bl != bl2) {
			if (!bl && bl2 || bl3) {
				this.ticketManager.method_14051(m, serverPlayerEntity);
			}

			if (bl && !bl2 || bl3) {
				this.ticketManager.method_14048(n, this.getRemainingWatchDistance(), serverPlayerEntity);
			}

			if (!bl && bl2) {
				this.players.disableWatch(serverPlayerEntity);
			}

			if (bl && !bl2) {
				this.players.enableWatch(serverPlayerEntity);
			}

			if (bl3) {
				this.players.movePlayer(m, n, serverPlayerEntity);
			}

			int o = Math.min(i, k) - this.maxWatchDistance;
			int p = Math.min(j, l) - this.maxWatchDistance;
			int q = Math.max(i, k) + this.maxWatchDistance;
			int r = Math.max(j, l) + this.maxWatchDistance;

			for (int s = o; s <= q; s++) {
				for (int t = p; t <= r; t++) {
					ChunkPos chunkPos = new ChunkPos(s, t);
					boolean bl4 = !bl && getWatchDistance(chunkPos, k, l) <= this.maxWatchDistance;
					boolean bl5 = !bl2 && getWatchDistance(chunkPos, i, j) <= this.maxWatchDistance;
					this.field_17254.method_17241(serverPlayerEntity, chunkPos, new Packet[2], bl4, bl5);
				}
			}
		}
	}

	public void applyViewDistance(int i) {
		int j = MathHelper.clamp(i + 1, 3, 33);
		if (j != this.maxWatchDistance) {
			this.field_17254.method_17214(this.maxWatchDistance, j);
			this.maxWatchDistance = j;
			int k = this.getRemainingWatchDistance();
			this.ticketManager.method_14049(k);
		}
	}

	public boolean method_14154(ServerPlayerEntity serverPlayerEntity, int i, int j) {
		ChunkPos chunkPos = new ChunkPos(i, j);
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(chunkPos.toLong());
		if (serverChunkManagerEntry == null) {
			return false;
		} else {
			return serverChunkManagerEntry.getChunk() != null ? method_17295(chunkPos, serverPlayerEntity, false) <= this.maxWatchDistance : false;
		}
	}

	@Override
	public Stream<ServerPlayerEntity> method_17211(ChunkPos chunkPos, boolean bl, boolean bl2) {
		return this.players.getPlayersWatchingChunk(chunkPos.toLong()).filter(serverPlayerEntity -> {
			int i = method_17295(chunkPos, serverPlayerEntity, bl2);
			return i > this.maxWatchDistance ? false : !bl || i == this.maxWatchDistance;
		});
	}

	@Override
	public void setMobSpawnOptions(boolean bl, boolean bl2) {
		this.spawnMonsters = bl;
		this.spawnAnimals = bl2;
	}

	@Environment(EnvType.CLIENT)
	public String method_17294(ChunkPos chunkPos) {
		return this.field_17254.method_17218(chunkPos);
	}
}

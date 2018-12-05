package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1948;
import net.minecraft.class_2839;
import net.minecraft.class_2843;
import net.minecraft.class_3204;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.util.MinecraftException;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
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
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerChunkManager extends ChunkManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_13920 = (int)Math.pow(17.0, 2.0);
	public static final int field_13922 = 33 + ChunkStatus.getOrderedSize();
	private static final List<ChunkStatus> chunkStatuses = ChunkStatus.createOrderedList();
	private final ServerChunkManager.class_3216 field_13926;
	private final ChunkGenerator<?> chunkGenerator;
	private final ChunkSaveHandler chunkSaveHandler;
	private final World world;
	private final Thread worldThread;
	private final Queue<Runnable> genQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	private final PlayerChunkWatchingManager players = new PlayerChunkWatchingManager();
	private final Long2ObjectLinkedOpenHashMap<ServerChunkManagerEntry> loadedChunkMap = new Long2ObjectLinkedOpenHashMap<>();
	private volatile Long2ObjectLinkedOpenHashMap<ServerChunkManagerEntry> field_16434 = this.loadedChunkMap.clone();
	private final LongSet field_13927 = new LongOpenHashSet();
	private boolean field_13936 = false;
	private int maxWatchDistance;
	private long field_13928;
	private final ExecutorService genWorkerPool;
	private final AtomicInteger genWorkerId = new AtomicInteger(1);
	private final List<Long2ObjectLinkedOpenHashMap<List<Runnable>>> field_13924 = (List<Long2ObjectLinkedOpenHashMap<List<Runnable>>>)IntStream.range(
			0, field_13922 + 2
		)
		.mapToObj(ix -> new Long2ObjectLinkedOpenHashMap())
		.collect(Collectors.toList());
	private int field_13925 = this.field_13924.size();
	private final ServerLightingProvider lightProvider;
	private final AtomicInteger field_13932 = new AtomicInteger();
	private final AtomicInteger field_13944 = new AtomicInteger();
	private final AtomicInteger field_13919 = new AtomicInteger();
	private int field_13933 = 0;
	private boolean spawnMonsters = true;
	private boolean spawnAnimals = true;

	public ServerChunkManager(World world, ChunkSaveHandler chunkSaveHandler, ChunkGenerator<?> chunkGenerator, int i, int j) {
		this.world = world;
		this.chunkSaveHandler = chunkSaveHandler;
		this.chunkGenerator = chunkGenerator;
		this.worldThread = Thread.currentThread();
		if (j <= 0) {
			this.genWorkerPool = MoreExecutors.newDirectExecutorService();
		} else {
			this.genWorkerPool = new ForkJoinPool(j, forkJoinPool -> {
				ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool) {
				};
				forkJoinWorkerThread.setName("WorldGen-Worker-" + this.genWorkerId.getAndIncrement());
				return forkJoinWorkerThread;
			}, (thread, throwable) -> LOGGER.error(String.format("Caught exception in thread %s", thread), throwable), true);
		}

		this.lightProvider = new ServerLightingProvider(this, this.genQueue::addAll, world.getDimension().method_12451());
		this.field_13926 = new ServerChunkManager.class_3216();
		this.applyViewDistance(i);
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.lightProvider;
	}

	public static double getSqDistance(ChunkPos chunkPos, Entity entity) {
		double d = (double)(chunkPos.x * 16 + 8);
		double e = (double)(chunkPos.z * 16 + 8);
		double f = d - entity.x;
		double g = e - entity.z;
		return f * f + g * g;
	}

	public static int getWatchDistance(ChunkPos chunkPos, Entity entity) {
		return getWatchDistance(chunkPos, MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0));
	}

	private static int getWatchDistance(ChunkPos chunkPos, int i, int j) {
		int k = chunkPos.x - i;
		int l = chunkPos.z - j;
		return Math.max(Math.abs(k), Math.abs(l));
	}

	public CompletableFuture<Chunk> method_14140(class_2839 arg) {
		return CompletableFuture.supplyAsync(() -> {
			ChunkPos chunkPos = arg.getPos();
			WorldChunk worldChunk;
			if (arg instanceof ReadOnlyChunk) {
				worldChunk = ((ReadOnlyChunk)arg).method_12240();
			} else {
				worldChunk = new WorldChunk(this.world, arg, chunkPos.x, chunkPos.z);
			}

			this.lightProvider.method_14272(worldChunk::loadToWorld);
			ServerChunkManagerEntry serverChunkManagerEntry = this.method_14131(chunkPos.toLong());
			worldChunk.method_12207(() -> ServerChunkManagerEntry.method_14008(serverChunkManagerEntry.method_14005()));
			return worldChunk;
		}, this.genQueue::add);
	}

	public CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> method_16175(ChunkPos chunkPos) {
		CompletableFuture<Either<List<Chunk>, ServerChunkManagerEntry.Unloaded>> completableFuture = this.method_16154(chunkPos, 1, i -> ChunkStatus.field_12803);
		return completableFuture.thenApplyAsync(either -> {
			Optional<ServerChunkManagerEntry.Unloaded> optional = ((Either)this.method_16154(chunkPos, 1, i -> ChunkStatus.field_12803).getNow(either)).right();
			return optional.isPresent() ? Either.right(optional.get()) : either.mapLeft(list -> {
				WorldChunk worldChunk = (WorldChunk)list.get(list.size() / 2);
				if (!Objects.equals(chunkPos, worldChunk.getPos())) {
					throw new IllegalStateException();
				} else {
					worldChunk.method_12221();
					this.field_13932.getAndIncrement();
					Packet<?>[] packets = new Packet[2];
					this.players.getPlayersWatchingChunk(chunkPos.toLong()).forEach(serverPlayerEntity -> {
						ChunkPos chunkPos2 = serverPlayerEntity.getChunkPos();
						if (getWatchDistance(chunkPos, chunkPos2.x, chunkPos2.z) <= this.maxWatchDistance) {
							if (packets[0] == null) {
								packets[0] = new ChunkDataClientPacket(worldChunk, 65535);
								packets[1] = new LightUpdateClientPacket(worldChunk.getPos(), this.getLightingProvider());
							}

							serverPlayerEntity.sendInitialChunkPackets(chunkPos, packets[0], packets[1]);
						}
					});
					return worldChunk;
				}
			});
		}, this.lightProvider::method_14272);
	}

	public CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> method_14123(ChunkPos chunkPos) {
		return this.method_16154(chunkPos, 2, i -> ChunkStatus.field_12803).thenApply(either -> {
			Optional<ServerChunkManagerEntry.Unloaded> optional = ((Either)this.method_16154(chunkPos, 1, i -> ChunkStatus.field_12803).getNow(either)).right();
			return optional.isPresent() ? Either.right(optional.get()) : either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2));
		});
	}

	public void method_14142(int i) {
		this.field_13932.set(0);
		this.field_13933 = i;
		this.field_13944.set(0);
		this.field_13919.set(0);
	}

	public String getProgressString() {
		int i = this.field_13944.get();
		int j = this.field_13919.get();
		int k = this.method_14155();
		int l = this.field_13932.get();
		return String.format(
			"%5d | %5d | %5d | %5d (%3d%% %3d%%)", i, i - j, k, l, (int)((float)j * 100.0F / (float)i), (int)((float)l * 100.0F / (float)this.field_13933)
		);
	}

	private int getRemainingWatchDistance() {
		return 33 - this.maxWatchDistance;
	}

	public boolean method_14107() {
		return this.field_13926.method_14055()
			|| this.field_13924.stream().anyMatch(long2ObjectLinkedOpenHashMap -> !long2ObjectLinkedOpenHashMap.isEmpty())
			|| this.method_14155() != 0
			|| this.getLightingProvider().method_15561();
	}

	@Nullable
	private ServerChunkManagerEntry method_14131(long l) {
		return this.loadedChunkMap.get(l);
	}

	@Nullable
	private ServerChunkManagerEntry method_16174(long l) {
		return this.field_16434.get(l);
	}

	@Nullable
	@Override
	public Chunk getChunkSync(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		boolean bl2 = Thread.currentThread() == this.worldThread;
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture;
		if (bl2) {
			completableFuture = this.getChunkAsync(i, j, chunkStatus, bl);

			while (!completableFuture.isDone()) {
				if (!this.method_16165()) {
					Thread.yield();
				}
			}
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.getChunkAsync(i, j, chunkStatus, bl), this.genQueue::add)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return ((Either)completableFuture.join()).map(chunk -> chunk, unloaded -> {
			if (bl) {
				throw new IllegalStateException("Chunk not there when requested: " + unloaded);
			} else {
				return null;
			}
		});
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> getChunkAsync(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(i, j);
		long l = chunkPos.toLong();
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(l);
		int k = 33 + ChunkStatus.method_12175(chunkStatus);
		if (bl && (serverChunkManagerEntry == null || serverChunkManagerEntry.method_14005() > k)) {
			this.method_16155();
			serverChunkManagerEntry = this.method_16174(l);
			if (serverChunkManagerEntry == null || serverChunkManagerEntry.method_14005() > k) {
				this.field_13926.method_14054(k, chunkPos);
				this.method_16155();
				serverChunkManagerEntry = this.method_16174(l);
			}

			if (serverChunkManagerEntry == null || serverChunkManagerEntry.method_14005() > k) {
				throw new IllegalStateException("No chunk holder after ticket has been added");
			}
		}

		if (serverChunkManagerEntry != null && serverChunkManagerEntry.method_14005() <= k) {
			return serverChunkManagerEntry.method_13997(chunkStatus);
		} else if (bl) {
			throw new IllegalStateException("No future after ticket has been added");
		} else {
			return ServerChunkManagerEntry.UNLOADED_CHUNK_FUTURE;
		}
	}

	@Override
	public boolean method_12123(int i, int j) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(new ChunkPos(i, j).toLong());
		int k = 33 + ChunkStatus.method_12175(ChunkStatus.field_12803);
		return serverChunkManagerEntry != null && serverChunkManagerEntry.method_14005() <= k
			? ((Either)serverChunkManagerEntry.method_13997(ChunkStatus.field_12803).getNow(ServerChunkManagerEntry.UNLOADED_CHUNK)).left().isPresent()
			: false;
	}

	@Override
	public BlockView get(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(l);
		if (serverChunkManagerEntry == null) {
			return null;
		} else {
			int k = chunkStatuses.size() - 1;

			while (true) {
				ChunkStatus chunkStatus = (ChunkStatus)chunkStatuses.get(k);
				Optional<Chunk> optional = ((Either)serverChunkManagerEntry.method_16146(chunkStatus).getNow(ServerChunkManagerEntry.UNLOADED_CHUNK)).left();
				if (optional.isPresent()) {
					return (BlockView)optional.get();
				}

				if (chunkStatus == ChunkStatus.field_12805.getPrevious()) {
					return null;
				}

				k--;
			}
		}
	}

	public World getWorld() {
		return this.world;
	}

	public boolean method_16165() {
		if (this.method_16155()) {
			return true;
		} else {
			Runnable runnable = (Runnable)this.genQueue.poll();
			if (runnable != null) {
				runnable.run();
				return true;
			} else if (64 - this.method_14155() > 0 && this.field_13925 < this.field_13924.size()) {
				Long2ObjectLinkedOpenHashMap<List<Runnable>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Runnable>>)this.field_13924
					.get(this.field_13925);
				List<Runnable> list = long2ObjectLinkedOpenHashMap.removeFirst();

				while (this.field_13925 < this.field_13924.size() && ((Long2ObjectLinkedOpenHashMap)this.field_13924.get(this.field_13925)).isEmpty()) {
					this.field_13925++;
				}

				this.field_13919.addAndGet(list.size());
				list.forEach(Runnable::run);
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean method_16155() {
		if (this.field_13926.method_15892(this)) {
			this.method_16166();
			return true;
		} else {
			return false;
		}
	}

	public void updateLight() {
		this.lightProvider.method_14277();
	}

	@Override
	public boolean method_12125(Entity entity) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(ChunkPos.toLong(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4));
		if (serverChunkManagerEntry == null) {
			return false;
		} else {
			Either<WorldChunk, ServerChunkManagerEntry.Unloaded> either = (Either<WorldChunk, ServerChunkManagerEntry.Unloaded>)serverChunkManagerEntry.method_14003()
				.getNow(ServerChunkManagerEntry.UNLOADED_WORLD_CHUNK);
			return either.left().isPresent();
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_14124(ChunkPos chunkPos) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(chunkPos.toLong());
		if (serverChunkManagerEntry == null) {
			return "null";
		} else {
			String string = serverChunkManagerEntry.method_14005() + "\n";
			ChunkStatus chunkStatus = serverChunkManagerEntry.method_16141();
			Chunk chunk = serverChunkManagerEntry.method_14010();
			if (chunkStatus != null) {
				string = string + "St: §" + chunkStatus.getOrderId() + chunkStatus + '§' + "r\n";
			}

			if (chunk != null) {
				string = string + "Ch: §" + chunk.getStatus().getOrderId() + chunk.getStatus() + '§' + "r\n";
			}

			ServerChunkManagerEntry.class_3194 lv = serverChunkManagerEntry.method_13995();
			string = string + "§" + lv.ordinal() + lv;
			return string + '§' + "r";
		}
	}

	private int method_14155() {
		if (this.genWorkerPool instanceof ForkJoinPool) {
			ForkJoinPool forkJoinPool = (ForkJoinPool)this.genWorkerPool;
			return forkJoinPool.getQueuedSubmissionCount() + (int)forkJoinPool.getQueuedTaskCount();
		} else {
			return 0;
		}
	}

	private void method_16149(long l, int i, Runnable runnable) {
		((List)((Long2ObjectLinkedOpenHashMap)this.field_13924.get(i)).computeIfAbsent(l, lx -> Lists.newArrayList())).add(runnable);
		this.field_13925 = Math.min(this.field_13925, i);
		this.field_13944.getAndIncrement();
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> method_16163(ChunkPos chunkPos, int i, ChunkStatus chunkStatus) {
		if (chunkStatus == ChunkStatus.field_12798) {
			return CompletableFuture.supplyAsync(() -> {
				Chunk chunk = this.chunkSaveHandler.method_12411(this.world, chunkPos.x, chunkPos.z);
				if (chunk != null) {
					chunk.method_12043(this.world.getTime());
					return Either.left(chunk);
				} else {
					return Either.left(new class_2839(chunkPos, class_2843.field_12950));
				}
			}, runnable -> this.method_16149(chunkPos.toLong(), i, runnable));
		} else {
			CompletableFuture<Either<List<Chunk>, ServerChunkManagerEntry.Unloaded>> completableFuture = this.method_16154(
				chunkPos, chunkStatus.method_12152(), ix -> this.method_14160(chunkStatus, ix)
			);
			return CompletableFuture.supplyAsync(() -> null, runnable -> this.method_16149(chunkPos.toLong(), i, runnable))
				.thenComposeAsync(object -> completableFuture.thenComposeAsync(either -> (CompletableFuture)either.map(list -> {
							try {
								return chunkStatus.method_12154(this, this.chunkGenerator, list).thenApply(Either::left);
							} catch (Exception var7) {
								CrashReport crashReport = CrashReport.create(var7, "Exception generating new chunk");
								CrashReportElement crashReportElement = crashReport.addElement("Chunk to be generated");
								crashReportElement.add("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
								crashReportElement.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
								crashReportElement.add("Generator", this.chunkGenerator);
								throw new CrashException(crashReport);
							}
						}, unloaded -> CompletableFuture.completedFuture(Either.right(unloaded))), this.genWorkerPool));
		}
	}

	private CompletableFuture<Either<List<Chunk>, ServerChunkManagerEntry.Unloaded>> method_16154(ChunkPos chunkPos, int i, IntFunction<ChunkStatus> intFunction) {
		List<CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>> list = Lists.<CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>>newArrayList();
		int j = chunkPos.x;
		int k = chunkPos.z;

		for (int l = -i; l <= i; l++) {
			for (int m = -i; m <= i; m++) {
				int n = Math.max(Math.abs(m), Math.abs(l));
				ChunkPos chunkPos2 = new ChunkPos(j + m, k + l);
				long o = chunkPos2.toLong();
				ServerChunkManagerEntry serverChunkManagerEntry = this.method_14131(o);
				if (serverChunkManagerEntry == null) {
					return CompletableFuture.completedFuture(Either.right(ServerChunkManagerEntry.Unloaded.INSTANCE));
				}

				ChunkStatus chunkStatus = (ChunkStatus)intFunction.apply(n);
				CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = serverChunkManagerEntry.method_13993(chunkStatus, this);
				list.add(completableFuture);
			}
		}

		CompletableFuture<List<Either<Chunk, ServerChunkManagerEntry.Unloaded>>> completableFuture2 = SystemUtil.method_652(list);
		return completableFuture2.thenApply(listx -> {
			List<Chunk> list2 = Lists.<Chunk>newArrayList();

			for (Either<Chunk, ServerChunkManagerEntry.Unloaded> either : listx) {
				Optional<Chunk> optional = either.left();
				if (!optional.isPresent()) {
					return Either.right(either.right().get());
				}

				list2.add(optional.get());
			}

			return Either.left(list2);
		});
	}

	private ChunkStatus method_14160(ChunkStatus chunkStatus, int i) {
		ChunkStatus chunkStatus2;
		if (i == 0) {
			chunkStatus2 = chunkStatus.getPrevious();
		} else {
			chunkStatus2 = ChunkStatus.getOrdered(ChunkStatus.method_12175(chunkStatus) + i);
		}

		return chunkStatus2;
	}

	@Nullable
	private ServerChunkManagerEntry method_14150(long l, int i, @Nullable ServerChunkManagerEntry serverChunkManagerEntry, int j) {
		if (i > field_13922) {
			if (j <= field_13922) {
				this.field_13927.add(l);
				if (serverChunkManagerEntry != null) {
					serverChunkManagerEntry.method_15890(i);
				}
			}
		} else {
			if (serverChunkManagerEntry != null) {
				Long2ObjectMap<List<Runnable>> long2ObjectMap = (Long2ObjectMap<List<Runnable>>)this.field_13924.get(j);
				List<Runnable> list = long2ObjectMap.remove(l);
				if (j == this.field_13925) {
					while (this.field_13925 < this.field_13924.size() && ((Long2ObjectLinkedOpenHashMap)this.field_13924.get(this.field_13925)).isEmpty()) {
						this.field_13925++;
					}
				}

				if (list != null) {
					((List)((Long2ObjectLinkedOpenHashMap)this.field_13924.get(i)).computeIfAbsent(l, lx -> Lists.newArrayList())).addAll(list);
					this.field_13925 = Math.min(this.field_13925, i);
				}

				this.field_13927.remove(l);
			}

			if (serverChunkManagerEntry == null) {
				serverChunkManagerEntry = new ServerChunkManagerEntry(new ChunkPos(l), i, this.getLightingProvider(), this.players);
				this.loadedChunkMap.put(l, serverChunkManagerEntry);
				this.field_13936 = true;
			} else {
				serverChunkManagerEntry.method_15890(i);
			}
		}

		return serverChunkManagerEntry;
	}

	private void method_14106(Chunk chunk, boolean bl) {
		try {
			if (bl && chunk instanceof WorldChunk) {
				((WorldChunk)chunk).unloadFromWorld();
			}

			if (!chunk.method_12044()) {
				return;
			}

			chunk.method_12043(this.world.getTime());
			this.chunkSaveHandler.method_12410(this.world, chunk);
			chunk.method_12008(false);
		} catch (IOException var4) {
			LOGGER.error("Couldn't save chunk", (Throwable)var4);
		} catch (MinecraftException var5) {
			LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)var5);
		}
	}

	public void save(boolean bl) {
		for (ServerChunkManagerEntry serverChunkManagerEntry : this.field_16434.values()) {
			WorldChunk worldChunk = serverChunkManagerEntry.getChunk();
			if (worldChunk != null) {
				this.method_14106(worldChunk, false);
			}
		}

		if (bl) {
			this.chunkSaveHandler.save();
		}
	}

	@Override
	public void close() {
		try {
			this.lightProvider.close();
			this.genWorkerPool.shutdown();
			if (!this.genWorkerPool.awaitTermination(3L, TimeUnit.SECONDS)) {
				this.genWorkerPool.shutdownNow();
			}

			this.field_16434.values().forEach(serverChunkManagerEntry -> {
				Chunk chunk = serverChunkManagerEntry.method_14010();
				if (chunk != null) {
					this.method_14106(chunk, false);
				}
			});
			this.chunkSaveHandler.close();
		} catch (InterruptedException var2) {
			LOGGER.error("Couldn't stop taskManager", (Throwable)var2);
		}
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.world.getProfiler().begin("purge");
		this.field_13926.method_14045();
		this.method_16166();
		this.world.getProfiler().endBegin("light");
		this.updateLight();
		this.world.getProfiler().endBegin("chunks");
		this.doMobSpawning();
		this.world.getProfiler().endBegin("unload");
		this.method_14111(booleanSupplier);
		this.world.getProfiler().endBegin("promote_chunks");
		this.world.getProfiler().endBegin("storage");

		boolean bl;
		do {
			bl = this.chunkSaveHandler.method_12412();
		} while (bl && booleanSupplier.getAsBoolean());

		this.world.getProfiler().end();
	}

	private void method_14111(BooleanSupplier booleanSupplier) {
		if (!this.world.method_8458()) {
			LongIterator longIterator = this.field_13927.iterator();

			for (int i = 0; longIterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.field_13927.size() > 2000); longIterator.remove()) {
				long l = longIterator.nextLong();
				ServerChunkManagerEntry serverChunkManagerEntry = this.loadedChunkMap.remove(l);
				if (serverChunkManagerEntry != null) {
					this.field_13936 = true;
					i++;
					CompletableFuture<Chunk> completableFuture = serverChunkManagerEntry.getChunkFuture();
					completableFuture.thenAcceptAsync(chunk -> {
						if (chunk != null) {
							this.method_14106(chunk, true);

							for (int ix = 0; ix < 16; ix++) {
								this.lightProvider.method_15551(chunk.getPos().x, ix, chunk.getPos().z, true);
							}
						}
					}, this.genQueue::add);
				}
			}
		}
	}

	private void method_16166() {
		if (this.field_13936) {
			this.field_16434 = this.loadedChunkMap.clone();
			this.field_13936 = false;
		}
	}

	private void doMobSpawning() {
		long l = this.world.getTime();
		long m = l - this.field_13928;
		this.field_13928 = l;
		LevelProperties levelProperties = this.world.getLevelProperties();
		boolean bl = levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
		boolean bl2 = this.world.getGameRules().getBoolean("doMobSpawning");
		if (!bl) {
			this.world.getProfiler().begin("pollingChunks");
			int i = this.field_13926.method_14052();
			int j = this.world.getGameRules().getInteger("randomTickSpeed");
			BlockPos blockPos = this.world.method_8395();
			boolean bl3 = levelProperties.getTime() % 400L == 0L;
			ObjectBidirectionalIterator<Entry<ServerChunkManagerEntry>> objectBidirectionalIterator = this.field_16434.long2ObjectEntrySet().fastIterator();

			while (objectBidirectionalIterator.hasNext()) {
				Entry<ServerChunkManagerEntry> entry = (Entry<ServerChunkManagerEntry>)objectBidirectionalIterator.next();
				ServerChunkManagerEntry serverChunkManagerEntry = (ServerChunkManagerEntry)entry.getValue();
				WorldChunk worldChunk = serverChunkManagerEntry.getChunk();
				if (worldChunk != null) {
					serverChunkManagerEntry.flushUpdates(worldChunk, this.maxWatchDistance);
					ChunkPos chunkPos = serverChunkManagerEntry.getPos();
					if (!this.players.getPlayersWatchingChunk(chunkPos.toLong()).noneMatch(serverPlayerEntity -> getSqDistance(chunkPos, serverPlayerEntity) < 16384.0)) {
						worldChunk.method_12028(worldChunk.method_12033() + m);
						if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk.getPos())) {
							this.world.getProfiler().begin("spawner");

							for (EntityCategory entityCategory : EntityCategory.values()) {
								if ((!entityCategory.isPeaceful() || this.spawnAnimals) && (entityCategory.isPeaceful() || this.spawnMonsters) && (!entityCategory.isAnimal() || bl3)) {
									int k = entityCategory.getSpawnCap() * i / field_13920;
									int n = this.world.countTransientEntities(entityCategory.getEntityClass(), k);
									if (n <= k) {
										class_1948.method_8663(entityCategory, this.world, worldChunk, blockPos);
									}
								}
							}

							this.world.getProfiler().end();
						}

						this.world.method_8462(worldChunk, j);
					}
				}
			}

			this.world.getProfiler().end();
			if (bl2) {
				this.chunkGenerator.method_12099(this.world, this.spawnMonsters, this.spawnAnimals);
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
		return this.field_16434.size();
	}

	public void markForUpdate(BlockPos blockPos) {
		int i = blockPos.getX() >> 4;
		int j = blockPos.getZ() >> 4;
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(ChunkPos.toLong(i, j));
		if (serverChunkManagerEntry != null) {
			serverChunkManagerEntry.markForUpdate(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15);
		}
	}

	@Override
	public void method_12247(LightType lightType, int i, int j, int k) {
		this.lightProvider.method_14272(() -> {
			ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(ChunkPos.toLong(i, k));
			if (serverChunkManagerEntry != null) {
				serverChunkManagerEntry.method_14012(lightType, j);
			}
		});
	}

	public void method_14135(ChunkPos chunkPos, int i) {
		this.field_13926.method_14043(chunkPos, i);
	}

	@Override
	public void method_12124(ChunkPos chunkPos, boolean bl) {
		this.field_13926.method_14036(chunkPos, bl);
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
				this.field_13926.method_14048(l, this.getRemainingWatchDistance(), serverPlayerEntity);
			}
		} else {
			long m = serverPlayerEntity.getChunkPos().toLong();
			this.players.remove(m, serverPlayerEntity);
			if (!bl2) {
				this.field_13926.method_14051(m, serverPlayerEntity);
			}
		}

		for (int k = i - this.maxWatchDistance; k <= i + this.maxWatchDistance; k++) {
			for (int n = j - this.maxWatchDistance; n <= j + this.maxWatchDistance; n++) {
				ChunkPos chunkPos = new ChunkPos(k, n);
				this.sendChunkPackets(serverPlayerEntity, chunkPos, new Packet[2], !bl && !bl3, bl && !bl2);
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
				this.field_13926.method_14051(m, serverPlayerEntity);
			}

			if (bl && !bl2 || bl3) {
				this.field_13926.method_14048(n, this.getRemainingWatchDistance(), serverPlayerEntity);
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
					this.sendChunkPackets(serverPlayerEntity, chunkPos, new Packet[2], bl4, bl5);
				}
			}
		}
	}

	public void applyViewDistance(int i) {
		int j = MathHelper.clamp(i + 1, 3, 33);
		if (j != this.maxWatchDistance) {
			for (ServerChunkManagerEntry serverChunkManagerEntry : this.loadedChunkMap.values()) {
				ChunkPos chunkPos = serverChunkManagerEntry.getPos();
				Packet<?>[] packets = new Packet[2];
				this.players.getPlayersWatchingChunk(chunkPos.toLong()).forEach(serverPlayerEntity -> {
					int jx = getWatchDistance(chunkPos, serverPlayerEntity);
					boolean bl = jx <= this.maxWatchDistance;
					boolean bl2 = jx <= j;
					this.sendChunkPackets(serverPlayerEntity, chunkPos, packets, bl, bl2);
				});
			}

			this.maxWatchDistance = j;
			int k = this.getRemainingWatchDistance();
			this.field_13926.method_14049(k);
		}
	}

	private void sendChunkPackets(ServerPlayerEntity serverPlayerEntity, ChunkPos chunkPos, Packet<?>[] packets, boolean bl, boolean bl2) {
		if (serverPlayerEntity.world == this.world) {
			if (bl2 && !bl) {
				ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(chunkPos.toLong());
				if (serverChunkManagerEntry != null) {
					WorldChunk worldChunk = serverChunkManagerEntry.getChunk();
					if (worldChunk != null) {
						this.lightProvider.method_14272(() -> {
							if (packets[0] == null) {
								packets[0] = new ChunkDataClientPacket(worldChunk, 65535);
								packets[1] = new LightUpdateClientPacket(chunkPos, this.getLightingProvider());
							}

							serverPlayerEntity.sendInitialChunkPackets(chunkPos, packets[0], packets[1]);
						});
					}
				}
			}

			if (!bl2 && bl) {
				serverPlayerEntity.sendRemoveChunkPacket(chunkPos);
			}
		}
	}

	public boolean method_14154(ServerPlayerEntity serverPlayerEntity, int i, int j) {
		ChunkPos chunkPos = new ChunkPos(i, j);
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_16174(chunkPos.toLong());
		if (serverChunkManagerEntry == null) {
			return false;
		} else {
			return serverChunkManagerEntry.getChunk() != null ? getWatchDistance(chunkPos, serverPlayerEntity) <= this.maxWatchDistance : false;
		}
	}

	@Override
	public void setMobSpawnOptions(boolean bl, boolean bl2) {
		this.spawnMonsters = bl;
		this.spawnAnimals = bl2;
	}

	class class_3216 extends class_3204 {
		private class_3216() {
		}

		@Override
		protected boolean method_14035(long l) {
			return ServerChunkManager.this.field_13927.contains(l);
		}

		@Nullable
		@Override
		protected ServerChunkManagerEntry method_14038(long l) {
			return ServerChunkManager.this.method_14131(l);
		}

		@Nullable
		@Override
		protected ServerChunkManagerEntry method_14053(long l, int i, @Nullable ServerChunkManagerEntry serverChunkManagerEntry, int j) {
			return ServerChunkManager.this.method_14150(l, i, serverChunkManagerEntry, j);
		}
	}
}

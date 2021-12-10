package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SimulationDistanceLevelPropagator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedAnvilChunkStorage extends VersionedChunkStorage implements ChunkHolder.PlayersWatchingChunkProvider {
	private static final byte PROTO_CHUNK = -1;
	private static final byte UNMARKED_CHUNK = 0;
	private static final byte LEVEL_CHUNK = 1;
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_29674 = 200;
	private static final int field_36291 = 20;
	private static final int field_29675 = 3;
	public static final int field_29669 = 33;
	/**
	 * Specifies the maximum ticket level a chunk can be before a chunk's {@link net.minecraft.server.world.ChunkHolder.LevelType} is {@link net.minecraft.server.world.ChunkHolder.LevelType#BORDER}.
	 */
	public static final int MAX_LEVEL = 33 + ChunkStatus.getMaxDistanceFromFull();
	public static final int field_29670 = 31;
	private final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders = new Long2ObjectLinkedOpenHashMap<>();
	private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders = this.currentChunkHolders.clone();
	private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload = new Long2ObjectLinkedOpenHashMap<>();
	private final LongSet loadedChunks = new LongOpenHashSet();
	final ServerWorld world;
	private final ServerLightingProvider lightingProvider;
	private final ThreadExecutor<Runnable> mainThreadExecutor;
	private ChunkGenerator chunkGenerator;
	private final Supplier<PersistentStateManager> persistentStateManagerFactory;
	private final PointOfInterestStorage pointOfInterestStorage;
	final LongSet unloadedChunks = new LongOpenHashSet();
	private boolean chunkHolderListDirty;
	private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
	private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> worldGenExecutor;
	private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> mainExecutor;
	private final WorldGenerationProgressListener worldGenerationProgressListener;
	private final ChunkStatusChangeListener chunkStatusChangeListener;
	private final ThreadedAnvilChunkStorage.TicketManager ticketManager;
	private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();
	private final StructureManager structureManager;
	private final String saveDir;
	private final PlayerChunkWatchingManager playerChunkWatchingManager = new PlayerChunkWatchingManager();
	private final Int2ObjectMap<ThreadedAnvilChunkStorage.EntityTracker> entityTrackers = new Int2ObjectOpenHashMap<>();
	private final Long2ByteMap chunkToType = new Long2ByteOpenHashMap();
	private final Queue<Runnable> unloadTaskQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	int watchDistance;

	public ThreadedAnvilChunkStorage(
		ServerWorld world,
		LevelStorage.Session session,
		DataFixer dataFixer,
		StructureManager structureManager,
		Executor executor,
		ThreadExecutor<Runnable> mainThreadExecutor,
		ChunkProvider chunkProvider,
		ChunkGenerator chunkGenerator,
		WorldGenerationProgressListener worldGenerationProgressListener,
		ChunkStatusChangeListener chunkStatusChangeListener,
		Supplier<PersistentStateManager> persistentStateManagerFactory,
		int viewDistance,
		boolean dsync
	) {
		super(session.getWorldDirectory(world.getRegistryKey()).resolve("region"), dataFixer, dsync);
		this.structureManager = structureManager;
		Path path = session.getWorldDirectory(world.getRegistryKey());
		this.saveDir = path.getFileName().toString();
		this.world = world;
		this.chunkGenerator = chunkGenerator;
		this.mainThreadExecutor = mainThreadExecutor;
		TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(executor, "worldgen");
		MessageListener<Runnable> messageListener = MessageListener.create("main", mainThreadExecutor::send);
		this.worldGenerationProgressListener = worldGenerationProgressListener;
		this.chunkStatusChangeListener = chunkStatusChangeListener;
		TaskExecutor<Runnable> taskExecutor2 = TaskExecutor.create(executor, "light");
		this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(taskExecutor, messageListener, taskExecutor2), executor, Integer.MAX_VALUE);
		this.worldGenExecutor = this.chunkTaskPrioritySystem.createExecutor(taskExecutor, false);
		this.mainExecutor = this.chunkTaskPrioritySystem.createExecutor(messageListener, false);
		this.lightingProvider = new ServerLightingProvider(
			chunkProvider, this, this.world.getDimension().hasSkyLight(), taskExecutor2, this.chunkTaskPrioritySystem.createExecutor(taskExecutor2, false)
		);
		this.ticketManager = new ThreadedAnvilChunkStorage.TicketManager(executor, mainThreadExecutor);
		this.persistentStateManagerFactory = persistentStateManagerFactory;
		this.pointOfInterestStorage = new PointOfInterestStorage(path.resolve("poi"), dataFixer, dsync, world);
		this.setViewDistance(viewDistance);
	}

	protected ChunkGenerator getChunkGenerator() {
		return this.chunkGenerator;
	}

	public void verifyChunkGenerator() {
		DataResult<JsonElement> dataResult = ChunkGenerator.CODEC.encodeStart(JsonOps.INSTANCE, this.chunkGenerator);
		DataResult<ChunkGenerator> dataResult2 = dataResult.flatMap(json -> ChunkGenerator.CODEC.parse(JsonOps.INSTANCE, json));
		dataResult2.result().ifPresent(chunkGenerator -> this.chunkGenerator = chunkGenerator);
	}

	private static double getSquaredDistance(ChunkPos pos, Entity entity) {
		double d = (double)ChunkSectionPos.getOffsetPos(pos.x, 8);
		double e = (double)ChunkSectionPos.getOffsetPos(pos.z, 8);
		double f = d - entity.getX();
		double g = e - entity.getZ();
		return f * f + g * g;
	}

	public static boolean isWithinDistance(int x1, int z1, int x2, int z2, int distance) {
		int i = Math.max(0, Math.abs(x1 - x2) - 1);
		int j = Math.max(0, Math.abs(z1 - z2) - 1);
		int k = Math.max(0, Math.max(i, j) - 1);
		int l = Math.min(i, j);
		int m = l * l + k * k;
		int n = distance - 1;
		int o = n * n;
		return m <= o;
	}

	private static boolean isOnDistanceEdge(int x1, int z1, int x2, int z2, int distance) {
		if (!isWithinDistance(x1, z1, x2, z2, distance)) {
			return false;
		} else if (!isWithinDistance(x1 + 1, z1, x2, z2, distance)) {
			return true;
		} else if (!isWithinDistance(x1, z1 + 1, x2, z2, distance)) {
			return true;
		} else {
			return !isWithinDistance(x1 - 1, z1, x2, z2, distance) ? true : !isWithinDistance(x1, z1 - 1, x2, z2, distance);
		}
	}

	protected ServerLightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	@Nullable
	protected ChunkHolder getCurrentChunkHolder(long pos) {
		return this.currentChunkHolders.get(pos);
	}

	@Nullable
	protected ChunkHolder getChunkHolder(long pos) {
		return this.chunkHolders.get(pos);
	}

	protected IntSupplier getCompletedLevelSupplier(long pos) {
		return () -> {
			ChunkHolder chunkHolder = this.getChunkHolder(pos);
			return chunkHolder == null ? LevelPrioritizedQueue.LEVEL_COUNT - 1 : Math.min(chunkHolder.getCompletedLevel(), LevelPrioritizedQueue.LEVEL_COUNT - 1);
		};
	}

	public String getChunkLoadingDebugInfo(ChunkPos chunkPos) {
		ChunkHolder chunkHolder = this.getChunkHolder(chunkPos.toLong());
		if (chunkHolder == null) {
			return "null";
		} else {
			String string = chunkHolder.getLevel() + "\n";
			ChunkStatus chunkStatus = chunkHolder.getCurrentStatus();
			Chunk chunk = chunkHolder.getCurrentChunk();
			if (chunkStatus != null) {
				string = string + "St: §" + chunkStatus.getIndex() + chunkStatus + "§r\n";
			}

			if (chunk != null) {
				string = string + "Ch: §" + chunk.getStatus().getIndex() + chunk.getStatus() + "§r\n";
			}

			ChunkHolder.LevelType levelType = chunkHolder.getLevelType();
			string = string + "§" + levelType.ordinal() + levelType;
			return string + "§r";
		}
	}

	private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> getRegion(ChunkPos centerChunk, int margin, IntFunction<ChunkStatus> distanceToStatus) {
		List<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> list = new ArrayList();
		List<ChunkHolder> list2 = new ArrayList();
		int i = centerChunk.x;
		int j = centerChunk.z;

		for (int k = -margin; k <= margin; k++) {
			for (int l = -margin; l <= margin; l++) {
				int m = Math.max(Math.abs(l), Math.abs(k));
				final ChunkPos chunkPos = new ChunkPos(i + l, j + k);
				long n = chunkPos.toLong();
				ChunkHolder chunkHolder = this.getCurrentChunkHolder(n);
				if (chunkHolder == null) {
					return CompletableFuture.completedFuture(Either.right(new ChunkHolder.Unloaded() {
						public String toString() {
							return "Unloaded " + chunkPos;
						}
					}));
				}

				ChunkStatus chunkStatus = (ChunkStatus)distanceToStatus.apply(m);
				CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getChunkAt(chunkStatus, this);
				list2.add(chunkHolder);
				list.add(completableFuture);
			}
		}

		CompletableFuture<List<Either<Chunk, ChunkHolder.Unloaded>>> completableFuture2 = Util.combineSafe(list);
		CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture3 = completableFuture2.thenApply(listx -> {
			List<Chunk> list2x = Lists.<Chunk>newArrayList();
			int l = 0;

			for (final Either<Chunk, ChunkHolder.Unloaded> either : listx) {
				Optional<Chunk> optional = either.left();
				if (!optional.isPresent()) {
					final int mx = l;
					return Either.right(new ChunkHolder.Unloaded() {
						public String toString() {
							return "Unloaded " + new ChunkPos(i + mx % (margin * 2 + 1), j + mx / (margin * 2 + 1)) + " " + either.right().get();
						}
					});
				}

				list2x.add((Chunk)optional.get());
				l++;
			}

			return Either.left(list2x);
		});

		for (ChunkHolder chunkHolder2 : list2) {
			chunkHolder2.combineSavingFuture("getChunkRangeFuture " + centerChunk + " " + margin, completableFuture3);
		}

		return completableFuture3;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkEntitiesTickable(ChunkPos pos) {
		return this.getRegion(pos, 2, distance -> ChunkStatus.FULL)
			.thenApplyAsync(either -> either.mapLeft(chunks -> (WorldChunk)chunks.get(chunks.size() / 2)), this.mainThreadExecutor);
	}

	@Nullable
	ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
		if (i > MAX_LEVEL && level > MAX_LEVEL) {
			return holder;
		} else {
			if (holder != null) {
				holder.setLevel(level);
			}

			if (holder != null) {
				if (level > MAX_LEVEL) {
					this.unloadedChunks.add(pos);
				} else {
					this.unloadedChunks.remove(pos);
				}
			}

			if (level <= MAX_LEVEL && holder == null) {
				holder = this.chunksToUnload.remove(pos);
				if (holder != null) {
					holder.setLevel(level);
				} else {
					holder = new ChunkHolder(new ChunkPos(pos), level, this.world, this.lightingProvider, this.chunkTaskPrioritySystem, this);
				}

				this.currentChunkHolders.put(pos, holder);
				this.chunkHolderListDirty = true;
			}

			return holder;
		}
	}

	@Override
	public void close() throws IOException {
		try {
			this.chunkTaskPrioritySystem.close();
			this.pointOfInterestStorage.close();
		} finally {
			super.close();
		}
	}

	protected void save(boolean flush) {
		if (flush) {
			List<ChunkHolder> list = (List<ChunkHolder>)this.chunkHolders
				.values()
				.stream()
				.filter(ChunkHolder::isAccessible)
				.peek(ChunkHolder::updateAccessibleStatus)
				.collect(Collectors.toList());
			MutableBoolean mutableBoolean = new MutableBoolean();

			do {
				mutableBoolean.setFalse();
				list.stream().map(chunkHolder -> {
					CompletableFuture<Chunk> completableFuture;
					do {
						completableFuture = chunkHolder.getSavingFuture();
						this.mainThreadExecutor.runTasks(completableFuture::isDone);
					} while (completableFuture != chunkHolder.getSavingFuture());

					return (Chunk)completableFuture.join();
				}).filter(chunk -> chunk instanceof ReadOnlyChunk || chunk instanceof WorldChunk).filter(this::save).forEach(chunk -> mutableBoolean.setTrue());
			} while (mutableBoolean.isTrue());

			this.unloadChunks(() -> true);
			this.completeAll();
		} else {
			this.chunkHolders.values().forEach(this::save);
		}
	}

	protected void tick(BooleanSupplier shouldKeepTicking) {
		Profiler profiler = this.world.getProfiler();
		profiler.push("poi");
		this.pointOfInterestStorage.tick(shouldKeepTicking);
		profiler.swap("chunk_unload");
		if (!this.world.isSavingDisabled()) {
			this.unloadChunks(shouldKeepTicking);
		}

		profiler.pop();
	}

	private void unloadChunks(BooleanSupplier shouldKeepTicking) {
		LongIterator longIterator = this.unloadedChunks.iterator();

		for (int i = 0; longIterator.hasNext() && (shouldKeepTicking.getAsBoolean() || i < 200 || this.unloadedChunks.size() > 2000); longIterator.remove()) {
			long l = longIterator.nextLong();
			ChunkHolder chunkHolder = this.currentChunkHolders.remove(l);
			if (chunkHolder != null) {
				this.chunksToUnload.put(l, chunkHolder);
				this.chunkHolderListDirty = true;
				i++;
				this.tryUnloadChunk(l, chunkHolder);
			}
		}

		int j = Math.max(0, this.unloadTaskQueue.size() - 2000);

		Runnable runnable;
		while ((shouldKeepTicking.getAsBoolean() || j > 0) && (runnable = (Runnable)this.unloadTaskQueue.poll()) != null) {
			j--;
			runnable.run();
		}

		int k = 0;
		ObjectIterator<ChunkHolder> objectIterator = this.chunkHolders.values().iterator();

		while (k < 20 && shouldKeepTicking.getAsBoolean() && objectIterator.hasNext()) {
			if (this.save((ChunkHolder)objectIterator.next())) {
				k++;
			}
		}
	}

	private void tryUnloadChunk(long pos, ChunkHolder holder) {
		CompletableFuture<Chunk> completableFuture = holder.getSavingFuture();
		completableFuture.thenAcceptAsync(chunk -> {
			CompletableFuture<Chunk> completableFuture2 = holder.getSavingFuture();
			if (completableFuture2 != completableFuture) {
				this.tryUnloadChunk(pos, holder);
			} else {
				if (this.chunksToUnload.remove(pos, holder) && chunk != null) {
					if (chunk instanceof WorldChunk) {
						((WorldChunk)chunk).setLoadedToWorld(false);
					}

					this.save(chunk);
					if (this.loadedChunks.remove(pos) && chunk instanceof WorldChunk worldChunk) {
						this.world.unloadEntities(worldChunk);
					}

					this.lightingProvider.updateChunkStatus(chunk.getPos());
					this.lightingProvider.tick();
					this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);
				}
			}
		}, this.unloadTaskQueue::add).whenComplete((void_, throwable) -> {
			if (throwable != null) {
				LOGGER.error("Failed to save chunk {}", holder.getPos(), throwable);
			}
		});
	}

	protected boolean updateHolderMap() {
		if (!this.chunkHolderListDirty) {
			return false;
		} else {
			this.chunkHolders = this.currentChunkHolders.clone();
			this.chunkHolderListDirty = false;
			return true;
		}
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
		ChunkPos chunkPos = holder.getPos();
		if (requiredStatus == ChunkStatus.EMPTY) {
			return this.loadChunk(chunkPos);
		} else {
			if (requiredStatus == ChunkStatus.LIGHT) {
				this.ticketManager.addTicketWithLevel(ChunkTicketType.LIGHT, chunkPos, 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.LIGHT), chunkPos);
			}

			Optional<Chunk> optional = ((Either)holder.getChunkAt(requiredStatus.getPrevious(), this).getNow(ChunkHolder.UNLOADED_CHUNK)).left();
			if (optional.isPresent() && ((Chunk)optional.get()).getStatus().isAtLeast(requiredStatus)) {
				CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = requiredStatus.runLoadTask(
					this.world, this.structureManager, this.lightingProvider, chunk -> this.convertToFullChunk(holder), (Chunk)optional.get()
				);
				this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
				return completableFuture;
			} else {
				return this.upgradeChunk(holder, requiredStatus);
			}
		}
	}

	private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				this.world.getProfiler().visit("chunkLoad");
				NbtCompound nbtCompound = this.getUpdatedChunkNbt(pos);
				if (nbtCompound != null) {
					boolean bl = nbtCompound.contains("Status", NbtElement.STRING_TYPE);
					if (bl) {
						Chunk chunk = ChunkSerializer.deserialize(this.world, this.pointOfInterestStorage, pos, nbtCompound);
						this.mark(pos, chunk.getStatus().getChunkType());
						return Either.left(chunk);
					}

					LOGGER.error("Chunk file at {} is missing level data, skipping", pos);
				}
			} catch (CrashException var5) {
				Throwable throwable = var5.getCause();
				if (!(throwable instanceof IOException)) {
					this.markAsProtoChunk(pos);
					throw var5;
				}

				LOGGER.error("Couldn't load chunk {}", pos, throwable);
			} catch (Exception var6) {
				LOGGER.error("Couldn't load chunk {}", pos, var6);
			}

			this.markAsProtoChunk(pos);
			return Either.left(new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, this.world, this.world.getRegistryManager().get(Registry.BIOME_KEY), null));
		}, this.mainThreadExecutor);
	}

	private void markAsProtoChunk(ChunkPos pos) {
		this.chunkToType.put(pos.toLong(), (byte)-1);
	}

	private byte mark(ChunkPos pos, ChunkStatus.ChunkType type) {
		return this.chunkToType.put(pos.toLong(), (byte)(type == ChunkStatus.ChunkType.PROTOCHUNK ? -1 : 1));
	}

	private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> upgradeChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
		ChunkPos chunkPos = holder.getPos();
		CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getRegion(
			chunkPos, requiredStatus.getTaskMargin(), i -> this.getRequiredStatusForGeneration(requiredStatus, i)
		);
		this.world.getProfiler().visit((Supplier<String>)(() -> "chunkGenerate " + requiredStatus.getId()));
		Executor executor = task -> this.worldGenExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task));
		return completableFuture.thenComposeAsync(
			either -> either.map(
					chunks -> {
						try {
							CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuturex = requiredStatus.runGenerationTask(
								executor, this.world, this.chunkGenerator, this.structureManager, this.lightingProvider, chunk -> this.convertToFullChunk(holder), chunks, false
							);
							this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
							return completableFuturex;
						} catch (Exception var9) {
							var9.getStackTrace();
							CrashReport crashReport = CrashReport.create(var9, "Exception generating new chunk");
							CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
							crashReportSection.add("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
							crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
							crashReportSection.add("Generator", this.chunkGenerator);
							this.mainThreadExecutor.execute(() -> {
								throw new CrashException(crashReport);
							});
							throw new CrashException(crashReport);
						}
					},
					unloaded -> {
						this.releaseLightTicket(chunkPos);
						return CompletableFuture.completedFuture(Either.right(unloaded));
					}
				),
			executor
		);
	}

	protected void releaseLightTicket(ChunkPos pos) {
		this.mainThreadExecutor
			.send(
				Util.debugRunnable(
					(Runnable)(() -> this.ticketManager.removeTicketWithLevel(ChunkTicketType.LIGHT, pos, 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.LIGHT), pos)),
					(Supplier<String>)(() -> "release light ticket " + pos)
				)
			);
	}

	private ChunkStatus getRequiredStatusForGeneration(ChunkStatus centerChunkTargetStatus, int distance) {
		ChunkStatus chunkStatus;
		if (distance == 0) {
			chunkStatus = centerChunkTargetStatus.getPrevious();
		} else {
			chunkStatus = ChunkStatus.byDistanceFromFull(ChunkStatus.getDistanceFromFull(centerChunkTargetStatus) + distance);
		}

		return chunkStatus;
	}

	private static void addEntitiesFromNbt(ServerWorld world, List<NbtCompound> nbt) {
		if (!nbt.isEmpty()) {
			world.addEntities(EntityType.streamFromNbt(nbt, world));
		}
	}

	private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> convertToFullChunk(ChunkHolder chunkHolder) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getFutureFor(ChunkStatus.FULL.getPrevious());
		return completableFuture.thenApplyAsync(either -> {
			ChunkStatus chunkStatus = ChunkHolder.getTargetStatusForLevel(chunkHolder.getLevel());
			return !chunkStatus.isAtLeast(ChunkStatus.FULL) ? ChunkHolder.UNLOADED_CHUNK : either.mapLeft(protoChunk -> {
				ChunkPos chunkPos = chunkHolder.getPos();
				ProtoChunk protoChunk2 = (ProtoChunk)protoChunk;
				WorldChunk worldChunk;
				if (protoChunk2 instanceof ReadOnlyChunk) {
					worldChunk = ((ReadOnlyChunk)protoChunk2).getWrappedChunk();
				} else {
					worldChunk = new WorldChunk(this.world, protoChunk2, chunk -> addEntitiesFromNbt(this.world, protoChunk2.getEntities()));
					chunkHolder.setCompletedChunk(new ReadOnlyChunk(worldChunk, false));
				}

				worldChunk.setLevelTypeProvider(() -> ChunkHolder.getLevelType(chunkHolder.getLevel()));
				worldChunk.loadEntities();
				if (this.loadedChunks.add(chunkPos.toLong())) {
					worldChunk.setLoadedToWorld(true);
					worldChunk.updateAllBlockEntities();
					worldChunk.addChunkTickSchedulers(this.world);
				}

				return worldChunk;
			});
		}, task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(task, chunkHolder.getPos().toLong(), chunkHolder::getLevel)));
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkTickable(ChunkHolder holder) {
		ChunkPos chunkPos = holder.getPos();
		CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getRegion(chunkPos, 1, i -> ChunkStatus.FULL);
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture2 = completableFuture.thenApplyAsync(either -> either.flatMap(chunks -> {
				WorldChunk worldChunk = (WorldChunk)chunks.get(chunks.size() / 2);
				worldChunk.runPostProcessing();
				this.world.disableTickSchedulers(worldChunk);
				return Either.left(worldChunk);
			}), task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task)));
		completableFuture2.thenAcceptAsync(either -> either.ifLeft(chunk -> {
				this.totalChunksLoadedCount.getAndIncrement();
				MutableObject<ChunkDataS2CPacket> mutableObject = new MutableObject<>();
				this.getPlayersWatchingChunk(chunkPos, false).forEach(player -> this.sendChunkDataPackets(player, mutableObject, chunk));
			}), task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task)));
		return completableFuture2;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkAccessible(ChunkHolder holder) {
		return this.getRegion(holder.getPos(), 1, ChunkStatus::byDistanceFromFull)
			.thenApplyAsync(
				either -> either.mapLeft(chunks -> (WorldChunk)chunks.get(chunks.size() / 2)),
				task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task))
			);
	}

	public int getTotalChunksLoadedCount() {
		return this.totalChunksLoadedCount.get();
	}

	private boolean save(ChunkHolder chunkHolder) {
		if (!chunkHolder.isAccessible()) {
			return false;
		} else {
			Chunk chunk = (Chunk)chunkHolder.getSavingFuture().getNow(null);
			if (!(chunk instanceof ReadOnlyChunk) && !(chunk instanceof WorldChunk)) {
				return false;
			} else {
				boolean bl = this.save(chunk);
				chunkHolder.updateAccessibleStatus();
				return bl;
			}
		}
	}

	private boolean save(Chunk chunk) {
		this.pointOfInterestStorage.saveChunk(chunk.getPos());
		if (!chunk.needsSaving()) {
			return false;
		} else {
			chunk.setShouldSave(false);
			ChunkPos chunkPos = chunk.getPos();

			try {
				ChunkStatus chunkStatus = chunk.getStatus();
				if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
					if (this.isLevelChunk(chunkPos)) {
						return false;
					}

					if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
						return false;
					}
				}

				this.world.getProfiler().visit("chunkSave");
				NbtCompound nbtCompound = ChunkSerializer.serialize(this.world, chunk);
				this.setNbt(chunkPos, nbtCompound);
				this.mark(chunkPos, chunkStatus.getChunkType());
				return true;
			} catch (Exception var5) {
				LOGGER.error("Failed to save chunk {},{}", chunkPos.x, chunkPos.z, var5);
				return false;
			}
		}
	}

	private boolean isLevelChunk(ChunkPos pos) {
		byte b = this.chunkToType.get(pos.toLong());
		if (b != 0) {
			return b == 1;
		} else {
			NbtCompound nbtCompound;
			try {
				nbtCompound = this.getUpdatedChunkNbt(pos);
				if (nbtCompound == null) {
					this.markAsProtoChunk(pos);
					return false;
				}
			} catch (Exception var5) {
				LOGGER.error("Failed to read chunk {}", pos, var5);
				this.markAsProtoChunk(pos);
				return false;
			}

			ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkType(nbtCompound);
			return this.mark(pos, chunkType) == 1;
		}
	}

	protected void setViewDistance(int watchDistance) {
		int i = MathHelper.clamp(watchDistance + 1, 3, 33);
		if (i != this.watchDistance) {
			int j = this.watchDistance;
			this.watchDistance = i;
			this.ticketManager.setWatchDistance(this.watchDistance + 1);

			for (ChunkHolder chunkHolder : this.currentChunkHolders.values()) {
				ChunkPos chunkPos = chunkHolder.getPos();
				MutableObject<ChunkDataS2CPacket> mutableObject = new MutableObject<>();
				this.getPlayersWatchingChunk(chunkPos, false).forEach(player -> {
					ChunkSectionPos chunkSectionPos = player.getWatchedSection();
					boolean bl = isWithinDistance(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), j);
					boolean bl2 = isWithinDistance(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), this.watchDistance);
					this.sendWatchPackets(player, chunkPos, mutableObject, bl, bl2);
				});
			}
		}
	}

	protected void sendWatchPackets(
		ServerPlayerEntity player, ChunkPos pos, MutableObject<ChunkDataS2CPacket> packet, boolean oldWithinViewDistance, boolean newWithinViewDistance
	) {
		if (player.world == this.world) {
			if (newWithinViewDistance && !oldWithinViewDistance) {
				ChunkHolder chunkHolder = this.getChunkHolder(pos.toLong());
				if (chunkHolder != null) {
					WorldChunk worldChunk = chunkHolder.getWorldChunk();
					if (worldChunk != null) {
						this.sendChunkDataPackets(player, packet, worldChunk);
					}

					DebugInfoSender.sendChunkWatchingChange(this.world, pos);
				}
			}

			if (!newWithinViewDistance && oldWithinViewDistance) {
				player.sendUnloadChunkPacket(pos);
			}
		}
	}

	public int getLoadedChunkCount() {
		return this.chunkHolders.size();
	}

	public ChunkTicketManager getTicketManager() {
		return this.ticketManager;
	}

	protected Iterable<ChunkHolder> entryIterator() {
		return Iterables.unmodifiableIterable(this.chunkHolders.values());
	}

	void dump(Writer writer) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader()
			.addColumn("x")
			.addColumn("z")
			.addColumn("level")
			.addColumn("in_memory")
			.addColumn("status")
			.addColumn("full_status")
			.addColumn("accessible_ready")
			.addColumn("ticking_ready")
			.addColumn("entity_ticking_ready")
			.addColumn("ticket")
			.addColumn("spawning")
			.addColumn("block_entity_count")
			.addColumn("ticking_ticket")
			.addColumn("ticking_level")
			.addColumn("block_ticks")
			.addColumn("fluid_ticks")
			.startBody(writer);
		SimulationDistanceLevelPropagator simulationDistanceLevelPropagator = this.ticketManager.getSimulationDistanceTracker();

		for (Entry<ChunkHolder> entry : this.chunkHolders.long2ObjectEntrySet()) {
			long l = entry.getLongKey();
			ChunkPos chunkPos = new ChunkPos(l);
			ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
			Optional<Chunk> optional = Optional.ofNullable(chunkHolder.getCurrentChunk());
			Optional<WorldChunk> optional2 = optional.flatMap(chunk -> chunk instanceof WorldChunk ? Optional.of((WorldChunk)chunk) : Optional.empty());
			csvWriter.printRow(
				chunkPos.x,
				chunkPos.z,
				chunkHolder.getLevel(),
				optional.isPresent(),
				optional.map(Chunk::getStatus).orElse(null),
				optional2.map(WorldChunk::getLevelType).orElse(null),
				getFutureStatus(chunkHolder.getAccessibleFuture()),
				getFutureStatus(chunkHolder.getTickingFuture()),
				getFutureStatus(chunkHolder.getEntityTickingFuture()),
				this.ticketManager.getTicket(l),
				this.shouldTick(chunkPos),
				optional2.map(worldChunk -> worldChunk.getBlockEntities().size()).orElse(0),
				simulationDistanceLevelPropagator.getTickingTicket(l),
				simulationDistanceLevelPropagator.getLevel(l),
				optional2.map(worldChunk -> worldChunk.getBlockTickScheduler().getTickCount()).orElse(0),
				optional2.map(worldChunk -> worldChunk.getFluidTickScheduler().getTickCount()).orElse(0)
			);
		}
	}

	private static String getFutureStatus(CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> future) {
		try {
			Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)future.getNow(null);
			return either != null ? either.map(chunk -> "done", unloaded -> "unloaded") : "not completed";
		} catch (CompletionException var2) {
			return "failed " + var2.getCause().getMessage();
		} catch (CancellationException var3) {
			return "cancelled";
		}
	}

	@Nullable
	private NbtCompound getUpdatedChunkNbt(ChunkPos pos) throws IOException {
		NbtCompound nbtCompound = this.getNbt(pos);
		return nbtCompound == null
			? null
			: this.updateChunkNbt(this.world.getRegistryKey(), this.persistentStateManagerFactory, nbtCompound, this.chunkGenerator.getCodecKey());
	}

	boolean shouldTick(ChunkPos pos) {
		long l = pos.toLong();
		if (!this.ticketManager.shouldTick(l)) {
			return false;
		} else {
			for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk(l)) {
				if (this.canTickChunk(serverPlayerEntity, pos)) {
					return true;
				}
			}

			return false;
		}
	}

	public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos pos) {
		long l = pos.toLong();
		if (!this.ticketManager.shouldTick(l)) {
			return List.of();
		} else {
			Builder<ServerPlayerEntity> builder = ImmutableList.builder();

			for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk(l)) {
				if (this.canTickChunk(serverPlayerEntity, pos)) {
					builder.add(serverPlayerEntity);
				}
			}

			return builder.build();
		}
	}

	/**
	 * {@return whether the {@code player} can tick the chunk at {@code pos}}
	 * 
	 * @implNote Spectators cannot tick chunks. Additionally, only chunks within 128
	 * block radius of that player can be ticked.
	 * 
	 * @apiNote This controls monster spawning and block random ticks.
	 */
	private boolean canTickChunk(ServerPlayerEntity player, ChunkPos pos) {
		if (player.isSpectator()) {
			return false;
		} else {
			double d = getSquaredDistance(pos, player);
			return d < 16384.0;
		}
	}

	private boolean doesNotGenerateChunks(ServerPlayerEntity player) {
		return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
	}

	void handlePlayerAddedOrRemoved(ServerPlayerEntity player, boolean added) {
		boolean bl = this.doesNotGenerateChunks(player);
		boolean bl2 = this.playerChunkWatchingManager.isWatchInactive(player);
		int i = ChunkSectionPos.getSectionCoord(player.getBlockX());
		int j = ChunkSectionPos.getSectionCoord(player.getBlockZ());
		if (added) {
			this.playerChunkWatchingManager.add(ChunkPos.toLong(i, j), player, bl);
			this.updateWatchedSection(player);
			if (!bl) {
				this.ticketManager.handleChunkEnter(ChunkSectionPos.from(player), player);
			}
		} else {
			ChunkSectionPos chunkSectionPos = player.getWatchedSection();
			this.playerChunkWatchingManager.remove(chunkSectionPos.toChunkPos().toLong(), player);
			if (!bl2) {
				this.ticketManager.handleChunkLeave(chunkSectionPos, player);
			}
		}

		for (int k = i - this.watchDistance - 1; k <= i + this.watchDistance + 1; k++) {
			for (int l = j - this.watchDistance - 1; l <= j + this.watchDistance + 1; l++) {
				if (isWithinDistance(k, l, i, j, this.watchDistance)) {
					ChunkPos chunkPos = new ChunkPos(k, l);
					this.sendWatchPackets(player, chunkPos, new MutableObject<>(), !added, added);
				}
			}
		}
	}

	/**
	 * Updates the watched chunk section position for the {@code player}, and sends a
	 * render distance update packet to the client.
	 */
	private ChunkSectionPos updateWatchedSection(ServerPlayerEntity player) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(player);
		player.setWatchedSection(chunkSectionPos);
		player.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ()));
		return chunkSectionPos;
	}

	/**
	 * Updates the chunk section position of the {@code player}. This updates the player
	 * position for both entity tracking and chunk loading (watching) logic.
	 * 
	 * @see ServerChunkManager#updatePosition(ServerPlayerEntity)
	 */
	public void updatePosition(ServerPlayerEntity player) {
		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.entityTrackers.values()) {
			if (entityTracker.entity == player) {
				entityTracker.updateTrackedStatus(this.world.getPlayers());
			} else {
				entityTracker.updateTrackedStatus(player);
			}
		}

		int i = ChunkSectionPos.getSectionCoord(player.getBlockX());
		int j = ChunkSectionPos.getSectionCoord(player.getBlockZ());
		ChunkSectionPos chunkSectionPos = player.getWatchedSection();
		ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(player);
		long l = chunkSectionPos.toChunkPos().toLong();
		long m = chunkSectionPos2.toChunkPos().toLong();
		boolean bl = this.playerChunkWatchingManager.isWatchDisabled(player);
		boolean bl2 = this.doesNotGenerateChunks(player);
		boolean bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();
		if (bl3 || bl != bl2) {
			this.updateWatchedSection(player);
			if (!bl) {
				this.ticketManager.handleChunkLeave(chunkSectionPos, player);
			}

			if (!bl2) {
				this.ticketManager.handleChunkEnter(chunkSectionPos2, player);
			}

			if (!bl && bl2) {
				this.playerChunkWatchingManager.disableWatch(player);
			}

			if (bl && !bl2) {
				this.playerChunkWatchingManager.enableWatch(player);
			}

			if (l != m) {
				this.playerChunkWatchingManager.movePlayer(l, m, player);
			}
		}

		int k = chunkSectionPos.getSectionX();
		int n = chunkSectionPos.getSectionZ();
		if (Math.abs(k - i) <= this.watchDistance * 2 && Math.abs(n - j) <= this.watchDistance * 2) {
			int o = Math.min(i, k) - this.watchDistance - 1;
			int p = Math.min(j, n) - this.watchDistance - 1;
			int q = Math.max(i, k) + this.watchDistance + 1;
			int r = Math.max(j, n) + this.watchDistance + 1;

			for (int s = o; s <= q; s++) {
				for (int t = p; t <= r; t++) {
					boolean bl4 = isWithinDistance(s, t, k, n, this.watchDistance);
					boolean bl5 = isWithinDistance(s, t, i, j, this.watchDistance);
					this.sendWatchPackets(player, new ChunkPos(s, t), new MutableObject<>(), bl4, bl5);
				}
			}
		} else {
			for (int o = k - this.watchDistance - 1; o <= k + this.watchDistance + 1; o++) {
				for (int p = n - this.watchDistance - 1; p <= n + this.watchDistance + 1; p++) {
					if (isWithinDistance(o, p, k, n, this.watchDistance)) {
						boolean bl6 = true;
						boolean bl7 = false;
						this.sendWatchPackets(player, new ChunkPos(o, p), new MutableObject<>(), true, false);
					}
				}
			}

			for (int o = i - this.watchDistance - 1; o <= i + this.watchDistance + 1; o++) {
				for (int px = j - this.watchDistance - 1; px <= j + this.watchDistance + 1; px++) {
					if (isWithinDistance(o, px, i, j, this.watchDistance)) {
						boolean bl6 = false;
						boolean bl7 = true;
						this.sendWatchPackets(player, new ChunkPos(o, px), new MutableObject<>(), false, true);
					}
				}
			}
		}
	}

	@Override
	public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
		Set<ServerPlayerEntity> set = this.playerChunkWatchingManager.getPlayersWatchingChunk(chunkPos.toLong());
		Builder<ServerPlayerEntity> builder = ImmutableList.builder();

		for (ServerPlayerEntity serverPlayerEntity : set) {
			ChunkSectionPos chunkSectionPos = serverPlayerEntity.getWatchedSection();
			if (onlyOnWatchDistanceEdge && isOnDistanceEdge(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), this.watchDistance)
				|| !onlyOnWatchDistanceEdge && isWithinDistance(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), this.watchDistance)) {
				builder.add(serverPlayerEntity);
			}
		}

		return builder.build();
	}

	protected void loadEntity(Entity entity) {
		if (!(entity instanceof EnderDragonPart)) {
			EntityType<?> entityType = entity.getType();
			int i = entityType.getMaxTrackDistance() * 16;
			if (i != 0) {
				int j = entityType.getTrackTickInterval();
				if (this.entityTrackers.containsKey(entity.getId())) {
					throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Entity is already tracked!"));
				} else {
					ThreadedAnvilChunkStorage.EntityTracker entityTracker = new ThreadedAnvilChunkStorage.EntityTracker(entity, i, j, entityType.alwaysUpdateVelocity());
					this.entityTrackers.put(entity.getId(), entityTracker);
					entityTracker.updateTrackedStatus(this.world.getPlayers());
					if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
						this.handlePlayerAddedOrRemoved(serverPlayerEntity, true);

						for (ThreadedAnvilChunkStorage.EntityTracker entityTracker2 : this.entityTrackers.values()) {
							if (entityTracker2.entity != serverPlayerEntity) {
								entityTracker2.updateTrackedStatus(serverPlayerEntity);
							}
						}
					}
				}
			}
		}
	}

	protected void unloadEntity(Entity entity) {
		if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
			this.handlePlayerAddedOrRemoved(serverPlayerEntity, false);

			for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.entityTrackers.values()) {
				entityTracker.stopTracking(serverPlayerEntity);
			}
		}

		ThreadedAnvilChunkStorage.EntityTracker entityTracker2 = this.entityTrackers.remove(entity.getId());
		if (entityTracker2 != null) {
			entityTracker2.stopTracking();
		}
	}

	/**
	 * Ticks and updates the tracked status of each tracker.
	 * 
	 * <p>This first checks if entities have changed chunk sections, and updates
	 * tracking status of those entities to all players. It then checks if any player
	 * has changed chunk sections, and updates all entities tracking status to those
	 * players. This ensures all possible updates are accounted for.
	 */
	protected void tickEntityMovement() {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();
		List<ServerPlayerEntity> list2 = this.world.getPlayers();

		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.entityTrackers.values()) {
			ChunkSectionPos chunkSectionPos = entityTracker.trackedSection;
			ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(entityTracker.entity);
			boolean bl = !Objects.equals(chunkSectionPos, chunkSectionPos2);
			if (bl) {
				entityTracker.updateTrackedStatus(list2);
				Entity entity = entityTracker.entity;
				if (entity instanceof ServerPlayerEntity) {
					list.add((ServerPlayerEntity)entity);
				}

				entityTracker.trackedSection = chunkSectionPos2;
			}

			if (bl || this.ticketManager.shouldTickEntities(chunkSectionPos2.toChunkPos().toLong())) {
				entityTracker.entry.tick();
			}
		}

		if (!list.isEmpty()) {
			for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.entityTrackers.values()) {
				entityTracker.updateTrackedStatus(list);
			}
		}
	}

	public void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
		ThreadedAnvilChunkStorage.EntityTracker entityTracker = this.entityTrackers.get(entity.getId());
		if (entityTracker != null) {
			entityTracker.sendToOtherNearbyPlayers(packet);
		}
	}

	protected void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
		ThreadedAnvilChunkStorage.EntityTracker entityTracker = this.entityTrackers.get(entity.getId());
		if (entityTracker != null) {
			entityTracker.sendToNearbyPlayers(packet);
		}
	}

	private void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> cachedDataPacket, WorldChunk chunk) {
		if (cachedDataPacket.getValue() == null) {
			cachedDataPacket.setValue(new ChunkDataS2CPacket(chunk, this.lightingProvider, null, null, true));
		}

		player.sendInitialChunkPackets(chunk.getPos(), cachedDataPacket.getValue());
		DebugInfoSender.sendChunkWatchingChange(this.world, chunk.getPos());
		List<Entity> list = Lists.<Entity>newArrayList();
		List<Entity> list2 = Lists.<Entity>newArrayList();

		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.entityTrackers.values()) {
			Entity entity = entityTracker.entity;
			if (entity != player && entity.getChunkPos().equals(chunk.getPos())) {
				entityTracker.updateTrackedStatus(player);
				if (entity instanceof MobEntity && ((MobEntity)entity).getHoldingEntity() != null) {
					list.add(entity);
				}

				if (!entity.getPassengerList().isEmpty()) {
					list2.add(entity);
				}
			}
		}

		if (!list.isEmpty()) {
			for (Entity entity2 : list) {
				player.networkHandler.sendPacket(new EntityAttachS2CPacket(entity2, ((MobEntity)entity2).getHoldingEntity()));
			}
		}

		if (!list2.isEmpty()) {
			for (Entity entity2 : list2) {
				player.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity2));
			}
		}
	}

	protected PointOfInterestStorage getPointOfInterestStorage() {
		return this.pointOfInterestStorage;
	}

	public String getSaveDir() {
		return this.saveDir;
	}

	void onChunkStatusChange(ChunkPos chunkPos, ChunkHolder.LevelType levelType) {
		this.chunkStatusChangeListener.onChunkStatusChange(chunkPos, levelType);
	}

	/**
	 * An entity tracker governs which players' clients can see an entity. Each
	 * tracker corresponds to one entity in a server world and is mapped from the
	 * entity's network ID.
	 * 
	 * @see ThreadedAnvilChunkStorage#entityTrackers
	 */
	class EntityTracker {
		final EntityTrackerEntry entry;
		final Entity entity;
		private final int maxDistance;
		/**
		 * The chunk section position of the tracked entity, may be outdated as an entity
		 * ticks. This is used by {@link ThreadedAnvilChunkStorage#tickEntityMovement()
		 * tickEntityMovement()} to bypass unnecessary status updates before calling
		 * {@link #updateTrackedStatus(ServerPlayerEntity) updateTrackedStatus()}.
		 */
		ChunkSectionPos trackedSection;
		private final Set<EntityTrackingListener> listeners = Sets.newIdentityHashSet();

		public EntityTracker(Entity entity, int maxDistance, int tickInterval, boolean alwaysUpdateVelocity) {
			this.entry = new EntityTrackerEntry(ThreadedAnvilChunkStorage.this.world, entity, tickInterval, alwaysUpdateVelocity, this::sendToOtherNearbyPlayers);
			this.entity = entity;
			this.maxDistance = maxDistance;
			this.trackedSection = ChunkSectionPos.from(entity);
		}

		public boolean equals(Object o) {
			return o instanceof ThreadedAnvilChunkStorage.EntityTracker ? ((ThreadedAnvilChunkStorage.EntityTracker)o).entity.getId() == this.entity.getId() : false;
		}

		public int hashCode() {
			return this.entity.getId();
		}

		public void sendToOtherNearbyPlayers(Packet<?> packet) {
			for (EntityTrackingListener entityTrackingListener : this.listeners) {
				entityTrackingListener.sendPacket(packet);
			}
		}

		public void sendToNearbyPlayers(Packet<?> packet) {
			this.sendToOtherNearbyPlayers(packet);
			if (this.entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
			}
		}

		public void stopTracking() {
			for (EntityTrackingListener entityTrackingListener : this.listeners) {
				this.entry.stopTracking(entityTrackingListener.getPlayer());
			}
		}

		public void stopTracking(ServerPlayerEntity player) {
			if (this.listeners.remove(player.networkHandler)) {
				this.entry.stopTracking(player);
			}
		}

		/**
		 * Updates the tracked status of this tracker's entity for the {@code player}.
		 * 
		 * <p>If this tracker should be listened by the player, the player's tracking
		 * listener is added if it is not in the listeners; if this tracker should not be
		 * listened by the player, the player's tracking listener is removed if it is in
		 * the listeners.
		 */
		public void updateTrackedStatus(ServerPlayerEntity player) {
			if (player != this.entity) {
				Vec3d vec3d = player.getPos().subtract(this.entry.getLastPos());
				double d = (double)Math.min(this.getMaxTrackDistance(), (ThreadedAnvilChunkStorage.this.watchDistance - 1) * 16);
				double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
				double f = d * d;
				boolean bl = e <= f && this.entity.canBeSpectated(player);
				if (bl) {
					if (this.listeners.add(player.networkHandler)) {
						this.entry.startTracking(player);
					}
				} else if (this.listeners.remove(player.networkHandler)) {
					this.entry.stopTracking(player);
				}
			}
		}

		private int adjustTrackingDistance(int initialDistance) {
			return ThreadedAnvilChunkStorage.this.world.getServer().adjustTrackingDistance(initialDistance);
		}

		private int getMaxTrackDistance() {
			int i = this.maxDistance;

			for (Entity entity : this.entity.getPassengersDeep()) {
				int j = entity.getType().getMaxTrackDistance() * 16;
				if (j > i) {
					i = j;
				}
			}

			return this.adjustTrackingDistance(i);
		}

		/**
		 * Updates the tracked status of this tracker's entity for the given players.
		 * 
		 * @see updateTrackedStatus(ServerPlayerEntity)
		 */
		public void updateTrackedStatus(List<ServerPlayerEntity> players) {
			for (ServerPlayerEntity serverPlayerEntity : players) {
				this.updateTrackedStatus(serverPlayerEntity);
			}
		}
	}

	class TicketManager extends ChunkTicketManager {
		protected TicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
			super(workerExecutor, mainThreadExecutor);
		}

		@Override
		protected boolean isUnloaded(long pos) {
			return ThreadedAnvilChunkStorage.this.unloadedChunks.contains(pos);
		}

		@Nullable
		@Override
		protected ChunkHolder getChunkHolder(long pos) {
			return ThreadedAnvilChunkStorage.this.getCurrentChunkHolder(pos);
		}

		@Nullable
		@Override
		protected ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
			return ThreadedAnvilChunkStorage.this.setLevel(pos, level, holder, i);
		}
	}
}

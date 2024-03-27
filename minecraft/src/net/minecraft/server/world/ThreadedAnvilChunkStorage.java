package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SimulationDistanceLevelPropagator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationContext;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.ChunkType;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class ThreadedAnvilChunkStorage extends VersionedChunkStorage implements ChunkHolder.PlayersWatchingChunkProvider {
	private static final byte PROTO_CHUNK = -1;
	private static final byte UNMARKED_CHUNK = 0;
	private static final byte LEVEL_CHUNK = 1;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29674 = 200;
	private static final int field_36291 = 20;
	private static final int field_36384 = 10000;
	public static final int DEFAULT_VIEW_DISTANCE = 2;
	public static final int field_29669 = 32;
	public static final int field_29670 = ChunkLevels.getLevelFromType(ChunkLevelType.ENTITY_TICKING);
	private final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders = new Long2ObjectLinkedOpenHashMap<>();
	private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders = this.currentChunkHolders.clone();
	private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload = new Long2ObjectLinkedOpenHashMap<>();
	private final LongSet loadedChunks = new LongOpenHashSet();
	final ServerWorld world;
	private final ServerLightingProvider lightingProvider;
	private final ThreadExecutor<Runnable> mainThreadExecutor;
	private ChunkGenerator chunkGenerator;
	private final NoiseConfig noiseConfig;
	private final StructurePlacementCalculator structurePlacementCalculator;
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
	private final String saveDir;
	private final PlayerChunkWatchingManager playerChunkWatchingManager = new PlayerChunkWatchingManager();
	private final Int2ObjectMap<ThreadedAnvilChunkStorage.EntityTracker> entityTrackers = new Int2ObjectOpenHashMap<>();
	private final Long2ByteMap chunkToType = new Long2ByteOpenHashMap();
	private final Long2LongMap chunkToNextSaveTimeMs = new Long2LongOpenHashMap();
	private final Queue<Runnable> unloadTaskQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	private int watchDistance;
	private ChunkGenerationContext field_49171;

	public ThreadedAnvilChunkStorage(
		ServerWorld world,
		LevelStorage.Session session,
		DataFixer dataFixer,
		StructureTemplateManager structureTemplateManager,
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
		super(
			new StorageKey(session.getDirectoryName(), world.getRegistryKey(), "chunk"),
			session.getWorldDirectory(world.getRegistryKey()).resolve("region"),
			dataFixer,
			dsync
		);
		Path path = session.getWorldDirectory(world.getRegistryKey());
		this.saveDir = path.getFileName().toString();
		this.world = world;
		this.chunkGenerator = chunkGenerator;
		DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
		long l = world.getSeed();
		if (chunkGenerator instanceof NoiseChunkGenerator noiseChunkGenerator) {
			this.noiseConfig = NoiseConfig.create(noiseChunkGenerator.getSettings().value(), dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.NOISE_PARAMETERS), l);
		} else {
			this.noiseConfig = NoiseConfig.create(
				ChunkGeneratorSettings.createMissingSettings(), dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.NOISE_PARAMETERS), l
			);
		}

		this.structurePlacementCalculator = chunkGenerator.createStructurePlacementCalculator(
			dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET), this.noiseConfig, l
		);
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
		this.pointOfInterestStorage = new PointOfInterestStorage(
			new StorageKey(session.getDirectoryName(), world.getRegistryKey(), "poi"), path.resolve("poi"), dataFixer, dsync, dynamicRegistryManager, world
		);
		this.setViewDistance(viewDistance);
		this.field_49171 = new ChunkGenerationContext(world, chunkGenerator, structureTemplateManager, this.lightingProvider);
	}

	protected ChunkGenerator getChunkGenerator() {
		return this.chunkGenerator;
	}

	protected StructurePlacementCalculator getStructurePlacementCalculator() {
		return this.structurePlacementCalculator;
	}

	protected NoiseConfig getNoiseConfig() {
		return this.noiseConfig;
	}

	public void verifyChunkGenerator() {
		DataResult<JsonElement> dataResult = ChunkGenerator.CODEC.encodeStart(JsonOps.INSTANCE, this.chunkGenerator);
		DataResult<ChunkGenerator> dataResult2 = dataResult.flatMap(json -> ChunkGenerator.CODEC.parse(JsonOps.INSTANCE, json));
		dataResult2.ifSuccess(
			chunkGenerator -> {
				this.chunkGenerator = chunkGenerator;
				this.field_49171 = new ChunkGenerationContext(
					this.field_49171.world(), chunkGenerator, this.field_49171.structureManager(), this.field_49171.lightingProvider()
				);
			}
		);
	}

	private static double getSquaredDistance(ChunkPos pos, Entity entity) {
		double d = (double)ChunkSectionPos.getOffsetPos(pos.x, 8);
		double e = (double)ChunkSectionPos.getOffsetPos(pos.z, 8);
		double f = d - entity.getX();
		double g = e - entity.getZ();
		return f * f + g * g;
	}

	boolean isTracked(ServerPlayerEntity player, int chunkX, int chunkZ) {
		return player.getChunkFilter().isWithinDistance(chunkX, chunkZ) && !player.networkHandler.chunkDataSender.isInNextBatch(ChunkPos.toLong(chunkX, chunkZ));
	}

	private boolean isOnTrackEdge(ServerPlayerEntity player, int chunkX, int chunkZ) {
		if (!this.isTracked(player, chunkX, chunkZ)) {
			return false;
		} else {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if ((i != 0 || j != 0) && !this.isTracked(player, chunkX + i, chunkZ + j)) {
						return true;
					}
				}
			}

			return false;
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

			ChunkLevelType chunkLevelType = chunkHolder.getLevelType();
			string = string + '§' + chunkLevelType.ordinal() + chunkLevelType;
			return string + "§r";
		}
	}

	private CompletableFuture<OptionalChunk<List<Chunk>>> getRegion(ChunkHolder centerChunk, int margin, IntFunction<ChunkStatus> distanceToStatus) {
		if (margin == 0) {
			ChunkStatus chunkStatus = (ChunkStatus)distanceToStatus.apply(0);
			return centerChunk.getChunkAt(chunkStatus, this).thenApply(optionalChunk -> optionalChunk.map(List::of));
		} else {
			List<CompletableFuture<OptionalChunk<Chunk>>> list = new ArrayList();
			List<ChunkHolder> list2 = new ArrayList();
			ChunkPos chunkPos = centerChunk.getPos();
			int i = chunkPos.x;
			int j = chunkPos.z;

			for (int k = -margin; k <= margin; k++) {
				for (int l = -margin; l <= margin; l++) {
					int m = Math.max(Math.abs(l), Math.abs(k));
					ChunkPos chunkPos2 = new ChunkPos(i + l, j + k);
					long n = chunkPos2.toLong();
					ChunkHolder chunkHolder = this.getCurrentChunkHolder(n);
					if (chunkHolder == null) {
						return CompletableFuture.completedFuture(OptionalChunk.of((Supplier<String>)(() -> "Unloaded " + chunkPos2)));
					}

					ChunkStatus chunkStatus2 = (ChunkStatus)distanceToStatus.apply(m);
					CompletableFuture<OptionalChunk<Chunk>> completableFuture = chunkHolder.getChunkAt(chunkStatus2, this);
					list2.add(chunkHolder);
					list.add(completableFuture);
				}
			}

			CompletableFuture<List<OptionalChunk<Chunk>>> completableFuture2 = Util.combineSafe(list);
			CompletableFuture<OptionalChunk<List<Chunk>>> completableFuture3 = completableFuture2.thenApply(
				chunks -> {
					List<Chunk> listx = Lists.<Chunk>newArrayList();
					int l = 0;

					for (OptionalChunk<Chunk> optionalChunk : chunks) {
						if (optionalChunk == null) {
							throw this.crash(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
						}

						Chunk chunk = optionalChunk.orElse(null);
						if (chunk == null) {
							int mx = l;
							return OptionalChunk.of(
								(Supplier<String>)(() -> "Unloaded " + new ChunkPos(i + mx % (margin * 2 + 1), j + mx / (margin * 2 + 1)) + " " + optionalChunk.getError())
							);
						}

						listx.add(chunk);
						l++;
					}

					return OptionalChunk.of(listx);
				}
			);

			for (ChunkHolder chunkHolder2 : list2) {
				chunkHolder2.combineSavingFuture("getChunkRangeFuture " + chunkPos + " " + margin, completableFuture3);
			}

			return completableFuture3;
		}
	}

	public CrashException crash(IllegalStateException exception, String details) {
		StringBuilder stringBuilder = new StringBuilder();
		Consumer<ChunkHolder> consumer = chunkHolder -> chunkHolder.collectFuturesByStatus()
				.forEach(
					pair -> {
						ChunkStatus chunkStatus = (ChunkStatus)pair.getFirst();
						CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)pair.getSecond();
						if (completableFuture != null && completableFuture.isDone() && completableFuture.join() == null) {
							stringBuilder.append(chunkHolder.getPos())
								.append(" - status: ")
								.append(chunkStatus)
								.append(" future: ")
								.append(completableFuture)
								.append(System.lineSeparator());
						}
					}
				);
		stringBuilder.append("Updating:").append(System.lineSeparator());
		this.currentChunkHolders.values().forEach(consumer);
		stringBuilder.append("Visible:").append(System.lineSeparator());
		this.chunkHolders.values().forEach(consumer);
		CrashReport crashReport = CrashReport.create(exception, "Chunk loading");
		CrashReportSection crashReportSection = crashReport.addElement("Chunk loading");
		crashReportSection.add("Details", details);
		crashReportSection.add("Futures", stringBuilder);
		return new CrashException(crashReport);
	}

	public CompletableFuture<OptionalChunk<WorldChunk>> makeChunkEntitiesTickable(ChunkHolder chunk) {
		return this.getRegion(chunk, 2, distance -> ChunkStatus.FULL)
			.thenApplyAsync(optionalChunk -> optionalChunk.map(chunks -> (WorldChunk)chunks.get(chunks.size() / 2)), this.mainThreadExecutor);
	}

	/**
	 * Sets the loading level of {@code ChunkHolder}s. Nonexistent {@code ChunkHolder}s will be created automatically
	 * if their loading level is 45 or lower, and chunks whose loading levels are 46 or higher will be scheduled to be removed.
	 */
	@Nullable
	ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
		if (!ChunkLevels.isAccessible(i) && !ChunkLevels.isAccessible(level)) {
			return holder;
		} else {
			if (holder != null) {
				holder.setLevel(level);
			}

			if (holder != null) {
				if (!ChunkLevels.isAccessible(level)) {
					this.unloadedChunks.add(pos);
				} else {
					this.unloadedChunks.remove(pos);
				}
			}

			if (ChunkLevels.isAccessible(level) && holder == null) {
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
			List<ChunkHolder> list = this.chunkHolders.values().stream().filter(ChunkHolder::isAccessible).peek(ChunkHolder::updateAccessibleStatus).toList();
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
				}).filter(chunk -> chunk instanceof WrapperProtoChunk || chunk instanceof WorldChunk).filter(this::save).forEach(chunk -> mutableBoolean.setTrue());
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

	/**
	 * {@return whether the server shutdown should be delayed to process some tasks}
	 */
	public boolean shouldDelayShutdown() {
		return this.lightingProvider.hasUpdates()
			|| !this.chunksToUnload.isEmpty()
			|| !this.currentChunkHolders.isEmpty()
			|| this.pointOfInterestStorage.hasUnsavedElements()
			|| !this.unloadedChunks.isEmpty()
			|| !this.unloadTaskQueue.isEmpty()
			|| this.chunkTaskPrioritySystem.shouldDelayShutdown()
			|| this.ticketManager.shouldDelayShutdown();
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
					this.chunkToNextSaveTimeMs.remove(chunk.getPos().toLong());
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

	public CompletableFuture<OptionalChunk<Chunk>> getChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
		ChunkPos chunkPos = holder.getPos();
		if (requiredStatus == ChunkStatus.EMPTY) {
			return this.loadChunk(chunkPos).thenApply(OptionalChunk::of);
		} else {
			if (requiredStatus == ChunkStatus.LIGHT) {
				this.ticketManager.addTicketWithLevel(ChunkTicketType.LIGHT, chunkPos, ChunkLevels.getLevelFromStatus(ChunkStatus.LIGHT), chunkPos);
			}

			if (!requiredStatus.shouldAlwaysUpgrade()) {
				Chunk chunk = (Chunk)((OptionalChunk)holder.getChunkAt(requiredStatus.getPrevious(), this).getNow(ChunkHolder.UNLOADED_CHUNK)).orElse(null);
				if (chunk != null && chunk.getStatus().isAtLeast(requiredStatus)) {
					CompletableFuture<Chunk> completableFuture = requiredStatus.runLoadTask(this.field_49171, chunkx -> this.convertToFullChunk(holder, chunkx), chunk);
					this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
					return completableFuture.thenApply(OptionalChunk::of);
				}
			}

			return this.upgradeChunk(holder, requiredStatus);
		}
	}

	private CompletableFuture<Chunk> loadChunk(ChunkPos pos) {
		return this.getUpdatedChunkNbt(pos).thenApply(nbt -> nbt.filter(nbt2 -> {
				boolean bl = containsStatus(nbt2);
				if (!bl) {
					LOGGER.error("Chunk file at {} is missing level data, skipping", pos);
				}

				return bl;
			})).thenApplyAsync(nbt -> {
			this.world.getProfiler().visit("chunkLoad");
			if (nbt.isPresent()) {
				Chunk chunk = ChunkSerializer.deserialize(this.world, this.pointOfInterestStorage, pos, (NbtCompound)nbt.get());
				this.mark(pos, chunk.getStatus().getChunkType());
				return chunk;
			} else {
				return this.getProtoChunk(pos);
			}
		}, this.mainThreadExecutor).exceptionallyAsync(throwable -> this.recoverFromException(throwable, pos), this.mainThreadExecutor);
	}

	private static boolean containsStatus(NbtCompound nbt) {
		return nbt.contains("Status", NbtElement.STRING_TYPE);
	}

	private Chunk recoverFromException(Throwable throwable, ChunkPos chunkPos) {
		Throwable throwable2 = throwable instanceof CompletionException completionException ? completionException.getCause() : throwable;
		Throwable throwable3 = throwable2 instanceof CrashException crashException ? crashException.getCause() : throwable2;
		boolean bl = throwable3 instanceof Error;
		boolean bl2 = throwable3 instanceof IOException || throwable3 instanceof ChunkSerializer.ChunkLoadingException;
		if (!bl && bl2) {
			LOGGER.error("Couldn't load chunk {}", chunkPos, throwable3);
			this.world.getServer().onChunkLoadFailure(chunkPos);
			return this.getProtoChunk(chunkPos);
		} else {
			CrashReport crashReport = CrashReport.create(throwable, "Exception loading chunk");
			CrashReportSection crashReportSection = crashReport.addElement("Chunk being loaded");
			crashReportSection.add("pos", chunkPos);
			this.markAsProtoChunk(chunkPos);
			throw new CrashException(crashReport);
		}
	}

	private Chunk getProtoChunk(ChunkPos chunkPos) {
		this.markAsProtoChunk(chunkPos);
		return new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA, this.world, this.world.getRegistryManager().get(RegistryKeys.BIOME), null);
	}

	private void markAsProtoChunk(ChunkPos pos) {
		this.chunkToType.put(pos.toLong(), (byte)-1);
	}

	private byte mark(ChunkPos pos, ChunkType type) {
		return this.chunkToType.put(pos.toLong(), (byte)(type == ChunkType.PROTOCHUNK ? -1 : 1));
	}

	private CompletableFuture<OptionalChunk<Chunk>> upgradeChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
		ChunkPos chunkPos = holder.getPos();
		CompletableFuture<OptionalChunk<List<Chunk>>> completableFuture = this.getRegion(
			holder, requiredStatus.getTaskMargin(), distance -> this.getRequiredStatusForGeneration(requiredStatus, distance)
		);
		this.world.getProfiler().visit((Supplier<String>)(() -> "chunkGenerate " + requiredStatus));
		Executor executor = task -> this.worldGenExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task));
		return completableFuture.thenComposeAsync(optionalChunk -> {
			List<Chunk> list = (List<Chunk>)optionalChunk.orElse(null);
			if (list == null) {
				this.releaseLightTicket(chunkPos);
				return CompletableFuture.completedFuture(OptionalChunk.of(optionalChunk::getError));
			} else {
				try {
					Chunk chunk = (Chunk)list.get(list.size() / 2);
					CompletableFuture<Chunk> completableFuturex;
					if (chunk.getStatus().isAtLeast(requiredStatus)) {
						completableFuturex = requiredStatus.runLoadTask(this.field_49171, chunkx -> this.convertToFullChunk(holder, chunkx), chunk);
					} else {
						completableFuturex = requiredStatus.runGenerationTask(this.field_49171, executor, chunkx -> this.convertToFullChunk(holder, chunkx), list);
					}

					this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
					return completableFuturex.thenApply(OptionalChunk::of);
				} catch (Exception var10) {
					var10.getStackTrace();
					CrashReport crashReport = CrashReport.create(var10, "Exception generating new chunk");
					CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
					crashReportSection.add("Status being generated", (CrashCallable<String>)(() -> Registries.CHUNK_STATUS.getId(requiredStatus).toString()));
					crashReportSection.add("Location", String.format(Locale.ROOT, "%d,%d", chunkPos.x, chunkPos.z));
					crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
					crashReportSection.add("Generator", this.chunkGenerator);
					this.mainThreadExecutor.execute(() -> {
						throw new CrashException(crashReport);
					});
					throw new CrashException(crashReport);
				}
			}
		}, executor);
	}

	protected void releaseLightTicket(ChunkPos pos) {
		this.mainThreadExecutor
			.send(
				Util.debugRunnable(
					(Runnable)(() -> this.ticketManager.removeTicketWithLevel(ChunkTicketType.LIGHT, pos, ChunkLevels.getLevelFromStatus(ChunkStatus.LIGHT), pos)),
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

	private CompletableFuture<Chunk> convertToFullChunk(ChunkHolder chunkHolder, Chunk chunk) {
		return CompletableFuture.supplyAsync(() -> {
			ChunkPos chunkPos = chunkHolder.getPos();
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			WorldChunk worldChunk;
			if (protoChunk2 instanceof WrapperProtoChunk) {
				worldChunk = ((WrapperProtoChunk)protoChunk2).getWrappedChunk();
			} else {
				worldChunk = new WorldChunk(this.world, protoChunk2, chunkx -> addEntitiesFromNbt(this.world, protoChunk2.getEntities()));
				chunkHolder.setCompletedChunk(new WrapperProtoChunk(worldChunk, false));
			}

			worldChunk.setLevelTypeProvider(() -> ChunkLevels.getType(chunkHolder.getLevel()));
			worldChunk.loadEntities();
			if (this.loadedChunks.add(chunkPos.toLong())) {
				worldChunk.setLoadedToWorld(true);
				worldChunk.updateAllBlockEntities();
				worldChunk.addChunkTickSchedulers(this.world);
			}

			return worldChunk;
		}, task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(task, chunkHolder.getPos().toLong(), chunkHolder::getLevel)));
	}

	public CompletableFuture<OptionalChunk<WorldChunk>> makeChunkTickable(ChunkHolder holder) {
		CompletableFuture<OptionalChunk<List<Chunk>>> completableFuture = this.getRegion(holder, 1, distance -> ChunkStatus.FULL);
		CompletableFuture<OptionalChunk<WorldChunk>> completableFuture2 = completableFuture.thenApplyAsync(
				optionalChunk -> optionalChunk.map(cs -> (WorldChunk)cs.get(cs.size() / 2)),
				task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task))
			)
			.thenApplyAsync(optionalChunk -> optionalChunk.ifPresent(chunk -> {
					chunk.runPostProcessing();
					this.world.disableTickSchedulers(chunk);
					CompletableFuture<?> completableFuturex = holder.getPostProcessingFuture();
					if (completableFuturex.isDone()) {
						this.sendToPlayers(chunk);
					} else {
						completableFuturex.thenAcceptAsync(v -> this.sendToPlayers(chunk), this.mainThreadExecutor);
					}
				}), this.mainThreadExecutor);
		completableFuture2.handle((optionalChunk, throwable) -> {
			this.totalChunksLoadedCount.getAndIncrement();
			return null;
		});
		return completableFuture2;
	}

	private void sendToPlayers(WorldChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();

		for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk()) {
			if (serverPlayerEntity.getChunkFilter().isWithinDistance(chunkPos)) {
				track(serverPlayerEntity, chunk);
			}
		}
	}

	public CompletableFuture<OptionalChunk<WorldChunk>> makeChunkAccessible(ChunkHolder holder) {
		return this.getRegion(holder, 1, ChunkStatus::byDistanceFromFull)
			.thenApplyAsync(
				optionalChunk -> optionalChunk.map(chunks -> (WorldChunk)chunks.get(chunks.size() / 2)),
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
			if (!(chunk instanceof WrapperProtoChunk) && !(chunk instanceof WorldChunk)) {
				return false;
			} else {
				long l = chunk.getPos().toLong();
				long m = this.chunkToNextSaveTimeMs.getOrDefault(l, -1L);
				long n = System.currentTimeMillis();
				if (n < m) {
					return false;
				} else {
					boolean bl = this.save(chunk);
					chunkHolder.updateAccessibleStatus();
					if (bl) {
						this.chunkToNextSaveTimeMs.put(l, n + 10000L);
					}

					return bl;
				}
			}
		}
	}

	private boolean save(Chunk chunk) {
		this.pointOfInterestStorage.saveChunk(chunk.getPos());
		if (!chunk.needsSaving()) {
			return false;
		} else {
			chunk.setNeedsSaving(false);
			ChunkPos chunkPos = chunk.getPos();

			try {
				ChunkStatus chunkStatus = chunk.getStatus();
				if (chunkStatus.getChunkType() != ChunkType.LEVELCHUNK) {
					if (this.isLevelChunk(chunkPos)) {
						return false;
					}

					if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
						return false;
					}
				}

				this.world.getProfiler().visit("chunkSave");
				NbtCompound nbtCompound = ChunkSerializer.serialize(this.world, chunk);
				this.setNbt(chunkPos, nbtCompound).exceptionallyAsync(throwable -> {
					this.world.getServer().onChunkSaveFailure(chunkPos);
					return null;
				}, this.mainThreadExecutor);
				this.mark(chunkPos, chunkStatus.getChunkType());
				return true;
			} catch (Exception var5) {
				LOGGER.error("Failed to save chunk {},{}", chunkPos.x, chunkPos.z, var5);
				this.world.getServer().onChunkSaveFailure(chunkPos);
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
				nbtCompound = (NbtCompound)((Optional)this.getUpdatedChunkNbt(pos).join()).orElse(null);
				if (nbtCompound == null) {
					this.markAsProtoChunk(pos);
					return false;
				}
			} catch (Exception var5) {
				LOGGER.error("Failed to read chunk {}", pos, var5);
				this.markAsProtoChunk(pos);
				return false;
			}

			ChunkType chunkType = ChunkSerializer.getChunkType(nbtCompound);
			return this.mark(pos, chunkType) == 1;
		}
	}

	protected void setViewDistance(int watchDistance) {
		int i = MathHelper.clamp(watchDistance, 2, 32);
		if (i != this.watchDistance) {
			this.watchDistance = i;
			this.ticketManager.setWatchDistance(this.watchDistance);

			for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk()) {
				this.sendWatchPackets(serverPlayerEntity);
			}
		}
	}

	int getViewDistance(ServerPlayerEntity player) {
		return MathHelper.clamp(player.getViewDistance(), 2, this.watchDistance);
	}

	private void track(ServerPlayerEntity player, ChunkPos pos) {
		WorldChunk worldChunk = this.getPostProcessedChunk(pos.toLong());
		if (worldChunk != null) {
			track(player, worldChunk);
		}
	}

	private static void track(ServerPlayerEntity player, WorldChunk chunk) {
		player.networkHandler.chunkDataSender.add(chunk);
	}

	private static void untrack(ServerPlayerEntity player, ChunkPos pos) {
		player.networkHandler.chunkDataSender.unload(player, pos);
	}

	@Nullable
	public WorldChunk getPostProcessedChunk(long pos) {
		ChunkHolder chunkHolder = this.getChunkHolder(pos);
		return chunkHolder == null ? null : chunkHolder.getPostProcessedChunk();
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
				optional2.map(chunk -> chunk.getBlockEntities().size()).orElse(0),
				simulationDistanceLevelPropagator.getTickingTicket(l),
				simulationDistanceLevelPropagator.getLevel(l),
				optional2.map(chunk -> chunk.getBlockTickScheduler().getTickCount()).orElse(0),
				optional2.map(chunk -> chunk.getFluidTickScheduler().getTickCount()).orElse(0)
			);
		}
	}

	private static String getFutureStatus(CompletableFuture<OptionalChunk<WorldChunk>> future) {
		try {
			OptionalChunk<WorldChunk> optionalChunk = (OptionalChunk<WorldChunk>)future.getNow(null);
			if (optionalChunk != null) {
				return optionalChunk.isPresent() ? "done" : "unloaded";
			} else {
				return "not completed";
			}
		} catch (CompletionException var2) {
			return "failed " + var2.getCause().getMessage();
		} catch (CancellationException var3) {
			return "cancelled";
		}
	}

	private CompletableFuture<Optional<NbtCompound>> getUpdatedChunkNbt(ChunkPos chunkPos) {
		return this.getNbt(chunkPos).thenApplyAsync(nbt -> nbt.map(this::updateChunkNbt), Util.getMainWorkerExecutor());
	}

	private NbtCompound updateChunkNbt(NbtCompound nbt) {
		return this.updateChunkNbt(this.world.getRegistryKey(), this.persistentStateManagerFactory, nbt, this.chunkGenerator.getCodecKey());
	}

	boolean shouldTick(ChunkPos pos) {
		if (!this.ticketManager.shouldTick(pos.toLong())) {
			return false;
		} else {
			for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk()) {
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

			for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk()) {
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
		if (added) {
			this.playerChunkWatchingManager.add(player, bl);
			this.updateWatchedSection(player);
			if (!bl) {
				this.ticketManager.handleChunkEnter(ChunkSectionPos.from(player), player);
			}

			player.setChunkFilter(ChunkFilter.IGNORE_ALL);
			this.sendWatchPackets(player);
		} else {
			ChunkSectionPos chunkSectionPos = player.getWatchedSection();
			this.playerChunkWatchingManager.remove(player);
			if (!bl2) {
				this.ticketManager.handleChunkLeave(chunkSectionPos, player);
			}

			this.sendWatchPackets(player, ChunkFilter.IGNORE_ALL);
		}
	}

	/**
	 * Updates the watched chunk section position for the {@code player}, and sends a
	 * render distance update packet to the client.
	 */
	private void updateWatchedSection(ServerPlayerEntity player) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(player);
		player.setWatchedSection(chunkSectionPos);
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

		ChunkSectionPos chunkSectionPos = player.getWatchedSection();
		ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(player);
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

			this.sendWatchPackets(player);
		}
	}

	private void sendWatchPackets(ServerPlayerEntity player) {
		ChunkPos chunkPos = player.getChunkPos();
		int i = this.getViewDistance(player);
		if (player.getChunkFilter() instanceof ChunkFilter.Cylindrical cylindrical && cylindrical.center().equals(chunkPos) && cylindrical.viewDistance() == i) {
			return;
		}

		this.sendWatchPackets(player, ChunkFilter.cylindrical(chunkPos, i));
	}

	private void sendWatchPackets(ServerPlayerEntity player, ChunkFilter chunkFilter) {
		if (player.getWorld() == this.world) {
			ChunkFilter chunkFilter2 = player.getChunkFilter();
			if (chunkFilter instanceof ChunkFilter.Cylindrical cylindrical
				&& (!(chunkFilter2 instanceof ChunkFilter.Cylindrical cylindrical2) || !cylindrical2.center().equals(cylindrical.center()))) {
				player.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(cylindrical.center().x, cylindrical.center().z));
			}

			ChunkFilter.forEachChangedChunk(chunkFilter2, chunkFilter, chunkPos -> this.track(player, chunkPos), chunkPos -> untrack(player, chunkPos));
			player.setChunkFilter(chunkFilter);
		}
	}

	@Override
	public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
		Set<ServerPlayerEntity> set = this.playerChunkWatchingManager.getPlayersWatchingChunk();
		Builder<ServerPlayerEntity> builder = ImmutableList.builder();

		for (ServerPlayerEntity serverPlayerEntity : set) {
			if (onlyOnWatchDistanceEdge && this.isOnTrackEdge(serverPlayerEntity, chunkPos.x, chunkPos.z)
				|| !onlyOnWatchDistanceEdge && this.isTracked(serverPlayerEntity, chunkPos.x, chunkPos.z)) {
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
		for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk()) {
			this.sendWatchPackets(serverPlayerEntity);
		}

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

	public void sendChunkBiomePackets(List<Chunk> chunks) {
		Map<ServerPlayerEntity, List<WorldChunk>> map = new HashMap();

		for (Chunk chunk : chunks) {
			ChunkPos chunkPos = chunk.getPos();
			WorldChunk worldChunk2;
			if (chunk instanceof WorldChunk worldChunk) {
				worldChunk2 = worldChunk;
			} else {
				worldChunk2 = this.world.getChunk(chunkPos.x, chunkPos.z);
			}

			for (ServerPlayerEntity serverPlayerEntity : this.getPlayersWatchingChunk(chunkPos, false)) {
				((List)map.computeIfAbsent(serverPlayerEntity, player -> new ArrayList())).add(worldChunk2);
			}
		}

		map.forEach((player, chunksx) -> player.networkHandler.sendPacket(ChunkBiomeDataS2CPacket.create(chunksx)));
	}

	protected PointOfInterestStorage getPointOfInterestStorage() {
		return this.pointOfInterestStorage;
	}

	public String getSaveDir() {
		return this.saveDir;
	}

	void onChunkStatusChange(ChunkPos chunkPos, ChunkLevelType levelType) {
		this.chunkStatusChangeListener.onChunkStatusChange(chunkPos, levelType);
	}

	public void forceLighting(ChunkPos centerPos, int radius) {
		int i = radius + 1;
		ChunkPos.stream(centerPos, i).forEach(pos -> {
			ChunkHolder chunkHolder = this.getChunkHolder(pos.toLong());
			if (chunkHolder != null) {
				chunkHolder.combinePostProcessingFuture(this.lightingProvider.enqueue(pos.x, pos.z));
			}
		});
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
		private final Set<PlayerAssociatedNetworkHandler> listeners = Sets.newIdentityHashSet();

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
			for (PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler : this.listeners) {
				playerAssociatedNetworkHandler.sendPacket(packet);
			}
		}

		public void sendToNearbyPlayers(Packet<?> packet) {
			this.sendToOtherNearbyPlayers(packet);
			if (this.entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
			}
		}

		public void stopTracking() {
			for (PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler : this.listeners) {
				this.entry.stopTracking(playerAssociatedNetworkHandler.getPlayer());
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
				Vec3d vec3d = player.getPos().subtract(this.entity.getPos());
				int i = ThreadedAnvilChunkStorage.this.getViewDistance(player);
				double d = (double)Math.min(this.getMaxTrackDistance(), i * 16);
				double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
				double f = d * d;
				boolean bl = e <= f
					&& this.entity.canBeSpectated(player)
					&& ThreadedAnvilChunkStorage.this.isTracked(player, this.entity.getChunkPos().x, this.entity.getChunkPos().z);
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

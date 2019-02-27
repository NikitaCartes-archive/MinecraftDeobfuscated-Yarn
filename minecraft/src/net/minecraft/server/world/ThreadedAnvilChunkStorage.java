package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Actor;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.VersionedRegionFileCache;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedAnvilChunkStorage extends VersionedRegionFileCache implements ChunkHolder.PlayersWatchingChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int field_18239 = 33 + ChunkStatus.getMaxTargetGenerationRadius();
	private final Long2ObjectLinkedOpenHashMap<ChunkHolder> posToHolder = new Long2ObjectLinkedOpenHashMap<>();
	private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> posToHolderCopy = this.posToHolder.clone();
	private final LongSet field_18307 = new LongOpenHashSet();
	private final ServerWorld world;
	private final ServerLightingProvider serverLightingProvider;
	private final Executor genQueueAdder;
	private final ChunkGenerator<?> chunkGenerator;
	private final Supplier<PersistentStateManager> persistentStateManagerFactory;
	private final LongSet field_17221 = new LongOpenHashSet();
	private boolean posToHolderCopyOutdated;
	private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> worldgenActor;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> mainActor;
	private final WorldGenerationProgressListener worldGenerationProgressListener;
	private final ThreadedAnvilChunkStorage.TicketManager ticketManager;
	private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();
	private final StructureManager structureManager;
	private final File saveDir;
	private final PlayerChunkWatchingManager field_18241 = new PlayerChunkWatchingManager();
	private final Int2ObjectMap<ThreadedAnvilChunkStorage.EntityTracker> field_18242 = new Int2ObjectOpenHashMap<>();
	private int field_18243;
	private int field_18244;

	public ThreadedAnvilChunkStorage(
		ServerWorld serverWorld,
		File file,
		DataFixer dataFixer,
		StructureManager structureManager,
		Executor executor,
		Executor executor2,
		ChunkProvider chunkProvider,
		ChunkGenerator<?> chunkGenerator,
		WorldGenerationProgressListener worldGenerationProgressListener,
		Supplier<PersistentStateManager> supplier,
		int i,
		int j
	) {
		super(dataFixer);
		this.structureManager = structureManager;
		this.saveDir = serverWorld.getDimension().getType().getFile(file);
		this.world = serverWorld;
		this.chunkGenerator = chunkGenerator;
		this.genQueueAdder = executor2;
		MailboxProcessor<Runnable> mailboxProcessor = MailboxProcessor.create(executor, "worldgen");
		MailboxProcessor<Runnable> mailboxProcessor2 = MailboxProcessor.create(executor2, "main");
		this.worldGenerationProgressListener = worldGenerationProgressListener;
		MailboxProcessor<Runnable> mailboxProcessor3 = MailboxProcessor.create(executor, "light");
		this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(
			ImmutableList.of(mailboxProcessor, mailboxProcessor2, mailboxProcessor3), executor, Integer.MAX_VALUE
		);
		this.worldgenActor = this.chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor, false);
		this.mainActor = this.chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor2, false);
		this.serverLightingProvider = new ServerLightingProvider(
			chunkProvider, this, this.world.getDimension().hasSkyLight(), mailboxProcessor3, this.chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor3, false)
		);
		this.ticketManager = new ThreadedAnvilChunkStorage.TicketManager(executor, executor2);
		this.persistentStateManagerFactory = supplier;
		this.applyViewDistance(i, j);
	}

	private static double method_18704(ChunkPos chunkPos, Entity entity) {
		double d = (double)(chunkPos.x * 16 + 8);
		double e = (double)(chunkPos.z * 16 + 8);
		double f = d - entity.x;
		double g = e - entity.z;
		return f * f + g * g;
	}

	private static int method_18719(ChunkPos chunkPos, ServerPlayerEntity serverPlayerEntity, boolean bl) {
		int i;
		int j;
		if (bl) {
			ChunkPos chunkPos2 = serverPlayerEntity.getChunkPos().toChunkPos();
			i = chunkPos2.x;
			j = chunkPos2.z;
		} else {
			i = MathHelper.floor(serverPlayerEntity.x / 16.0);
			j = MathHelper.floor(serverPlayerEntity.z / 16.0);
		}

		return method_18703(chunkPos, i, j);
	}

	private static int method_18718(ChunkPos chunkPos, Entity entity) {
		return method_18703(chunkPos, MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0));
	}

	private static int method_18703(ChunkPos chunkPos, int i, int j) {
		int k = chunkPos.x - i;
		int l = chunkPos.z - j;
		return Math.max(Math.abs(k), Math.abs(l));
	}

	protected ServerLightingProvider getLightProvider() {
		return this.serverLightingProvider;
	}

	@Nullable
	protected ChunkHolder getChunkHolder(long l) {
		return this.posToHolder.get(l);
	}

	@Nullable
	protected ChunkHolder getCopiedChunkHolder(long l) {
		return this.posToHolderCopy.get(l);
	}

	public IntSupplier method_17604(long l) {
		return () -> {
			ChunkHolder chunkHolder = this.getCopiedChunkHolder(l);
			return chunkHolder == null ? LevelIndexedQueue.LEVEL_COUNT - 1 : Math.min(chunkHolder.getLastLevelUpdatedTo(), LevelIndexedQueue.LEVEL_COUNT - 1);
		};
	}

	@Environment(EnvType.CLIENT)
	public String getDebugString(ChunkPos chunkPos) {
		ChunkHolder chunkHolder = this.getCopiedChunkHolder(chunkPos.toLong());
		if (chunkHolder == null) {
			return "null";
		} else {
			String string = chunkHolder.getLevel() + "\n";
			ChunkStatus chunkStatus = chunkHolder.method_16141();
			Chunk chunk = chunkHolder.getChunk();
			if (chunkStatus != null) {
				string = string + "St: §" + chunkStatus.getIndex() + chunkStatus + '§' + "r\n";
			}

			if (chunk != null) {
				string = string + "Ch: §" + chunk.getStatus().getIndex() + chunk.getStatus() + '§' + "r\n";
			}

			ChunkHolder.LevelType levelType = chunkHolder.getLevelType();
			string = string + "§" + levelType.ordinal() + levelType;
			return string + '§' + "r";
		}
	}

	private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> getChunkRegion(ChunkPos chunkPos, int i, IntFunction<ChunkStatus> intFunction) {
		List<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> list = Lists.<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>>newArrayList();
		int j = chunkPos.x;
		int k = chunkPos.z;

		for (int l = -i; l <= i; l++) {
			for (int m = -i; m <= i; m++) {
				int n = Math.max(Math.abs(m), Math.abs(l));
				final ChunkPos chunkPos2 = new ChunkPos(j + m, k + l);
				long o = chunkPos2.toLong();
				ChunkHolder chunkHolder = this.getChunkHolder(o);
				if (chunkHolder == null) {
					return CompletableFuture.completedFuture(Either.right(new ChunkHolder.Unloaded() {
						public String toString() {
							return "Unloaded " + chunkPos2.toString();
						}
					}));
				}

				ChunkStatus chunkStatus = (ChunkStatus)intFunction.apply(n);
				CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getChunk(chunkStatus, this);
				list.add(completableFuture);
			}
		}

		CompletableFuture<List<Either<Chunk, ChunkHolder.Unloaded>>> completableFuture2 = SystemUtil.thenCombine(list);
		return completableFuture2.thenApply(listx -> {
			List<Chunk> list2 = Lists.<Chunk>newArrayList();
			int l = 0;

			for (final Either<Chunk, ChunkHolder.Unloaded> either : listx) {
				Optional<Chunk> optional = either.left();
				if (!optional.isPresent()) {
					final int mx = l;
					return Either.right(new ChunkHolder.Unloaded() {
						public String toString() {
							return "Unloaded " + new ChunkPos(j + mx % (i * 2 + 1), k + mx / (i * 2 + 1)) + " " + ((ChunkHolder.Unloaded)either.right().get()).toString();
						}
					});
				}

				list2.add(optional.get());
				l++;
			}

			return Either.left(list2);
		});
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> method_17247(ChunkPos chunkPos) {
		return this.getChunkRegion(chunkPos, 2, i -> ChunkStatus.FULL)
			.thenApplyAsync(either -> either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2)), this.genQueueAdder);
	}

	@Nullable
	private ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j) {
		if (j > field_18239 && i > field_18239) {
			return chunkHolder;
		} else {
			if (chunkHolder != null) {
				chunkHolder.setLevel(i);
			}

			if (chunkHolder != null) {
				if (i > field_18239) {
					this.field_17221.add(l);
				} else {
					this.field_17221.remove(l);
				}
			}

			if (i <= field_18239 && chunkHolder == null) {
				chunkHolder = new ChunkHolder(new ChunkPos(l), i, this.serverLightingProvider, this.chunkTaskPrioritySystem, this);
				this.posToHolder.put(l, chunkHolder);
				this.posToHolderCopyOutdated = true;
			}

			return chunkHolder;
		}
	}

	@Override
	public void close() throws IOException {
		this.chunkTaskPrioritySystem.close();
		this.save(true);
		super.close();
	}

	protected void save(boolean bl) {
		this.posToHolderCopy.values().stream().map(ChunkHolder::getChunk).filter(Objects::nonNull).forEach(this::save);
		if (bl) {
			LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.saveDir.getName());
		}
	}

	protected void unload(BooleanSupplier booleanSupplier) {
		if (!this.world.isSavingDisabled()) {
			LongIterator longIterator = this.field_17221.iterator();

			for (int i = 0; longIterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.field_17221.size() > 2000); longIterator.remove()) {
				long l = longIterator.nextLong();
				ChunkHolder chunkHolder = this.posToHolder.remove(l);
				if (chunkHolder != null) {
					this.posToHolderCopyOutdated = true;
					i++;
					CompletableFuture<Chunk> completableFuture = chunkHolder.getChunkFuture();
					completableFuture.thenAcceptAsync(chunk -> {
						if (chunk != null) {
							this.save(chunk);
							if (this.field_18307.remove(l) && chunk instanceof WorldChunk) {
								WorldChunk worldChunk = (WorldChunk)chunk;
								worldChunk.setLoadedToWorld(false);
								this.world.method_18764(worldChunk);
							}

							for (int ix = 0; ix < 16; ix++) {
								this.serverLightingProvider.scheduleChunkLightUpdate(ChunkSectionPos.from(chunk.getPos(), ix), true);
							}

							this.serverLightingProvider.tick();
							this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);
						}
					}, this.genQueueAdder);
				}
			}
		}
	}

	protected void updateHolderMap() {
		if (this.posToHolderCopyOutdated) {
			this.posToHolderCopy = this.posToHolder.clone();
			this.posToHolderCopyOutdated = false;
		}
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunk(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
		ChunkPos chunkPos = chunkHolder.getPos();
		if (chunkStatus == ChunkStatus.EMPTY) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					CompoundTag compoundTag = this.getUpdatedChunkTag(chunkPos);
					if (compoundTag != null) {
						boolean bl = compoundTag.containsKey("Level", 10) && compoundTag.getCompound("Level").containsKey("Status", 8);
						if (bl) {
							Chunk chunk = ChunkSerializer.deserialize(this.world, this.structureManager, chunkPos, compoundTag);
							chunk.setLastSaveTime(this.world.getTime());
							return Either.left(chunk);
						}

						LOGGER.error("Chunk file at {} is missing level data, skipping", chunkPos);
					}
				} catch (CrashException var5) {
					throw var5;
				} catch (Exception var6) {
					LOGGER.error("Couldn't load chunk", (Throwable)var6);
				}

				return Either.left(new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA));
			}, this.genQueueAdder);
		} else {
			CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getChunkRegion(
				chunkPos, chunkStatus.getTaskMargin(), i -> this.getPrecedingTargetChunkGenerationStatus(chunkStatus, i)
			);
			return completableFuture.thenComposeAsync(
				either -> (CompletableFuture)either.map(
						list -> {
							try {
								CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuturex = chunkStatus.runTask(
										this.world, this.chunkGenerator, this.structureManager, this.serverLightingProvider, this::convertToFullChunk, list
									)
									.thenApply(Either::left);
								this.worldGenerationProgressListener.setChunkStatus(chunkPos, chunkStatus);
								return completableFuturex;
							} catch (Exception var8) {
								CrashReport crashReport = CrashReport.create(var8, "Exception generating new chunk");
								CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
								crashReportSection.add("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
								crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
								crashReportSection.add("Generator", this.chunkGenerator);
								throw new CrashException(crashReport);
							}
						},
						unloaded -> CompletableFuture.completedFuture(Either.right(unloaded))
					),
				runnable -> this.worldgenActor.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(chunkHolder, runnable))
			);
		}
	}

	private ChunkStatus getPrecedingTargetChunkGenerationStatus(ChunkStatus chunkStatus, int i) {
		ChunkStatus chunkStatus2;
		if (i == 0) {
			chunkStatus2 = chunkStatus.getPrevious();
		} else {
			chunkStatus2 = ChunkStatus.getTargetGenerationStatus(ChunkStatus.getTargetGenerationRadius(chunkStatus) + i);
		}

		return chunkStatus2;
	}

	public CompletableFuture<Chunk> convertToFullChunk(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkHolder chunkHolder = this.getCopiedChunkHolder(chunkPos.toLong());
		if (chunkHolder == null) {
			throw new IllegalStateException("No chunk holder while trying to convert to full chunk: " + chunkPos);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				WorldChunk worldChunk;
				if (chunk instanceof ReadOnlyChunk) {
					worldChunk = ((ReadOnlyChunk)chunk).getWrappedChunk();
				} else {
					worldChunk = new WorldChunk(this.world, (ProtoChunk)chunk);
				}

				worldChunk.setLevelTypeProvider(() -> ChunkHolder.getLevelType(chunkHolder.getLevel()));
				worldChunk.loadToWorld();
				if (this.field_18307.add(chunkPos.toLong())) {
					worldChunk.setLoadedToWorld(true);
					this.world.addBlockEntities(worldChunk.getBlockEntityMap().values());

					for (TypeFilterableList<Entity> typeFilterableList : worldChunk.getEntitySectionArray()) {
						typeFilterableList.stream().filter(entity -> !(entity instanceof PlayerEntity)).forEach(this.world::method_18214);
					}
				}

				return worldChunk;
			}, runnable -> this.mainActor.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(chunkHolder, () -> this.genQueueAdder.execute(runnable))));
		}
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> method_17235(ChunkHolder chunkHolder) {
		ChunkPos chunkPos = chunkHolder.getPos();
		CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getChunkRegion(chunkPos, 1, i -> ChunkStatus.FULL);
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture2 = completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
				WorldChunk worldChunk = (WorldChunk)list.get(list.size() / 2);
				worldChunk.runPostProcessing();
				return Either.left(worldChunk);
			}), runnable -> this.mainActor.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(chunkHolder, () -> this.genQueueAdder.execute(runnable))));
		completableFuture2.thenAcceptAsync(either -> either.mapLeft(worldChunk -> {
				this.totalChunksLoadedCount.getAndIncrement();
				Packet<?>[] packets = new Packet[2];
				this.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> this.method_18715(serverPlayerEntity, packets, worldChunk));
				return Either.left(worldChunk);
			}), runnable -> this.mainActor.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(chunkHolder, () -> this.genQueueAdder.execute(runnable))));
		return completableFuture2;
	}

	public int getTotalChunksLoadedCount() {
		return this.totalChunksLoadedCount.get();
	}

	private void save(Chunk chunk) {
		if (chunk.needsSaving()) {
			try {
				this.world.checkSessionLock();
			} catch (SessionLockException var6) {
				LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)var6);
				return;
			}

			chunk.setLastSaveTime(this.world.getTime());
			chunk.setShouldSave(false);
			ChunkPos chunkPos = chunk.getPos();

			try {
				ChunkStatus chunkStatus = chunk.getStatus();
				if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
					CompoundTag compoundTag = this.getUpdatedChunkTag(chunkPos);
					if (compoundTag != null && ChunkSerializer.getChunkType(compoundTag) == ChunkStatus.ChunkType.LEVELCHUNK) {
						return;
					}

					if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
						return;
					}
				}

				CompoundTag compoundTagx = ChunkSerializer.serialize(this.world, chunk);
				this.writeChunkTag(chunkPos, compoundTagx);
			} catch (Exception var5) {
				LOGGER.error("Failed to save chunk {},{}", chunkPos.x, chunkPos.z, var5);
			}
		}
	}

	protected void applyViewDistance(int i, int j) {
		int k = MathHelper.clamp(i + 1, 3, 33);
		if (k != this.field_18243) {
			int l = this.field_18243;
			this.field_18243 = k;
			this.ticketManager.method_14049(this.field_18243);

			for (ChunkHolder chunkHolder : this.posToHolder.values()) {
				ChunkPos chunkPos = chunkHolder.getPos();
				Packet<?>[] packets = new Packet[2];
				this.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> {
					int jx = method_18718(chunkPos, serverPlayerEntity);
					boolean bl = jx <= l;
					boolean bl2 = jx <= this.field_18243;
					this.sendWatchPackets(serverPlayerEntity, chunkPos, packets, bl, bl2);
				});
			}
		}

		int l = MathHelper.clamp(j + 1, 1, 16);
		if (l != this.field_18244) {
			this.field_18244 = l;
			this.ticketManager.method_18738(this.field_18244);
		}
	}

	protected void sendWatchPackets(ServerPlayerEntity serverPlayerEntity, ChunkPos chunkPos, Packet<?>[] packets, boolean bl, boolean bl2) {
		if (serverPlayerEntity.world == this.world) {
			if (bl2 && !bl) {
				ChunkHolder chunkHolder = this.getCopiedChunkHolder(chunkPos.toLong());
				if (chunkHolder != null) {
					WorldChunk worldChunk = chunkHolder.getWorldChunk();
					if (worldChunk != null) {
						this.method_18715(serverPlayerEntity, packets, worldChunk);
					}
				}
			}

			if (!bl2 && bl) {
				serverPlayerEntity.sendUnloadChunkPacket(chunkPos);
			}
		}
	}

	public int getLoadedChunkCount() {
		return this.posToHolderCopy.size();
	}

	protected ThreadedAnvilChunkStorage.TicketManager getTicketManager() {
		return this.ticketManager;
	}

	protected ObjectBidirectionalIterator<Entry<ChunkHolder>> entryIterator() {
		return this.posToHolderCopy.long2ObjectEntrySet().fastIterator();
	}

	@Nullable
	private CompoundTag getUpdatedChunkTag(ChunkPos chunkPos) throws IOException {
		CompoundTag compoundTag = this.readChunkTag(chunkPos);
		return compoundTag == null ? null : this.updateChunkTag(this.world.getDimension().getType(), this.persistentStateManagerFactory, compoundTag);
	}

	@Override
	protected File getRegionDir() {
		return new File(this.saveDir, "region");
	}

	boolean method_18724(ChunkPos chunkPos) {
		return this.field_18241.getPlayersWatchingChunk(chunkPos.toLong()).noneMatch(serverPlayerEntity -> method_18704(chunkPos, serverPlayerEntity) < 16384.0);
	}

	private boolean method_18722(ServerPlayerEntity serverPlayerEntity) {
		return serverPlayerEntity.isSpectator() && !this.world.getGameRules().getBoolean("spectatorsGenerateChunks");
	}

	void method_18714(ServerPlayerEntity serverPlayerEntity, boolean bl) {
		boolean bl2 = this.method_18722(serverPlayerEntity);
		boolean bl3 = this.field_18241.isWatchDisabled(serverPlayerEntity);
		int i = MathHelper.floor(serverPlayerEntity.x) >> 4;
		int j = MathHelper.floor(serverPlayerEntity.z) >> 4;
		if (bl) {
			this.field_18241.add(ChunkPos.toLong(i, j), serverPlayerEntity, bl2);
			if (!bl2) {
				this.ticketManager.method_14048(ChunkSectionPos.from(serverPlayerEntity), serverPlayerEntity);
			}
		} else {
			ChunkSectionPos chunkSectionPos = serverPlayerEntity.getChunkPos();
			this.field_18241.remove(chunkSectionPos.toChunkPos().toLong(), serverPlayerEntity);
			if (!bl2) {
				this.ticketManager.method_14051(chunkSectionPos, serverPlayerEntity);
			}
		}

		for (int k = i - this.field_18243; k <= i + this.field_18243; k++) {
			for (int l = j - this.field_18243; l <= j + this.field_18243; l++) {
				ChunkPos chunkPos = new ChunkPos(k, l);
				this.sendWatchPackets(serverPlayerEntity, chunkPos, new Packet[2], !bl && !bl3, bl && !bl2);
			}
		}
	}

	public void method_18713(ServerPlayerEntity serverPlayerEntity) {
		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.field_18242.values()) {
			if (entityTracker.field_18247 == serverPlayerEntity) {
				entityTracker.method_18729(this.world.getPlayers());
			} else {
				entityTracker.method_18736(serverPlayerEntity);
			}
		}

		int i = MathHelper.floor(serverPlayerEntity.x) >> 4;
		int j = MathHelper.floor(serverPlayerEntity.z) >> 4;
		ChunkSectionPos chunkSectionPos = serverPlayerEntity.getChunkPos();
		ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(serverPlayerEntity);
		long l = chunkSectionPos.toChunkPos().toLong();
		long m = chunkSectionPos2.toChunkPos().toLong();
		boolean bl = this.field_18241.isWatchDisabled(serverPlayerEntity);
		boolean bl2 = this.method_18722(serverPlayerEntity);
		boolean bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();
		if (bl3 || bl != bl2) {
			if (!bl && bl2 || bl3) {
				this.ticketManager.method_14051(chunkSectionPos, serverPlayerEntity);
			}

			if (bl && !bl2 || bl3) {
				this.ticketManager.method_14048(chunkSectionPos2, serverPlayerEntity);
			}

			if (!bl && bl2) {
				this.field_18241.disableWatch(serverPlayerEntity);
			}

			if (bl && !bl2) {
				this.field_18241.enableWatch(serverPlayerEntity);
			}

			if (l != m) {
				this.field_18241.movePlayer(l, m, serverPlayerEntity);
			}

			int k = chunkSectionPos.getChunkX();
			int n = chunkSectionPos.getChunkZ();
			int o = Math.min(i, k) - this.field_18243;
			int p = Math.min(j, n) - this.field_18243;
			int q = Math.max(i, k) + this.field_18243;
			int r = Math.max(j, n) + this.field_18243;

			for (int s = o; s <= q; s++) {
				for (int t = p; t <= r; t++) {
					ChunkPos chunkPos = new ChunkPos(s, t);
					boolean bl4 = !bl && method_18703(chunkPos, k, n) <= this.field_18243;
					boolean bl5 = !bl2 && method_18703(chunkPos, i, j) <= this.field_18243;
					this.sendWatchPackets(serverPlayerEntity, chunkPos, new Packet[2], bl4, bl5);
				}
			}
		}
	}

	@Override
	public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean bl) {
		return this.field_18241.getPlayersWatchingChunk(chunkPos.toLong()).filter(serverPlayerEntity -> {
			int i = method_18719(chunkPos, serverPlayerEntity, true);
			return i > this.field_18243 ? false : !bl || i == this.field_18243;
		});
	}

	protected void method_18701(Entity entity) {
		if (!(entity instanceof EnderDragonPart)) {
			if (!(entity instanceof LightningEntity)) {
				EntityType<?> entityType = entity.getType();
				int i = entityType.method_18387() * 16;
				int j = entityType.method_18388();
				if (this.field_18242.containsKey(entity.getEntityId())) {
					throw new IllegalStateException("Entity is already tracked!");
				} else {
					ThreadedAnvilChunkStorage.EntityTracker entityTracker = new ThreadedAnvilChunkStorage.EntityTracker(entity, i, j, entityType.method_18389());
					this.field_18242.put(entity.getEntityId(), entityTracker);
					entityTracker.method_18729(this.world.getPlayers());
					if (entity instanceof ServerPlayerEntity) {
						ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
						this.method_18714(serverPlayerEntity, true);

						for (ThreadedAnvilChunkStorage.EntityTracker entityTracker2 : this.field_18242.values()) {
							if (entityTracker2.field_18247 != serverPlayerEntity) {
								entityTracker2.method_18736(serverPlayerEntity);
							}
						}
					}
				}
			}
		}
	}

	protected void method_18716(Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			this.method_18714(serverPlayerEntity, false);

			for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.field_18242.values()) {
				entityTracker.method_18733(serverPlayerEntity);
			}
		}

		ThreadedAnvilChunkStorage.EntityTracker entityTracker2 = this.field_18242.remove(entity.getEntityId());
		if (entityTracker2 != null) {
			entityTracker2.method_18728();
		}
	}

	protected void method_18727() {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();
		List<ServerPlayerEntity> list2 = this.world.getPlayers();

		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.field_18242.values()) {
			ChunkSectionPos chunkSectionPos = entityTracker.field_18249;
			ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(entityTracker.field_18247);
			if (!Objects.equals(chunkSectionPos, chunkSectionPos2)) {
				entityTracker.method_18729(list2);
				Entity entity = entityTracker.field_18247;
				if (entity instanceof ServerPlayerEntity) {
					list.add((ServerPlayerEntity)entity);
				}

				entityTracker.field_18249 = chunkSectionPos2;
			}

			entityTracker.field_18246.method_18756();
		}

		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.field_18242.values()) {
			entityTracker.method_18729(list);
		}
	}

	protected void method_18702(Entity entity, Packet<?> packet) {
		ThreadedAnvilChunkStorage.EntityTracker entityTracker = this.field_18242.get(entity.getEntityId());
		if (entityTracker != null) {
			entityTracker.method_18730(packet);
		}
	}

	protected void method_18717(Entity entity, Packet<?> packet) {
		ThreadedAnvilChunkStorage.EntityTracker entityTracker = this.field_18242.get(entity.getEntityId());
		if (entityTracker != null) {
			entityTracker.method_18734(packet);
		}
	}

	private void method_18715(ServerPlayerEntity serverPlayerEntity, Packet<?>[] packets, WorldChunk worldChunk) {
		if (packets[0] == null) {
			packets[0] = new ChunkDataS2CPacket(worldChunk, 65535);
			packets[1] = new LightUpdateS2CPacket(worldChunk.getPos(), this.serverLightingProvider);
		}

		serverPlayerEntity.sendInitialChunkPackets(worldChunk.getPos(), packets[0], packets[1]);
		List<Entity> list = Lists.<Entity>newArrayList();
		List<Entity> list2 = Lists.<Entity>newArrayList();

		for (ThreadedAnvilChunkStorage.EntityTracker entityTracker : this.field_18242.values()) {
			Entity entity = entityTracker.field_18247;
			if (entity != serverPlayerEntity && entity.chunkX == worldChunk.getPos().x && entity.chunkZ == worldChunk.getPos().z) {
				entityTracker.method_18736(serverPlayerEntity);
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
				serverPlayerEntity.networkHandler.sendPacket(new EntityAttachS2CPacket(entity2, ((MobEntity)entity2).getHoldingEntity()));
			}
		}

		if (!list2.isEmpty()) {
			for (Entity entity2 : list2) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity2));
			}
		}
	}

	class EntityTracker {
		private final EntityTrackerEntry field_18246;
		private final Entity field_18247;
		private final int field_18248;
		private ChunkSectionPos field_18249;
		private final Set<ServerPlayerEntity> field_18250 = Sets.<ServerPlayerEntity>newHashSet();

		public EntityTracker(Entity entity, int i, int j, boolean bl) {
			this.field_18246 = new EntityTrackerEntry(ThreadedAnvilChunkStorage.this.world, entity, j, bl, this::method_18730);
			this.field_18247 = entity;
			this.field_18248 = i;
			this.field_18249 = ChunkSectionPos.from(entity);
		}

		public boolean equals(Object object) {
			return object instanceof ThreadedAnvilChunkStorage.EntityTracker
				? ((ThreadedAnvilChunkStorage.EntityTracker)object).field_18247.getEntityId() == this.field_18247.getEntityId()
				: false;
		}

		public int hashCode() {
			return this.field_18247.getEntityId();
		}

		public void method_18730(Packet<?> packet) {
			for (ServerPlayerEntity serverPlayerEntity : this.field_18250) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}

		public void method_18734(Packet<?> packet) {
			this.method_18730(packet);
			if (this.field_18247 instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)this.field_18247).networkHandler.sendPacket(packet);
			}
		}

		public void method_18728() {
			for (ServerPlayerEntity serverPlayerEntity : this.field_18250) {
				this.field_18246.method_14302(serverPlayerEntity);
			}
		}

		public void method_18733(ServerPlayerEntity serverPlayerEntity) {
			if (this.field_18250.remove(serverPlayerEntity)) {
				this.field_18246.method_14302(serverPlayerEntity);
			}
		}

		public void method_18736(ServerPlayerEntity serverPlayerEntity) {
			if (serverPlayerEntity != this.field_18247) {
				Vec3d vec3d = new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z).subtract(this.field_18246.method_18759());
				int i = Math.min(this.field_18248, (ThreadedAnvilChunkStorage.this.field_18243 - 1) * 16);
				boolean bl = vec3d.x >= (double)(-i)
					&& vec3d.x <= (double)i
					&& vec3d.z >= (double)(-i)
					&& vec3d.z <= (double)i
					&& this.field_18247.method_5680(serverPlayerEntity);
				if (bl) {
					boolean bl2 = this.field_18247.teleporting;
					if (!bl2) {
						ChunkPos chunkPos = new ChunkPos(this.field_18247.chunkX, this.field_18247.chunkZ);
						ChunkHolder chunkHolder = ThreadedAnvilChunkStorage.this.getCopiedChunkHolder(chunkPos.toLong());
						if (chunkHolder != null && chunkHolder.getWorldChunk() != null) {
							bl2 = ThreadedAnvilChunkStorage.method_18719(chunkPos, serverPlayerEntity, false) <= ThreadedAnvilChunkStorage.this.field_18243;
						}
					}

					if (bl2 && this.field_18250.add(serverPlayerEntity)) {
						this.field_18246.method_18760(serverPlayerEntity);
					}
				} else if (this.field_18250.remove(serverPlayerEntity)) {
					this.field_18246.method_14302(serverPlayerEntity);
				}
			}
		}

		public void method_18729(List<ServerPlayerEntity> list) {
			for (ServerPlayerEntity serverPlayerEntity : list) {
				this.method_18736(serverPlayerEntity);
			}
		}
	}

	class TicketManager extends ChunkTicketManager {
		protected TicketManager(Executor executor, Executor executor2) {
			super(executor, executor2);
		}

		@Override
		protected boolean method_14035(long l) {
			return ThreadedAnvilChunkStorage.this.field_17221.contains(l);
		}

		@Nullable
		@Override
		protected ChunkHolder getChunkHolder(long l) {
			return ThreadedAnvilChunkStorage.this.getChunkHolder(l);
		}

		@Nullable
		@Override
		protected ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j) {
			return ThreadedAnvilChunkStorage.this.setLevel(l, i, chunkHolder, j);
		}
	}
}

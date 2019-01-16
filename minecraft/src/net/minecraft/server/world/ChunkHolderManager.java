package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.util.Actor;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.ChunkSaveHandler;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkHolderManager implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Long2ObjectLinkedOpenHashMap<ChunkHolder> posToHolder = new Long2ObjectLinkedOpenHashMap<>();
	private final World world;
	private final ServerLightingProvider serverLightingProvider;
	private final Executor genQueueAdder;
	private final ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;
	private final ChunkGenerator<?> chunkGenerator;
	private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> posToHolderCopy = this.posToHolder.clone();
	private final LongSet field_17221 = new LongOpenHashSet();
	private boolean posToHolderCopyOutdated;
	private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> worldgenActor;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> mainActor;
	private final WorldGenerationProgressListener field_17442;
	private final ChunkSaveHandler chunkSaveHandler;
	private final ChunkHolderManager.TicketManager ticketManager;
	private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();

	public ChunkHolderManager(
		Executor executor,
		ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider,
		Executor executor2,
		ChunkSaveHandler chunkSaveHandler,
		ChunkProvider chunkProvider,
		World world,
		ChunkGenerator<?> chunkGenerator,
		WorldGenerationProgressListener worldGenerationProgressListener
	) {
		this.world = world;
		this.playersWatchingChunkProvider = playersWatchingChunkProvider;
		this.chunkGenerator = chunkGenerator;
		this.genQueueAdder = executor2;
		this.chunkSaveHandler = chunkSaveHandler;
		MailboxProcessor<Runnable> mailboxProcessor = MailboxProcessor.create(executor, "worldgen");
		MailboxProcessor<Runnable> mailboxProcessor2 = MailboxProcessor.create(executor2, "main");
		this.field_17442 = worldGenerationProgressListener;
		MailboxProcessor<Runnable> mailboxProcessor3 = MailboxProcessor.create(executor, "light");
		this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(
			ImmutableList.of(mailboxProcessor, mailboxProcessor2, mailboxProcessor3), executor, Integer.MAX_VALUE
		);
		this.worldgenActor = this.chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor, false);
		this.mainActor = this.chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor2, false);
		this.serverLightingProvider = new ServerLightingProvider(
			chunkProvider, this, this.world.getDimension().hasSkyLight(), mailboxProcessor3, this.chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor3, false)
		);
		this.ticketManager = new ChunkHolderManager.TicketManager(executor, executor2);
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
		if (j > ServerChunkManager.LEVEL_COUNT && i > ServerChunkManager.LEVEL_COUNT) {
			return chunkHolder;
		} else {
			if (chunkHolder != null) {
				chunkHolder.setLevel(i);
			}

			if (chunkHolder != null) {
				if (i > ServerChunkManager.LEVEL_COUNT) {
					this.field_17221.add(l);
				} else {
					this.field_17221.remove(l);
				}
			}

			if (i <= ServerChunkManager.LEVEL_COUNT && chunkHolder == null) {
				chunkHolder = new ChunkHolder(new ChunkPos(l), i, this.serverLightingProvider, this.chunkTaskPrioritySystem, this.playersWatchingChunkProvider);
				this.posToHolder.put(l, chunkHolder);
				this.posToHolderCopyOutdated = true;
			}

			return chunkHolder;
		}
	}

	public void close() {
		this.chunkTaskPrioritySystem.close();
		this.posToHolderCopy.values().forEach(chunkHolder -> {
			Chunk chunk = chunkHolder.getChunk();
			if (chunk != null) {
				this.save(chunk, false);
			}
		});
		this.chunkSaveHandler.close();
	}

	protected void save(boolean bl) {
		for (ChunkHolder chunkHolder : this.posToHolderCopy.values()) {
			WorldChunk worldChunk = chunkHolder.getWorldChunk();
			if (worldChunk != null) {
				this.save(worldChunk, false);
			}
		}

		if (bl) {
			this.chunkSaveHandler.saveAllChunks();
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
							this.save(chunk, true);

							for (int ix = 0; ix < 16; ix++) {
								this.serverLightingProvider.scheduleChunkLightUpdate(chunk.getPos().x, ix, chunk.getPos().z, true);
							}

							this.serverLightingProvider.tick();
							this.field_17442.setChunkStatus(chunk.getPos(), null);
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

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> generateChunk(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
		ChunkPos chunkPos = chunkHolder.getPos();
		if (chunkStatus == ChunkStatus.EMPTY) {
			return CompletableFuture.supplyAsync(() -> {
				Chunk chunk = this.chunkSaveHandler.readChunk(this.world, chunkPos.x, chunkPos.z);
				if (chunk != null) {
					chunk.setLastSaveTime(this.world.getTime());
					return Either.left(chunk);
				} else {
					return Either.left(new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA));
				}
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
										this.world, this.chunkGenerator, this.serverLightingProvider, this::convertToFullChunk, list
									)
									.thenApply(Either::left);
								this.field_17442.setChunkStatus(chunkPos, chunkStatus);
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
					worldChunk = new WorldChunk(this.world, (ProtoChunk)chunk, chunkPos.x, chunkPos.z);
				}

				worldChunk.method_12207(() -> ChunkHolder.getLevelType(chunkHolder.getLevel()));
				worldChunk.loadToWorld();
				return worldChunk;
			}, runnable -> this.mainActor.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(chunkHolder, () -> this.genQueueAdder.execute(runnable))));
		}
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> method_17235(ChunkHolder chunkHolder) {
		ChunkPos chunkPos = chunkHolder.getPos();
		CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getChunkRegion(chunkPos, 1, i -> ChunkStatus.FULL);
		return completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
				final WorldChunk worldChunk = (WorldChunk)list.get(list.size() / 2);
				if (!Objects.equals(chunkPos, worldChunk.getPos())) {
					throw new IllegalStateException();
				} else if (!worldChunk.isLoadedToWorld()) {
					return Either.right(new ChunkHolder.Unloaded() {
						public String toString() {
							return "Not isLoaded " + worldChunk.getPos().toString();
						}
					});
				} else {
					worldChunk.runPostProcessing();
					this.totalChunksLoadedCount.getAndIncrement();
					Packet<?>[] packets = new Packet[2];
					this.playersWatchingChunkProvider.getPlayersWatchingChunk(chunkPos, false, true).forEach(serverPlayerEntity -> {
						if (packets[0] == null) {
							packets[0] = new ChunkDataClientPacket(worldChunk, 65535);
							packets[1] = new LightUpdateClientPacket(worldChunk.getPos(), this.serverLightingProvider);
						}

						serverPlayerEntity.sendInitialChunkPackets(chunkPos, packets[0], packets[1]);
					});
					return Either.left(worldChunk);
				}
			}), runnable -> this.mainActor.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(chunkHolder, () -> this.genQueueAdder.execute(runnable))));
	}

	public int getTotalChunksLoadedCount() {
		return this.totalChunksLoadedCount.get();
	}

	private void save(Chunk chunk, boolean bl) {
		try {
			if (bl && chunk instanceof WorldChunk) {
				((WorldChunk)chunk).unloadFromWorld();
			}

			if (!chunk.needsSaving()) {
				return;
			}

			chunk.setLastSaveTime(this.world.getTime());
			this.chunkSaveHandler.saveChunk(this.world, chunk);
			chunk.setShouldSave(false);
		} catch (IOException var4) {
			LOGGER.error("Couldn't save chunk", (Throwable)var4);
		} catch (SessionLockException var5) {
			LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)var5);
		}
	}

	protected void applyViewDistance(int i, int j) {
		for (ChunkHolder chunkHolder : this.posToHolder.values()) {
			ChunkPos chunkPos = chunkHolder.getPos();
			Packet<?>[] packets = new Packet[2];
			this.playersWatchingChunkProvider.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> {
				int k = ServerChunkManager.getWatchDistance(chunkPos, serverPlayerEntity);
				boolean bl = k <= i;
				boolean bl2 = k <= j;
				this.method_17241(serverPlayerEntity, chunkPos, packets, bl, bl2);
			});
		}
	}

	protected void method_17241(ServerPlayerEntity serverPlayerEntity, ChunkPos chunkPos, Packet<?>[] packets, boolean bl, boolean bl2) {
		if (serverPlayerEntity.world == this.world) {
			if (bl2 && !bl) {
				ChunkHolder chunkHolder = this.getCopiedChunkHolder(chunkPos.toLong());
				if (chunkHolder != null) {
					WorldChunk worldChunk = chunkHolder.getWorldChunk();
					if (worldChunk != null) {
						if (packets[0] == null) {
							packets[0] = new ChunkDataClientPacket(worldChunk, 65535);
							packets[1] = new LightUpdateClientPacket(chunkPos, this.serverLightingProvider);
						}

						serverPlayerEntity.sendInitialChunkPackets(chunkPos, packets[0], packets[1]);
					}
				}
			}

			if (!bl2 && bl) {
				serverPlayerEntity.sendRemoveChunkPacket(chunkPos);
			}
		}
	}

	public int getLoadedChunkCount() {
		return this.posToHolderCopy.size();
	}

	protected ChunkHolderManager.TicketManager getTicketManager() {
		return this.ticketManager;
	}

	protected ObjectBidirectionalIterator<Entry<ChunkHolder>> entryIterator() {
		return this.posToHolderCopy.long2ObjectEntrySet().fastIterator();
	}

	public void save(BooleanSupplier booleanSupplier) {
		boolean bl;
		do {
			bl = this.chunkSaveHandler.saveNextChunk();
		} while (bl && booleanSupplier.getAsBoolean());
	}

	class TicketManager extends ChunkTicketManager {
		protected TicketManager(Executor executor, Executor executor2) {
			super(executor, executor2);
		}

		@Override
		protected boolean method_14035(long l) {
			return ChunkHolderManager.this.field_17221.contains(l);
		}

		@Nullable
		@Override
		protected ChunkHolder getChunkHolder(long l) {
			return ChunkHolderManager.this.getChunkHolder(l);
		}

		@Nullable
		@Override
		protected ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j) {
			return ChunkHolderManager.this.setLevel(l, i, chunkHolder, j);
		}
	}
}

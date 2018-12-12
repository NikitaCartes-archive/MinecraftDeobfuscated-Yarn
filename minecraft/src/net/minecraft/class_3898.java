package net.minecraft;

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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerChunkManagerEntry;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
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

public class class_3898 implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Long2ObjectLinkedOpenHashMap<ServerChunkManagerEntry> posToEntryMapSource = new Long2ObjectLinkedOpenHashMap<>();
	private final World world;
	private final ServerLightingProvider serverLightingProvider;
	private final Executor field_17216;
	private final ServerChunkManagerEntry.class_3897 field_17217;
	private final ChunkGenerator<?> chunkGenerator;
	private final Executor field_17219;
	private volatile Long2ObjectLinkedOpenHashMap<ServerChunkManagerEntry> posToEntryMap = this.posToEntryMapSource.clone();
	private final LongSet field_17221 = new LongOpenHashSet();
	private boolean posToEntryMapSourceDirty = false;
	private final class_3900 field_17223;
	private final class_3846<Runnable> field_17224;
	private final class_3899 field_17225;
	private final class_3846<Runnable> field_17226;
	private final ChunkSaveHandler chunkSaveHandler;
	private final class_3898.class_3216 field_17228;
	private int field_17229 = 0;
	private final AtomicInteger field_17230 = new AtomicInteger();

	public class_3898(
		Executor executor,
		ServerChunkManagerEntry.class_3897 arg,
		Executor executor2,
		ChunkSaveHandler chunkSaveHandler,
		ChunkProvider chunkProvider,
		World world,
		ChunkGenerator<?> chunkGenerator
	) {
		this.world = world;
		this.field_17217 = arg;
		this.chunkGenerator = chunkGenerator;
		this.field_17219 = executor;
		this.field_17216 = executor2;
		this.chunkSaveHandler = chunkSaveHandler;
		this.field_17224 = class_3846.method_16902(executor, "worldgen");
		this.field_17226 = class_3846.method_16902(executor2, "main");
		class_3846<Runnable> lv = class_3846.method_16902(executor, "light");
		this.field_17223 = new class_3900(ImmutableList.of(this.field_17224, this.field_17226, lv), executor);
		this.serverLightingProvider = new ServerLightingProvider(chunkProvider, this, this.world.getDimension().hasSkyLight(), executor, this.field_17223, lv);
		this.field_17225 = this.field_17223.method_17281(this.field_17224);
		this.field_17228 = new class_3898.class_3216();
	}

	public ServerLightingProvider getLightProvider() {
		return this.serverLightingProvider;
	}

	@Nullable
	private ServerChunkManagerEntry method_17255(long l) {
		return this.posToEntryMapSource.get(l);
	}

	@Nullable
	public ServerChunkManagerEntry getEntry(long l) {
		return this.posToEntryMap.get(l);
	}

	public int method_17246(long l) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(l);
		return serverChunkManagerEntry == null ? ServerChunkManager.FULL_CHUNK_LEVEL + 2 : serverChunkManagerEntry.method_17208();
	}

	@Environment(EnvType.CLIENT)
	public String method_17218(ChunkPos chunkPos) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(chunkPos.toLong());
		if (serverChunkManagerEntry == null) {
			return "null";
		} else {
			String string = serverChunkManagerEntry.getLevel() + "\n";
			ChunkStatus chunkStatus = serverChunkManagerEntry.method_16141();
			Chunk chunk = serverChunkManagerEntry.method_14010();
			if (chunkStatus != null) {
				string = string + "St: §" + chunkStatus.getIndex() + chunkStatus + '§' + "r\n";
			}

			if (chunk != null) {
				string = string + "Ch: §" + chunk.getStatus().getIndex() + chunk.getStatus() + '§' + "r\n";
			}

			ServerChunkManagerEntry.class_3194 lv = serverChunkManagerEntry.method_13995();
			string = string + "§" + lv.ordinal() + lv;
			return string + '§' + "r";
		}
	}

	private CompletableFuture<Either<List<Chunk>, ServerChunkManagerEntry.Unloaded>> method_17220(ChunkPos chunkPos, int i, IntFunction<ChunkStatus> intFunction) {
		List<CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>> list = Lists.<CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>>newArrayList();
		int j = chunkPos.x;
		int k = chunkPos.z;

		for (int l = -i; l <= i; l++) {
			for (int m = -i; m <= i; m++) {
				int n = Math.max(Math.abs(m), Math.abs(l));
				final ChunkPos chunkPos2 = new ChunkPos(j + m, k + l);
				long o = chunkPos2.toLong();
				ServerChunkManagerEntry serverChunkManagerEntry = this.method_17255(o);
				if (serverChunkManagerEntry == null) {
					return CompletableFuture.completedFuture(Either.right(new ServerChunkManagerEntry.Unloaded() {
						public String toString() {
							return "Unloaded " + chunkPos2.toString();
						}
					}));
				}

				ChunkStatus chunkStatus = (ChunkStatus)intFunction.apply(n);
				CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = serverChunkManagerEntry.method_13993(chunkStatus, this);
				list.add(completableFuture);
			}
		}

		CompletableFuture<List<Either<Chunk, ServerChunkManagerEntry.Unloaded>>> completableFuture2 = SystemUtil.method_652(list);
		return completableFuture2.thenApply(
			listx -> {
				List<Chunk> list2 = Lists.<Chunk>newArrayList();
				int l = 0;

				for (final Either<Chunk, ServerChunkManagerEntry.Unloaded> either : listx) {
					Optional<Chunk> optional = either.left();
					if (!optional.isPresent()) {
						final int mx = l;
						return Either.right(
							new ServerChunkManagerEntry.Unloaded() {
								public String toString() {
									return "Unloaded "
										+ new ChunkPos(j + mx % (i * 2 + 1), k + mx / (i * 2 + 1))
										+ " "
										+ ((ServerChunkManagerEntry.Unloaded)either.right().get()).toString();
								}
							}
						);
					}

					list2.add(optional.get());
					l++;
				}

				return Either.left(list2);
			}
		);
	}

	public CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> method_17247(ChunkPos chunkPos) {
		return this.method_17220(chunkPos, 2, i -> ChunkStatus.FULL).thenApply(either -> either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2)));
	}

	@Nullable
	private ServerChunkManagerEntry method_17217(long l, int i, @Nullable ServerChunkManagerEntry serverChunkManagerEntry, int j) {
		if (j > ServerChunkManager.FULL_CHUNK_LEVEL && i > ServerChunkManager.FULL_CHUNK_LEVEL) {
			return serverChunkManagerEntry;
		} else {
			if (serverChunkManagerEntry != null) {
				serverChunkManagerEntry.setLevel(i);
			}

			if (serverChunkManagerEntry != null) {
				if (i > ServerChunkManager.FULL_CHUNK_LEVEL) {
					this.field_17221.add(l);
				} else {
					this.field_17221.remove(l);
				}
			}

			if (i <= ServerChunkManager.FULL_CHUNK_LEVEL && serverChunkManagerEntry == null) {
				serverChunkManagerEntry = new ServerChunkManagerEntry(new ChunkPos(l), i, this.serverLightingProvider, this.field_17223, this.field_17217);
				this.posToEntryMapSource.put(l, serverChunkManagerEntry);
				this.posToEntryMapSourceDirty = true;
			}

			return serverChunkManagerEntry;
		}
	}

	public void close() {
		this.field_17223.close();
		this.posToEntryMap.values().forEach(serverChunkManagerEntry -> {
			Chunk chunk = serverChunkManagerEntry.method_14010();
			if (chunk != null) {
				this.save(chunk, false);
			}
		});
		this.chunkSaveHandler.close();
	}

	public void save(boolean bl) {
		for (ServerChunkManagerEntry serverChunkManagerEntry : this.posToEntryMap.values()) {
			WorldChunk worldChunk = serverChunkManagerEntry.getChunk();
			if (worldChunk != null) {
				this.save(worldChunk, false);
			}
		}

		if (bl) {
			this.chunkSaveHandler.saveAllChunks();
		}
	}

	public void method_17233(BooleanSupplier booleanSupplier) {
		if (!this.world.isSavingDisabled()) {
			LongIterator longIterator = this.field_17221.iterator();

			for (int i = 0; longIterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.field_17221.size() > 2000); longIterator.remove()) {
				long l = longIterator.nextLong();
				ServerChunkManagerEntry serverChunkManagerEntry = this.posToEntryMapSource.remove(l);
				if (serverChunkManagerEntry != null) {
					this.posToEntryMapSourceDirty = true;
					i++;
					CompletableFuture<Chunk> completableFuture = serverChunkManagerEntry.getChunkFuture();
					completableFuture.thenAcceptAsync(chunk -> {
						if (chunk != null) {
							this.save(chunk, true);

							for (int ix = 0; ix < 16; ix++) {
								this.serverLightingProvider.scheduleChunkLightUpdate(chunk.getPos().x, ix, chunk.getPos().z, true);
							}

							this.serverLightingProvider.method_17303();
						}
					}, this.field_17216);
				}
			}
		}
	}

	public void syncPosToEntryMap() {
		if (this.posToEntryMapSourceDirty) {
			this.posToEntryMap = this.posToEntryMapSource.clone();
			this.posToEntryMapSourceDirty = false;
		}
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> method_17236(
		ServerChunkManagerEntry serverChunkManagerEntry, ChunkStatus chunkStatus
	) {
		ChunkPos chunkPos = serverChunkManagerEntry.getPos();
		if (chunkStatus == ChunkStatus.EMPTY) {
			return CompletableFuture.supplyAsync(() -> {
				Chunk chunk = this.chunkSaveHandler.readChunk(this.world, chunkPos.x, chunkPos.z);
				if (chunk != null) {
					chunk.setLastSaveTime(this.world.getTime());
					return Either.left(chunk);
				} else {
					return Either.left(new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA));
				}
			}, this.field_17216);
		} else {
			CompletableFuture<Either<List<Chunk>, ServerChunkManagerEntry.Unloaded>> completableFuture = CompletableFuture.supplyAsync(
					() -> null, runnable -> this.field_17223.method_17284(this.field_17226, serverChunkManagerEntry, () -> this.field_17216.execute(runnable))
				)
				.thenCompose(object -> this.method_17220(chunkPos, chunkStatus.method_12152(), i -> this.method_17229(chunkStatus, i)));
			return completableFuture.thenComposeAsync(
				either -> (CompletableFuture)either.map(list -> {
						try {
							return chunkStatus.method_12154(this.world, this.chunkGenerator, this.serverLightingProvider, this::method_17226, list).thenApply(Either::left);
						} catch (Exception var8) {
							CrashReport crashReport = CrashReport.create(var8, "Exception generating new chunk");
							CrashReportSection crashReportSection = crashReport.method_562("Chunk to be generated");
							crashReportSection.add("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
							crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
							crashReportSection.add("Generator", this.chunkGenerator);
							throw new CrashException(crashReport);
						}
					}, unloaded -> CompletableFuture.completedFuture(Either.right(unloaded))),
				runnable -> this.field_17223.method_17284(this.field_17224, serverChunkManagerEntry, runnable)
			);
		}
	}

	private ChunkStatus method_17229(ChunkStatus chunkStatus, int i) {
		ChunkStatus chunkStatus2;
		if (i == 0) {
			chunkStatus2 = chunkStatus.getPrevious();
		} else {
			chunkStatus2 = ChunkStatus.getByIndex(ChunkStatus.getIndex(chunkStatus) + i);
		}

		return chunkStatus2;
	}

	public CompletableFuture<Chunk> method_17226(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(chunkPos.toLong());
		if (serverChunkManagerEntry == null) {
			throw new IllegalStateException("No chunk holder while trying to convert to full chunk: " + chunkPos);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				WorldChunk worldChunk;
				if (chunk instanceof ReadOnlyChunk) {
					worldChunk = ((ReadOnlyChunk)chunk).method_12240();
				} else {
					worldChunk = new WorldChunk(this.world, (ProtoChunk)chunk, chunkPos.x, chunkPos.z);
				}

				worldChunk.method_12207(() -> ServerChunkManagerEntry.method_14008(serverChunkManagerEntry.getLevel()));
				worldChunk.loadToWorld();
				return worldChunk;
			}, runnable -> this.field_17223.method_17284(this.field_17226, serverChunkManagerEntry, () -> this.field_17216.execute(runnable)));
		}
	}

	public CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> method_17235(ServerChunkManagerEntry serverChunkManagerEntry) {
		ChunkPos chunkPos = serverChunkManagerEntry.getPos();
		CompletableFuture<Either<List<Chunk>, ServerChunkManagerEntry.Unloaded>> completableFuture = this.method_17220(chunkPos, 1, i -> ChunkStatus.FULL);
		return completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
				final WorldChunk worldChunk = (WorldChunk)list.get(list.size() / 2);
				if (!Objects.equals(chunkPos, worldChunk.getPos())) {
					throw new IllegalStateException();
				} else if (!worldChunk.isLoadedToWorld()) {
					return Either.right(new ServerChunkManagerEntry.Unloaded() {
						public String toString() {
							return "Not isLoaded " + worldChunk.getPos().toString();
						}
					});
				} else {
					worldChunk.runPostProcessing();
					this.field_17230.getAndIncrement();
					Packet<?>[] packets = new Packet[2];
					this.field_17217.method_17211(chunkPos, false, true).forEach(serverPlayerEntity -> {
						if (packets[0] == null) {
							packets[0] = new ChunkDataClientPacket(worldChunk, 65535);
							packets[1] = new LightUpdateClientPacket(worldChunk.getPos(), this.serverLightingProvider);
						}

						serverPlayerEntity.sendInitialChunkPackets(chunkPos, packets[0], packets[1]);
					});
					return Either.left(worldChunk);
				}
			}), runnable -> this.field_17223.method_17284(this.field_17226, serverChunkManagerEntry, () -> this.field_17216.execute(runnable)));
	}

	public int method_17253() {
		return this.field_17230.get();
	}

	public void method_17213(int i) {
		this.field_17229 = i;
		this.field_17230.set(0);
		this.field_17225.method_17276();
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

	public void method_17214(int i, int j) {
		for (ServerChunkManagerEntry serverChunkManagerEntry : this.posToEntryMapSource.values()) {
			ChunkPos chunkPos = serverChunkManagerEntry.getPos();
			Packet<?>[] packets = new Packet[2];
			this.field_17217.method_17210(chunkPos, false).forEach(serverPlayerEntity -> {
				int k = ServerChunkManager.getWatchDistance(chunkPos, serverPlayerEntity);
				boolean bl = k <= i;
				boolean bl2 = k <= j;
				this.method_17241(serverPlayerEntity, chunkPos, packets, bl, bl2);
			});
		}
	}

	public void method_17241(ServerPlayerEntity serverPlayerEntity, ChunkPos chunkPos, Packet<?>[] packets, boolean bl, boolean bl2) {
		if (serverPlayerEntity.world == this.world) {
			if (bl2 && !bl) {
				ServerChunkManagerEntry serverChunkManagerEntry = this.getEntry(chunkPos.toLong());
				if (serverChunkManagerEntry != null) {
					WorldChunk worldChunk = serverChunkManagerEntry.getChunk();
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

	public int method_17260() {
		return this.posToEntryMap.size();
	}

	public class_3898.class_3216 getTicketManager() {
		return this.field_17228;
	}

	public ObjectBidirectionalIterator<Entry<ServerChunkManagerEntry>> method_17264() {
		return this.posToEntryMap.long2ObjectEntrySet().fastIterator();
	}

	public void method_17250(BooleanSupplier booleanSupplier) {
		boolean bl;
		do {
			bl = this.chunkSaveHandler.saveNextChunk();
		} while (bl && booleanSupplier.getAsBoolean());
	}

	public int method_17265() {
		return this.field_17225.method_17278();
	}

	public int method_17266() {
		return this.field_17225.method_17279();
	}

	public String method_17267() {
		int i = this.method_17265();
		int j = this.method_17266();
		int k = this.method_17268();
		int l = this.method_17253();
		return String.format(
			"%5d | %5d | %5d | %5d (%3d%% %3d%%)", i, i - j, k, l, (int)((float)j * 100.0F / (float)i), (int)((float)l * 100.0F / (float)this.field_17229)
		);
	}

	private int method_17268() {
		if (this.field_17219 instanceof ForkJoinPool) {
			ForkJoinPool forkJoinPool = (ForkJoinPool)this.field_17219;
			return forkJoinPool.getQueuedSubmissionCount() + (int)forkJoinPool.getQueuedTaskCount();
		} else {
			return 0;
		}
	}

	class class_3216 extends ChunkTicketManager {
		private class_3216() {
		}

		@Override
		protected boolean method_14035(long l) {
			return class_3898.this.field_17221.contains(l);
		}

		@Nullable
		@Override
		protected ServerChunkManagerEntry method_14038(long l) {
			return class_3898.this.method_17255(l);
		}

		@Nullable
		@Override
		protected ServerChunkManagerEntry method_14053(long l, int i, @Nullable ServerChunkManagerEntry serverChunkManagerEntry, int j) {
			return class_3898.this.method_17217(l, i, serverChunkManagerEntry, j);
		}
	}
}

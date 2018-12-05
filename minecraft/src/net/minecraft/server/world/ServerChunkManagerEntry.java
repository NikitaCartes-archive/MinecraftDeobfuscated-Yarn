package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ServerChunkManagerEntry {
	public static final Either<Chunk, ServerChunkManagerEntry.Unloaded> UNLOADED_CHUNK = Either.right(ServerChunkManagerEntry.Unloaded.INSTANCE);
	public static final CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(
		UNLOADED_CHUNK
	);
	public static final Either<WorldChunk, ServerChunkManagerEntry.Unloaded> UNLOADED_WORLD_CHUNK = Either.right(ServerChunkManagerEntry.Unloaded.INSTANCE);
	private static final CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(
		UNLOADED_WORLD_CHUNK
	);
	private static final List<ChunkStatus> chunkStatuses = ChunkStatus.createOrderedList();
	private static final ServerChunkManagerEntry.class_3194[] field_13873 = ServerChunkManagerEntry.class_3194.values();
	private final AtomicReferenceArray<CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>> field_16425 = new AtomicReferenceArray(
		chunkStatuses.size()
	);
	@Nullable
	private volatile CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> field_16431;
	@Nullable
	private volatile CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> field_13865;
	private CompletableFuture<Chunk> chunk = CompletableFuture.completedFuture(null);
	private int field_16432;
	private int field_13862;
	private final PlayerChunkWatchingManager players;
	private final ChunkPos pos;
	private final short[] blockUpdatePositions = new short[64];
	private int blockUpdateCount;
	private int field_13872;
	private int field_16209;
	private int field_13871;
	private int field_13870;
	private final LightingProvider lightingProvider;

	public ServerChunkManagerEntry(ChunkPos chunkPos, int i, LightingProvider lightingProvider, PlayerChunkWatchingManager playerChunkWatchingManager) {
		this.pos = chunkPos;
		this.lightingProvider = lightingProvider;
		this.players = playerChunkWatchingManager;
		this.field_16432 = ServerChunkManager.field_13922 + 1;
		this.method_15890(i);
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> method_16146(ChunkStatus chunkStatus) {
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>)this.field_16425
			.get(chunkStatus.getOrderId());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> method_13997(ChunkStatus chunkStatus) {
		return method_14011(this.field_13862).isAfter(chunkStatus) ? this.method_16146(chunkStatus) : UNLOADED_CHUNK_FUTURE;
	}

	public CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> method_16145() {
		CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.field_16431;
		return completableFuture == null ? UNLOADED_WORLD_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> method_14003() {
		CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.field_13865;
		return completableFuture == null ? UNLOADED_WORLD_CHUNK_FUTURE : completableFuture;
	}

	@Nullable
	public WorldChunk getChunk() {
		CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.method_16145();
		Either<WorldChunk, ServerChunkManagerEntry.Unloaded> either = (Either<WorldChunk, ServerChunkManagerEntry.Unloaded>)completableFuture.getNow(null);
		return either == null ? null : (WorldChunk)either.left().orElse(null);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ChunkStatus method_16141() {
		for (int i = chunkStatuses.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)chunkStatuses.get(i);
			CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.method_16146(chunkStatus);
			if (((Either)completableFuture.getNow(UNLOADED_CHUNK)).left().isPresent()) {
				return chunkStatus;
			}
		}

		return null;
	}

	@Nullable
	public Chunk method_14010() {
		for (int i = chunkStatuses.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)chunkStatuses.get(i);
			CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.method_16146(chunkStatus);
			if (!completableFuture.isCompletedExceptionally()) {
				Optional<Chunk> optional = ((Either)completableFuture.getNow(UNLOADED_CHUNK)).left();
				if (optional.isPresent()) {
					return (Chunk)optional.get();
				}
			}
		}

		return null;
	}

	public CompletableFuture<Chunk> getChunkFuture() {
		return this.chunk;
	}

	public void markForUpdate(int i, int j, int k) {
		WorldChunk worldChunk = this.getChunk();
		if (worldChunk != null) {
			this.field_13872 |= 1 << (j >> 4);
			if (this.blockUpdateCount < 64) {
				short s = (short)(i << 12 | k << 8 | j);

				for (int l = 0; l < this.blockUpdateCount; l++) {
					if (this.blockUpdatePositions[l] == s) {
						return;
					}
				}

				this.blockUpdatePositions[this.blockUpdateCount++] = s;
			}
		}
	}

	public void method_14012(LightType lightType, int i) {
		WorldChunk worldChunk = this.getChunk();
		if (worldChunk != null) {
			if (lightType == LightType.field_9284) {
				this.field_13870 |= 1 << i - -1;
			} else {
				this.field_13871 |= 1 << i - -1;
			}
		}
	}

	public void flushUpdates(WorldChunk worldChunk, int i) {
		if (this.blockUpdateCount != 0 || this.field_13870 != 0 || this.field_13871 != 0) {
			World world = worldChunk.getWorld();
			if (this.blockUpdateCount == 1) {
				int j = (this.blockUpdatePositions[0] >> 12 & 15) + this.pos.x * 16;
				int k = this.blockUpdatePositions[0] & 255;
				int l = (this.blockUpdatePositions[0] >> 8 & 15) + this.pos.z * 16;
				BlockPos blockPos = new BlockPos(j, k, l);
				this.sendPacket(new BlockUpdateClientPacket(world, blockPos), false, i);
				if (world.getBlockState(blockPos).getBlock().hasBlockEntity()) {
					this.sendBlockEntityUpdatePacket(world, blockPos, i);
				}
			} else if (this.blockUpdateCount == 64) {
				this.sendPacket(new ChunkDataClientPacket(worldChunk, this.field_13872), false, i);
				this.field_16209 = this.field_13872 << 1;
			} else if (this.blockUpdateCount != 0) {
				this.sendPacket(new ChunkDeltaUpdateClientPacket(this.blockUpdateCount, this.blockUpdatePositions, worldChunk), false, i);

				for (int j = 0; j < this.blockUpdateCount; j++) {
					int k = (this.blockUpdatePositions[j] >> 12 & 15) + this.pos.x * 16;
					int l = this.blockUpdatePositions[j] & 255;
					int m = (this.blockUpdatePositions[j] >> 8 & 15) + this.pos.z * 16;
					BlockPos blockPos2 = new BlockPos(k, l, m);
					if (world.getBlockState(blockPos2).getBlock().hasBlockEntity()) {
						this.sendBlockEntityUpdatePacket(world, blockPos2, i);
					}
				}
			}

			this.blockUpdateCount = 0;
			this.field_13872 = 0;
			if (this.field_13870 != 0 || this.field_13871 != 0) {
				this.sendPacket(
					new LightUpdateClientPacket(worldChunk.getPos(), this.lightingProvider, this.field_13870 & ~this.field_16209, this.field_13871 & ~this.field_16209),
					true,
					i
				);
				int jx = this.field_13870 & this.field_16209;
				int k = this.field_13871 & this.field_16209;
				if (jx != 0 || k != 0) {
					this.sendPacket(new LightUpdateClientPacket(worldChunk.getPos(), this.lightingProvider, jx, k), false, i);
				}

				this.field_13870 = 0;
				this.field_13871 = 0;
				this.field_16209 = this.field_16209 & ~(this.field_13870 & this.field_13871);
			}
		}
	}

	private void sendBlockEntityUpdatePacket(World world, BlockPos blockPos, int i) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity != null) {
			BlockEntityUpdateClientPacket blockEntityUpdateClientPacket = blockEntity.toUpdatePacket();
			if (blockEntityUpdateClientPacket != null) {
				this.sendPacket(blockEntityUpdateClientPacket, false, i);
			}
		}
	}

	private void sendPacket(Packet<?> packet, boolean bl, int i) {
		this.players.getPlayersWatchingChunk(this.pos.toLong()).forEach(serverPlayerEntity -> {
			int j = ServerChunkManager.getWatchDistance(this.pos, serverPlayerEntity);
			if (j <= i) {
				if (!bl || j >= i) {
					serverPlayerEntity.networkHandler.sendPacket(packet);
				}
			}
		});
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> method_13993(ChunkStatus chunkStatus, ServerChunkManager serverChunkManager) {
		int i = chunkStatus.getOrderId();
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>)this.field_16425
			.get(i);
		if (completableFuture != null) {
			return completableFuture;
		} else if (method_14011(this.field_13862).isAfter(chunkStatus)) {
			CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture2 = serverChunkManager.method_16163(
				this.pos, this.field_13862, chunkStatus
			);
			this.updateChunk(completableFuture2);
			this.field_16425.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return UNLOADED_CHUNK_FUTURE;
		}
	}

	private void updateChunk(CompletableFuture<? extends Either<? extends Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture) {
		this.chunk = this.chunk.thenCombine(completableFuture, (chunk, either) -> either.map(chunkx -> chunkx, unloaded -> chunk));
	}

	@Environment(EnvType.CLIENT)
	public ServerChunkManagerEntry.class_3194 method_13995() {
		return method_14008(this.field_13862);
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	public int method_14005() {
		return this.field_13862;
	}

	public void method_15890(int i) {
		this.field_13862 = i;
	}

	public void method_14007(ServerChunkManager serverChunkManager) {
		ChunkStatus chunkStatus = method_14011(this.field_16432);
		ChunkStatus chunkStatus2 = method_14011(this.field_13862);
		if (this.field_13862 <= ServerChunkManager.field_13922) {
			for (int i = chunkStatus.getOrderId() + 1; i <= chunkStatus2.getOrderId(); i++) {
				this.method_13993((ChunkStatus)chunkStatuses.get(i), serverChunkManager);
			}
		}

		ServerChunkManagerEntry.class_3194 lv = method_14008(this.field_16432);
		ServerChunkManagerEntry.class_3194 lv2 = method_14008(this.field_13862);
		boolean bl = lv.method_14014(ServerChunkManagerEntry.class_3194.field_13875);
		boolean bl2 = lv2.method_14014(ServerChunkManagerEntry.class_3194.field_13875);
		if (!bl && bl2) {
			this.field_16431 = serverChunkManager.method_16175(this.pos);
			this.updateChunk(this.field_16431);
		}

		if (bl && !bl2) {
			this.field_16431 = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl3 = lv.method_14014(ServerChunkManagerEntry.class_3194.field_13877);
		boolean bl4 = lv2.method_14014(ServerChunkManagerEntry.class_3194.field_13877);
		if (!bl3 && bl4) {
			this.field_13865 = serverChunkManager.method_14123(this.pos);
			this.updateChunk(this.field_13865);
		}

		if (bl3 && !bl4) {
			this.field_13865 = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		this.field_16432 = this.field_13862;
	}

	public static ChunkStatus method_14011(int i) {
		return i <= 33 ? ChunkStatus.field_12803 : ChunkStatus.getOrdered(i - 33 - 1);
	}

	public static ServerChunkManagerEntry.class_3194 method_14008(int i) {
		return field_13873[MathHelper.clamp(33 - i, 0, field_13873.length - 1)];
	}

	interface Unloaded {
		ServerChunkManagerEntry.Unloaded INSTANCE = new ServerChunkManagerEntry.Unloaded() {
			public String toString() {
				return "UNLOADED";
			}
		};
	}

	public static enum class_3194 {
		field_13876,
		field_13875,
		field_13877;

		public boolean method_14014(ServerChunkManagerEntry.class_3194 arg) {
			return this.ordinal() >= arg.ordinal();
		}
	}
}

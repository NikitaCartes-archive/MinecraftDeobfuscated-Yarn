package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3898;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
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
	private final AtomicReferenceArray<CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>> statusToChunk = new AtomicReferenceArray(
		chunkStatuses.size()
	);
	@Nullable
	private volatile CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> field_16431;
	@Nullable
	private volatile CompletableFuture<Either<WorldChunk, ServerChunkManagerEntry.Unloaded>> field_13865;
	private CompletableFuture<Chunk> chunk = CompletableFuture.completedFuture(null);
	private int field_16432;
	private int level;
	private int field_17208;
	private final ChunkPos pos;
	private final short[] blockUpdatePositions = new short[64];
	private int blockUpdateCount;
	private int field_13872;
	private int field_16209;
	private int field_13871;
	private int field_13870;
	private final LightingProvider lightingProvider;
	private final ServerChunkManagerEntry.class_3896 field_17209;
	private final ServerChunkManagerEntry.class_3897 field_17210;

	public ServerChunkManagerEntry(
		ChunkPos chunkPos, int i, LightingProvider lightingProvider, ServerChunkManagerEntry.class_3896 arg, ServerChunkManagerEntry.class_3897 arg2
	) {
		this.pos = chunkPos;
		this.lightingProvider = lightingProvider;
		this.field_17209 = arg;
		this.field_17210 = arg2;
		this.field_16432 = ServerChunkManager.FULL_CHUNK_LEVEL + 1;
		this.level = this.field_16432;
		this.field_17208 = this.field_16432;
		this.setLevel(i);
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> getChunkForStatus(ChunkStatus chunkStatus) {
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>)this.statusToChunk
			.get(chunkStatus.getIndex());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> getChunkMinimumStatus(ChunkStatus chunkStatus) {
		return getStatusByLevel(this.level).isAfter(chunkStatus) ? this.getChunkForStatus(chunkStatus) : UNLOADED_CHUNK_FUTURE;
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
			CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.getChunkForStatus(chunkStatus);
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
			CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = this.getChunkForStatus(chunkStatus);
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
			if (lightType == LightType.SKY_LIGHT) {
				this.field_13870 |= 1 << i - -1;
			} else {
				this.field_13871 |= 1 << i - -1;
			}
		}
	}

	public void flushUpdates(WorldChunk worldChunk) {
		if (this.blockUpdateCount != 0 || this.field_13870 != 0 || this.field_13871 != 0) {
			World world = worldChunk.getWorld();
			if (this.blockUpdateCount == 1) {
				int i = (this.blockUpdatePositions[0] >> 12 & 15) + this.pos.x * 16;
				int j = this.blockUpdatePositions[0] & 255;
				int k = (this.blockUpdatePositions[0] >> 8 & 15) + this.pos.z * 16;
				BlockPos blockPos = new BlockPos(i, j, k);
				this.sendPacket(new BlockUpdateClientPacket(world, blockPos), false);
				if (world.getBlockState(blockPos).getBlock().hasBlockEntity()) {
					this.sendBlockEntityUpdatePacket(world, blockPos);
				}
			} else if (this.blockUpdateCount == 64) {
				this.sendPacket(new ChunkDataClientPacket(worldChunk, this.field_13872), false);
				this.field_16209 = this.field_13872 << 1;
			} else if (this.blockUpdateCount != 0) {
				this.sendPacket(new ChunkDeltaUpdateClientPacket(this.blockUpdateCount, this.blockUpdatePositions, worldChunk), false);

				for (int i = 0; i < this.blockUpdateCount; i++) {
					int j = (this.blockUpdatePositions[i] >> 12 & 15) + this.pos.x * 16;
					int k = this.blockUpdatePositions[i] & 255;
					int l = (this.blockUpdatePositions[i] >> 8 & 15) + this.pos.z * 16;
					BlockPos blockPos2 = new BlockPos(j, k, l);
					if (world.getBlockState(blockPos2).getBlock().hasBlockEntity()) {
						this.sendBlockEntityUpdatePacket(world, blockPos2);
					}
				}
			}

			this.blockUpdateCount = 0;
			this.field_13872 = 0;
			if (this.field_13870 != 0 || this.field_13871 != 0) {
				this.sendPacket(
					new LightUpdateClientPacket(worldChunk.getPos(), this.lightingProvider, this.field_13870 & ~this.field_16209, this.field_13871 & ~this.field_16209), true
				);
				int ix = this.field_13870 & this.field_16209;
				int j = this.field_13871 & this.field_16209;
				if (ix != 0 || j != 0) {
					this.sendPacket(new LightUpdateClientPacket(worldChunk.getPos(), this.lightingProvider, ix, j), false);
				}

				this.field_13870 = 0;
				this.field_13871 = 0;
				this.field_16209 = this.field_16209 & ~(this.field_13870 & this.field_13871);
			}
		}
	}

	private void sendBlockEntityUpdatePacket(World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity != null) {
			BlockEntityUpdateClientPacket blockEntityUpdateClientPacket = blockEntity.toUpdatePacket();
			if (blockEntityUpdateClientPacket != null) {
				this.sendPacket(blockEntityUpdateClientPacket, false);
			}
		}
	}

	private void sendPacket(Packet<?> packet, boolean bl) {
		this.field_17210.method_17210(this.pos, bl).forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(packet));
	}

	public CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> method_13993(ChunkStatus chunkStatus, class_3898 arg) {
		int i = chunkStatus.getIndex();
		CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>)this.statusToChunk
			.get(i);
		if (completableFuture != null) {
			Either<Chunk, ServerChunkManagerEntry.Unloaded> either = (Either<Chunk, ServerChunkManagerEntry.Unloaded>)completableFuture.getNow(null);
			if (either == null || either.left().isPresent()) {
				return completableFuture;
			}
		}

		if (getStatusByLevel(this.level).isAfter(chunkStatus)) {
			CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture2 = arg.method_17236(this, chunkStatus);
			this.updateChunk(completableFuture2);
			this.statusToChunk.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
		}
	}

	private void updateChunk(CompletableFuture<? extends Either<? extends Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture) {
		this.chunk = this.chunk.thenCombine(completableFuture, (chunk, either) -> either.map(chunkx -> chunkx, unloaded -> chunk));
	}

	@Environment(EnvType.CLIENT)
	public ServerChunkManagerEntry.class_3194 method_13995() {
		return method_14008(this.level);
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	public int getLevel() {
		return this.level;
	}

	public int method_17208() {
		return this.field_17208;
	}

	private void method_17207(int i) {
		this.field_17208 = i;
	}

	public void setLevel(int i) {
		this.level = i;
	}

	protected void method_14007(class_3898 arg) {
		ChunkStatus chunkStatus = getStatusByLevel(this.field_16432);
		ChunkStatus chunkStatus2 = getStatusByLevel(this.level);
		if (this.level <= ServerChunkManager.FULL_CHUNK_LEVEL) {
			for (int i = chunkStatus.getIndex() + 1; i <= chunkStatus2.getIndex(); i++) {
				this.method_13993((ChunkStatus)chunkStatuses.get(i), arg);
			}
		}

		if (this.field_16432 <= ServerChunkManager.FULL_CHUNK_LEVEL) {
			Either<Chunk, ServerChunkManagerEntry.Unloaded> either = Either.right(new ServerChunkManagerEntry.Unloaded() {
				public String toString() {
					return "Unloaded ticket level " + ServerChunkManagerEntry.this.pos.toString();
				}
			});

			for (int j = chunkStatus2.getIndex() + 1; j <= chunkStatus.getIndex(); j++) {
				CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ServerChunkManagerEntry.Unloaded>>)this.statusToChunk
					.get(j);
				if (completableFuture != null) {
					completableFuture.complete(either);
				} else {
					this.statusToChunk.set(j, CompletableFuture.completedFuture(either));
				}
			}
		}

		ServerChunkManagerEntry.class_3194 lv = method_14008(this.field_16432);
		ServerChunkManagerEntry.class_3194 lv2 = method_14008(this.level);
		boolean bl = lv.method_14014(ServerChunkManagerEntry.class_3194.field_13875);
		boolean bl2 = lv2.method_14014(ServerChunkManagerEntry.class_3194.field_13875);
		if (!bl && bl2) {
			this.field_16431 = arg.method_17235(this);
			this.updateChunk(this.field_16431);
		}

		if (bl && !bl2) {
			this.field_16431 = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl3 = lv.method_14014(ServerChunkManagerEntry.class_3194.field_13877);
		boolean bl4 = lv2.method_14014(ServerChunkManagerEntry.class_3194.field_13877);
		if (!bl3 && bl4) {
			this.field_13865 = arg.method_17247(this.pos);
			this.updateChunk(this.field_13865);
		}

		if (bl3 && !bl4) {
			this.field_13865 = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		this.field_17209.method_17209(this.pos, this::method_17208, this.level, this::method_17207);
		this.field_16432 = this.level;
	}

	public static ChunkStatus getStatusByLevel(int i) {
		return i <= 33 ? ChunkStatus.FULL : ChunkStatus.getByIndex(i - 33 - 1);
	}

	public static ServerChunkManagerEntry.class_3194 method_14008(int i) {
		return field_13873[MathHelper.clamp(33 - i, 0, field_13873.length - 1)];
	}

	public interface Unloaded {
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

	public interface class_3896 {
		void method_17209(ChunkPos chunkPos, IntSupplier intSupplier, int i, IntConsumer intConsumer);
	}

	public interface class_3897 {
		default Stream<ServerPlayerEntity> method_17210(ChunkPos chunkPos, boolean bl) {
			return this.method_17211(chunkPos, bl, false);
		}

		Stream<ServerPlayerEntity> method_17211(ChunkPos chunkPos, boolean bl, boolean bl2);
	}
}

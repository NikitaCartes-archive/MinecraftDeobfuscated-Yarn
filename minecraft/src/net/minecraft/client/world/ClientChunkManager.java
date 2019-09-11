package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4548;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientChunkManager extends ChunkManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final WorldChunk emptyChunk;
	private final LightingProvider lightingProvider;
	private volatile ClientChunkManager.ClientChunkMap chunks;
	private final ClientWorld world;

	public ClientChunkManager(ClientWorld clientWorld, int i) {
		this.world = clientWorld;
		this.emptyChunk = new EmptyChunk(clientWorld, new ChunkPos(0, 0));
		this.lightingProvider = new LightingProvider(this, true, clientWorld.getDimension().hasSkyLight());
		this.chunks = new ClientChunkManager.ClientChunkMap(getChunkMapRadius(i));
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	private static boolean positionEquals(@Nullable WorldChunk worldChunk, int i, int j) {
		if (worldChunk == null) {
			return false;
		} else {
			ChunkPos chunkPos = worldChunk.getPos();
			return chunkPos.x == i && chunkPos.z == j;
		}
	}

	public void unload(int i, int j) {
		if (this.chunks.isInRadius(i, j)) {
			int k = this.chunks.getIndex(i, j);
			WorldChunk worldChunk = this.chunks.getChunk(k);
			if (positionEquals(worldChunk, i, j)) {
				this.chunks.compareAndSet(k, worldChunk, null);
			}
		}
	}

	@Nullable
	public WorldChunk method_2857(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		if (this.chunks.isInRadius(i, j)) {
			WorldChunk worldChunk = this.chunks.getChunk(this.chunks.getIndex(i, j));
			if (positionEquals(worldChunk, i, j)) {
				return worldChunk;
			}
		}

		return bl ? this.emptyChunk : null;
	}

	@Override
	public BlockView getWorld() {
		return this.world;
	}

	@Nullable
	public WorldChunk loadChunkFromPacket(World world, int i, int j, @Nullable class_4548 arg, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int k) {
		if (!this.chunks.isInRadius(i, j)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", i, j);
			return null;
		} else {
			int l = this.chunks.getIndex(i, j);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(l);
			if (!positionEquals(worldChunk, i, j)) {
				if (arg == null) {
					LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", i, j);
					return null;
				}

				worldChunk = new WorldChunk(world, new ChunkPos(i, j), arg);
				worldChunk.loadFromPacket(arg, packetByteBuf, compoundTag, k);
				this.chunks.set(l, worldChunk);
			} else {
				worldChunk.loadFromPacket(arg, packetByteBuf, compoundTag, k);
			}

			ChunkSection[] chunkSections = worldChunk.getSectionArray();
			LightingProvider lightingProvider = this.getLightingProvider();
			lightingProvider.setLightEnabled(new ChunkPos(i, j), true);

			for (int m = 0; m < chunkSections.length; m++) {
				ChunkSection chunkSection = chunkSections[m];
				lightingProvider.updateSectionStatus(ChunkSectionPos.from(i, m, j), ChunkSection.isEmpty(chunkSection));
			}

			return worldChunk;
		}
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
	}

	public void setChunkMapCenter(int i, int j) {
		this.chunks.centerChunkX = i;
		this.chunks.centerChunkZ = j;
	}

	public void updateLoadDistance(int i) {
		int j = this.chunks.radius;
		int k = getChunkMapRadius(i);
		if (j != k) {
			ClientChunkManager.ClientChunkMap clientChunkMap = new ClientChunkManager.ClientChunkMap(k);
			clientChunkMap.centerChunkX = this.chunks.centerChunkX;
			clientChunkMap.centerChunkZ = this.chunks.centerChunkZ;

			for (int l = 0; l < this.chunks.chunks.length(); l++) {
				WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(l);
				if (worldChunk != null) {
					ChunkPos chunkPos = worldChunk.getPos();
					if (clientChunkMap.isInRadius(chunkPos.x, chunkPos.z)) {
						clientChunkMap.set(clientChunkMap.getIndex(chunkPos.x, chunkPos.z), worldChunk);
					}
				}
			}

			this.chunks = clientChunkMap;
		}
	}

	private static int getChunkMapRadius(int i) {
		return Math.max(2, i) + 3;
	}

	@Override
	public String getDebugString() {
		return "Client Chunk Cache: " + this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
	}

	public int getLoadedChunkCount() {
		return this.chunks.loadedChunkCount;
	}

	@Override
	public void onLightUpdate(LightType lightType, ChunkSectionPos chunkSectionPos) {
		MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionY(), chunkSectionPos.getSectionZ());
	}

	@Override
	public boolean shouldTickBlock(BlockPos blockPos) {
		return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	@Override
	public boolean shouldTickChunk(ChunkPos chunkPos) {
		return this.isChunkLoaded(chunkPos.x, chunkPos.z);
	}

	@Override
	public boolean shouldTickEntity(Entity entity) {
		return this.isChunkLoaded(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4);
	}

	@Environment(EnvType.CLIENT)
	final class ClientChunkMap {
		private final AtomicReferenceArray<WorldChunk> chunks;
		private final int radius;
		private final int diameter;
		private volatile int centerChunkX;
		private volatile int centerChunkZ;
		private int loadedChunkCount;

		private ClientChunkMap(int i) {
			this.radius = i;
			this.diameter = i * 2 + 1;
			this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
		}

		private int getIndex(int i, int j) {
			return Math.floorMod(j, this.diameter) * this.diameter + Math.floorMod(i, this.diameter);
		}

		protected void set(int i, @Nullable WorldChunk worldChunk) {
			WorldChunk worldChunk2 = (WorldChunk)this.chunks.getAndSet(i, worldChunk);
			if (worldChunk2 != null) {
				this.loadedChunkCount--;
				ClientChunkManager.this.world.unloadBlockEntities(worldChunk2);
			}

			if (worldChunk != null) {
				this.loadedChunkCount++;
			}
		}

		protected WorldChunk compareAndSet(int i, WorldChunk worldChunk, @Nullable WorldChunk worldChunk2) {
			if (this.chunks.compareAndSet(i, worldChunk, worldChunk2) && worldChunk2 == null) {
				this.loadedChunkCount--;
			}

			ClientChunkManager.this.world.unloadBlockEntities(worldChunk);
			return worldChunk;
		}

		private boolean isInRadius(int i, int j) {
			return Math.abs(i - this.centerChunkX) <= this.radius && Math.abs(j - this.centerChunkZ) <= this.radius;
		}

		@Nullable
		protected WorldChunk getChunk(int i) {
			return (WorldChunk)this.chunks.get(i);
		}
	}
}

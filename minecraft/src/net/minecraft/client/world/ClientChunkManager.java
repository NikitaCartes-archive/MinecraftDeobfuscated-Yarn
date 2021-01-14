package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.source.BiomeArray;
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

	public ClientChunkManager(ClientWorld world, int loadDistance) {
		this.world = world;
		this.emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0));
		this.lightingProvider = new LightingProvider(this, true, world.getDimension().hasSkyLight());
		this.chunks = new ClientChunkManager.ClientChunkMap(getChunkMapRadius(loadDistance));
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	private static boolean positionEquals(@Nullable WorldChunk chunk, int x, int z) {
		if (chunk == null) {
			return false;
		} else {
			ChunkPos chunkPos = chunk.getPos();
			return chunkPos.x == x && chunkPos.z == z;
		}
	}

	public void unload(int chunkX, int chunkZ) {
		if (this.chunks.isInRadius(chunkX, chunkZ)) {
			int i = this.chunks.getIndex(chunkX, chunkZ);
			WorldChunk worldChunk = this.chunks.getChunk(i);
			if (positionEquals(worldChunk, chunkX, chunkZ)) {
				this.chunks.compareAndSet(i, worldChunk, null);
			}
		}
	}

	@Nullable
	public WorldChunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
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
	public WorldChunk loadChunkFromPacket(
		int x, int z, @Nullable BiomeArray biomes, PacketByteBuf buf, NbtCompound tag, int verticalStripBitmask, boolean complete
	) {
		if (!this.chunks.isInRadius(x, z)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", x, z);
			return null;
		} else {
			int i = this.chunks.getIndex(x, z);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(i);
			if (!complete && positionEquals(worldChunk, x, z)) {
				worldChunk.loadFromPacket(biomes, buf, tag, verticalStripBitmask);
			} else {
				if (biomes == null) {
					LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", x, z);
					return null;
				}

				worldChunk = new WorldChunk(this.world, new ChunkPos(x, z), biomes);
				worldChunk.loadFromPacket(biomes, buf, tag, verticalStripBitmask);
				this.chunks.set(i, worldChunk);
			}

			ChunkSection[] chunkSections = worldChunk.getSectionArray();
			LightingProvider lightingProvider = this.getLightingProvider();
			lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);

			for (int j = 0; j < chunkSections.length; j++) {
				ChunkSection chunkSection = chunkSections[j];
				lightingProvider.setSectionStatus(ChunkSectionPos.from(x, j, z), ChunkSection.isEmpty(chunkSection));
			}

			this.world.resetChunkColor(x, z);
			return worldChunk;
		}
	}

	public void tick(BooleanSupplier shouldKeepTicking) {
	}

	public void setChunkMapCenter(int x, int z) {
		this.chunks.centerChunkX = x;
		this.chunks.centerChunkZ = z;
	}

	public void updateLoadDistance(int loadDistance) {
		int i = this.chunks.radius;
		int j = getChunkMapRadius(loadDistance);
		if (i != j) {
			ClientChunkManager.ClientChunkMap clientChunkMap = new ClientChunkManager.ClientChunkMap(j);
			clientChunkMap.centerChunkX = this.chunks.centerChunkX;
			clientChunkMap.centerChunkZ = this.chunks.centerChunkZ;

			for (int k = 0; k < this.chunks.chunks.length(); k++) {
				WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(k);
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

	private static int getChunkMapRadius(int loadDistance) {
		return Math.max(2, loadDistance) + 3;
	}

	@Override
	public String getDebugString() {
		return "Client Chunk Cache: " + this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
	}

	public int getLoadedChunkCount() {
		return this.chunks.loadedChunkCount;
	}

	@Override
	public void onLightUpdate(LightType type, ChunkSectionPos pos) {
		MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
	}

	@Override
	public boolean shouldTickBlock(BlockPos pos) {
		return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
	}

	@Override
	public boolean shouldTickChunk(ChunkPos pos) {
		return this.isChunkLoaded(pos.x, pos.z);
	}

	@Override
	public boolean shouldTickEntity(Entity entity) {
		return this.isChunkLoaded(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4);
	}

	@Environment(EnvType.CLIENT)
	final class ClientChunkMap {
		private final AtomicReferenceArray<WorldChunk> chunks;
		private final int radius;
		private final int diameter;
		private volatile int centerChunkX;
		private volatile int centerChunkZ;
		private int loadedChunkCount;

		private ClientChunkMap(int loadDistance) {
			this.radius = loadDistance;
			this.diameter = loadDistance * 2 + 1;
			this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
		}

		private int getIndex(int chunkX, int chunkZ) {
			return Math.floorMod(chunkZ, this.diameter) * this.diameter + Math.floorMod(chunkX, this.diameter);
		}

		protected void set(int index, @Nullable WorldChunk chunk) {
			WorldChunk worldChunk = (WorldChunk)this.chunks.getAndSet(index, chunk);
			if (worldChunk != null) {
				this.loadedChunkCount--;
				ClientChunkManager.this.world.unloadBlockEntities(worldChunk);
			}

			if (chunk != null) {
				this.loadedChunkCount++;
			}
		}

		protected WorldChunk compareAndSet(int index, WorldChunk expect, @Nullable WorldChunk update) {
			if (this.chunks.compareAndSet(index, expect, update) && update == null) {
				this.loadedChunkCount--;
			}

			ClientChunkManager.this.world.unloadBlockEntities(expect);
			return expect;
		}

		private boolean isInRadius(int chunkX, int chunkZ) {
			return Math.abs(chunkX - this.centerChunkX) <= this.radius && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
		}

		@Nullable
		protected WorldChunk getChunk(int index) {
			return (WorldChunk)this.chunks.get(index);
		}
	}
}

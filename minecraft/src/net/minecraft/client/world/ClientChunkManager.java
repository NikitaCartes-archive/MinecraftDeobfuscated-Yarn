package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
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
		this.chunks = new ClientChunkManager.ClientChunkMap(method_20230(i));
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	private static boolean method_20181(@Nullable WorldChunk worldChunk, int i, int j) {
		if (worldChunk == null) {
			return false;
		} else {
			ChunkPos chunkPos = worldChunk.getPos();
			return chunkPos.x == i && chunkPos.z == j;
		}
	}

	public void unload(int chunkX, int chunkZ) {
		if (this.chunks.hasChunk(chunkX, chunkZ)) {
			int i = this.chunks.index(chunkX, chunkZ);
			WorldChunk worldChunk = this.chunks.getChunk(i);
			if (method_20181(worldChunk, chunkX, chunkZ)) {
				this.chunks.method_20183(i, worldChunk, null);
			}
		}
	}

	@Nullable
	public WorldChunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		if (this.chunks.hasChunk(i, j)) {
			WorldChunk worldChunk = this.chunks.getChunk(this.chunks.index(i, j));
			if (method_20181(worldChunk, i, j)) {
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
	public WorldChunk loadChunkFromPacket(World world, int chunkX, int chunkZ, PacketByteBuf data, CompoundTag nbt, int updatedSectionsBits, boolean clearOld) {
		if (!this.chunks.hasChunk(chunkX, chunkZ)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ);
			return null;
		} else {
			int i = this.chunks.index(chunkX, chunkZ);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(i);
			if (!method_20181(worldChunk, chunkX, chunkZ)) {
				if (!clearOld) {
					LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", chunkX, chunkZ);
					return null;
				}

				worldChunk = new WorldChunk(world, new ChunkPos(chunkX, chunkZ), new Biome[256]);
				worldChunk.loadFromPacket(data, nbt, updatedSectionsBits, clearOld);
				this.chunks.unload(i, worldChunk);
			} else {
				worldChunk.loadFromPacket(data, nbt, updatedSectionsBits, clearOld);
			}

			ChunkSection[] chunkSections = worldChunk.getSectionArray();
			LightingProvider lightingProvider = this.getLightingProvider();
			lightingProvider.setLightEnabled(new ChunkPos(chunkX, chunkZ), true);

			for (int j = 0; j < chunkSections.length; j++) {
				ChunkSection chunkSection = chunkSections[j];
				lightingProvider.updateSectionStatus(ChunkSectionPos.from(chunkX, j, chunkZ), ChunkSection.isEmpty(chunkSection));
			}

			return worldChunk;
		}
	}

	@Override
	public void tick(BooleanSupplier shouldKeepTicking) {
	}

	public void setChunkMapCenter(int x, int z) {
		this.chunks.centerChunkX = x;
		this.chunks.centerChunkZ = z;
	}

	public void updateLoadDistance(int loadDistance) {
		int i = this.chunks.loadDistance;
		int j = method_20230(loadDistance);
		if (i != j) {
			ClientChunkManager.ClientChunkMap clientChunkMap = new ClientChunkManager.ClientChunkMap(j);
			clientChunkMap.centerChunkX = this.chunks.centerChunkX;
			clientChunkMap.centerChunkZ = this.chunks.centerChunkZ;

			for (int k = 0; k < this.chunks.chunks.length(); k++) {
				WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(k);
				if (worldChunk != null) {
					ChunkPos chunkPos = worldChunk.getPos();
					if (clientChunkMap.hasChunk(chunkPos.x, chunkPos.z)) {
						clientChunkMap.unload(clientChunkMap.index(chunkPos.x, chunkPos.z), worldChunk);
					}
				}
			}

			this.chunks = clientChunkMap;
		}
	}

	private static int method_20230(int i) {
		return Math.max(2, i) + 3;
	}

	@Override
	public String getDebugString() {
		return "Client Chunk Cache: " + this.chunks.chunks.length() + ", " + this.method_20182();
	}

	@Override
	public ChunkGenerator<?> getChunkGenerator() {
		return null;
	}

	public int method_20182() {
		return this.chunks.field_19143;
	}

	@Override
	public void onLightUpdate(LightType type, ChunkSectionPos chunkSectionPos) {
		MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionY(), chunkSectionPos.getSectionZ());
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
		return this.isChunkLoaded(MathHelper.floor(entity.x) >> 4, MathHelper.floor(entity.z) >> 4);
	}

	@Environment(EnvType.CLIENT)
	final class ClientChunkMap {
		private final AtomicReferenceArray<WorldChunk> chunks;
		private final int loadDistance;
		private final int loadDiameter;
		private volatile int centerChunkX;
		private volatile int centerChunkZ;
		private int field_19143;

		private ClientChunkMap(int loadDistance) {
			this.loadDistance = loadDistance;
			this.loadDiameter = loadDistance * 2 + 1;
			this.chunks = new AtomicReferenceArray(this.loadDiameter * this.loadDiameter);
		}

		private int index(int chunkX, int chunkZ) {
			return Math.floorMod(chunkZ, this.loadDiameter) * this.loadDiameter + Math.floorMod(chunkX, this.loadDiameter);
		}

		protected void unload(int chunkX, @Nullable WorldChunk worldChunk) {
			WorldChunk worldChunk2 = (WorldChunk)this.chunks.getAndSet(chunkX, worldChunk);
			if (worldChunk2 != null) {
				this.field_19143--;
				ClientChunkManager.this.world.unloadBlockEntities(worldChunk2);
			}

			if (worldChunk != null) {
				this.field_19143++;
			}
		}

		protected WorldChunk method_20183(int i, WorldChunk worldChunk, @Nullable WorldChunk worldChunk2) {
			if (this.chunks.compareAndSet(i, worldChunk, worldChunk2) && worldChunk2 == null) {
				this.field_19143--;
			}

			ClientChunkManager.this.world.unloadBlockEntities(worldChunk);
			return worldChunk;
		}

		private boolean hasChunk(int chunkX, int chunkZ) {
			return Math.abs(chunkX - this.centerChunkX) <= this.loadDistance && Math.abs(chunkZ - this.centerChunkZ) <= this.loadDistance;
		}

		@Nullable
		protected WorldChunk getChunk(int chunkX) {
			return (WorldChunk)this.chunks.get(chunkX);
		}
	}
}

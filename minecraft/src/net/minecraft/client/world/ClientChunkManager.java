package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
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

	public void unload(int i, int j) {
		if (this.chunks.hasChunk(i, j)) {
			int k = this.chunks.index(i, j);
			WorldChunk worldChunk = this.chunks.getChunk(k);
			if (method_20181(worldChunk, i, j)) {
				this.chunks.method_20183(k, worldChunk, null);
			}
		}
	}

	@Nullable
	public WorldChunk method_2857(int i, int j, ChunkStatus chunkStatus, boolean bl) {
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
	public WorldChunk loadChunkFromPacket(World world, int i, int j, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int k, boolean bl) {
		if (!this.chunks.hasChunk(i, j)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", i, j);
			return null;
		} else {
			int l = this.chunks.index(i, j);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(l);
			if (!method_20181(worldChunk, i, j)) {
				if (!bl) {
					LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", i, j);
					return null;
				}

				worldChunk = new WorldChunk(world, new ChunkPos(i, j), new Biome[256]);
				worldChunk.loadFromPacket(packetByteBuf, compoundTag, k, bl);
				this.chunks.unload(l, worldChunk);
			} else {
				worldChunk.loadFromPacket(packetByteBuf, compoundTag, k, bl);
			}

			ChunkSection[] chunkSections = worldChunk.getSectionArray();
			LightingProvider lightingProvider = this.getLightingProvider();
			lightingProvider.suppressLight(new ChunkPos(i, j), true);

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
		int j = this.chunks.loadDistance;
		int k = method_20230(i);
		if (j != k) {
			ClientChunkManager.ClientChunkMap clientChunkMap = new ClientChunkManager.ClientChunkMap(k);
			clientChunkMap.centerChunkX = this.chunks.centerChunkX;
			clientChunkMap.centerChunkZ = this.chunks.centerChunkZ;

			for (int l = 0; l < this.chunks.chunks.length(); l++) {
				WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(l);
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
	public String getStatus() {
		return "MultiplayerChunkCache: " + this.chunks.chunks.length() + ", " + this.method_20182();
	}

	@Override
	public ChunkGenerator<?> getChunkGenerator() {
		return null;
	}

	public int method_20182() {
		return this.chunks.field_19143;
	}

	@Override
	public void onLightUpdate(LightType lightType, ChunkSectionPos chunkSectionPos) {
		MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkY(), chunkSectionPos.getChunkZ());
	}

	@Environment(EnvType.CLIENT)
	final class ClientChunkMap {
		private final AtomicReferenceArray<WorldChunk> chunks;
		private final int loadDistance;
		private final int loadDiameter;
		private volatile int centerChunkX;
		private volatile int centerChunkZ;
		private int field_19143;

		private ClientChunkMap(int i) {
			this.loadDistance = i;
			this.loadDiameter = i * 2 + 1;
			this.chunks = new AtomicReferenceArray(this.loadDiameter * this.loadDiameter);
		}

		private int index(int i, int j) {
			return Math.floorMod(j, this.loadDiameter) * this.loadDiameter + Math.floorMod(i, this.loadDiameter);
		}

		protected void unload(int i, @Nullable WorldChunk worldChunk) {
			WorldChunk worldChunk2 = (WorldChunk)this.chunks.getAndSet(i, worldChunk);
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

		private boolean hasChunk(int i, int j) {
			return Math.abs(i - this.centerChunkX) <= this.loadDistance && Math.abs(j - this.centerChunkZ) <= this.loadDistance;
		}

		@Nullable
		protected WorldChunk getChunk(int i) {
			return (WorldChunk)this.chunks.get(i);
		}
	}
}

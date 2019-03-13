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
import net.minecraft.util.math.MathHelper;
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
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final WorldChunk emptyChunk;
	private final LightingProvider lightingProvider;
	private volatile ClientChunkManager.ClientChunkMap chunks = new ClientChunkManager.ClientChunkMap(3);
	private int loadedChunkCount;
	private volatile int playerChunkX;
	private volatile int playerChunkZ;
	private final ClientWorld field_16525;

	public ClientChunkManager(ClientWorld clientWorld) {
		this.field_16525 = clientWorld;
		this.emptyChunk = new EmptyChunk(clientWorld, new ChunkPos(0, 0));
		this.lightingProvider = new LightingProvider(this, true, clientWorld.method_8597().hasSkyLight());
	}

	private static boolean isWithinDistance(int i, int j, int k, int l, int m) {
		return Math.abs(i - k) <= m && Math.abs(j - l) <= m;
	}

	@Override
	public LightingProvider method_12130() {
		return this.lightingProvider;
	}

	public void unload(int i, int j) {
		if (this.chunks.hasChunk(i, j)) {
			this.chunks.unload(this.chunks.index(i, j), null);
		}
	}

	@Nullable
	public WorldChunk method_2857(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		WorldChunk worldChunk = this.chunks.getChunk(i, j);
		if (worldChunk != null) {
			return worldChunk;
		} else {
			return bl ? this.emptyChunk : null;
		}
	}

	@Override
	public BlockView getWorld() {
		return this.field_16525;
	}

	@Nullable
	public WorldChunk method_16020(World world, int i, int j, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int k, boolean bl) {
		this.updateChunkList();
		if (!this.chunks.hasChunk(i, j)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", i, j);
			return null;
		} else {
			int l = this.chunks.index(i, j);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(l);
			if (worldChunk == null) {
				if (!bl) {
					LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", i, j);
					return null;
				}

				worldChunk = new WorldChunk(world, new ChunkPos(i, j), new Biome[256]);
				worldChunk.method_12224(packetByteBuf, compoundTag, k, bl);
				this.chunks.unload(l, worldChunk);
				this.loadedChunkCount++;
			} else {
				worldChunk.method_12224(packetByteBuf, compoundTag, k, bl);
			}

			ChunkSection[] chunkSections = worldChunk.method_12006();
			LightingProvider lightingProvider = this.method_12130();

			for (int m = 0; m < chunkSections.length; m++) {
				ChunkSection chunkSection = chunkSections[m];
				lightingProvider.method_15551(ChunkSectionPos.from(i, m, j), ChunkSection.isEmpty(chunkSection));
			}

			return worldChunk;
		}
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.updateChunkList();
	}

	private void updateChunkList() {
		int i = this.chunks.loadDistance;
		int j = Math.max(2, this.client.field_1690.viewDistance + -2) + 2;
		if (i != j) {
			ClientChunkManager.ClientChunkMap clientChunkMap = new ClientChunkManager.ClientChunkMap(j);

			for (int k = this.playerChunkZ - i; k <= this.playerChunkZ + i; k++) {
				for (int l = this.playerChunkX - i; l <= this.playerChunkX + i; l++) {
					WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(this.chunks.index(l, k));
					if (worldChunk != null) {
						if (!isWithinDistance(l, k, this.playerChunkX, this.playerChunkZ, j)) {
							this.loadedChunkCount--;
						} else {
							clientChunkMap.chunks.set(clientChunkMap.index(l, k), worldChunk);
						}
					}
				}
			}

			this.chunks = clientChunkMap;
		}

		int m = MathHelper.floor(this.client.field_1724.x) >> 4;
		int k = MathHelper.floor(this.client.field_1724.z) >> 4;
		if (this.playerChunkX != m || this.playerChunkZ != k) {
			for (int lx = this.playerChunkZ - j; lx <= this.playerChunkZ + j; lx++) {
				for (int n = this.playerChunkX - j; n <= this.playerChunkX + j; n++) {
					if (!isWithinDistance(n, lx, m, k, j)) {
						this.chunks.unload(this.chunks.index(n, lx), null);
					}
				}
			}

			this.playerChunkX = m;
			this.playerChunkZ = k;
		}
	}

	@Override
	public String getStatus() {
		return "MultiplayerChunkCache: " + this.chunks.chunks.length() + ", " + this.loadedChunkCount;
	}

	@Override
	public ChunkGenerator<?> getChunkGenerator() {
		return null;
	}

	@Override
	public void method_12247(LightType lightType, ChunkSectionPos chunkSectionPos) {
		MinecraftClient.getInstance().field_1769.method_8571(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkY(), chunkSectionPos.getChunkZ());
	}

	@Environment(EnvType.CLIENT)
	final class ClientChunkMap {
		private final AtomicReferenceArray<WorldChunk> chunks;
		private final int loadDistance;
		private final int loadDiameter;

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
				ClientChunkManager.this.loadedChunkCount--;
				ClientChunkManager.this.field_16525.method_18110(worldChunk2);
			}
		}

		private boolean hasChunk(int i, int j) {
			return ClientChunkManager.isWithinDistance(i, j, ClientChunkManager.this.playerChunkX, ClientChunkManager.this.playerChunkZ, this.loadDistance);
		}

		@Nullable
		protected WorldChunk getChunk(int i, int j) {
			return this.hasChunk(i, j) ? (WorldChunk)this.chunks.get(this.index(i, j)) : null;
		}
	}
}

package net.minecraft.client.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientChunkManager extends ChunkManager {
	static final Logger LOGGER = LogUtils.getLogger();
	private final WorldChunk emptyChunk;
	private final LightingProvider lightingProvider;
	volatile ClientChunkManager.ClientChunkMap chunks;
	final ClientWorld world;

	public ClientChunkManager(ClientWorld world, int loadDistance) {
		this.world = world;
		this.emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0), world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
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

	public void unload(ChunkPos pos) {
		if (this.chunks.isInRadius(pos.x, pos.z)) {
			int i = this.chunks.getIndex(pos.x, pos.z);
			WorldChunk worldChunk = this.chunks.getChunk(i);
			if (positionEquals(worldChunk, pos.x, pos.z)) {
				this.chunks.unloadChunk(i, worldChunk);
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

	public void onChunkBiomeData(int x, int z, PacketByteBuf buf) {
		if (!this.chunks.isInRadius(x, z)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", x, z);
		} else {
			int i = this.chunks.getIndex(x, z);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(i);
			if (!positionEquals(worldChunk, x, z)) {
				LOGGER.warn("Ignoring chunk since it's not present: {}, {}", x, z);
			} else {
				worldChunk.loadBiomeFromPacket(buf);
			}
		}
	}

	@Nullable
	public WorldChunk loadChunkFromPacket(int x, int z, PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer) {
		if (!this.chunks.isInRadius(x, z)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", x, z);
			return null;
		} else {
			int i = this.chunks.getIndex(x, z);
			WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(i);
			ChunkPos chunkPos = new ChunkPos(x, z);
			if (!positionEquals(worldChunk, x, z)) {
				worldChunk = new WorldChunk(this.world, chunkPos);
				worldChunk.loadFromPacket(buf, nbt, consumer);
				this.chunks.set(i, worldChunk);
			} else {
				worldChunk.loadFromPacket(buf, nbt, consumer);
			}

			this.world.resetChunkColor(chunkPos);
			return worldChunk;
		}
	}

	@Override
	public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
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
		return this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
	}

	@Override
	public int getLoadedChunkCount() {
		return this.chunks.loadedChunkCount;
	}

	@Override
	public void onLightUpdate(LightType type, ChunkSectionPos pos) {
		MinecraftClient.getInstance().worldRenderer.scheduleChunkRender(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
	}

	public LongOpenHashSet getActiveSections() {
		return this.chunks.activeSections;
	}

	@Override
	public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
		this.chunks.onSectionStatusChanged(x, sectionY, z, previouslyEmpty);
	}

	@Environment(EnvType.CLIENT)
	final class ClientChunkMap {
		final AtomicReferenceArray<WorldChunk> chunks;
		final LongOpenHashSet activeSections = new LongOpenHashSet();
		final int radius;
		private final int diameter;
		volatile int centerChunkX;
		volatile int centerChunkZ;
		int loadedChunkCount;

		ClientChunkMap(final int radius) {
			this.radius = radius;
			this.diameter = radius * 2 + 1;
			this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
		}

		int getIndex(int chunkX, int chunkZ) {
			return Math.floorMod(chunkZ, this.diameter) * this.diameter + Math.floorMod(chunkX, this.diameter);
		}

		void set(int index, @Nullable WorldChunk chunk) {
			WorldChunk worldChunk = (WorldChunk)this.chunks.getAndSet(index, chunk);
			if (worldChunk != null) {
				this.loadedChunkCount--;
				this.unloadChunkSections(worldChunk);
				ClientChunkManager.this.world.unloadBlockEntities(worldChunk);
			}

			if (chunk != null) {
				this.loadedChunkCount++;
				this.loadChunkSections(chunk);
			}
		}

		void unloadChunk(int index, WorldChunk chunk) {
			if (this.chunks.compareAndSet(index, chunk, null)) {
				this.loadedChunkCount--;
				this.unloadChunkSections(chunk);
			}

			ClientChunkManager.this.world.unloadBlockEntities(chunk);
		}

		public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
			if (this.isInRadius(x, z)) {
				long l = ChunkSectionPos.asLong(x, sectionY, z);
				if (previouslyEmpty) {
					this.activeSections.add(l);
				} else if (this.activeSections.remove(l)) {
					ClientChunkManager.this.world.onChunkUnload(l);
				}
			}
		}

		private void unloadChunkSections(WorldChunk chunk) {
			ChunkSection[] chunkSections = chunk.getSectionArray();

			for (int i = 0; i < chunkSections.length; i++) {
				ChunkPos chunkPos = chunk.getPos();
				this.activeSections.remove(ChunkSectionPos.asLong(chunkPos.x, chunk.sectionIndexToCoord(i), chunkPos.z));
			}
		}

		private void loadChunkSections(WorldChunk chunk) {
			ChunkSection[] chunkSections = chunk.getSectionArray();

			for (int i = 0; i < chunkSections.length; i++) {
				ChunkSection chunkSection = chunkSections[i];
				if (chunkSection.isEmpty()) {
					ChunkPos chunkPos = chunk.getPos();
					this.activeSections.add(ChunkSectionPos.asLong(chunkPos.x, chunk.sectionIndexToCoord(i), chunkPos.z));
				}
			}
		}

		boolean isInRadius(int chunkX, int chunkZ) {
			return Math.abs(chunkX - this.centerChunkX) <= this.radius && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
		}

		@Nullable
		protected WorldChunk getChunk(int index) {
			return (WorldChunk)this.chunks.get(index);
		}

		private void writePositions(String fileName) {
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(fileName);

				try {
					int i = ClientChunkManager.this.chunks.radius;

					for (int j = this.centerChunkZ - i; j <= this.centerChunkZ + i; j++) {
						for (int k = this.centerChunkX - i; k <= this.centerChunkX + i; k++) {
							WorldChunk worldChunk = (WorldChunk)ClientChunkManager.this.chunks.chunks.get(ClientChunkManager.this.chunks.getIndex(k, j));
							if (worldChunk != null) {
								ChunkPos chunkPos = worldChunk.getPos();
								fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + worldChunk.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
							}
						}
					}
				} catch (Throwable var9) {
					try {
						fileOutputStream.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}

					throw var9;
				}

				fileOutputStream.close();
			} catch (IOException var10) {
				ClientChunkManager.LOGGER.error("Failed to dump chunks to file {}", fileName, var10);
			}
		}
	}
}

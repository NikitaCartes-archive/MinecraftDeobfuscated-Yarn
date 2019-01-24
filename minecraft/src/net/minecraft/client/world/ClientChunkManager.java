package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
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
	private volatile ClientChunkManager.class_3681 field_16246 = new ClientChunkManager.class_3681(3);
	private int field_16249;
	private volatile int field_16248;
	private volatile int field_16247;
	private final BlockView world;

	public ClientChunkManager(World world) {
		this.world = world;
		this.emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0));
		this.lightingProvider = new LightingProvider(this, true, world.getDimension().hasSkyLight());
	}

	private static boolean method_16024(int i, int j, int k, int l, int m) {
		return Math.abs(i - k) <= m && Math.abs(j - l) <= m;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	public void method_2859(int i, int j) {
		this.field_16246.method_16031(i, j);
	}

	@Nullable
	public WorldChunk method_2857(int i, int j, ChunkStatus chunkStatus, boolean bl) {
		WorldChunk worldChunk = this.field_16246.method_16033(i, j);
		if (worldChunk != null) {
			return worldChunk;
		} else {
			return bl ? this.emptyChunk : null;
		}
	}

	@Override
	public BlockView getWorld() {
		return this.world;
	}

	public void method_16020(World world, int i, int j, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int k, boolean bl) {
		this.method_16026();
		if (!this.field_16246.method_16034(i, j)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", i, j);
		} else {
			int l = this.field_16246.method_16027(i, j);
			WorldChunk worldChunk = (WorldChunk)this.field_16246.field_16251.get(l);
			if (worldChunk == null) {
				worldChunk = new WorldChunk(world, new ChunkPos(i, j), new Biome[256]);
				this.field_16246.field_16251.set(l, worldChunk);
				this.field_16249++;
			}

			worldChunk.method_12224(packetByteBuf, compoundTag, k, bl);
			worldChunk.setLoadedToWorld(true);
			ChunkSection[] chunkSections = worldChunk.getSectionArray();
			LightingProvider lightingProvider = this.getLightingProvider();

			for (int m = 0; m < chunkSections.length; m++) {
				ChunkSection chunkSection = chunkSections[m];
				lightingProvider.scheduleChunkLightUpdate(i, m, j, chunkSection == WorldChunk.EMPTY_SECTION || chunkSection.isEmpty());
			}
		}
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.method_16026();
	}

	private void method_16026() {
		int i = this.field_16246.field_16253;
		int j = Math.max(2, this.client.options.viewDistance + -2) + 2;
		if (i != j) {
			ClientChunkManager.class_3681 lv = new ClientChunkManager.class_3681(j);

			for (int k = this.field_16247 - i; k <= this.field_16247 + i; k++) {
				for (int l = this.field_16248 - i; l <= this.field_16248 + i; l++) {
					WorldChunk worldChunk = (WorldChunk)this.field_16246.field_16251.get(this.field_16246.method_16027(l, k));
					if (worldChunk != null) {
						if (!method_16024(l, k, this.field_16248, this.field_16247, j)) {
							this.field_16249--;
						} else {
							lv.field_16251.set(lv.method_16027(l, k), worldChunk);
						}
					}
				}
			}

			this.field_16246 = lv;
		}

		int m = MathHelper.floor(this.client.player.x) >> 4;
		int k = MathHelper.floor(this.client.player.z) >> 4;
		if (this.field_16248 != m || this.field_16247 != k) {
			for (int lx = this.field_16247 - j; lx <= this.field_16247 + j; lx++) {
				for (int n = this.field_16248 - j; n <= this.field_16248 + j; n++) {
					if (!method_16024(n, lx, m, k, j)) {
						this.field_16246.field_16251.set(this.field_16246.method_16027(n, lx), null);
					}
				}
			}

			this.field_16248 = m;
			this.field_16247 = k;
		}
	}

	@Override
	public String getStatus() {
		return "MultiplayerChunkCache: " + this.field_16246.field_16251.length() + ", " + this.field_16249;
	}

	@Override
	public ChunkGenerator<?> getChunkGenerator() {
		return null;
	}

	@Override
	public void onLightUpdate(LightType lightType, int i, int j, int k) {
		MinecraftClient.getInstance().worldRenderer.scheduleChunkRender(i, j, k);
	}

	@Environment(EnvType.CLIENT)
	final class class_3681 {
		private final AtomicReferenceArray<WorldChunk> field_16251;
		private final int field_16253;
		private final int field_16252;

		private class_3681(int i) {
			this.field_16253 = i;
			this.field_16252 = i * 2 + 1;
			this.field_16251 = new AtomicReferenceArray(this.field_16252 * this.field_16252);
		}

		private int method_16027(int i, int j) {
			return Math.floorMod(j, this.field_16252) * this.field_16252 + Math.floorMod(i, this.field_16252);
		}

		protected void method_16031(int i, int j) {
			if (this.method_16034(i, j)) {
				WorldChunk worldChunk = (WorldChunk)this.field_16251.getAndSet(this.method_16027(i, j), null);
				if (worldChunk != null) {
					ClientChunkManager.this.field_16249--;
					worldChunk.unloadFromWorld();
				}
			}
		}

		private boolean method_16034(int i, int j) {
			return ClientChunkManager.method_16024(i, j, ClientChunkManager.this.field_16248, ClientChunkManager.this.field_16247, this.field_16253);
		}

		@Nullable
		protected WorldChunk method_16033(int i, int j) {
			if (this.method_16034(i, j)) {
				WorldChunk worldChunk = (WorldChunk)this.field_16251.get(this.method_16027(i, j));
				if (worldChunk != null) {
					return worldChunk;
				}
			}

			return null;
		}
	}
}

package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.text.TextFormat;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.FluidRayTraceMode;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class DebugHud extends Drawable {
	private final MinecraftClient client;
	private final FontRenderer fontRenderer;
	private HitResult blockHit;
	private HitResult fluidHit;
	@Nullable
	private ChunkPos pos;
	@Nullable
	private WorldChunk chunk;
	@Nullable
	private CompletableFuture<WorldChunk> chunkFuture;

	public DebugHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.fontRenderer = minecraftClient.fontRenderer;
	}

	public void resetChunk() {
		this.chunkFuture = null;
		this.chunk = null;
	}

	public void draw() {
		this.client.getProfiler().push("debug");
		GlStateManager.pushMatrix();
		Entity entity = this.client.getCameraEntity();
		this.blockHit = entity.rayTrace(20.0, 0.0F, FluidRayTraceMode.NONE);
		this.fluidHit = entity.rayTrace(20.0, 0.0F, FluidRayTraceMode.field_1347);
		this.drawLeftText();
		this.drawRightText();
		GlStateManager.popMatrix();
		if (this.client.field_1690.debugTpsEnabled) {
			int i = this.client.window.getScaledWidth();
			this.drawMetricsData(this.client.getMetricsData(), 0, i / 2, true);
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				this.drawMetricsData(integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
			}
		}

		this.client.getProfiler().pop();
	}

	protected void drawLeftText() {
		List<String> list = this.getLeftText();
		list.add("");
		list.add(
			"Debug: Pie [shift]: "
				+ (this.client.field_1690.debugProfilerEnabled ? "visible" : "hidden")
				+ " FPS [alt]: "
				+ (this.client.field_1690.debugTpsEnabled ? "visible" : "hidden")
		);
		list.add("For help: press F3 + Q");

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.fontRenderer.fontHeight;
				int k = this.fontRenderer.getStringWidth(string);
				int l = 2;
				int m = 2 + j * i;
				drawRect(1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
				this.fontRenderer.draw(string, 2.0F, (float)m, 14737632);
			}
		}
	}

	protected void drawRightText() {
		List<String> list = this.getRightText();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.fontRenderer.fontHeight;
				int k = this.fontRenderer.getStringWidth(string);
				int l = this.client.window.getScaledWidth() - 2 - k;
				int m = 2 + j * i;
				drawRect(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
				this.fontRenderer.draw(string, (float)l, (float)m, 14737632);
			}
		}
	}

	protected List<String> getLeftText() {
		IntegratedServer integratedServer = this.client.getServer();
		ClientConnection clientConnection = this.client.getNetworkHandler().getClientConnection();
		float f = clientConnection.getPacketsSent();
		float g = clientConnection.getPacketsReceived();
		String string;
		if (integratedServer != null) {
			string = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer.getTickTime(), f, g);
		} else {
			string = String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.method_3135(), f, g);
		}

		BlockPos blockPos = new BlockPos(this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z);
		if (this.client.hasReducedDebugInfo()) {
			return Lists.<String>newArrayList(
				"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
				this.client.fpsDebugString,
				string,
				this.client.field_1769.getChunksDebugString(),
				this.client.field_1769.getEntitiesDebugString(),
				"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getEntityCountAsString(),
				this.client.world.getChunkProviderStatus(),
				"",
				String.format("Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
			);
		} else {
			Entity entity = this.client.getCameraEntity();
			Direction direction = entity.getHorizontalFacing();
			String string2;
			switch (direction) {
				case NORTH:
					string2 = "Towards negative Z";
					break;
				case SOUTH:
					string2 = "Towards positive Z";
					break;
				case WEST:
					string2 = "Towards negative X";
					break;
				case EAST:
					string2 = "Towards positive X";
					break;
				default:
					string2 = "Invalid";
			}

			DimensionType dimensionType = this.client.world.dimension.getType();
			ChunkPos chunkPos = new ChunkPos(blockPos);
			if (!Objects.equals(this.pos, chunkPos)) {
				this.pos = chunkPos;
				this.resetChunk();
			}

			World world = this.getWorld();
			ForcedChunkState forcedChunkState = world.method_8648(dimensionType, ForcedChunkState::new, "chunks");
			List<String> list = Lists.<String>newArrayList(
				"Minecraft "
					+ SharedConstants.getGameVersion().getName()
					+ " ("
					+ this.client.getGameVersion()
					+ "/"
					+ ClientBrandRetriever.getClientModName()
					+ ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType())
					+ ")",
				this.client.fpsDebugString,
				string,
				this.client.field_1769.getChunksDebugString(),
				this.client.field_1769.getEntitiesDebugString(),
				"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getEntityCountAsString(),
				this.client.world.getChunkProviderStatus(),
				DimensionType.getId(dimensionType).toString() + " FC: " + (forcedChunkState == null ? "n/a" : Integer.toString(forcedChunkState.getChunks().size())),
				"",
				String.format(
					Locale.ROOT,
					"XYZ: %.3f / %.5f / %.3f",
					this.client.getCameraEntity().x,
					this.client.getCameraEntity().getBoundingBox().minY,
					this.client.getCameraEntity().z
				),
				String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()),
				String.format(
					"Chunk: %d %d %d in %d %d %d",
					blockPos.getX() & 15,
					blockPos.getY() & 15,
					blockPos.getZ() & 15,
					blockPos.getX() >> 4,
					blockPos.getY() >> 4,
					blockPos.getZ() >> 4
				),
				String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string2, MathHelper.wrapDegrees(entity.yaw), MathHelper.wrapDegrees(entity.pitch))
			);
			if (this.client.world != null) {
				if (this.client.world.isBlockLoaded(blockPos)) {
					WorldChunk worldChunk = this.getClientChunk();
					if (worldChunk.isEmpty()) {
						list.add("Waiting for chunk...");
					} else {
						list.add(
							"Client Light: "
								+ worldChunk.getLightLevel(blockPos, 0)
								+ " ("
								+ this.client.world.getLightLevel(LightType.SKY_LIGHT, blockPos)
								+ " sky, "
								+ this.client.world.getLightLevel(LightType.BLOCK_LIGHT, blockPos)
								+ " block)"
						);
						WorldChunk worldChunk2 = this.getChunk();
						if (worldChunk2 != null) {
							LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
							list.add(
								"Server Light: ("
									+ lightingProvider.get(LightType.SKY_LIGHT).getLightLevel(blockPos)
									+ " sky, "
									+ lightingProvider.get(LightType.BLOCK_LIGHT).getLightLevel(blockPos)
									+ " block)"
							);
						}

						if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
							list.add("Biome: " + Registry.BIOME.getId(worldChunk.getBiome(blockPos)));
							long l = 0L;
							float h = 0.0F;
							if (worldChunk2 != null) {
								h = world.method_8391();
								l = worldChunk2.getInhabitedTime();
							}

							LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
							list.add(
								String.format(
									Locale.ROOT,
									"Local Difficulty: %.2f // %.2f (Day %d)",
									localDifficulty.getLocalDifficulty(),
									localDifficulty.getClampedLocalDifficulty(),
									this.client.world.getTimeOfDay() / 24000L
								)
							);
						}
					}
				} else {
					list.add("Outside of world...");
				}
			} else {
				list.add("Outside of world...");
			}

			if (this.client.field_1773 != null && this.client.field_1773.method_3175()) {
				list.add("Shader: " + this.client.field_1773.getShader().getName());
			}

			if (this.blockHit != null && this.blockHit.type == HitResult.Type.BLOCK) {
				BlockPos blockPos2 = this.blockHit.getBlockPos();
				list.add(String.format("Looking at block: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
			}

			if (this.fluidHit != null && this.fluidHit.type == HitResult.Type.BLOCK) {
				BlockPos blockPos2 = this.fluidHit.getBlockPos();
				list.add(String.format("Looking at liquid: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
			}

			return list;
		}
	}

	private World getWorld() {
		return DataFixUtils.orElse(
			Optional.ofNullable(this.client.getServer()).map(integratedServer -> integratedServer.getWorld(this.client.world.dimension.getType())), this.client.world
		);
	}

	@Nullable
	private WorldChunk getChunk() {
		if (this.chunkFuture == null) {
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				ServerWorld serverWorld = integratedServer.getWorld(this.client.world.dimension.getType());
				if (serverWorld != null) {
					this.chunkFuture = serverWorld.method_16177(this.pos.x, this.pos.z, false);
				}
			}

			if (this.chunkFuture == null) {
				this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
			}
		}

		return (WorldChunk)this.chunkFuture.getNow(null);
	}

	private WorldChunk getClientChunk() {
		if (this.chunk == null) {
			this.chunk = this.client.world.getWorldChunk(this.pos.x, this.pos.z);
		}

		return this.chunk;
	}

	protected List<String> getRightText() {
		long l = Runtime.getRuntime().maxMemory();
		long m = Runtime.getRuntime().totalMemory();
		long n = Runtime.getRuntime().freeMemory();
		long o = m - n;
		List<String> list = Lists.<String>newArrayList(
			String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32),
			String.format("Mem: % 2d%% %03d/%03dMB", o * 100L / l, method_1838(o), method_1838(l)),
			String.format("Allocated: % 2d%% %03dMB", m * 100L / l, method_1838(m)),
			"",
			String.format("CPU: %s", GLX.getCpuInfo()),
			"",
			String.format(
				"Display: %dx%d (%s)", MinecraftClient.getInstance().window.getWindowWidth(), MinecraftClient.getInstance().window.getWindowHeight(), GLX.getVendor()
			),
			GLX.getRenderer(),
			GLX.getOpenGLVersion()
		);
		if (this.client.hasReducedDebugInfo()) {
			return list;
		} else {
			if (this.blockHit != null && this.blockHit.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = this.blockHit.getBlockPos();
				BlockState blockState = this.client.world.getBlockState(blockPos);
				list.add("");
				list.add(TextFormat.UNDERLINE + "Targeted Block");
				list.add(String.valueOf(Registry.BLOCK.getId(blockState.getBlock())));

				for (Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
					list.add(this.propertyToString(entry));
				}

				for (Identifier identifier : this.client.getNetworkHandler().getTagManager().blocks().getTagsFor(blockState.getBlock())) {
					list.add("#" + identifier);
				}
			}

			if (this.fluidHit != null && this.fluidHit.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = this.fluidHit.getBlockPos();
				FluidState fluidState = this.client.world.getFluidState(blockPos);
				list.add("");
				list.add(TextFormat.UNDERLINE + "Targeted Fluid");
				list.add(String.valueOf(Registry.FLUID.getId(fluidState.getFluid())));

				for (Entry<Property<?>, Comparable<?>> entry : fluidState.getEntries().entrySet()) {
					list.add(this.propertyToString(entry));
				}

				for (Identifier identifier : this.client.getNetworkHandler().getTagManager().fluids().getTagsFor(fluidState.getFluid())) {
					list.add("#" + identifier);
				}
			}

			Entity entity = this.client.field_1692;
			if (entity != null) {
				list.add("");
				list.add(TextFormat.UNDERLINE + "Targeted Entity");
				list.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity.getType())));
			}

			return list;
		}
	}

	private String propertyToString(Entry<Property<?>, Comparable<?>> entry) {
		Property<?> property = (Property<?>)entry.getKey();
		Comparable<?> comparable = (Comparable<?>)entry.getValue();
		String string = SystemUtil.getValueAsString(property, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			string = TextFormat.GREEN + string;
		} else if (Boolean.FALSE.equals(comparable)) {
			string = TextFormat.RED + string;
		}

		return property.getName() + ": " + string;
	}

	private void drawMetricsData(MetricsData metricsData, int i, int j, boolean bl) {
		GlStateManager.disableDepthTest();
		int k = metricsData.method_15249();
		int l = metricsData.getCurrentIndex();
		long[] ls = metricsData.getSamples();
		int n = i;
		int o = Math.max(0, ls.length - j);
		int p = ls.length - o;
		int m = metricsData.wrapIndex(k + o);
		long q = 0L;
		int r = Integer.MAX_VALUE;
		int s = Integer.MIN_VALUE;

		for (int t = 0; t < p; t++) {
			int u = (int)(ls[metricsData.wrapIndex(m + t)] / 1000000L);
			r = Math.min(r, u);
			s = Math.max(s, u);
			q += (long)u;
		}

		int t = this.client.window.getScaledHeight();
		drawRect(i, t - 60, i + p, t, -1873784752);

		while (m != l) {
			int u = metricsData.method_15248(ls[m], bl ? 30 : 60, bl ? 60 : 20);
			int v = bl ? 100 : 60;
			int w = this.method_1833(MathHelper.clamp(u, 0, v), 0, v / 2, v);
			this.drawVerticalLine(n, t, t - u, w);
			n++;
			m = metricsData.wrapIndex(m + 1);
		}

		if (bl) {
			drawRect(i + 1, t - 30 + 1, i + 14, t - 30 + 10, -1873784752);
			this.fontRenderer.draw("60 FPS", (float)(i + 2), (float)(t - 30 + 2), 14737632);
			this.drawHorizontalLine(i, i + p - 1, t - 30, -1);
			drawRect(i + 1, t - 60 + 1, i + 14, t - 60 + 10, -1873784752);
			this.fontRenderer.draw("30 FPS", (float)(i + 2), (float)(t - 60 + 2), 14737632);
			this.drawHorizontalLine(i, i + p - 1, t - 60, -1);
		} else {
			drawRect(i + 1, t - 60 + 1, i + 14, t - 60 + 10, -1873784752);
			this.fontRenderer.draw("20 TPS", (float)(i + 2), (float)(t - 60 + 2), 14737632);
			this.drawHorizontalLine(i, i + p - 1, t - 60, -1);
		}

		this.drawHorizontalLine(i, i + p - 1, t - 1, -1);
		this.drawVerticalLine(i, t - 60, t, -1);
		this.drawVerticalLine(i + p - 1, t - 60, t, -1);
		if (bl && this.client.field_1690.maxFps > 0 && this.client.field_1690.maxFps <= 250) {
			this.drawHorizontalLine(i, i + p - 1, t - 1 - (int)(1800.0 / (double)this.client.field_1690.maxFps), -16711681);
		}

		String string = r + " ms min";
		String string2 = q / (long)ls.length + " ms avg";
		String string3 = s + " ms max";
		this.fontRenderer.drawWithShadow(string, (float)(i + 2), (float)(t - 60 - this.fontRenderer.fontHeight), 14737632);
		this.fontRenderer
			.drawWithShadow(string2, (float)(i + p / 2 - this.fontRenderer.getStringWidth(string2) / 2), (float)(t - 60 - this.fontRenderer.fontHeight), 14737632);
		this.fontRenderer
			.drawWithShadow(string3, (float)(i + p - this.fontRenderer.getStringWidth(string3)), (float)(t - 60 - this.fontRenderer.fontHeight), 14737632);
		GlStateManager.enableDepthTest();
	}

	private int method_1833(int i, int j, int k, int l) {
		return i < k ? this.interpolateColor(-16711936, -256, (float)i / (float)k) : this.interpolateColor(-256, -65536, (float)(i - k) / (float)(l - k));
	}

	private int interpolateColor(int i, int j, float f) {
		int k = i >> 24 & 0xFF;
		int l = i >> 16 & 0xFF;
		int m = i >> 8 & 0xFF;
		int n = i & 0xFF;
		int o = j >> 24 & 0xFF;
		int p = j >> 16 & 0xFF;
		int q = j >> 8 & 0xFF;
		int r = j & 0xFF;
		int s = MathHelper.clamp((int)MathHelper.lerp(f, (float)k, (float)o), 0, 255);
		int t = MathHelper.clamp((int)MathHelper.lerp(f, (float)l, (float)p), 0, 255);
		int u = MathHelper.clamp((int)MathHelper.lerp(f, (float)m, (float)q), 0, 255);
		int v = MathHelper.clamp((int)MathHelper.lerp(f, (float)n, (float)r), 0, 255);
		return s << 24 | t << 16 | u << 8 | v;
	}

	private static long method_1838(long l) {
		return l / 1024L / 1024L;
	}
}

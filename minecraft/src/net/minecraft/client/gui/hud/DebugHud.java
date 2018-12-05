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
import net.minecraft.class_1932;
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
	private CompletableFuture<WorldChunk> field_2080;

	public DebugHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.fontRenderer = minecraftClient.fontRenderer;
	}

	public void method_1842() {
		this.field_2080 = null;
		this.chunk = null;
	}

	public void method_1846() {
		this.client.getProfiler().begin("debug");
		GlStateManager.pushMatrix();
		Entity entity = this.client.getCameraEntity();
		this.blockHit = entity.rayTrace(20.0, 0.0F, FluidRayTraceMode.NONE);
		this.fluidHit = entity.rayTrace(20.0, 0.0F, FluidRayTraceMode.field_1347);
		this.drawLeftInfoText();
		this.method_1848();
		GlStateManager.popMatrix();
		if (this.client.options.debugTpsEnabled) {
			int i = this.client.window.getScaledWidth();
			this.method_15870(this.client.getMetricsData(), 0, i / 2, true);
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				this.method_15870(integratedServer.method_15876(), i - Math.min(i / 2, 240), i / 2, false);
			}
		}

		this.client.getProfiler().end();
	}

	protected void drawLeftInfoText() {
		List<String> list = this.method_1835();
		list.add("");
		list.add(
			"Debug: Pie [shift]: "
				+ (this.client.options.debugProfilerEnabled ? "visible" : "hidden")
				+ " FPS [alt]: "
				+ (this.client.options.debugTpsEnabled ? "visible" : "hidden")
		);
		list.add("For help: press F3 + Q");

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.fontRenderer.FONT_HEIGHT;
				int k = this.fontRenderer.getStringWidth(string);
				int l = 2;
				int m = 2 + j * i;
				drawRect(1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
				this.fontRenderer.draw(string, 2.0F, (float)m, 14737632);
			}
		}
	}

	protected void method_1848() {
		List<String> list = this.method_1839();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = this.fontRenderer.FONT_HEIGHT;
				int k = this.fontRenderer.getStringWidth(string);
				int l = this.client.window.getScaledWidth() - 2 - k;
				int m = 2 + j * i;
				drawRect(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
				this.fontRenderer.draw(string, (float)l, (float)m, 14737632);
			}
		}
	}

	protected List<String> method_1835() {
		IntegratedServer integratedServer = this.client.getServer();
		ClientConnection clientConnection = this.client.getNetworkHandler().getClientConnection();
		float f = clientConnection.method_10745();
		float g = clientConnection.method_10762();
		String string;
		if (integratedServer != null) {
			string = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer.method_3830(), f, g);
		} else {
			string = String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), f, g);
		}

		BlockPos blockPos = new BlockPos(this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().minY, this.client.getCameraEntity().z);
		if (this.client.hasReducedDebugInfo()) {
			return Lists.<String>newArrayList(
				"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
				this.client.fpsDebugString,
				string,
				this.client.renderer.method_3289(),
				this.client.renderer.getEntitiesDebugString(),
				"P: " + this.client.particleManager.method_3052() + ". T: " + this.client.world.getEntityCountAsString(),
				this.client.world.getChunkProviderStatus(),
				"",
				String.format("Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
			);
		} else {
			Entity entity = this.client.getCameraEntity();
			Direction direction = entity.method_5735();
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
				this.method_1842();
			}

			World world = this.method_1840();
			class_1932 lv = world.method_8648(dimensionType, class_1932::new, "chunks");
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
				this.client.renderer.method_3289(),
				this.client.renderer.getEntitiesDebugString(),
				"P: " + this.client.particleManager.method_3052() + ". T: " + this.client.world.getEntityCountAsString(),
				this.client.world.getChunkProviderStatus(),
				DimensionType.getId(dimensionType).toString() + " FC: " + (lv == null ? "n/a" : Integer.toString(lv.method_8375().size())),
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
					WorldChunk worldChunk = this.method_1836();
					if (worldChunk.method_12223()) {
						list.add("Waiting for chunk...");
					} else {
						list.add(
							"Client Light: "
								+ worldChunk.method_12233(blockPos, 0)
								+ " ("
								+ this.client.world.getLightLevel(LightType.field_9284, blockPos)
								+ " sky, "
								+ this.client.world.getLightLevel(LightType.field_9282, blockPos)
								+ " block)"
						);
						WorldChunk worldChunk2 = this.method_1834();
						if (worldChunk2 != null) {
							LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
							list.add(
								"Server Light: ("
									+ lightingProvider.get(LightType.field_9284).getLightLevel(blockPos)
									+ " sky, "
									+ lightingProvider.get(LightType.field_9282).getLightLevel(blockPos)
									+ " block)"
							);
						}

						if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
							list.add("Biome: " + Registry.BIOME.getId(worldChunk.getBiome(blockPos)));
							long l = 0L;
							float h = 0.0F;
							if (worldChunk2 != null) {
								h = world.method_8391();
								l = worldChunk2.method_12033();
							}

							LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
							list.add(
								String.format(
									Locale.ROOT,
									"Local Difficulty: %.2f // %.2f (Day %d)",
									localDifficulty.method_5457(),
									localDifficulty.method_5458(),
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

			if (this.client.worldRenderer != null && this.client.worldRenderer.method_3175()) {
				list.add("Shader: " + this.client.worldRenderer.method_3183().method_1260());
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

	private World method_1840() {
		return DataFixUtils.orElse(
			Optional.ofNullable(this.client.getServer()).map(integratedServer -> integratedServer.getWorld(this.client.world.dimension.getType())), this.client.world
		);
	}

	@Nullable
	private WorldChunk method_1834() {
		if (this.field_2080 == null) {
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				ServerWorld serverWorld = integratedServer.getWorld(this.client.world.dimension.getType());
				if (serverWorld != null) {
					this.field_2080 = serverWorld.method_16177(this.pos.x, this.pos.z, false);
				}
			}

			if (this.field_2080 == null) {
				this.field_2080 = CompletableFuture.completedFuture(this.method_1836());
			}
		}

		return (WorldChunk)this.field_2080.getNow(null);
	}

	private WorldChunk method_1836() {
		if (this.chunk == null) {
			this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
		}

		return this.chunk;
	}

	protected List<String> method_1839() {
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
					list.add(this.method_1845(entry));
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
					list.add(this.method_1845(entry));
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

	private String method_1845(Entry<Property<?>, Comparable<?>> entry) {
		Property<?> property = (Property<?>)entry.getKey();
		Comparable<?> comparable = (Comparable<?>)entry.getValue();
		String string = SystemUtil.method_650(property, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			string = TextFormat.GREEN + string;
		} else if (Boolean.FALSE.equals(comparable)) {
			string = TextFormat.RED + string;
		}

		return property.getName() + ": " + string;
	}

	private void method_15870(MetricsData metricsData, int i, int j, boolean bl) {
		GlStateManager.disableDepthTest();
		int k = metricsData.method_15249();
		int l = metricsData.getCurrentIndex();
		long[] ls = metricsData.getSamples();
		int n = i;
		int o = Math.max(0, ls.length - j);
		int p = ls.length - o;
		int m = metricsData.method_15251(k + o);
		long q = 0L;
		int r = Integer.MAX_VALUE;
		int s = Integer.MIN_VALUE;

		for (int t = 0; t < p; t++) {
			int u = (int)(ls[metricsData.method_15251(m + t)] / 1000000L);
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
			m = metricsData.method_15251(m + 1);
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
		if (bl && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
			this.drawHorizontalLine(i, i + p - 1, t - 1 - (int)(1800.0 / (double)this.client.options.maxFps), -16711681);
		}

		String string = r + " ms min";
		String string2 = q / (long)ls.length + " ms avg";
		String string3 = s + " ms max";
		this.fontRenderer.drawWithShadow(string, (float)(i + 2), (float)(t - 60 - this.fontRenderer.FONT_HEIGHT), 14737632);
		this.fontRenderer
			.drawWithShadow(string2, (float)(i + p / 2 - this.fontRenderer.getStringWidth(string2) / 2), (float)(t - 60 - this.fontRenderer.FONT_HEIGHT), 14737632);
		this.fontRenderer
			.drawWithShadow(string3, (float)(i + p - this.fontRenderer.getStringWidth(string3)), (float)(t - 60 - this.fontRenderer.FONT_HEIGHT), 14737632);
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

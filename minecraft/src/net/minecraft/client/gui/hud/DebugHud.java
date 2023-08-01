package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

@Environment(EnvType.CLIENT)
public class DebugHud {
	private static final int TEXT_COLOR = 14737632;
	private static final int field_32188 = 2;
	private static final int field_32189 = 2;
	private static final int field_32190 = 2;
	private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Util.make(new EnumMap(Heightmap.Type.class), types -> {
		types.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
		types.put(Heightmap.Type.WORLD_SURFACE, "S");
		types.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
		types.put(Heightmap.Type.OCEAN_FLOOR, "O");
		types.put(Heightmap.Type.MOTION_BLOCKING, "M");
		types.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
	});
	private final MinecraftClient client;
	private final DebugHud.AllocationRateCalculator allocationRateCalculator;
	private final TextRenderer textRenderer;
	private HitResult blockHit;
	private HitResult fluidHit;
	@Nullable
	private ChunkPos pos;
	@Nullable
	private WorldChunk chunk;
	@Nullable
	private CompletableFuture<WorldChunk> chunkFuture;
	private static final int METRICS_RED = -65536;
	private static final int METRICS_YELLOW = -256;
	private static final int METRICS_GREEN = -16711936;

	public DebugHud(MinecraftClient client) {
		this.client = client;
		this.allocationRateCalculator = new DebugHud.AllocationRateCalculator();
		this.textRenderer = client.textRenderer;
	}

	public void resetChunk() {
		this.chunkFuture = null;
		this.chunk = null;
	}

	public void render(DrawContext context) {
		this.client.getProfiler().push("debug");
		Entity entity = this.client.getCameraEntity();
		this.blockHit = entity.raycast(20.0, 0.0F, false);
		this.fluidHit = entity.raycast(20.0, 0.0F, true);
		context.draw(() -> {
			this.drawLeftText(context);
			this.drawRightText(context);
			if (this.client.options.debugTpsEnabled) {
				int i = context.getScaledWindowWidth();
				this.drawMetricsData(context, this.client.getMetricsData(), 0, i / 2, true);
				IntegratedServer integratedServer = this.client.getServer();
				if (integratedServer != null) {
					this.drawMetricsData(context, integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
				}
			}
		});
		this.client.getProfiler().pop();
	}

	protected void drawLeftText(DrawContext context) {
		List<String> list = this.getLeftText();
		list.add("");
		boolean bl = this.client.getServer() != null;
		list.add(
			"Debug: Pie [shift]: "
				+ (this.client.options.debugProfilerEnabled ? "visible" : "hidden")
				+ (bl ? " FPS + TPS" : " FPS")
				+ " [alt]: "
				+ (this.client.options.debugTpsEnabled ? "visible" : "hidden")
		);
		list.add("For help: press F3 + Q");
		this.drawText(context, list, true);
	}

	protected void drawRightText(DrawContext context) {
		List<String> list = this.getRightText();
		this.drawText(context, list, false);
	}

	private void drawText(DrawContext context, List<String> text, boolean left) {
		int i = 9;

		for (int j = 0; j < text.size(); j++) {
			String string = (String)text.get(j);
			if (!Strings.isNullOrEmpty(string)) {
				int k = this.textRenderer.getWidth(string);
				int l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
				int m = 2 + i * j;
				context.fill(l - 1, m - 1, l + k + 1, m + i - 1, -1873784752);
			}
		}

		for (int jx = 0; jx < text.size(); jx++) {
			String string = (String)text.get(jx);
			if (!Strings.isNullOrEmpty(string)) {
				int k = this.textRenderer.getWidth(string);
				int l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
				int m = 2 + i * jx;
				context.drawText(this.textRenderer, string, l, m, 14737632, false);
			}
		}
	}

	protected List<String> getLeftText() {
		IntegratedServer integratedServer = this.client.getServer();
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		ClientConnection clientConnection = clientPlayNetworkHandler.getConnection();
		float f = clientConnection.getAveragePacketsSent();
		float g = clientConnection.getAveragePacketsReceived();
		String string;
		if (integratedServer != null) {
			string = String.format(Locale.ROOT, "Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer.getTickTime(), f, g);
		} else {
			string = String.format(Locale.ROOT, "\"%s\" server, %.0f tx, %.0f rx", clientPlayNetworkHandler.getBrand(), f, g);
		}

		BlockPos blockPos = this.client.getCameraEntity().getBlockPos();
		if (this.client.hasReducedDebugInfo()) {
			return Lists.<String>newArrayList(
				"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
				this.client.fpsDebugString,
				string,
				this.client.worldRenderer.getChunksDebugString(),
				this.client.worldRenderer.getEntitiesDebugString(),
				"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(),
				this.client.world.asString(),
				"",
				String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
			);
		} else {
			Entity entity = this.client.getCameraEntity();
			Direction direction = entity.getHorizontalFacing();

			String string2 = switch (direction) {
				case NORTH -> "Towards negative Z";
				case SOUTH -> "Towards positive Z";
				case WEST -> "Towards negative X";
				case EAST -> "Towards positive X";
				default -> "Invalid";
			};
			ChunkPos chunkPos = new ChunkPos(blockPos);
			if (!Objects.equals(this.pos, chunkPos)) {
				this.pos = chunkPos;
				this.resetChunk();
			}

			World world = this.getWorld();
			LongSet longSet = (LongSet)(world instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET);
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
				this.client.worldRenderer.getChunksDebugString(),
				this.client.worldRenderer.getEntitiesDebugString(),
				"P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(),
				this.client.world.asString()
			);
			String string3 = this.getServerWorldDebugString();
			if (string3 != null) {
				list.add(string3);
			}

			list.add(this.client.world.getRegistryKey().getValue() + " FC: " + longSet.size());
			list.add("");
			list.add(
				String.format(
					Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()
				)
			);
			list.add(
				String.format(
					Locale.ROOT,
					"Block: %d %d %d [%d %d %d]",
					blockPos.getX(),
					blockPos.getY(),
					blockPos.getZ(),
					blockPos.getX() & 15,
					blockPos.getY() & 15,
					blockPos.getZ() & 15
				)
			);
			list.add(
				String.format(
					Locale.ROOT,
					"Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
					chunkPos.x,
					ChunkSectionPos.getSectionCoord(blockPos.getY()),
					chunkPos.z,
					chunkPos.getRegionRelativeX(),
					chunkPos.getRegionRelativeZ(),
					chunkPos.getRegionX(),
					chunkPos.getRegionZ()
				)
			);
			list.add(
				String.format(
					Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string2, MathHelper.wrapDegrees(entity.getYaw()), MathHelper.wrapDegrees(entity.getPitch())
				)
			);
			WorldChunk worldChunk = this.getClientChunk();
			if (worldChunk.isEmpty()) {
				list.add("Waiting for chunk...");
			} else {
				int i = this.client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
				int j = this.client.world.getLightLevel(LightType.SKY, blockPos);
				int k = this.client.world.getLightLevel(LightType.BLOCK, blockPos);
				list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
				WorldChunk worldChunk2 = this.getChunk();
				StringBuilder stringBuilder = new StringBuilder("CH");

				for (Heightmap.Type type : Heightmap.Type.values()) {
					if (type.shouldSendToClient()) {
						stringBuilder.append(" ")
							.append((String)HEIGHT_MAP_TYPES.get(type))
							.append(": ")
							.append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
					}
				}

				list.add(stringBuilder.toString());
				stringBuilder.setLength(0);
				stringBuilder.append("SH");

				for (Heightmap.Type typex : Heightmap.Type.values()) {
					if (typex.isStoredServerSide()) {
						stringBuilder.append(" ").append((String)HEIGHT_MAP_TYPES.get(typex)).append(": ");
						if (worldChunk2 != null) {
							stringBuilder.append(worldChunk2.sampleHeightmap(typex, blockPos.getX(), blockPos.getZ()));
						} else {
							stringBuilder.append("??");
						}
					}
				}

				list.add(stringBuilder.toString());
				if (blockPos.getY() >= this.client.world.getBottomY() && blockPos.getY() < this.client.world.getTopY()) {
					list.add("Biome: " + getBiomeString(this.client.world.getBiome(blockPos)));
					if (worldChunk2 != null) {
						float h = world.getMoonSize();
						long l = worldChunk2.getInhabitedTime();
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
					} else {
						list.add("Local Difficulty: ??");
					}
				}

				if (worldChunk2 != null && worldChunk2.usesOldNoise()) {
					list.add("Blending: Old");
				}
			}

			ServerWorld serverWorld = this.getServerWorld();
			if (serverWorld != null) {
				ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
				ChunkGenerator chunkGenerator = serverChunkManager.getChunkGenerator();
				NoiseConfig noiseConfig = serverChunkManager.getNoiseConfig();
				chunkGenerator.getDebugHudText(list, noiseConfig, blockPos);
				MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler = noiseConfig.getMultiNoiseSampler();
				BiomeSource biomeSource = chunkGenerator.getBiomeSource();
				biomeSource.addDebugInfo(list, blockPos, multiNoiseSampler);
				SpawnHelper.Info info = serverChunkManager.getSpawnInfo();
				if (info != null) {
					Object2IntMap<SpawnGroup> object2IntMap = info.getGroupToCount();
					int m = info.getSpawningChunkCount();
					list.add(
						"SC: "
							+ m
							+ ", "
							+ (String)Stream.of(SpawnGroup.values())
								.map(group -> Character.toUpperCase(group.getName().charAt(0)) + ": " + object2IntMap.getInt(group))
								.collect(Collectors.joining(", "))
					);
				} else {
					list.add("SC: N/A");
				}
			}

			PostEffectProcessor postEffectProcessor = this.client.gameRenderer.getPostProcessor();
			if (postEffectProcessor != null) {
				list.add("Shader: " + postEffectProcessor.getName());
			}

			list.add(
				this.client.getSoundManager().getDebugString() + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.client.player.getMoodPercentage() * 100.0F))
			);
			return list;
		}
	}

	private static String getBiomeString(RegistryEntry<Biome> biome) {
		return biome.getKeyOrValue().map(biomeKey -> biomeKey.getValue().toString(), biome_ -> "[unregistered " + biome_ + "]");
	}

	@Nullable
	private ServerWorld getServerWorld() {
		IntegratedServer integratedServer = this.client.getServer();
		return integratedServer != null ? integratedServer.getWorld(this.client.world.getRegistryKey()) : null;
	}

	@Nullable
	private String getServerWorldDebugString() {
		ServerWorld serverWorld = this.getServerWorld();
		return serverWorld != null ? serverWorld.asString() : null;
	}

	private World getWorld() {
		return DataFixUtils.orElse(
			Optional.ofNullable(this.client.getServer()).flatMap(server -> Optional.ofNullable(server.getWorld(this.client.world.getRegistryKey()))), this.client.world
		);
	}

	@Nullable
	private WorldChunk getChunk() {
		if (this.chunkFuture == null) {
			ServerWorld serverWorld = this.getServerWorld();
			if (serverWorld == null) {
				return null;
			}

			this.chunkFuture = serverWorld.getChunkManager()
				.getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false)
				.thenApply(either -> either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
		}

		return (WorldChunk)this.chunkFuture.getNow(null);
	}

	private WorldChunk getClientChunk() {
		if (this.chunk == null) {
			this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
		}

		return this.chunk;
	}

	protected List<String> getRightText() {
		long l = Runtime.getRuntime().maxMemory();
		long m = Runtime.getRuntime().totalMemory();
		long n = Runtime.getRuntime().freeMemory();
		long o = m - n;
		List<String> list = Lists.<String>newArrayList(
			String.format(Locale.ROOT, "Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32),
			String.format(Locale.ROOT, "Mem: % 2d%% %03d/%03dMB", o * 100L / l, toMiB(o), toMiB(l)),
			String.format(Locale.ROOT, "Allocation rate: %03dMB /s", toMiB(this.allocationRateCalculator.get(o))),
			String.format(Locale.ROOT, "Allocated: % 2d%% %03dMB", m * 100L / l, toMiB(m)),
			"",
			String.format(Locale.ROOT, "CPU: %s", GlDebugInfo.getCpuInfo()),
			"",
			String.format(
				Locale.ROOT,
				"Display: %dx%d (%s)",
				MinecraftClient.getInstance().getWindow().getFramebufferWidth(),
				MinecraftClient.getInstance().getWindow().getFramebufferHeight(),
				GlDebugInfo.getVendor()
			),
			GlDebugInfo.getRenderer(),
			GlDebugInfo.getVersion()
		);
		if (this.client.hasReducedDebugInfo()) {
			return list;
		} else {
			if (this.blockHit.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult)this.blockHit).getBlockPos();
				BlockState blockState = this.client.world.getBlockState(blockPos);
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
				list.add(String.valueOf(Registries.BLOCK.getId(blockState.getBlock())));

				for (Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
					list.add(this.propertyToString(entry));
				}

				blockState.streamTags().map(tag -> "#" + tag.id()).forEach(list::add);
			}

			if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult)this.fluidHit).getBlockPos();
				FluidState fluidState = this.client.world.getFluidState(blockPos);
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
				list.add(String.valueOf(Registries.FLUID.getId(fluidState.getFluid())));

				for (Entry<Property<?>, Comparable<?>> entry : fluidState.getEntries().entrySet()) {
					list.add(this.propertyToString(entry));
				}

				fluidState.streamTags().map(tag -> "#" + tag.id()).forEach(list::add);
			}

			Entity entity = this.client.targetedEntity;
			if (entity != null) {
				list.add("");
				list.add(Formatting.UNDERLINE + "Targeted Entity");
				list.add(String.valueOf(Registries.ENTITY_TYPE.getId(entity.getType())));
			}

			return list;
		}
	}

	private String propertyToString(Entry<Property<?>, Comparable<?>> propEntry) {
		Property<?> property = (Property<?>)propEntry.getKey();
		Comparable<?> comparable = (Comparable<?>)propEntry.getValue();
		String string = Util.getValueAsString(property, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			string = Formatting.GREEN + string;
		} else if (Boolean.FALSE.equals(comparable)) {
			string = Formatting.RED + string;
		}

		return property.getName() + ": " + string;
	}

	private void drawMetricsData(DrawContext context, MetricsData metricsData, int x, int width, boolean showFps) {
		int i = metricsData.getStartIndex();
		int j = metricsData.getCurrentIndex();
		long[] ls = metricsData.getSamples();
		int l = x;
		int m = Math.max(0, ls.length - width);
		int n = ls.length - m;
		int k = metricsData.wrapIndex(i + m);
		long o = 0L;
		int p = Integer.MAX_VALUE;
		int q = Integer.MIN_VALUE;

		for (int r = 0; r < n; r++) {
			int s = (int)(ls[metricsData.wrapIndex(k + r)] / 1000000L);
			p = Math.min(p, s);
			q = Math.max(q, s);
			o += (long)s;
		}

		int r = context.getScaledWindowHeight();
		context.fill(RenderLayer.getGuiOverlay(), x, r - 60, x + n, r, -1873784752);

		while (k != j) {
			int s = metricsData.scaleSample(ls[k], showFps ? 30 : 60, showFps ? 60 : 20);
			int t = showFps ? 100 : 60;
			int u = this.getMetricsLineColor(MathHelper.clamp(s, 0, t), 0, t / 2, t);
			context.fill(RenderLayer.getGuiOverlay(), l, r - s, l + 1, r, u);
			l++;
			k = metricsData.wrapIndex(k + 1);
		}

		if (showFps) {
			context.fill(RenderLayer.getGuiOverlay(), x + 1, r - 30 + 1, x + 14, r - 30 + 10, -1873784752);
			context.drawText(this.textRenderer, "60 FPS", x + 2, r - 30 + 2, 14737632, false);
			context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + n - 1, r - 30, -1);
			context.fill(RenderLayer.getGuiOverlay(), x + 1, r - 60 + 1, x + 14, r - 60 + 10, -1873784752);
			context.drawText(this.textRenderer, "30 FPS", x + 2, r - 60 + 2, 14737632, false);
			context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + n - 1, r - 60, -1);
		} else {
			context.fill(RenderLayer.getGuiOverlay(), x + 1, r - 60 + 1, x + 14, r - 60 + 10, -1873784752);
			context.drawText(this.textRenderer, "20 TPS", x + 2, r - 60 + 2, 14737632, false);
			context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + n - 1, r - 60, -1);
		}

		context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + n - 1, r - 1, -1);
		context.drawVerticalLine(RenderLayer.getGuiOverlay(), x, r - 60, r, -1);
		context.drawVerticalLine(RenderLayer.getGuiOverlay(), x + n - 1, r - 60, r, -1);
		int s = this.client.options.getMaxFps().getValue();
		if (showFps && s > 0 && s <= 250) {
			context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + n - 1, r - 1 - (int)(1800.0 / (double)s), -16711681);
		}

		String string = p + " ms min";
		String string2 = o / (long)n + " ms avg";
		String string3 = q + " ms max";
		context.drawTextWithShadow(this.textRenderer, string, x + 2, r - 60 - 9, 14737632);
		context.drawCenteredTextWithShadow(this.textRenderer, string2, x + n / 2, r - 60 - 9, 14737632);
		context.drawTextWithShadow(this.textRenderer, string3, x + n - this.textRenderer.getWidth(string3), r - 60 - 9, 14737632);
	}

	private int getMetricsLineColor(int value, int greenValue, int yellowValue, int redValue) {
		return value < yellowValue
			? this.interpolateColor(-16711936, -256, (float)value / (float)yellowValue)
			: this.interpolateColor(-256, -65536, (float)(value - yellowValue) / (float)(redValue - yellowValue));
	}

	private int interpolateColor(int color1, int color2, float dt) {
		int i = color1 >> 24 & 0xFF;
		int j = color1 >> 16 & 0xFF;
		int k = color1 >> 8 & 0xFF;
		int l = color1 & 0xFF;
		int m = color2 >> 24 & 0xFF;
		int n = color2 >> 16 & 0xFF;
		int o = color2 >> 8 & 0xFF;
		int p = color2 & 0xFF;
		int q = MathHelper.clamp((int)MathHelper.lerp(dt, (float)i, (float)m), 0, 255);
		int r = MathHelper.clamp((int)MathHelper.lerp(dt, (float)j, (float)n), 0, 255);
		int s = MathHelper.clamp((int)MathHelper.lerp(dt, (float)k, (float)o), 0, 255);
		int t = MathHelper.clamp((int)MathHelper.lerp(dt, (float)l, (float)p), 0, 255);
		return q << 24 | r << 16 | s << 8 | t;
	}

	private static long toMiB(long bytes) {
		return bytes / 1024L / 1024L;
	}

	@Environment(EnvType.CLIENT)
	static class AllocationRateCalculator {
		private static final int INTERVAL = 500;
		private static final List<GarbageCollectorMXBean> GARBAGE_COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();
		private long lastCalculated = 0L;
		private long allocatedBytes = -1L;
		private long collectionCount = -1L;
		private long allocationRate = 0L;

		long get(long allocatedBytes) {
			long l = System.currentTimeMillis();
			if (l - this.lastCalculated < 500L) {
				return this.allocationRate;
			} else {
				long m = getCollectionCount();
				if (this.lastCalculated != 0L && m == this.collectionCount) {
					double d = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(l - this.lastCalculated);
					long n = allocatedBytes - this.allocatedBytes;
					this.allocationRate = Math.round((double)n * d);
				}

				this.lastCalculated = l;
				this.allocatedBytes = allocatedBytes;
				this.collectionCount = m;
				return this.allocationRate;
			}
		}

		private static long getCollectionCount() {
			long l = 0L;

			for (GarbageCollectorMXBean garbageCollectorMXBean : GARBAGE_COLLECTORS) {
				l += garbageCollectorMXBean.getCollectionCount();
			}

			return l;
		}
	}
}

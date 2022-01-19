/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DebugHud
extends DrawableHelper {
    private static final int TEXT_COLOR = 0xE0E0E0;
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
    private final TextRenderer textRenderer;
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos pos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;
    private static final int field_32191 = -65536;
    private static final int field_32192 = -256;
    private static final int field_32193 = -16711936;

    public DebugHud(MinecraftClient client) {
        this.client = client;
        this.textRenderer = client.textRenderer;
    }

    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }

    public void render(MatrixStack matrices) {
        this.client.getProfiler().push("debug");
        Entity entity = this.client.getCameraEntity();
        this.blockHit = entity.raycast(20.0, 0.0f, false);
        this.fluidHit = entity.raycast(20.0, 0.0f, true);
        this.renderLeftText(matrices);
        this.renderRightText(matrices);
        if (this.client.options.debugTpsEnabled) {
            int i = this.client.getWindow().getScaledWidth();
            this.drawMetricsData(matrices, this.client.getMetricsData(), 0, i / 2, true);
            IntegratedServer integratedServer = this.client.getServer();
            if (integratedServer != null) {
                this.drawMetricsData(matrices, integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
            }
        }
        this.client.getProfiler().pop();
    }

    protected void renderLeftText(MatrixStack matrices) {
        List<String> list = this.getLeftText();
        list.add("");
        boolean bl = this.client.getServer() != null;
        list.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + (bl ? " FPS + TPS" : " FPS") + " [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty(string)) continue;
            int j = this.textRenderer.fontHeight;
            int k = this.textRenderer.getWidth(string);
            int l = 2;
            int m = 2 + j * i;
            DebugHud.fill(matrices, 1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
            this.textRenderer.draw(matrices, string, 2.0f, (float)m, 0xE0E0E0);
        }
    }

    protected void renderRightText(MatrixStack matrices) {
        List<String> list = this.getRightText();
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty(string)) continue;
            int j = this.textRenderer.fontHeight;
            int k = this.textRenderer.getWidth(string);
            int l = this.client.getWindow().getScaledWidth() - 2 - k;
            int m = 2 + j * i;
            DebugHud.fill(matrices, l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
            this.textRenderer.draw(matrices, string, (float)l, (float)m, 0xE0E0E0);
        }
    }

    protected List<String> getLeftText() {
        ShaderEffect shaderEffect;
        World world;
        IntegratedServer integratedServer = this.client.getServer();
        ClientConnection clientConnection = this.client.getNetworkHandler().getConnection();
        float f = clientConnection.getAveragePacketsSent();
        float g = clientConnection.getAveragePacketsReceived();
        String string = integratedServer != null ? String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", Float.valueOf(integratedServer.getTickTime()), Float.valueOf(f), Float.valueOf(g)) : String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), Float.valueOf(f), Float.valueOf(g));
        BlockPos blockPos = this.client.getCameraEntity().getBlockPos();
        if (this.client.hasReducedDebugInfo()) {
            return Lists.newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.client.fpsDebugString, string, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.asString(), "", String.format("Chunk-relative: %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF));
        }
        Entity entity = this.client.getCameraEntity();
        Direction direction = entity.getHorizontalFacing();
        String string2 = switch (direction) {
            case Direction.NORTH -> "Towards negative Z";
            case Direction.SOUTH -> "Towards positive Z";
            case Direction.WEST -> "Towards negative X";
            case Direction.EAST -> "Towards positive X";
            default -> "Invalid";
        };
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!Objects.equals(this.pos, chunkPos)) {
            this.pos = chunkPos;
            this.resetChunk();
        }
        LongSets.EmptySet longSet = (world = this.getWorld()) instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList<String> list = Lists.newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + (String)("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")", this.client.fpsDebugString, string, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.asString());
        String string3 = this.getServerWorldDebugString();
        if (string3 != null) {
            list.add(string3);
        }
        list.add(this.client.world.getRegistryKey().getValue() + " FC: " + longSet.size());
        list.add("");
        list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()));
        list.add(String.format("Block: %d %d %d [%d %d %d]", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF));
        list.add(String.format("Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z, chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ(), chunkPos.getRegionX(), chunkPos.getRegionZ()));
        list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string2, Float.valueOf(MathHelper.wrapDegrees(entity.getYaw())), Float.valueOf(MathHelper.wrapDegrees(entity.getPitch()))));
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
                if (!type.shouldSendToClient()) continue;
                stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get(type)).append(": ").append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
            }
            list.add(stringBuilder.toString());
            stringBuilder.setLength(0);
            stringBuilder.append("SH");
            for (Heightmap.Type type : Heightmap.Type.values()) {
                if (!type.isStoredServerSide()) continue;
                stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get(type)).append(": ");
                if (worldChunk2 != null) {
                    stringBuilder.append(worldChunk2.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                    continue;
                }
                stringBuilder.append("??");
            }
            list.add(stringBuilder.toString());
            if (blockPos.getY() >= this.client.world.getBottomY() && blockPos.getY() < this.client.world.getTopY()) {
                list.add("Biome: " + this.client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(this.client.world.getBiome(blockPos)));
                long l = 0L;
                float h = 0.0f;
                if (worldChunk2 != null) {
                    h = world.getMoonSize();
                    l = worldChunk2.getInhabitedTime();
                }
                LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
                list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", Float.valueOf(localDifficulty.getLocalDifficulty()), Float.valueOf(localDifficulty.getClampedLocalDifficulty()), this.client.world.getTimeOfDay() / 24000L));
            }
        }
        ServerWorld serverWorld = this.getServerWorld();
        if (serverWorld != null) {
            ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
            ChunkGenerator chunkGenerator = serverChunkManager.getChunkGenerator();
            MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler = chunkGenerator.getMultiNoiseSampler();
            BiomeSource biomeSource = chunkGenerator.getBiomeSource();
            biomeSource.addDebugInfo(list, blockPos, multiNoiseSampler);
            SpawnHelper.Info info = serverChunkManager.getSpawnInfo();
            if (info != null) {
                Object2IntMap<SpawnGroup> object2IntMap = info.getGroupToCount();
                int m = info.getSpawningChunkCount();
                list.add("SC: " + m + ", " + Stream.of(SpawnGroup.values()).map(group -> Character.toUpperCase(group.getName().charAt(0)) + ": " + object2IntMap.getInt(group)).collect(Collectors.joining(", ")));
            } else {
                list.add("SC: N/A");
            }
        }
        if ((shaderEffect = this.client.gameRenderer.getShader()) != null) {
            list.add("Shader: " + shaderEffect.getName());
        }
        list.add(this.client.getSoundManager().getDebugString() + String.format(" (Mood %d%%)", Math.round(this.client.player.getMoodPercentage() * 100.0f)));
        return list;
    }

    @Nullable
    private ServerWorld getServerWorld() {
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer != null) {
            return integratedServer.getWorld(this.client.world.getRegistryKey());
        }
        return null;
    }

    @Nullable
    private String getServerWorldDebugString() {
        ServerWorld serverWorld = this.getServerWorld();
        if (serverWorld != null) {
            return serverWorld.asString();
        }
        return null;
    }

    private World getWorld() {
        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).flatMap(server -> Optional.ofNullable(server.getWorld(this.client.world.getRegistryKey()))), this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            ServerWorld serverWorld = this.getServerWorld();
            if (serverWorld != null) {
                this.chunkFuture = serverWorld.getChunkManager().getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false).thenApply(either -> either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
            }
            if (this.chunkFuture == null) {
                this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
            }
        }
        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null) {
            this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
        }
        return this.chunk;
    }

    protected List<String> getRightText() {
        Entity entity;
        BlockPos blockPos;
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        ArrayList<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", o * 100L / l, DebugHud.toMiB(o), DebugHud.toMiB(l)), String.format("Allocated: % 2d%% %03dMB", m * 100L / l, DebugHud.toMiB(m)), "", String.format("CPU: %s", GlDebugInfo.getCpuInfo()), "", String.format("Display: %dx%d (%s)", MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), GlDebugInfo.getVendor()), GlDebugInfo.getRenderer(), GlDebugInfo.getVersion());
        if (this.client.hasReducedDebugInfo()) {
            return list;
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.blockHit).getBlockPos();
            BlockState blockState = this.client.world.getBlockState(blockPos);
            list.add("");
            list.add(Formatting.UNDERLINE + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registry.BLOCK.getId(blockState.getBlock())));
            for (Map.Entry entry : blockState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier identifier : this.client.getNetworkHandler().getTagManager().getOrCreateTagGroup(Registry.BLOCK_KEY).getTagsFor(blockState.getBlock())) {
                list.add("#" + identifier);
            }
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.fluidHit).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(blockPos);
            list.add("");
            list.add(Formatting.UNDERLINE + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registry.FLUID.getId(fluidState.getFluid())));
            for (Map.Entry entry : fluidState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier identifier : this.client.getNetworkHandler().getTagManager().getOrCreateTagGroup(Registry.FLUID_KEY).getTagsFor(fluidState.getFluid())) {
                list.add("#" + identifier);
            }
        }
        if ((entity = this.client.targetedEntity) != null) {
            list.add("");
            list.add(Formatting.UNDERLINE + "Targeted Entity");
            list.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity.getType())));
        }
        return list;
    }

    private String propertyToString(Map.Entry<Property<?>, Comparable<?>> propEntry) {
        Property<?> property = propEntry.getKey();
        Comparable<?> comparable = propEntry.getValue();
        Object string = Util.getValueAsString(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = Formatting.GREEN + (String)string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = Formatting.RED + (String)string;
        }
        return property.getName() + ": " + (String)string;
    }

    private void drawMetricsData(MatrixStack matrices, MetricsData metricsData, int x, int width, boolean showFps) {
        int r;
        RenderSystem.disableDepthTest();
        int i = metricsData.getStartIndex();
        int j = metricsData.getCurrentIndex();
        long[] ls = metricsData.getSamples();
        int k = i;
        int l = x;
        int m = Math.max(0, ls.length - width);
        int n = ls.length - m;
        k = metricsData.wrapIndex(k + m);
        long o = 0L;
        int p = Integer.MAX_VALUE;
        int q = Integer.MIN_VALUE;
        for (r = 0; r < n; ++r) {
            int s = (int)(ls[metricsData.wrapIndex(k + r)] / 1000000L);
            p = Math.min(p, s);
            q = Math.max(q, s);
            o += (long)s;
        }
        r = this.client.getWindow().getScaledHeight();
        DebugHud.fill(matrices, x, r - 60, x + n, r, -1873784752);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = AffineTransformation.identity().getMatrix();
        while (k != j) {
            int t = metricsData.method_15248(ls[k], showFps ? 30 : 60, showFps ? 60 : 20);
            int u = showFps ? 100 : 60;
            int v = this.getMetricsLineColor(MathHelper.clamp(t, 0, u), 0, u / 2, u);
            int w = v >> 24 & 0xFF;
            int y = v >> 16 & 0xFF;
            int z = v >> 8 & 0xFF;
            int aa = v & 0xFF;
            bufferBuilder.vertex(matrix4f, l + 1, r, 0.0f).color(y, z, aa, w).next();
            bufferBuilder.vertex(matrix4f, l + 1, r - t + 1, 0.0f).color(y, z, aa, w).next();
            bufferBuilder.vertex(matrix4f, l, r - t + 1, 0.0f).color(y, z, aa, w).next();
            bufferBuilder.vertex(matrix4f, l, r, 0.0f).color(y, z, aa, w).next();
            ++l;
            k = metricsData.wrapIndex(k + 1);
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        if (showFps) {
            DebugHud.fill(matrices, x + 1, r - 30 + 1, x + 14, r - 30 + 10, -1873784752);
            this.textRenderer.draw(matrices, "60 FPS", (float)(x + 2), (float)(r - 30 + 2), 0xE0E0E0);
            this.drawHorizontalLine(matrices, x, x + n - 1, r - 30, -1);
            DebugHud.fill(matrices, x + 1, r - 60 + 1, x + 14, r - 60 + 10, -1873784752);
            this.textRenderer.draw(matrices, "30 FPS", (float)(x + 2), (float)(r - 60 + 2), 0xE0E0E0);
            this.drawHorizontalLine(matrices, x, x + n - 1, r - 60, -1);
        } else {
            DebugHud.fill(matrices, x + 1, r - 60 + 1, x + 14, r - 60 + 10, -1873784752);
            this.textRenderer.draw(matrices, "20 TPS", (float)(x + 2), (float)(r - 60 + 2), 0xE0E0E0);
            this.drawHorizontalLine(matrices, x, x + n - 1, r - 60, -1);
        }
        this.drawHorizontalLine(matrices, x, x + n - 1, r - 1, -1);
        this.drawVerticalLine(matrices, x, r - 60, r, -1);
        this.drawVerticalLine(matrices, x + n - 1, r - 60, r, -1);
        if (showFps && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
            this.drawHorizontalLine(matrices, x, x + n - 1, r - 1 - (int)(1800.0 / (double)this.client.options.maxFps), -16711681);
        }
        String string = p + " ms min";
        String string2 = o / (long)n + " ms avg";
        String string3 = q + " ms max";
        this.textRenderer.drawWithShadow(matrices, string, (float)(x + 2), (float)(r - 60 - this.textRenderer.fontHeight), 0xE0E0E0);
        this.textRenderer.drawWithShadow(matrices, string2, (float)(x + n / 2 - this.textRenderer.getWidth(string2) / 2), (float)(r - 60 - this.textRenderer.fontHeight), 0xE0E0E0);
        this.textRenderer.drawWithShadow(matrices, string3, (float)(x + n - this.textRenderer.getWidth(string3)), (float)(r - 60 - this.textRenderer.fontHeight), 0xE0E0E0);
        RenderSystem.enableDepthTest();
    }

    private int getMetricsLineColor(int value, int greenValue, int yellowValue, int redValue) {
        if (value < yellowValue) {
            return this.interpolateColor(-16711936, -256, (float)value / (float)yellowValue);
        }
        return this.interpolateColor(-256, -65536, (float)(value - yellowValue) / (float)(redValue - yellowValue));
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
        int q = MathHelper.clamp((int)MathHelper.lerp(dt, i, m), 0, 255);
        int r = MathHelper.clamp((int)MathHelper.lerp(dt, j, n), 0, 255);
        int s = MathHelper.clamp((int)MathHelper.lerp(dt, k, o), 0, 255);
        int t = MathHelper.clamp((int)MathHelper.lerp(dt, l, p), 0, 255);
        return q << 24 | r << 16 | s << 8 | t;
    }

    private static long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }
}


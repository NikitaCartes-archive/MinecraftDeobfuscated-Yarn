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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DebugHud
extends DrawableHelper {
    private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Util.make(new EnumMap(Heightmap.Type.class), enumMap -> {
        enumMap.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
        enumMap.put(Heightmap.Type.WORLD_SURFACE, "S");
        enumMap.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
        enumMap.put(Heightmap.Type.OCEAN_FLOOR, "O");
        enumMap.put(Heightmap.Type.MOTION_BLOCKING, "M");
        enumMap.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
    });
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
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
        this.fontRenderer = minecraftClient.textRenderer;
    }

    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }

    public void render() {
        this.client.getProfiler().push("debug");
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, -100.0f);
        RenderSystem.scalef(1.0f, 1.0f, -1.0f);
        Entity entity = this.client.getCameraEntity();
        this.blockHit = entity.rayTrace(20.0, 0.0f, false);
        this.fluidHit = entity.rayTrace(20.0, 0.0f, true);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        Matrix4f matrix4f = Rotation3.identity().getMatrix();
        this.renderLeftText(matrix4f, immediate);
        this.renderRightText(matrix4f, immediate);
        if (this.client.options.debugTpsEnabled) {
            int i = this.client.getWindow().getScaledWidth();
            this.drawMetricsData(matrix4f, immediate, this.client.getMetricsData(), 0, i / 2, true);
            IntegratedServer integratedServer = this.client.getServer();
            if (integratedServer != null) {
                this.drawMetricsData(matrix4f, immediate, integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
            }
        }
        immediate.draw();
        RenderSystem.popMatrix();
        this.client.getProfiler().pop();
    }

    protected void renderLeftText(Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
        List<String> list = this.getLeftText();
        list.add("");
        boolean bl = this.client.getServer() != null;
        list.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + (bl ? " FPS + TPS" : " FPS") + " [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty(string)) continue;
            int j = this.fontRenderer.fontHeight + 1;
            int k = 2 + j * i;
            this.fontRenderer.draw(string, 2.0f, k, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
        }
    }

    protected void renderRightText(Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
        List<String> list = this.getRightText();
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty(string)) continue;
            int j = this.fontRenderer.fontHeight + 1;
            int k = this.fontRenderer.getStringWidth(string);
            int l = this.client.getWindow().getScaledWidth() - 2 - k;
            int m = 2 + j * i;
            this.fontRenderer.draw(string, l, m, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
        }
    }

    protected List<String> getLeftText() {
        World world;
        String string2;
        IntegratedServer integratedServer = this.client.getServer();
        ClientConnection clientConnection = this.client.getNetworkHandler().getConnection();
        float f = clientConnection.getAveragePacketsSent();
        float g = clientConnection.getAveragePacketsReceived();
        String string = integratedServer != null ? String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", Float.valueOf(integratedServer.getTickTime()), Float.valueOf(f), Float.valueOf(g)) : String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), Float.valueOf(f), Float.valueOf(g));
        BlockPos blockPos = new BlockPos(this.client.getCameraEntity());
        if (this.client.hasReducedDebugInfo()) {
            return Lists.newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.client.fpsDebugString, string, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getDebugString(), "", String.format("Chunk-relative: %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF));
        }
        Entity entity = this.client.getCameraEntity();
        Direction direction = entity.getHorizontalFacing();
        switch (direction) {
            case NORTH: {
                string2 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                string2 = "Towards positive Z";
                break;
            }
            case WEST: {
                string2 = "Towards negative X";
                break;
            }
            case EAST: {
                string2 = "Towards positive X";
                break;
            }
            default: {
                string2 = "Invalid";
            }
        }
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!Objects.equals(this.pos, chunkPos)) {
            this.pos = chunkPos;
            this.resetChunk();
        }
        LongSets.EmptySet longSet = (world = this.getWorld()) instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList<String> list = Lists.newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")", this.client.fpsDebugString, string, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getDebugString());
        String string3 = this.getServerWorldDebugString();
        if (string3 != null) {
            list.add(string3);
        }
        list.add(DimensionType.getId(this.client.world.dimension.getType()).toString() + " FC: " + Integer.toString(longSet.size()));
        list.add("");
        list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()));
        list.add(String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        list.add(String.format("Chunk: %d %d %d in %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4));
        list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string2, Float.valueOf(MathHelper.wrapDegrees(entity.yaw)), Float.valueOf(MathHelper.wrapDegrees(entity.pitch))));
        if (this.client.world != null) {
            if (this.client.world.isChunkLoaded(blockPos)) {
                WorldChunk worldChunk = this.getClientChunk();
                if (worldChunk.isEmpty()) {
                    list.add("Waiting for chunk...");
                } else {
                    int i = this.client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
                    int j = this.client.world.getLightLevel(LightType.SKY, blockPos);
                    int k = this.client.world.getLightLevel(LightType.BLOCK, blockPos);
                    list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
                    WorldChunk worldChunk2 = this.getChunk();
                    if (worldChunk2 != null) {
                        LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
                        list.add("Server Light: (" + lightingProvider.get(LightType.SKY).getLightLevel(blockPos) + " sky, " + lightingProvider.get(LightType.BLOCK).getLightLevel(blockPos) + " block)");
                    } else {
                        list.add("Server Light: (?? sky, ?? block)");
                    }
                    StringBuilder stringBuilder = new StringBuilder("CH");
                    for (Heightmap.Type type : Heightmap.Type.values()) {
                        if (!type.shouldSendToClient()) continue;
                        stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get((Object)type)).append(": ").append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                    }
                    list.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    stringBuilder.append("SH");
                    for (Heightmap.Type type : Heightmap.Type.values()) {
                        if (!type.isStoredServerSide()) continue;
                        stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get((Object)type)).append(": ");
                        if (worldChunk2 != null) {
                            stringBuilder.append(worldChunk2.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                            continue;
                        }
                        stringBuilder.append("??");
                    }
                    list.add(stringBuilder.toString());
                    if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
                        list.add("Biome: " + Registry.BIOME.getId(this.client.world.getBiome(blockPos)));
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
            } else {
                list.add("Outside of world...");
            }
        } else {
            list.add("Outside of world...");
        }
        ShaderEffect shaderEffect = this.client.gameRenderer.getShader();
        if (shaderEffect != null) {
            list.add("Shader: " + shaderEffect.getName());
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos2 = ((BlockHitResult)this.blockHit).getBlockPos();
            list.add(String.format("Looking at block: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos2 = ((BlockHitResult)this.fluidHit).getBlockPos();
            list.add(String.format("Looking at liquid: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
        }
        list.add(this.client.getSoundManager().getDebugString());
        return list;
    }

    @Nullable
    private String getServerWorldDebugString() {
        ServerWorld serverWorld;
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer != null && (serverWorld = integratedServer.getWorld(this.client.world.getDimension().getType())) != null) {
            return serverWorld.getDebugString();
        }
        return null;
    }

    private World getWorld() {
        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map(integratedServer -> integratedServer.getWorld(this.client.world.dimension.getType())), this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            ServerWorld serverWorld;
            IntegratedServer integratedServer = this.client.getServer();
            if (integratedServer != null && (serverWorld = integratedServer.getWorld(this.client.world.dimension.getType())) != null) {
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
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Block");
            list.add(String.valueOf(Registry.BLOCK.getId(blockState.getBlock())));
            for (Map.Entry entry : blockState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier identifier : this.client.getNetworkHandler().getTagManager().blocks().getTagsFor(blockState.getBlock())) {
                list.add("#" + identifier);
            }
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.fluidHit).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(blockPos);
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Fluid");
            list.add(String.valueOf(Registry.FLUID.getId(fluidState.getFluid())));
            for (Map.Entry entry : fluidState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier identifier : this.client.getNetworkHandler().getTagManager().fluids().getTagsFor(fluidState.getFluid())) {
                list.add("#" + identifier);
            }
        }
        if ((entity = this.client.targetedEntity) != null) {
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity.getType())));
        }
        return list;
    }

    private String propertyToString(Map.Entry<Property<?>, Comparable<?>> entry) {
        Property<?> property = entry.getKey();
        Comparable<?> comparable = entry.getValue();
        String string = Util.getValueAsString(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = (Object)((Object)Formatting.GREEN) + string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = (Object)((Object)Formatting.RED) + string;
        }
        return property.getName() + ": " + string;
    }

    private void drawMetricsData(Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate, MetricsData metricsData, int i, int j, boolean bl) {
        int y;
        int t;
        int k = metricsData.getStartIndex();
        int l = metricsData.getCurrentIndex();
        long[] ls = metricsData.getSamples();
        int m = k;
        int n = i;
        int o = Math.max(0, ls.length - j);
        int p = ls.length - o;
        m = metricsData.wrapIndex(m + o);
        long q = 0L;
        int r = Integer.MAX_VALUE;
        int s = Integer.MIN_VALUE;
        for (t = 0; t < p; ++t) {
            int u = (int)(ls[metricsData.wrapIndex(m + t)] / 1000000L);
            r = Math.min(r, u);
            s = Math.max(s, u);
            q += (long)u;
        }
        t = this.client.getWindow().getScaledHeight();
        Matrix4f matrix4f2 = matrix4f.copy();
        matrix4f2.multiply(Matrix4f.method_24021(0.0f, 0.0f, 100.0f));
        DebugHud.fill(matrix4f2, i, t - 60, i + p, t, -1873784752);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        while (m != l) {
            int v = metricsData.method_15248(ls[m], bl ? 30 : 60, bl ? 60 : 20);
            int w = bl ? 100 : 60;
            int x = this.getMetricsLineColor(MathHelper.clamp(v, 0, w), 0, w / 2, w);
            y = x >> 24 & 0xFF;
            int z = x >> 16 & 0xFF;
            int aa = x >> 8 & 0xFF;
            int ab = x & 0xFF;
            bufferBuilder.vertex(matrix4f, n + 1, t, 0.0f).color(z, aa, ab, y).next();
            bufferBuilder.vertex(matrix4f, n, t, 0.0f).color(z, aa, ab, y).next();
            bufferBuilder.vertex(matrix4f, n, t - v + 1, 0.0f).color(z, aa, ab, y).next();
            bufferBuilder.vertex(matrix4f, n + 1, t - v + 1, 0.0f).color(z, aa, ab, y).next();
            ++n;
            m = metricsData.wrapIndex(m + 1);
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        if (bl) {
            this.fontRenderer.draw("60 FPS", i + 2, t - 30 + 2, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
            this.hLine(i, i + p - 1, t - 30, -1);
            this.fontRenderer.draw("30 FPS", i + 2, t - 60 + 2, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
            this.hLine(i, i + p - 1, t - 60, -1);
        } else {
            this.fontRenderer.draw("20 TPS", i + 2, t - 60 + 2, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
            this.hLine(i, i + p - 1, t - 60, -1);
        }
        this.hLine(i, i + p - 1, t - 1, -1);
        this.vLine(i, t - 60, t, -1);
        this.vLine(i + p - 1, t - 60, t, -1);
        if (bl && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
            this.hLine(i, i + p - 1, t - 1 - (int)(1800.0 / (double)this.client.options.maxFps), -16711681);
        }
        String string = r + " ms min";
        String string2 = q / (long)p + " ms avg";
        String string3 = s + " ms max";
        y = t - 60 - this.fontRenderer.fontHeight;
        this.fontRenderer.draw(string, i + 2, y, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
        this.fontRenderer.draw(string2, i + p / 2 - this.fontRenderer.getStringWidth(string2) / 2, y, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
        this.fontRenderer.draw(string3, i + p - this.fontRenderer.getStringWidth(string3), y, 0xE0E0E0, false, matrix4f, immediate, false, -1873784752, 0xF000F0);
    }

    private int getMetricsLineColor(int i, int j, int k, int l) {
        if (i < k) {
            return this.interpolateColor(-16711936, -256, (float)i / (float)k);
        }
        return this.interpolateColor(-256, -65536, (float)(i - k) / (float)(l - k));
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
        int s = MathHelper.clamp((int)MathHelper.lerp(f, k, o), 0, 255);
        int t = MathHelper.clamp((int)MathHelper.lerp(f, l, p), 0, 255);
        int u = MathHelper.clamp((int)MathHelper.lerp(f, m, q), 0, 255);
        int v = MathHelper.clamp((int)MathHelper.lerp(f, n, r), 0, 255);
        return s << 24 | t << 16 | u << 8 | v;
    }

    private static long toMiB(long l) {
        return l / 1024L / 1024L;
    }
}


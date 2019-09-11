/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.render.debug.CaveDebugRenderer;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.VoxelDebugRenderer;
import net.minecraft.client.render.debug.WaterDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DebugRenderer {
    public final PathfindingDebugRenderer pathfindingDebugRenderer;
    public final Renderer waterDebugRenderer;
    public final Renderer chunkBorderDebugRenderer;
    public final Renderer heightmapDebugRenderer;
    public final Renderer voxelDebugRenderer;
    public final Renderer neighborUpdateDebugRenderer;
    public final CaveDebugRenderer caveDebugRenderer;
    public final StructureDebugRenderer structureDebugRenderer;
    public final Renderer skyLightDebugRenderer;
    public final Renderer worldGenAttemptDebugRenderer;
    public final Renderer blockOutlineDebugRenderer;
    public final Renderer chunkLoadingDebugRenderer;
    public final PointOfInterestDebugRenderer pointsOfInterestDebugRenderer;
    public final RaidCenterDebugRenderer raidCenterDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    public final GameTestDebugRenderer gameTestDebugRenderer;
    private boolean showChunkBorder;

    public DebugRenderer(MinecraftClient minecraftClient) {
        this.pathfindingDebugRenderer = new PathfindingDebugRenderer(minecraftClient);
        this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
        this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
        this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
        this.voxelDebugRenderer = new VoxelDebugRenderer(minecraftClient);
        this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
        this.caveDebugRenderer = new CaveDebugRenderer(minecraftClient);
        this.structureDebugRenderer = new StructureDebugRenderer(minecraftClient);
        this.skyLightDebugRenderer = new SkyLightDebugRenderer(minecraftClient);
        this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer(minecraftClient);
        this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(minecraftClient);
        this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(minecraftClient);
        this.pointsOfInterestDebugRenderer = new PointOfInterestDebugRenderer(minecraftClient);
        this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(minecraftClient);
        this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(minecraftClient);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
    }

    public void reset() {
        this.pathfindingDebugRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderDebugRenderer.clear();
        this.heightmapDebugRenderer.clear();
        this.voxelDebugRenderer.clear();
        this.neighborUpdateDebugRenderer.clear();
        this.caveDebugRenderer.clear();
        this.structureDebugRenderer.clear();
        this.skyLightDebugRenderer.clear();
        this.worldGenAttemptDebugRenderer.clear();
        this.blockOutlineDebugRenderer.clear();
        this.chunkLoadingDebugRenderer.clear();
        this.pointsOfInterestDebugRenderer.clear();
        this.raidCenterDebugRenderer.clear();
        this.goalSelectorDebugRenderer.clear();
        this.gameTestDebugRenderer.clear();
    }

    public boolean toggleShowChunkBorder() {
        this.showChunkBorder = !this.showChunkBorder;
        return this.showChunkBorder;
    }

    public void render(long l) {
        if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            this.chunkBorderDebugRenderer.render(l);
        }
        this.gameTestDebugRenderer.render(l);
    }

    public static Optional<Entity> method_19694(@Nullable Entity entity2, int i) {
        int j;
        Predicate<Entity> predicate;
        Box box;
        Vec3d vec3d2;
        Vec3d vec3d3;
        if (entity2 == null) {
            return Optional.empty();
        }
        Vec3d vec3d = entity2.getCameraPosVec(1.0f);
        EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity2, vec3d, vec3d3 = vec3d.add(vec3d2 = entity2.getRotationVec(1.0f).multiply(i)), box = entity2.getBoundingBox().stretch(vec3d2).expand(1.0), predicate = entity -> !entity.isSpectator() && entity.collides(), j = i * i);
        if (entityHitResult == null) {
            return Optional.empty();
        }
        if (vec3d.squaredDistanceTo(entityHitResult.getPos()) > (double)j) {
            return Optional.empty();
        }
        return Optional.of(entityHitResult.getEntity());
    }

    public static void method_19697(BlockPos blockPos, BlockPos blockPos2, float f, float g, float h, float i) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos().negate();
        Box box = new Box(blockPos, blockPos2).offset(vec3d);
        DebugRenderer.method_19695(box, f, g, h, i);
    }

    public static void method_19696(BlockPos blockPos, float f, float g, float h, float i, float j) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos().negate();
        Box box = new Box(blockPos).offset(vec3d).expand(f);
        DebugRenderer.method_19695(box, g, h, i, j);
    }

    public static void method_19695(Box box, float f, float g, float h, float i) {
        DebugRenderer.method_19692(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, f, g, h, i);
    }

    public static void method_19692(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        WorldRenderer.buildBox(bufferBuilder, d, e, f, g, h, i, j, k, l, m);
        tessellator.draw();
    }

    public static void drawBlockFloatingText(String string, int i, int j, int k, int l) {
        DebugRenderer.drawFloatingText(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, l);
    }

    public static void drawFloatingText(String string, double d, double e, double f, int i) {
        DebugRenderer.drawFloatingText(string, d, e, f, i, 0.02f);
    }

    public static void drawFloatingText(String string, double d, double e, double f, int i, float g) {
        DebugRenderer.drawFloatingText(string, d, e, f, i, g, true, 0.0f, false);
    }

    public static void drawFloatingText(String string, double d, double e, double f, int i, float g, boolean bl, float h, boolean bl2) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Camera camera = minecraftClient.gameRenderer.getCamera();
        if (!camera.isReady() || minecraftClient.getEntityRenderManager().gameOptions == null) {
            return;
        }
        TextRenderer textRenderer = minecraftClient.textRenderer;
        double j = camera.getPos().x;
        double k = camera.getPos().y;
        double l = camera.getPos().z;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)(d - j), (float)(e - k) + 0.07f, (float)(f - l));
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.scalef(g, -g, g);
        EntityRenderDispatcher entityRenderDispatcher = minecraftClient.getEntityRenderManager();
        RenderSystem.rotatef(-entityRenderDispatcher.cameraYaw, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(-entityRenderDispatcher.cameraPitch, 1.0f, 0.0f, 0.0f);
        RenderSystem.enableTexture();
        if (bl2) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(true);
        RenderSystem.scalef(-1.0f, 1.0f, 1.0f);
        float m = bl ? (float)(-textRenderer.getStringWidth(string)) / 2.0f : 0.0f;
        textRenderer.draw(string, m -= h / g, 0.0f, i);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Renderer {
        public void render(long var1);

        default public void clear() {
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.render.debug.CollisionDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
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
    public final Renderer collisionDebugRenderer;
    public final Renderer neighborUpdateDebugRenderer;
    public final CaveDebugRenderer caveDebugRenderer;
    public final StructureDebugRenderer structureDebugRenderer;
    public final Renderer skyLightDebugRenderer;
    public final Renderer worldGenAttemptDebugRenderer;
    public final Renderer blockOutlineDebugRenderer;
    public final Renderer chunkLoadingDebugRenderer;
    public final VillageDebugRenderer villageDebugRenderer;
    public final RaidCenterDebugRenderer raidCenterDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    private boolean showChunkBorder;

    public DebugRenderer(MinecraftClient minecraftClient) {
        this.pathfindingDebugRenderer = new PathfindingDebugRenderer(minecraftClient);
        this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
        this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
        this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
        this.collisionDebugRenderer = new CollisionDebugRenderer(minecraftClient);
        this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
        this.caveDebugRenderer = new CaveDebugRenderer(minecraftClient);
        this.structureDebugRenderer = new StructureDebugRenderer(minecraftClient);
        this.skyLightDebugRenderer = new SkyLightDebugRenderer(minecraftClient);
        this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer(minecraftClient);
        this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(minecraftClient);
        this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(minecraftClient);
        this.villageDebugRenderer = new VillageDebugRenderer(minecraftClient);
        this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(minecraftClient);
        this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(minecraftClient);
    }

    public void method_20413() {
        this.pathfindingDebugRenderer.method_20414();
        this.waterDebugRenderer.method_20414();
        this.chunkBorderDebugRenderer.method_20414();
        this.heightmapDebugRenderer.method_20414();
        this.collisionDebugRenderer.method_20414();
        this.neighborUpdateDebugRenderer.method_20414();
        this.caveDebugRenderer.method_20414();
        this.structureDebugRenderer.method_20414();
        this.skyLightDebugRenderer.method_20414();
        this.worldGenAttemptDebugRenderer.method_20414();
        this.blockOutlineDebugRenderer.method_20414();
        this.chunkLoadingDebugRenderer.method_20414();
        this.villageDebugRenderer.method_20414();
        this.raidCenterDebugRenderer.method_20414();
        this.goalSelectorDebugRenderer.method_20414();
    }

    public boolean shouldRender() {
        return this.showChunkBorder;
    }

    public boolean toggleShowChunkBorder() {
        this.showChunkBorder = !this.showChunkBorder;
        return this.showChunkBorder;
    }

    public void renderDebuggers(long l) {
        if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            this.chunkBorderDebugRenderer.render(l);
        }
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
        DebugRenderer.method_19692(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2, f, g, h, i);
    }

    public static void method_19692(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        WorldRenderer.buildBox(bufferBuilder, d, e, f, g, h, i, j, k, l, m);
        tessellator.draw();
    }

    public static void method_3711(String string, int i, int j, int k, int l) {
        DebugRenderer.method_3714(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, l);
    }

    public static void method_3714(String string, double d, double e, double f, int i) {
        DebugRenderer.method_19429(string, d, e, f, i, 0.02f);
    }

    public static void method_19429(String string, double d, double e, double f, int i, float g) {
        DebugRenderer.method_3712(string, d, e, f, i, g, true, 0.0f, false);
    }

    public static void method_3712(String string, double d, double e, double f, int i, float g, boolean bl, float h, boolean bl2) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Camera camera = minecraftClient.gameRenderer.getCamera();
        if (!camera.isReady() || minecraftClient.getEntityRenderManager().gameOptions == null) {
            return;
        }
        TextRenderer textRenderer = minecraftClient.textRenderer;
        double j = camera.getPos().x;
        double k = camera.getPos().y;
        double l = camera.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(d - j), (float)(e - k) + 0.07f, (float)(f - l));
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(g, -g, g);
        EntityRenderDispatcher entityRenderDispatcher = minecraftClient.getEntityRenderManager();
        GlStateManager.rotatef(-entityRenderDispatcher.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-entityRenderDispatcher.cameraPitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.enableTexture();
        if (bl2) {
            GlStateManager.disableDepthTest();
        } else {
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        GlStateManager.scalef(-1.0f, 1.0f, 1.0f);
        float m = bl ? (float)(-textRenderer.getStringWidth(string)) / 2.0f : 0.0f;
        textRenderer.draw(string, m -= h / g, 0.0f, i);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Renderer {
        public void render(long var1);

        default public void method_20414() {
        }
    }
}


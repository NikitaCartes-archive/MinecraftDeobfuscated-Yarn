/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameEventDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private static final int field_32899 = 32;
    private static final float field_32900 = 1.0f;
    private final List<Entry> entries = Lists.newArrayList();
    private final List<Listener> listeners = Lists.newArrayList();

    public GameEventDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        ClientWorld world = this.client.world;
        if (world == null) {
            this.entries.clear();
            this.listeners.clear();
            return;
        }
        Vec3d vec3d2 = new Vec3d(cameraX, 0.0, cameraZ);
        this.entries.removeIf(Entry::hasExpired);
        this.listeners.removeIf(listener -> listener.isTooFar(world, vec3d2));
        RenderSystem.disableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(vec3d -> {
                double g = vec3d.getX() - (double)listener2.getRange();
                double h = vec3d.getY() - (double)listener2.getRange();
                double i = vec3d.getZ() - (double)listener2.getRange();
                double j = vec3d.getX() + (double)listener2.getRange();
                double k = vec3d.getY() + (double)listener2.getRange();
                double l = vec3d.getZ() + (double)listener2.getRange();
                Vec3f vec3f = new Vec3f(1.0f, 1.0f, 0.0f);
                WorldRenderer.method_22983(matrices, vertexConsumer, VoxelShapes.cuboid(new Box(g, h, i, j, k, l)), -cameraX, -cameraY, -cameraZ, vec3f.getX(), vec3f.getY(), vec3f.getZ(), 0.35f);
            });
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(vec3d -> {
                Vec3f vec3f = new Vec3f(1.0f, 1.0f, 0.0f);
                WorldRenderer.drawBox(bufferBuilder, vec3d.getX() - 0.25 - cameraX, vec3d.getY() - cameraY, vec3d.getZ() - 0.25 - cameraZ, vec3d.getX() + 0.25 - cameraX, vec3d.getY() - cameraY + 1.0, vec3d.getZ() + 0.25 - cameraZ, vec3f.getX(), vec3f.getY(), vec3f.getZ(), 0.35f);
            });
        }
        tessellator.draw();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.depthMask(false);
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(vec3d -> {
                DebugRenderer.drawString("Listener Origin", vec3d.getX(), vec3d.getY() + (double)1.8f, vec3d.getZ(), -1, 0.025f);
                DebugRenderer.drawString(new BlockPos((Vec3d)vec3d).toString(), vec3d.getX(), vec3d.getY() + 1.5, vec3d.getZ(), -6959665, 0.025f);
            });
        }
        for (Entry entry : this.entries) {
            Vec3d vec3d22 = entry.pos;
            double d = 0.2f;
            double e = vec3d22.x - (double)0.2f;
            double f = vec3d22.y - (double)0.2f;
            double g = vec3d22.z - (double)0.2f;
            double h = vec3d22.x + (double)0.2f;
            double i = vec3d22.y + (double)0.2f + 0.5;
            double j = vec3d22.z + (double)0.2f;
            GameEventDebugRenderer.drawBoxIfCameraReady(new Box(e, f, g, h, i, j), 1.0f, 1.0f, 1.0f, 0.2f);
            DebugRenderer.drawString(entry.event.getId(), vec3d22.x, vec3d22.y + (double)0.85f, vec3d22.z, -7564911, 0.0075f);
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static void drawBoxIfCameraReady(Box box, float red, float green, float blue, float alpha) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3d vec3d = camera.getPos().negate();
        DebugRenderer.drawBox(box.offset(vec3d), red, green, blue, alpha);
    }

    public void addEvent(GameEvent event, Vec3d vec3d) {
        this.entries.add(new Entry(Util.getMeasuringTimeMs(), event, vec3d));
    }

    public void addListener(PositionSource positionSource, int range) {
        this.listeners.add(new Listener(positionSource, range));
    }

    @Environment(value=EnvType.CLIENT)
    static class Listener
    implements GameEventListener {
        public final PositionSource positionSource;
        public final int range;

        public Listener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public boolean isTooFar(World world, Vec3d vec3d) {
            return this.positionSource.getPos(world).filter(vec3d2 -> vec3d2.squaredDistanceTo(vec3d) <= 1024.0).isPresent();
        }

        public Optional<Vec3d> getPos(World world) {
            return this.positionSource.getPos(world);
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public int getRange() {
            return this.range;
        }

        @Override
        public boolean listen(ServerWorld world, GameEvent event, @Nullable Entity entity, Vec3d pos) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Entry(long startingMs, GameEvent event, Vec3d pos) {
        public boolean hasExpired() {
            return Util.getMeasuringTimeMs() - this.startingMs > 3000L;
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

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
        Vec3d vec3d = new Vec3d(cameraX, 0.0, cameraZ);
        this.entries.removeIf(Entry::hasExpired);
        this.listeners.removeIf(listener -> listener.isTooFar(world, vec3d));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(pos -> {
                double g = pos.getX() - (double)listener2.getRange();
                double h = pos.getY() - (double)listener2.getRange();
                double i = pos.getZ() - (double)listener2.getRange();
                double j = pos.getX() + (double)listener2.getRange();
                double k = pos.getY() + (double)listener2.getRange();
                double l = pos.getZ() + (double)listener2.getRange();
                WorldRenderer.drawShapeOutline(matrices, vertexConsumer, VoxelShapes.cuboid(new Box(g, h, i, j, k, l)), -cameraX, -cameraY, -cameraZ, 1.0f, 1.0f, 0.0f, 0.35f);
            });
        }
        VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(pos -> WorldRenderer.method_3258(matrices, vertexConsumer2, pos.getX() - 0.25 - cameraX, pos.getY() - cameraY, pos.getZ() - 0.25 - cameraZ, pos.getX() + 0.25 - cameraX, pos.getY() - cameraY + 1.0, pos.getZ() + 0.25 - cameraZ, 1.0f, 1.0f, 0.0f, 0.35f));
        }
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(pos -> {
                DebugRenderer.drawString(matrices, vertexConsumers, "Listener Origin", pos.getX(), pos.getY() + (double)1.8f, pos.getZ(), -1, 0.025f);
                DebugRenderer.drawString(matrices, vertexConsumers, BlockPos.ofFloored(pos).toString(), pos.getX(), pos.getY() + 1.5, pos.getZ(), -6959665, 0.025f);
            });
        }
        for (Entry entry : this.entries) {
            Vec3d vec3d2 = entry.pos;
            double d = 0.2f;
            double e = vec3d2.x - (double)0.2f;
            double f = vec3d2.y - (double)0.2f;
            double g = vec3d2.z - (double)0.2f;
            double h = vec3d2.x + (double)0.2f;
            double i = vec3d2.y + (double)0.2f + 0.5;
            double j = vec3d2.z + (double)0.2f;
            GameEventDebugRenderer.drawBoxIfCameraReady(matrices, vertexConsumers, new Box(e, f, g, h, i, j), 1.0f, 1.0f, 1.0f, 0.2f);
            DebugRenderer.drawString(matrices, vertexConsumers, entry.event.getId(), vec3d2.x, vec3d2.y + (double)0.85f, vec3d2.z, -7564911, 0.0075f);
        }
    }

    private static void drawBoxIfCameraReady(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float red, float green, float blue, float alpha) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        Vec3d vec3d = camera.getPos().negate();
        DebugRenderer.drawBox(matrices, vertexConsumers, box.offset(vec3d), red, green, blue, alpha);
    }

    public void addEvent(GameEvent event, Vec3d pos) {
        this.entries.add(new Entry(Util.getMeasuringTimeMs(), event, pos));
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

        public boolean isTooFar(World world, Vec3d pos) {
            return this.positionSource.getPos(world).filter(pos2 -> pos2.squaredDistanceTo(pos) <= 1024.0).isPresent();
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
        public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
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


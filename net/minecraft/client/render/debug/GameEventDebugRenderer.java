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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
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
        BlockPos blockPos2 = new BlockPos(cameraX, 0.0, cameraZ);
        this.entries.removeIf(Entry::hasExpired);
        this.listeners.removeIf(listener -> listener.isTooFar(world, blockPos2));
        RenderSystem.disableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(pos -> {
                int i = pos.getX() - listener2.getRange();
                int j = pos.getY() - listener2.getRange();
                int k = pos.getZ() - listener2.getRange();
                int l = pos.getX() + listener2.getRange();
                int m = pos.getY() + listener2.getRange();
                int n = pos.getZ() + listener2.getRange();
                Vec3f vec3f = new Vec3f(1.0f, 1.0f, 0.0f);
                WorldRenderer.method_22983(matrices, vertexConsumer, VoxelShapes.cuboid(new Box(i, j, k, l, m, n)), -cameraX, -cameraY, -cameraZ, vec3f.getX(), vec3f.getY(), vec3f.getZ(), 0.35f);
            });
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(blockPos -> {
                Vec3f vec3f = new Vec3f(1.0f, 1.0f, 0.0f);
                WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos.getX() - 0.25f) - cameraX, (double)blockPos.getY() - cameraY, (double)((float)blockPos.getZ() - 0.25f) - cameraZ, (double)((float)blockPos.getX() + 0.25f) - cameraX, (double)blockPos.getY() - cameraY + 1.0, (double)((float)blockPos.getZ() + 0.25f) - cameraZ, vec3f.getX(), vec3f.getY(), vec3f.getZ(), 0.35f);
            });
        }
        tessellator.draw();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.depthMask(false);
        for (Listener listener2 : this.listeners) {
            listener2.getPos(world).ifPresent(blockPos -> {
                DebugRenderer.drawString("Listener Origin", blockPos.getX(), (float)blockPos.getY() + 1.8f, blockPos.getZ(), -1, 0.025f);
                DebugRenderer.drawString(new BlockPos((Vec3i)blockPos).toString(), blockPos.getX(), (float)blockPos.getY() + 1.5f, blockPos.getZ(), -6959665, 0.025f);
            });
        }
        for (Entry entry : this.entries) {
            Vec3d vec3d = entry.pos;
            double d = 0.2f;
            double e = vec3d.x - (double)0.2f;
            double f = vec3d.y - (double)0.2f;
            double g = vec3d.z - (double)0.2f;
            double h = vec3d.x + (double)0.2f;
            double i = vec3d.y + (double)0.2f + 0.5;
            double j = vec3d.z + (double)0.2f;
            GameEventDebugRenderer.method_33089(new Box(e, f, g, h, i, j), 1.0f, 1.0f, 1.0f, 0.2f);
            DebugRenderer.drawString(entry.event.getId(), vec3d.x, vec3d.y + (double)0.85f, vec3d.z, -7564911, 0.0075f);
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static void method_33089(Box box, float f, float g, float h, float i) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera.isReady()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3d vec3d = camera.getPos().negate();
        DebugRenderer.drawBox(box.offset(vec3d), f, g, h, i);
    }

    public void addEvent(GameEvent event, BlockPos pos) {
        this.entries.add(new Entry(Util.getMeasuringTimeMs(), event, Vec3d.ofBottomCenter(pos)));
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

        public boolean isTooFar(World world, BlockPos pos) {
            Optional<BlockPos> optional = this.positionSource.getPos(world);
            return !optional.isPresent() || optional.get().getSquaredDistance(pos) <= 1024.0;
        }

        public Optional<BlockPos> getPos(World world) {
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
        public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Entry {
        public final long startingMs;
        public final GameEvent event;
        public final Vec3d pos;

        public Entry(long startingMs, GameEvent event, Vec3d pos) {
            this.startingMs = startingMs;
            this.event = event;
            this.pos = pos;
        }

        public boolean hasExpired() {
            return Util.getMeasuringTimeMs() - this.startingMs > 3000L;
        }
    }
}


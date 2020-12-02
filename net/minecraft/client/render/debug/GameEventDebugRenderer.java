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
    private final List<Listener> field_28256 = Lists.newArrayList();
    private final List<class_5741> field_28257 = Lists.newArrayList();

    public GameEventDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        ClientWorld world = this.client.world;
        if (world == null) {
            this.field_28256.clear();
            this.field_28257.clear();
            return;
        }
        BlockPos blockPos2 = new BlockPos(cameraX, 0.0, cameraZ);
        this.field_28256.removeIf(Listener::method_33093);
        this.field_28257.removeIf(arg -> arg.method_33095(world, blockPos2));
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        for (class_5741 lv : this.field_28257) {
            lv.method_33094(world).ifPresent(blockPos -> {
                int i = blockPos.getX() - lv.getRange();
                int j = blockPos.getY() - lv.getRange();
                int k = blockPos.getZ() - lv.getRange();
                int l = blockPos.getX() + lv.getRange();
                int m = blockPos.getY() + lv.getRange();
                int n = blockPos.getZ() + lv.getRange();
                Vec3f vec3f = new Vec3f(1.0f, 1.0f, 0.0f);
                WorldRenderer.method_22983(matrices, vertexConsumer, VoxelShapes.cuboid(new Box(i, j, k, l, m, n)), -cameraX, -cameraY, -cameraZ, vec3f.getX(), vec3f.getY(), vec3f.getZ(), 0.35f);
            });
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (class_5741 lv2 : this.field_28257) {
            lv2.method_33094(world).ifPresent(blockPos -> {
                Vec3f vec3f = new Vec3f(1.0f, 1.0f, 0.0f);
                WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos.getX() - 0.25f) - cameraX, (double)blockPos.getY() - cameraY, (double)((float)blockPos.getZ() - 0.25f) - cameraZ, (double)((float)blockPos.getX() + 0.25f) - cameraX, (double)blockPos.getY() - cameraY + 1.0, (double)((float)blockPos.getZ() + 0.25f) - cameraZ, vec3f.getX(), vec3f.getY(), vec3f.getZ(), 0.35f);
            });
        }
        tessellator.draw();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.depthMask(false);
        for (class_5741 lv2 : this.field_28257) {
            lv2.method_33094(world).ifPresent(blockPos -> {
                DebugRenderer.drawString("Listener Origin", blockPos.getX(), (float)blockPos.getY() + 1.8f, blockPos.getZ(), -1, 0.025f);
                DebugRenderer.drawString(new BlockPos((Vec3i)blockPos).toString(), blockPos.getX(), (float)blockPos.getY() + 1.5f, blockPos.getZ(), -6959665, 0.025f);
            });
        }
        for (Listener listener : this.field_28256) {
            Vec3d vec3d = listener.field_28260;
            double d = 0.2f;
            double e = vec3d.x - (double)0.2f;
            double f = vec3d.y - (double)0.2f;
            double g = vec3d.z - (double)0.2f;
            double h = vec3d.x + (double)0.2f;
            double i = vec3d.y + (double)0.2f + 0.5;
            double j = vec3d.z + (double)0.2f;
            GameEventDebugRenderer.method_33089(new Box(e, f, g, h, i, j), 1.0f, 1.0f, 1.0f, 0.2f);
            DebugRenderer.drawString(listener.field_28259.getId(), vec3d.x, vec3d.y + (double)0.85f, vec3d.z, -7564911, 0.0075f);
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
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

    public void method_33087(GameEvent gameEvent, BlockPos blockPos) {
        this.field_28256.add(new Listener(Util.getMeasuringTimeMs(), gameEvent, Vec3d.ofBottomCenter(blockPos)));
    }

    public void method_33088(PositionSource positionSource, int i) {
        this.field_28257.add(new class_5741(positionSource, i));
    }

    @Environment(value=EnvType.CLIENT)
    static class Listener {
        public final long field_28258;
        public final GameEvent field_28259;
        public final Vec3d field_28260;

        public Listener(long l, GameEvent gameEvent, Vec3d vec3d) {
            this.field_28258 = l;
            this.field_28259 = gameEvent;
            this.field_28260 = vec3d;
        }

        public boolean method_33093() {
            return Util.getMeasuringTimeMs() - this.field_28258 > 3000L;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5741
    implements GameEventListener {
        public final PositionSource event;
        public final int field_28262;

        public class_5741(PositionSource positionSource, int i) {
            this.event = positionSource;
            this.field_28262 = i;
        }

        public boolean method_33095(World world, BlockPos blockPos) {
            Optional<BlockPos> optional = this.event.getPos(world);
            return !optional.isPresent() || optional.get().getSquaredDistance(blockPos) <= 1024.0;
        }

        public Optional<BlockPos> method_33094(World world) {
            return this.event.getPos(world);
        }

        @Override
        public PositionSource getPositionSource() {
            return this.event;
        }

        @Override
        public int getRange() {
            return this.field_28262;
        }

        @Override
        public boolean method_32947(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
            return false;
        }
    }
}


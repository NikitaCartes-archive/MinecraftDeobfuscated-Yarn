/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
    protected final EntityRenderDispatcher dispatcher;
    private final TextRenderer textRenderer;
    protected float shadowRadius;
    protected float shadowOpacity = 1.0f;

    protected EntityRenderer(EntityRendererFactory.Context ctx) {
        this.dispatcher = ctx.getRenderDispatcher();
        this.textRenderer = ctx.getTextRenderer();
    }

    public final int getLight(T entity, float tickDelta) {
        BlockPos blockPos = new BlockPos(((Entity)entity).getClientCameraPosVec(tickDelta));
        return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.getSkyLight(entity, blockPos));
    }

    protected int getSkyLight(T entity, BlockPos pos) {
        return ((Entity)entity).world.getLightLevel(LightType.SKY, pos);
    }

    protected int getBlockLight(T entity, BlockPos pos) {
        if (((Entity)entity).isOnFire()) {
            return 15;
        }
        return ((Entity)entity).world.getLightLevel(LightType.BLOCK, pos);
    }

    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        if (!((Entity)entity).shouldRender(x, y, z)) {
            return false;
        }
        if (((Entity)entity).ignoreCameraFrustum) {
            return true;
        }
        Box box = ((Entity)entity).getVisibilityBoundingBox().expand(0.5);
        if (box.isValid() || box.getAverageSideLength() == 0.0) {
            box = new Box(((Entity)entity).getX() - 2.0, ((Entity)entity).getY() - 2.0, ((Entity)entity).getZ() - 2.0, ((Entity)entity).getX() + 2.0, ((Entity)entity).getY() + 2.0, ((Entity)entity).getZ() + 2.0);
        }
        return frustum.isVisible(box);
    }

    public Vec3d getPositionOffset(T entity, float tickDelta) {
        return Vec3d.ZERO;
    }

    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!this.hasLabel(entity)) {
            return;
        }
        this.renderLabelIfPresent(entity, ((Entity)entity).getDisplayName(), matrices, vertexConsumers, light);
    }

    /**
     * Determines whether the passed entity should render with a nameplate above its head.
     * 
     * <p>Checks for a custom nametag on living entities, and for teams/team visibilities for players.</p>
     */
    protected boolean hasLabel(T entity) {
        return ((Entity)entity).shouldRenderName() && ((Entity)entity).hasCustomName();
    }

    public abstract Identifier getTexture(T var1);

    public TextRenderer getFontRenderer() {
        return this.textRenderer;
    }

    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double d = this.dispatcher.getSquaredDistanceToCamera((Entity)entity);
        if (d > 4096.0) {
            return;
        }
        boolean bl = !((Entity)entity).isSneaky();
        float f = ((Entity)entity).getHeight() + 0.5f;
        int i = "deadmau5".equals(text.getString()) ? -10 : 0;
        matrices.push();
        matrices.translate(0.0, f, 0.0);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrices.peek().getModel();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int j = (int)(g * 255.0f) << 24;
        TextRenderer textRenderer = this.getFontRenderer();
        float h = -textRenderer.getWidth(text) / 2;
        textRenderer.draw(text, h, (float)i, 0x20FFFFFF, false, matrix4f, vertexConsumers, bl, j, light);
        if (bl) {
            textRenderer.draw(text, h, (float)i, -1, false, matrix4f, vertexConsumers, false, 0, light);
        }
        matrices.pop();
    }
}


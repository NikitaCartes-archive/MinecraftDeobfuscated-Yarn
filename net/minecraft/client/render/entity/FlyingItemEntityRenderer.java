/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity>
extends EntityRenderer<T> {
    private static final float field_32943 = 12.25f;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public FlyingItemEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public FlyingItemEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (((Entity)entity).age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo((Entity)entity) < 12.25) {
            return;
        }
        matrices.push();
        matrices.scale(this.scale, this.scale, this.scale);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        this.itemRenderer.renderItem(((FlyingItemEntity)entity).getStack(), ModelTransformation.Mode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, ((Entity)entity).getId());
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}


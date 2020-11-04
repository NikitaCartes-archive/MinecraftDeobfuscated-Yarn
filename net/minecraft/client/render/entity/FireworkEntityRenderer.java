/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FireworkEntityRenderer
extends EntityRenderer<FireworkRocketEntity> {
    private final ItemRenderer itemRenderer;

    public FireworkEntityRenderer(class_5617.class_5618 arg) {
        super(arg);
        this.itemRenderer = arg.method_32168();
    }

    @Override
    public void render(FireworkRocketEntity fireworkRocketEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        if (fireworkRocketEntity.wasShotAtAngle()) {
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        }
        this.itemRenderer.renderItem(fireworkRocketEntity.getStack(), ModelTransformation.Mode.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
        matrixStack.pop();
        super.render(fireworkRocketEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(FireworkRocketEntity fireworkRocketEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}


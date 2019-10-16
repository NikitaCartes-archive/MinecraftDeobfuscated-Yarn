/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherSkullEntityRenderer
extends EntityRenderer<WitherSkullEntity> {
    private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");
    private final SkullEntityModel model = new SkullEntityModel();

    public WitherSkullEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4159(WitherSkullEntity witherSkullEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        float i = MathHelper.method_22859(witherSkullEntity.prevYaw, witherSkullEntity.yaw, h);
        float j = MathHelper.lerp(h, witherSkullEntity.prevPitch, witherSkullEntity.pitch);
        int k = witherSkullEntity.getLightmapCoordinates();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(this.model.getLayer(this.method_4160(witherSkullEntity)));
        this.model.render(0.0f, i, j);
        this.model.render(matrixStack, vertexConsumer, k, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(witherSkullEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public Identifier method_4160(WitherSkullEntity witherSkullEntity) {
        return witherSkullEntity.isCharged() ? INVINCIBLE_SKIN : SKIN;
    }
}


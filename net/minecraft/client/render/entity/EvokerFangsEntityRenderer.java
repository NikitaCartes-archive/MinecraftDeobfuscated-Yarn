/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel<EvokerFangsEntity> model;

    public EvokerFangsEntityRenderer(class_5617.class_5618 arg) {
        super(arg);
        this.model = new EvokerFangsEntityModel(arg.method_32167(EntityModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(EvokerFangsEntity evokerFangsEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float h = evokerFangsEntity.getAnimationProgress(g);
        if (h == 0.0f) {
            return;
        }
        float j = 2.0f;
        if (h > 0.9f) {
            j = (float)((double)j * ((1.0 - (double)h) / (double)0.1f));
        }
        matrixStack.push();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f - evokerFangsEntity.yaw));
        matrixStack.scale(-j, -j, j);
        float k = 0.03125f;
        matrixStack.translate(0.0, -0.626f, 0.0);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        this.model.setAngles(evokerFangsEntity, h, 0.0f, 0.0f, evokerFangsEntity.yaw, evokerFangsEntity.pitch);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(evokerFangsEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(EvokerFangsEntity evokerFangsEntity) {
        return TEXTURE;
    }
}


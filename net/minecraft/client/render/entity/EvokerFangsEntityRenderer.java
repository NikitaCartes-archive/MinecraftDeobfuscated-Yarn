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
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel();

    public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3962(EvokerFangsEntity evokerFangsEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        float i = evokerFangsEntity.getAnimationProgress(h);
        if (i == 0.0f) {
            return;
        }
        float j = 2.0f;
        if (i > 0.9f) {
            j = (float)((double)j * ((1.0 - (double)i) / (double)0.1f));
        }
        matrixStack.push();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0f - evokerFangsEntity.yaw));
        matrixStack.scale(-j, -j, j);
        float k = 0.03125f;
        matrixStack.translate(0.0, -0.626f, 0.0);
        int l = evokerFangsEntity.getLightmapCoordinates();
        this.model.setAngles(evokerFangsEntity, i, 0.0f, 0.0f, evokerFangsEntity.yaw, evokerFangsEntity.pitch, 0.03125f);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(this.model.method_23500(SKIN));
        this.model.renderItem(matrixStack, vertexConsumer, l, OverlayTexture.field_21444, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(evokerFangsEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public Identifier method_3963(EvokerFangsEntity evokerFangsEntity) {
        return SKIN;
    }
}


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
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class LeashKnotEntityRenderer
extends EntityRenderer<LeadKnotEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/lead_knot.png");
    private final LeashEntityModel<LeadKnotEntity> model = new LeashEntityModel();

    public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4035(LeadKnotEntity leadKnotEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        float i = 0.0625f;
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        int j = leadKnotEntity.getLightmapCoordinates();
        this.model.setAngles(leadKnotEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(this.model.method_23500(SKIN));
        this.model.renderItem(matrixStack, vertexConsumer, j, OverlayTexture.field_21444, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(leadKnotEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public Identifier method_4036(LeadKnotEntity leadKnotEntity) {
        return SKIN;
    }
}


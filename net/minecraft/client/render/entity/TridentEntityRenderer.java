/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class TridentEntityRenderer
extends EntityRenderer<TridentEntity> {
    public static final Identifier SKIN = new Identifier("textures/entity/trident.png");
    private final TridentEntityModel model = new TridentEntityModel();

    public TridentEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4133(TridentEntity tridentEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.lerp(h, tridentEntity.prevYaw, tridentEntity.yaw) - 90.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.lerp(h, tridentEntity.prevPitch, tridentEntity.pitch) + 90.0f, true));
        int i = tridentEntity.getLightmapCoordinates();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(this.method_4134(tridentEntity)));
        OverlayTexture.clearDefaultOverlay(vertexConsumer);
        this.model.renderItem(matrixStack, vertexConsumer, i);
        vertexConsumer.clearDefaultOverlay();
        matrixStack.pop();
        super.render(tridentEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public Identifier method_4134(TridentEntity tridentEntity) {
        return SKIN;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private final HeldItemRenderer field_38896;

    public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, HeldItemRenderer heldItemRenderer) {
        super(featureRendererContext);
        this.field_38896 = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        matrixStack.push();
        matrixStack.translate(0.0, 0.4f, -0.4f);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.MAINHAND);
        this.field_38896.renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}


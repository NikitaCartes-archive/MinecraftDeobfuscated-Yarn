/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, WitchEntityModel<T>> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4208(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getMainHandStack();
        if (itemStack.isEmpty()) {
            return;
        }
        matrixStack.push();
        if (((WitchEntityModel)this.getModel()).isChild) {
            matrixStack.translate(0.0, 0.625, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(20.0f, true));
            float n = 0.5f;
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        }
        ((WitchEntityModel)this.getModel()).method_2839().rotate(matrixStack, 0.0625f);
        matrixStack.translate(-0.0625, 0.53125, 0.21875);
        Item item = itemStack.getItem();
        if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
            matrixStack.translate(0.0, 0.0625, -0.25);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(30.0f, true));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-5.0f, true));
            float o = 0.375f;
            matrixStack.scale(0.375f, -0.375f, 0.375f);
        } else if (item == Items.BOW) {
            matrixStack.translate(0.0, 0.125, -0.125);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-45.0f, true));
            float o = 0.625f;
            matrixStack.scale(0.625f, -0.625f, 0.625f);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-100.0f, true));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-20.0f, true));
        } else {
            matrixStack.translate(0.1875, 0.1875, 0.0);
            float o = 0.875f;
            matrixStack.scale(0.875f, 0.875f, 0.875f);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-20.0f, true));
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-60.0f, true));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-30.0f, true));
        }
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-15.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(40.0f, true));
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, false, matrixStack, layeredVertexConsumerStorage);
        matrixStack.pop();
    }
}


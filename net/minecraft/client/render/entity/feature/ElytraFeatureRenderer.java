/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
    private final ElytraEntityModel<T> elytra = new ElytraEntityModel();

    public ElytraFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_17161(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        AbstractClientPlayerEntity abstractClientPlayerEntity;
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() != Items.ELYTRA) {
            return;
        }
        Identifier identifier = livingEntity instanceof AbstractClientPlayerEntity ? ((abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity).canRenderElytraTexture() && abstractClientPlayerEntity.getElytraTexture() != null ? abstractClientPlayerEntity.getElytraTexture() : (abstractClientPlayerEntity.canRenderCapeTexture() && abstractClientPlayerEntity.getCapeTexture() != null && abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE) ? abstractClientPlayerEntity.getCapeTexture() : SKIN)) : SKIN;
        matrixStack.push();
        matrixStack.translate(0.0, 0.0, 0.125);
        ((EntityModel)this.getModel()).copyStateTo(this.elytra);
        this.elytra.method_17079(livingEntity, f, g, j, k, l, m);
        VertexConsumer vertexConsumer = ItemRenderer.method_23181(layeredVertexConsumerStorage, this.elytra.method_23500(identifier), false, itemStack.hasEnchantmentGlint());
        this.elytra.renderItem(matrixStack, vertexConsumer, i, OverlayTexture.field_21444, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends FeatureRenderer<T, M> {
    protected final A modelLeggings;
    protected final A modelBody;
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();

    protected ArmorFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2) {
        super(featureRendererContext);
        this.modelLeggings = bipedEntityModel;
        this.modelBody = bipedEntityModel2;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.CHEST, i);
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.LEGS, i);
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.FEET, i);
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.HEAD, i);
    }

    private void renderArmor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, T livingEntity, float f, float g, float h, float i, float j, float k, EquipmentSlot equipmentSlot, int l) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(equipmentSlot);
        if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)itemStack.getItem();
        if (armorItem.getSlotType() != equipmentSlot) {
            return;
        }
        A bipedEntityModel = this.getArmor(equipmentSlot);
        ((BipedEntityModel)this.getContextModel()).setAttributes(bipedEntityModel);
        ((BipedEntityModel)bipedEntityModel).animateModel(livingEntity, f, g, h);
        this.setVisible(bipedEntityModel, equipmentSlot);
        ((BipedEntityModel)bipedEntityModel).setAngles(livingEntity, f, g, i, j, k);
        boolean bl = this.isLegs(equipmentSlot);
        boolean bl2 = itemStack.hasEnchantmentGlint();
        if (armorItem instanceof DyeableArmorItem) {
            int m = ((DyeableArmorItem)armorItem).getColor(itemStack);
            float n = (float)(m >> 16 & 0xFF) / 255.0f;
            float o = (float)(m >> 8 & 0xFF) / 255.0f;
            float p = (float)(m & 0xFF) / 255.0f;
            this.renderArmorParts(matrixStack, vertexConsumerProvider, l, armorItem, bl2, bipedEntityModel, bl, n, o, p, null);
            this.renderArmorParts(matrixStack, vertexConsumerProvider, l, armorItem, bl2, bipedEntityModel, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderArmorParts(matrixStack, vertexConsumerProvider, l, armorItem, bl2, bipedEntityModel, bl, 1.0f, 1.0f, 1.0f, null);
        }
    }

    private void renderArmorParts(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorItem armorItem, boolean bl, A bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, RenderLayer.getEntityCutoutNoCull(this.getArmorTexture(armorItem, bl2, string)), false, bl);
        ((AnimalModel)bipedEntityModel).render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, f, g, h, 1.0f);
    }

    public A getArmor(EquipmentSlot equipmentSlot) {
        return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
    }

    private boolean isLegs(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }

    private Identifier getArmorTexture(ArmorItem armorItem, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
    }

    protected abstract void setVisible(A var1, EquipmentSlot var2);

    protected abstract void setInvisible(A var1);
}


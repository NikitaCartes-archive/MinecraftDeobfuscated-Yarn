/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4592;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;
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

    public void method_17157(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        this.renderArmor(matrixStack, layeredVertexConsumerStorage, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.CHEST, i);
        this.renderArmor(matrixStack, layeredVertexConsumerStorage, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.LEGS, i);
        this.renderArmor(matrixStack, layeredVertexConsumerStorage, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.FEET, i);
        this.renderArmor(matrixStack, layeredVertexConsumerStorage, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.HEAD, i);
    }

    private void renderArmor(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, T livingEntity, float f, float g, float h, float i, float j, float k, float l, EquipmentSlot equipmentSlot, int m) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(equipmentSlot);
        if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)itemStack.getItem();
        if (armorItem.getSlotType() != equipmentSlot) {
            return;
        }
        A bipedEntityModel = this.getArmor(equipmentSlot);
        ((BipedEntityModel)this.getModel()).setAttributes(bipedEntityModel);
        ((BipedEntityModel)bipedEntityModel).method_17086(livingEntity, f, g, h);
        this.method_4170(bipedEntityModel, equipmentSlot);
        ((BipedEntityModel)bipedEntityModel).method_17087(livingEntity, f, g, i, j, k, l);
        boolean bl = this.isLegs(equipmentSlot);
        boolean bl2 = itemStack.hasEnchantmentGlint();
        if (armorItem instanceof DyeableArmorItem) {
            int n = ((DyeableArmorItem)armorItem).getColor(itemStack);
            float o = (float)(n >> 16 & 0xFF) / 255.0f;
            float p = (float)(n >> 8 & 0xFF) / 255.0f;
            float q = (float)(n & 0xFF) / 255.0f;
            this.method_23192(matrixStack, layeredVertexConsumerStorage, m, armorItem, bl2, bipedEntityModel, bl, o, p, q, null);
            this.method_23192(matrixStack, layeredVertexConsumerStorage, m, armorItem, bl2, bipedEntityModel, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.method_23192(matrixStack, layeredVertexConsumerStorage, m, armorItem, bl2, bipedEntityModel, bl, 1.0f, 1.0f, 1.0f, null);
        }
    }

    private void method_23192(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, ArmorItem armorItem, boolean bl, A bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string) {
        VertexConsumer vertexConsumer = ItemRenderer.method_23181(layeredVertexConsumerStorage, RenderLayer.getEntityCutoutNoCull(this.method_4174(armorItem, bl2, string)), false, bl);
        ((class_4592)bipedEntityModel).renderItem(matrixStack, vertexConsumer, i, OverlayTexture.field_21444, f, g, h);
    }

    public A getArmor(EquipmentSlot equipmentSlot) {
        return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
    }

    private boolean isLegs(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }

    private Identifier method_4174(ArmorItem armorItem, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
    }

    protected abstract void method_4170(A var1, EquipmentSlot var2);

    protected abstract void method_4190(A var1);
}


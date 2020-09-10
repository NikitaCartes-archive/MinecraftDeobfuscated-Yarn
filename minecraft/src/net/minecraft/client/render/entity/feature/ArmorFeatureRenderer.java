package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.<String, Identifier>newHashMap();
	private final A leggingsModel;
	private final A bodyModel;

	public ArmorFeatureRenderer(FeatureRendererContext<T, M> context, A leggingsModel, A bodyModel) {
		super(context);
		this.leggingsModel = leggingsModel;
		this.bodyModel = bodyModel;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getArmor(EquipmentSlot.CHEST));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getArmor(EquipmentSlot.LEGS));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getArmor(EquipmentSlot.FEET));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getArmor(EquipmentSlot.HEAD));
	}

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot equipmentSlot, int i, A bipedEntityModel) {
		ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
		if (itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.getSlotType() == equipmentSlot) {
				this.getContextModel().setAttributes(bipedEntityModel);
				this.setVisible(bipedEntityModel, equipmentSlot);
				boolean bl = this.usesSecondLayer(equipmentSlot);
				boolean bl2 = itemStack.hasGlint();
				if (armorItem instanceof DyeableArmorItem) {
					int j = ((DyeableArmorItem)armorItem).getColor(itemStack);
					float f = (float)(j >> 16 & 0xFF) / 255.0F;
					float g = (float)(j >> 8 & 0xFF) / 255.0F;
					float h = (float)(j & 0xFF) / 255.0F;
					this.renderArmorParts(matrices, vertexConsumers, i, armorItem, bl2, bipedEntityModel, bl, f, g, h, null);
					this.renderArmorParts(matrices, vertexConsumers, i, armorItem, bl2, bipedEntityModel, bl, 1.0F, 1.0F, 1.0F, "overlay");
				} else {
					this.renderArmorParts(matrices, vertexConsumers, i, armorItem, bl2, bipedEntityModel, bl, 1.0F, 1.0F, 1.0F, null);
				}
			}
		}
	}

	protected void setVisible(A bipedModel, EquipmentSlot slot) {
		bipedModel.setVisible(false);
		switch (slot) {
			case HEAD:
				bipedModel.head.visible = true;
				bipedModel.helmet.visible = true;
				break;
			case CHEST:
				bipedModel.torso.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				break;
			case LEGS:
				bipedModel.torso.visible = true;
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
				break;
			case FEET:
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
		}
	}

	private void renderArmorParts(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		ArmorItem armorItem,
		boolean bl,
		A bipedEntityModel,
		boolean bl2,
		float f,
		float g,
		float h,
		@Nullable String string
	) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
			vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(armorItem, bl2, string)), false, bl
		);
		bipedEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
	}

	private A getArmor(EquipmentSlot slot) {
		return this.usesSecondLayer(slot) ? this.leggingsModel : this.bodyModel;
	}

	private boolean usesSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	private Identifier getArmorTexture(ArmorItem armorItem, boolean bl, @Nullable String string) {
		String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
	}
}

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

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model) {
		ItemStack itemStack = entity.getEquippedStack(armorSlot);
		if (itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.getSlotType() == armorSlot) {
				this.getContextModel().setAttributes(model);
				this.setVisible(model, armorSlot);
				boolean bl = this.usesSecondLayer(armorSlot);
				boolean bl2 = itemStack.hasGlint();
				if (armorItem instanceof DyeableArmorItem) {
					int i = ((DyeableArmorItem)armorItem).getColor(itemStack);
					float f = (float)(i >> 16 & 0xFF) / 255.0F;
					float g = (float)(i >> 8 & 0xFF) / 255.0F;
					float h = (float)(i & 0xFF) / 255.0F;
					this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, null);
					this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, "overlay");
				} else {
					this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, null);
				}
			}
		}
	}

	protected void setVisible(A bipedModel, EquipmentSlot slot) {
		bipedModel.setVisible(false);
		switch (slot) {
			case HEAD:
				bipedModel.head.visible = true;
				bipedModel.hat.visible = true;
				break;
			case CHEST:
				bipedModel.body.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				break;
			case LEGS:
				bipedModel.body.visible = true;
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
				break;
			case FEET:
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
		}
	}

	private void renderArmorParts(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ArmorItem item,
		boolean usesSecondLayer,
		A model,
		boolean legs,
		float red,
		float green,
		float blue,
		@Nullable String overlay
	) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
			vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, legs, overlay)), false, usesSecondLayer
		);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
	}

	private A getArmor(EquipmentSlot slot) {
		return this.usesSecondLayer(slot) ? this.leggingsModel : this.bodyModel;
	}

	private boolean usesSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	private Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay) {
		String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
	}
}

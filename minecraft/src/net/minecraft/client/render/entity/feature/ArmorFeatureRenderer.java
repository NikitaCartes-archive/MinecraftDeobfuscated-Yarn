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
public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
	protected final A leggingsModel;
	protected final A bodyModel;
	protected static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.<String, Identifier>newHashMap();

	protected ArmorFeatureRenderer(FeatureRendererContext<T, M> context, A leggingsModel, A bodyModel) {
		super(context);
		this.leggingsModel = leggingsModel;
		this.bodyModel = bodyModel;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.CHEST, i, this.getArmor(EquipmentSlot.CHEST));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.LEGS, i, this.getArmor(EquipmentSlot.LEGS));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.FEET, i, this.getArmor(EquipmentSlot.FEET));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, f, g, h, j, k, l, EquipmentSlot.HEAD, i, this.getArmor(EquipmentSlot.HEAD));
	}

	private void renderArmor(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		T entity,
		float limbAngle,
		float limbDistance,
		float tickDelta,
		float customAngle,
		float headYaw,
		float headPitch,
		EquipmentSlot slot,
		int light,
		A armorModel
	) {
		ItemStack itemStack = entity.getEquippedStack(slot);
		if (itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.getSlotType() == slot) {
				this.getContextModel().setAttributes(armorModel);
				armorModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
				this.setVisible(armorModel, slot);
				armorModel.setAngles(entity, limbAngle, limbDistance, customAngle, headYaw, headPitch);
				boolean bl = this.usesSecondLayer(slot);
				boolean bl2 = itemStack.hasEnchantmentGlint();
				if (armorItem instanceof DyeableArmorItem) {
					int i = ((DyeableArmorItem)armorItem).getColor(itemStack);
					float f = (float)(i >> 16 & 0xFF) / 255.0F;
					float g = (float)(i >> 8 & 0xFF) / 255.0F;
					float h = (float)(i & 0xFF) / 255.0F;
					this.renderArmorParts(slot, matrices, vertexConsumers, light, armorItem, bl2, armorModel, bl, f, g, h, null);
					this.renderArmorParts(slot, matrices, vertexConsumers, light, armorItem, bl2, armorModel, bl, 1.0F, 1.0F, 1.0F, "overlay");
				} else {
					this.renderArmorParts(slot, matrices, vertexConsumers, light, armorItem, bl2, armorModel, bl, 1.0F, 1.0F, 1.0F, null);
				}
			}
		}
	}

	private void renderArmorParts(
		EquipmentSlot slot,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ArmorItem item,
		boolean glint,
		A armorModel,
		boolean secondLayer,
		float red,
		float green,
		float blue,
		@Nullable String suffix
	) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(
			vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(slot, item, secondLayer, suffix)), false, glint
		);
		armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
	}

	public A getArmor(EquipmentSlot slot) {
		return this.usesSecondLayer(slot) ? this.leggingsModel : this.bodyModel;
	}

	private boolean usesSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	protected Identifier getArmorTexture(EquipmentSlot slot, ArmorItem item, boolean secondLayer, @Nullable String suffix) {
		String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (secondLayer ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
	}

	protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

	protected abstract void setInvisible(A bipedModel);
}

package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.<String, Identifier>newHashMap();
	private final A innerModel;
	private final A outerModel;
	private final SpriteAtlasTexture armorTrimsAtlas;

	public ArmorFeatureRenderer(FeatureRendererContext<T, M> context, A innerModel, A outerModel, BakedModelManager bakery) {
		super(context);
		this.innerModel = innerModel;
		this.outerModel = outerModel;
		this.armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getModel(EquipmentSlot.CHEST));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getModel(EquipmentSlot.LEGS));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getModel(EquipmentSlot.FEET));
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getModel(EquipmentSlot.HEAD));
	}

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model) {
		ItemStack itemStack = entity.getEquippedStack(armorSlot);
		if (itemStack.getItem() instanceof ArmorItem armorItem) {
			if (armorItem.getSlotType() == armorSlot) {
				this.getContextModel().copyBipedStateTo(model);
				this.setVisible(model, armorSlot);
				boolean bl = this.usesInnerModel(armorSlot);
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

				if (entity.world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
					ArmorTrim.getTrim(entity.world.getRegistryManager(), itemStack)
						.ifPresent(trim -> this.renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, trim, bl2, model, bl, 1.0F, 1.0F, 1.0F));
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
		boolean glint,
		A model,
		boolean secondTextureLayer,
		float red,
		float green,
		float blue,
		@Nullable String overlay
	) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
			vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, secondTextureLayer, overlay)), false, glint
		);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
	}

	private void renderTrim(
		ArmorMaterial material,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ArmorTrim trim,
		boolean glint,
		A model,
		boolean leggings,
		float red,
		float green,
		float blue
	) {
		Sprite sprite = this.armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
			ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, TexturedRenderLayers.getArmorTrims(), true, glint)
		);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
	}

	private A getModel(EquipmentSlot slot) {
		return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
	}

	private boolean usesInnerModel(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	private Identifier getArmorTexture(ArmorItem item, boolean secondLayer, @Nullable String overlay) {
		String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (secondLayer ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
	}
}

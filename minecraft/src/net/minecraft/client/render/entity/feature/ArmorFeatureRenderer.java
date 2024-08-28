package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class ArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {
	private final A innerModel;
	private final A outerModel;
	private final A babyInnerModel;
	private final A babyOuterModel;
	private final SpriteAtlasTexture armorTrimsAtlas;

	public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, A innerModel, A outerModel, BakedModelManager bakedModelManager) {
		this(context, innerModel, outerModel, innerModel, outerModel, bakedModelManager);
	}

	public ArmorFeatureRenderer(
		FeatureRendererContext<S, M> context, A innerModel, A outerModel, A babyInnerModel, A babyOuterModel, BakedModelManager bakedModelManager
	) {
		super(context);
		this.innerModel = innerModel;
		this.outerModel = outerModel;
		this.babyInnerModel = babyInnerModel;
		this.babyOuterModel = babyOuterModel;
		this.armorTrimsAtlas = bakedModelManager.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState,
			bipedEntityRenderState.equippedChestStack,
			EquipmentSlot.CHEST,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.CHEST)
		);
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState,
			bipedEntityRenderState.equippedLegsStack,
			EquipmentSlot.LEGS,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.LEGS)
		);
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState,
			bipedEntityRenderState.equippedFeetStack,
			EquipmentSlot.FEET,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.FEET)
		);
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState,
			bipedEntityRenderState.headEquippedStack,
			EquipmentSlot.HEAD,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.HEAD)
		);
	}

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, S state, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
		if (stack.getItem() instanceof ArmorItem armorItem) {
			if (armorItem.getSlotType() == slot) {
				armorModel.setAngles(state);
				this.setVisible(armorModel, slot);
				boolean bl = this.usesInnerModel(slot);
				ArmorMaterial armorMaterial = armorItem.getMaterial().value();
				int i = stack.isIn(ItemTags.DYEABLE) ? ColorHelper.fullAlpha(DyedColorComponent.getColor(stack, -6265536)) : Colors.WHITE;

				for (ArmorMaterial.Layer layer : armorMaterial.layers()) {
					int j = layer.isDyeable() ? i : -1;
					this.renderArmorParts(matrices, vertexConsumers, light, armorModel, j, layer.getTexture(bl));
				}

				ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
				if (armorTrim != null) {
					this.renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, armorTrim, armorModel, bl);
				}

				if (stack.hasGlint()) {
					this.renderGlint(matrices, vertexConsumers, light, armorModel);
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

	private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model, int color, Identifier texture) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(texture));
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, color);
	}

	private void renderTrim(
		RegistryEntry<ArmorMaterial> armorMaterial,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ArmorTrim trim,
		A model,
		boolean leggings
	) {
		Sprite sprite = this.armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(armorMaterial) : trim.getGenericModelId(armorMaterial));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
			vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(trim.getPattern().value().decal()))
		);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
	}

	private void renderGlint(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model) {
		model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorEntityGlint()), light, OverlayTexture.DEFAULT_UV);
	}

	private A getModel(S state, EquipmentSlot slot) {
		if (this.usesInnerModel(slot)) {
			return state.baby ? this.babyInnerModel : this.innerModel;
		} else {
			return state.baby ? this.babyOuterModel : this.outerModel;
		}
	}

	private boolean usesInnerModel(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}
}

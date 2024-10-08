package net.minecraft.client.render.entity.feature;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer<S extends BipedEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {
	private final ElytraEntityModel model;
	private final ElytraEntityModel babyModel;
	private final EquipmentRenderer equipmentRenderer;

	public ElytraFeatureRenderer(FeatureRendererContext<S, M> context, EntityModelLoader loader, EquipmentRenderer equipmentRenderer) {
		super(context);
		this.model = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
		this.babyModel = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA_BABY));
		this.equipmentRenderer = equipmentRenderer;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
		ItemStack itemStack = bipedEntityRenderState.equippedChestStack;
		EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
		if (equippableComponent != null && !equippableComponent.model().isEmpty()) {
			Identifier identifier = getTexture(bipedEntityRenderState);
			ElytraEntityModel elytraEntityModel = bipedEntityRenderState.baby ? this.babyModel : this.model;
			Identifier identifier2 = (Identifier)equippableComponent.model().get();
			matrixStack.push();
			matrixStack.translate(0.0F, 0.0F, 0.125F);
			elytraEntityModel.setAngles(bipedEntityRenderState);
			this.equipmentRenderer.render(EquipmentModel.LayerType.WINGS, identifier2, elytraEntityModel, itemStack, matrixStack, vertexConsumerProvider, i, identifier);
			matrixStack.pop();
		}
	}

	@Nullable
	private static Identifier getTexture(BipedEntityRenderState state) {
		if (state instanceof PlayerEntityRenderState playerEntityRenderState) {
			SkinTextures skinTextures = playerEntityRenderState.skinTextures;
			if (skinTextures.elytraTexture() != null) {
				return skinTextures.elytraTexture();
			}

			if (skinTextures.capeTexture() != null && playerEntityRenderState.capeVisible) {
				return skinTextures.capeTexture();
			}
		}

		return null;
	}
}

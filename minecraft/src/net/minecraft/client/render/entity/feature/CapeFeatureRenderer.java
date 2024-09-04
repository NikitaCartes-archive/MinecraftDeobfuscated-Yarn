package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CapeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	private final BipedEntityModel<PlayerEntityRenderState> model;
	private final EquipmentModelLoader field_54177;

	public CapeFeatureRenderer(
		FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EntityModelLoader modelLoader, EquipmentModelLoader equipmentModelLoader
	) {
		super(context);
		this.model = new PlayerCapeModel<>(modelLoader.getModelPart(EntityModelLayers.PLAYER_CAPE));
		this.field_54177 = equipmentModelLoader;
	}

	private boolean method_64075(ItemStack itemStack) {
		EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
		if (equippableComponent != null && !equippableComponent.model().isEmpty()) {
			EquipmentModel equipmentModel = this.field_54177.get((Identifier)equippableComponent.model().get());
			return !equipmentModel.getLayers(EquipmentModel.LayerType.WINGS).isEmpty();
		} else {
			return false;
		}
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g
	) {
		if (!playerEntityRenderState.invisible && playerEntityRenderState.capeVisible) {
			SkinTextures skinTextures = playerEntityRenderState.skinTextures;
			if (skinTextures.capeTexture() != null) {
				if (!this.method_64075(playerEntityRenderState.equippedChestStack)) {
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(skinTextures.capeTexture()));
					this.model.setAngles(playerEntityRenderState);
					this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
				}
			}
		}
	}
}

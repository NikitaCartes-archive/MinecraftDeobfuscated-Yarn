package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
	private final HorseEntityModel model;
	private final HorseEntityModel babyModel;
	private final EquipmentRenderer equipmentRenderer;

	public HorseArmorFeatureRenderer(
		FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> context, EntityModelLoader loader, EquipmentRenderer equipmentRenderer
	) {
		super(context);
		this.equipmentRenderer = equipmentRenderer;
		this.model = new HorseEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR));
		this.babyModel = new HorseEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR_BABY));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntityRenderState horseEntityRenderState, float f, float g
	) {
		ItemStack itemStack = horseEntityRenderState.armor;
		EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
		if (equippableComponent != null && !equippableComponent.model().isEmpty()) {
			HorseEntityModel horseEntityModel = horseEntityRenderState.baby ? this.babyModel : this.model;
			Identifier identifier = (Identifier)equippableComponent.model().get();
			horseEntityModel.setAngles(horseEntityRenderState);
			this.equipmentRenderer.render(EquipmentModel.LayerType.HORSE_BODY, identifier, horseEntityModel, itemStack, matrixStack, vertexConsumerProvider, i);
		}
	}
}

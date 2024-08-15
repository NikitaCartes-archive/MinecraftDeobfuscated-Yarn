package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
	private final HorseEntityModel model;
	private final HorseEntityModel babyModel;

	public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new HorseEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR));
		this.babyModel = new HorseEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR_BABY));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntityRenderState horseEntityRenderState, float f, float g
	) {
		ItemStack itemStack = horseEntityRenderState.armor;
		if (itemStack.getItem() instanceof AnimalArmorItem animalArmorItem && animalArmorItem.getType() == AnimalArmorItem.Type.EQUESTRIAN) {
			HorseEntityModel horseEntityModel = horseEntityRenderState.baby ? this.babyModel : this.model;
			horseEntityModel.setAngles(horseEntityRenderState);
			int j;
			if (itemStack.isIn(ItemTags.DYEABLE)) {
				j = ColorHelper.fullAlpha(DyedColorComponent.getColor(itemStack, -6265536));
			} else {
				j = Colors.WHITE;
			}

			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
			horseEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, j);
			return;
		}
	}
}

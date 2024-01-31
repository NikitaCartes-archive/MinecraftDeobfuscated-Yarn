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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;

@Environment(EnvType.CLIENT)
public class HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
	private final HorseEntityModel<HorseEntity> model;

	public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> context, EntityModelLoader loader) {
		super(context);
		this.model = new HorseEntityModel<>(loader.getModelPart(EntityModelLayers.HORSE_ARMOR));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = horseEntity.getBodyArmor();
		if (itemStack.getItem() instanceof AnimalArmorItem animalArmorItem && animalArmorItem.getType() == AnimalArmorItem.Type.EQUESTRIAN) {
			this.getContextModel().copyStateTo(this.model);
			this.model.animateModel(horseEntity, f, g, h);
			this.model.setAngles(horseEntity, f, g, j, k, l);
			float o;
			float p;
			float n;
			if (itemStack.isIn(ItemTags.DYEABLE)) {
				int m = DyeableItem.getColor(itemStack);
				n = (float)(m >> 16 & 0xFF) / 255.0F;
				o = (float)(m >> 8 & 0xFF) / 255.0F;
				p = (float)(m & 0xFF) / 255.0F;
			} else {
				n = 1.0F;
				o = 1.0F;
				p = 1.0F;
			}

			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0F);
			return;
		}
	}
}

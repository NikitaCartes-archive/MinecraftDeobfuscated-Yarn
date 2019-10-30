package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
	private final HorseEntityModel<HorseEntity> model = new HorseEntityModel<>(0.1F);

	public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18658(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		HorseEntity horseEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		ItemStack itemStack = horseEntity.getArmorType();
		if (itemStack.getItem() instanceof HorseArmorItem) {
			HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
			this.getModel().copyStateTo(this.model);
			this.model.method_17084(horseEntity, f, g, h);
			this.model.method_17085(horseEntity, f, g, j, k, l, m);
			float o;
			float p;
			float q;
			if (horseArmorItem instanceof DyeableHorseArmorItem) {
				int n = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
				o = (float)(n >> 16 & 0xFF) / 255.0F;
				p = (float)(n >> 8 & 0xFF) / 255.0F;
				q = (float)(n & 0xFF) / 255.0F;
			} else {
				o = 1.0F;
				p = 1.0F;
				q = 1.0F;
			}

			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(horseArmorItem.getEntityTexture()));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, o, p, q);
		}
	}
}

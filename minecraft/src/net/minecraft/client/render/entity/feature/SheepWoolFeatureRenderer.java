package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepWoolFeatureRenderer extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/sheep/sheep_fur.png");
	private final SheepWoolEntityModel<SheepEntity> model = new SheepWoolEntityModel<>();

	public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4198(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		SheepEntity sheepEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (!sheepEntity.isSheared() && !sheepEntity.isInvisible()) {
			float t;
			float u;
			float v;
			if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().asString())) {
				int n = 25;
				int o = sheepEntity.age / 25 + sheepEntity.getEntityId();
				int p = DyeColor.values().length;
				int q = o % p;
				int r = (o + 1) % p;
				float s = ((float)(sheepEntity.age % 25) + h) / 25.0F;
				float[] fs = SheepEntity.getRgbColor(DyeColor.byId(q));
				float[] gs = SheepEntity.getRgbColor(DyeColor.byId(r));
				t = fs[0] * (1.0F - s) + gs[0] * s;
				u = fs[1] * (1.0F - s) + gs[1] * s;
				v = fs[2] * (1.0F - s) + gs[2] * s;
			} else {
				float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
				t = hs[0];
				u = hs[1];
				v = hs[2];
			}

			render(this.getModel(), this.model, SKIN, matrixStack, layeredVertexConsumerStorage, i, sheepEntity, f, g, j, k, l, m, h, t, u, v);
		}
	}
}

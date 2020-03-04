package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
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

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!sheepEntity.isSheared() && !sheepEntity.isInvisible()) {
			float s;
			float t;
			float u;
			if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().asString())) {
				int m = 25;
				int n = sheepEntity.age / 25 + sheepEntity.getEntityId();
				int o = DyeColor.values().length;
				int p = n % o;
				int q = (n + 1) % o;
				float r = ((float)(sheepEntity.age % 25) + h) / 25.0F;
				float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
				float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
				s = fs[0] * (1.0F - r) + gs[0] * r;
				t = fs[1] * (1.0F - r) + gs[1] * r;
				u = fs[2] * (1.0F - r) + gs[2] * r;
			} else {
				float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
				s = hs[0];
				t = hs[1];
				u = hs[2];
			}

			render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, s, t, u);
		}
	}
}

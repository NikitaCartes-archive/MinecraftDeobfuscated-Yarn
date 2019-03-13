package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepWoolFeatureRenderer extends FeatureRenderer<SheepEntity, SheepWoolEntityModel<SheepEntity>> {
	private static final Identifier field_4892 = new Identifier("textures/entity/sheep/sheep_fur.png");
	private final SheepEntityModel<SheepEntity> field_4891 = new SheepEntityModel<>();

	public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntity, SheepWoolEntityModel<SheepEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4198(SheepEntity sheepEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!sheepEntity.isSheared() && !sheepEntity.isInvisible()) {
			this.method_17164(field_4892);
			if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.method_5477().getText())) {
				int m = 25;
				int n = sheepEntity.age / 25 + sheepEntity.getEntityId();
				int o = DyeColor.values().length;
				int p = n % o;
				int q = (n + 1) % o;
				float r = ((float)(sheepEntity.age % 25) + h) / 25.0F;
				float[] fs = SheepEntity.method_6634(DyeColor.byId(p));
				float[] gs = SheepEntity.method_6634(DyeColor.byId(q));
				GlStateManager.color3f(fs[0] * (1.0F - r) + gs[0] * r, fs[1] * (1.0F - r) + gs[1] * r, fs[2] * (1.0F - r) + gs[2] * r);
			} else {
				float[] hs = SheepEntity.method_6634(sheepEntity.method_6633());
				GlStateManager.color3f(hs[0], hs[1], hs[2]);
			}

			this.getModel().method_17081(this.field_4891);
			this.field_4891.method_17118(sheepEntity, f, g, h);
			this.field_4891.render(sheepEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}

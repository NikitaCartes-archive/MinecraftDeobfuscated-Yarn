package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepWoolEntityRenderer implements LayerEntityRenderer<SheepEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/sheep/sheep_fur.png");
	private final SheepEntityRenderer field_4893;
	private final SheepEntityModel field_4891 = new SheepEntityModel();

	public SheepWoolEntityRenderer(SheepEntityRenderer sheepEntityRenderer) {
		this.field_4893 = sheepEntityRenderer;
	}

	public void render(SheepEntity sheepEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!sheepEntity.isSheared() && !sheepEntity.isInvisible()) {
			this.field_4893.bindTexture(SKIN);
			if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().getText())) {
				int m = 25;
				int n = sheepEntity.age / 25 + sheepEntity.getEntityId();
				int o = DyeColor.values().length;
				int p = n % o;
				int q = (n + 1) % o;
				float r = ((float)(sheepEntity.age % 25) + h) / 25.0F;
				float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
				float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
				GlStateManager.color3f(fs[0] * (1.0F - r) + gs[0] * r, fs[1] * (1.0F - r) + gs[1] * r, fs[2] * (1.0F - r) + gs[2] * r);
			} else {
				float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
				GlStateManager.color3f(hs[0], hs[1], hs[2]);
			}

			this.field_4891.setAttributes(this.field_4893.method_4038());
			this.field_4891.animateModel(sheepEntity, f, g, h);
			this.field_4891.render(sheepEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}

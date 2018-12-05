package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.LayerEntityRenderer;
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class class_1001 implements LayerEntityRenderer<TropicalFishEntity> {
	private final TropicalFishEntityRenderer field_4905;
	private final class_612 field_4904;
	private final class_615 field_4903;

	public class_1001(TropicalFishEntityRenderer tropicalFishEntityRenderer) {
		this.field_4905 = tropicalFishEntityRenderer;
		this.field_4904 = new class_612(0.008F);
		this.field_4903 = new class_615(0.008F);
	}

	public void render(TropicalFishEntity tropicalFishEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!tropicalFishEntity.isInvisible()) {
			Model model = (Model)(tropicalFishEntity.method_6654() == 0 ? this.field_4904 : this.field_4903);
			this.field_4905.bindTexture(tropicalFishEntity.method_6646());
			float[] fs = tropicalFishEntity.method_6655();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			model.setAttributes(this.field_4905.method_4038());
			model.animateModel(tropicalFishEntity, f, g, h);
			model.render(tropicalFishEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}

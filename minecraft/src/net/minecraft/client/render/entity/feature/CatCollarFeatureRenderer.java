package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CatCollarFeatureRenderer extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/cat/cat_collar.png");
	private final CatEntityModel<CatEntity> model = new CatEntityModel<>(0.01F);

	public CatCollarFeatureRenderer(FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_16047(CatEntity catEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (catEntity.isTamed() && !catEntity.isInvisible()) {
			this.bindTexture(SKIN);
			float[] fs = catEntity.getCollarColor().getColorComponents();
			RenderSystem.color3f(fs[0], fs[1], fs[2]);
			this.getModel().copyStateTo(this.model);
			this.model.method_17074(catEntity, f, g, h);
			this.model.render(catEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}

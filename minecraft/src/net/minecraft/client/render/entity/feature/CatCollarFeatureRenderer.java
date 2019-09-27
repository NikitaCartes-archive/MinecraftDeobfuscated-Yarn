package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
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

	public void method_16047(class_4587 arg, class_4597 arg2, int i, CatEntity catEntity, float f, float g, float h, float j, float k, float l, float m) {
		if (catEntity.isTamed()) {
			float[] fs = catEntity.getCollarColor().getColorComponents();
			method_23196(this.getModel(), this.model, SKIN, arg, arg2, i, catEntity, f, g, j, k, l, m, h, fs[0], fs[1], fs[2]);
		}
	}
}

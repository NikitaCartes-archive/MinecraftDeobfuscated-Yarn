package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class class_4607<T extends Entity & class_4582, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public class_4607(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(class_4587 arg, class_4597 arg2, int i, T entity, float f, float g, float h, float j, float k, float l, float m) {
		if (entity.isAtHalfHealth()) {
			float n = (float)entity.age + h;
			EntityModel<T> entityModel = this.method_23203();
			entityModel.animateModel(entity, f, g, h);
			this.getModel().copyStateTo(entityModel);
			class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23018(this.method_23201(), this.method_23202(n), n * 0.01F));
			class_4608.method_23211(lv);
			entityModel.setAngles(entity, f, g, j, k, l, m);
			entityModel.method_17116(arg, lv, i, 0.5F, 0.5F, 0.5F);
			lv.method_22923();
		}
	}

	protected abstract float method_23202(float f);

	protected abstract Identifier method_23201();

	protected abstract EntityModel<T> method_23203();
}

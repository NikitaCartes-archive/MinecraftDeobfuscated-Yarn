package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4594;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.feature.TropicalFishSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
	private final TropicalFishEntityModelA<TropicalFishEntity> field_4800 = new TropicalFishEntityModelA<>();
	private final TropicalFishEntityModelB<TropicalFishEntity> field_4799 = new TropicalFishEntityModelB<>();

	public TropicalFishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new TropicalFishEntityModelA<>(), 0.15F);
		this.addFeature(new TropicalFishSomethingFeatureRenderer(this));
	}

	public Identifier method_4141(TropicalFishEntity tropicalFishEntity) {
		return tropicalFishEntity.getShapeId();
	}

	public void method_4140(TropicalFishEntity tropicalFishEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		class_4594<TropicalFishEntity> lv = (class_4594<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0 ? this.field_4800 : this.field_4799);
		this.model = lv;
		float[] fs = tropicalFishEntity.getBaseColorComponents();
		lv.method_22956(fs[0], fs[1], fs[2]);
		super.method_4072(tropicalFishEntity, d, e, f, g, h, arg, arg2);
		lv.method_22956(1.0F, 1.0F, 1.0F);
	}

	protected void method_4142(TropicalFishEntity tropicalFishEntity, class_4587 arg, float f, float g, float h) {
		super.setupTransforms(tropicalFishEntity, arg, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		arg.method_22907(Vector3f.field_20705.method_23214(i, true));
		if (!tropicalFishEntity.isInsideWater()) {
			arg.method_22904(0.2F, 0.1F, 0.0);
			arg.method_22907(Vector3f.field_20707.method_23214(90.0F, true));
		}
	}
}

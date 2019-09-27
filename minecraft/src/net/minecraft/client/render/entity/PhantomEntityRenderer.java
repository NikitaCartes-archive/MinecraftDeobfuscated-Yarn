package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhantomEntityRenderer extends MobEntityRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/phantom.png");

	public PhantomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PhantomEntityModel<>(), 0.75F);
		this.addFeature(new PhantomEyesFeatureRenderer<>(this));
	}

	public Identifier method_4090(PhantomEntity phantomEntity) {
		return SKIN;
	}

	protected void method_4088(PhantomEntity phantomEntity, class_4587 arg, float f) {
		int i = phantomEntity.getPhantomSize();
		float g = 1.0F + 0.15F * (float)i;
		arg.method_22905(g, g, g);
		arg.method_22904(0.0, 1.3125, 0.1875);
	}

	protected void method_4089(PhantomEntity phantomEntity, class_4587 arg, float f, float g, float h) {
		super.setupTransforms(phantomEntity, arg, f, g, h);
		arg.method_22907(Vector3f.field_20703.method_23214(phantomEntity.pitch, true));
	}
}

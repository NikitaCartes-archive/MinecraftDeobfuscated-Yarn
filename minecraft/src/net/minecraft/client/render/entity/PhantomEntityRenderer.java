package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

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

	protected void method_4088(PhantomEntity phantomEntity, MatrixStack matrixStack, float f) {
		int i = phantomEntity.getPhantomSize();
		float g = 1.0F + 0.15F * (float)i;
		matrixStack.scale(g, g, g);
		matrixStack.translate(0.0, 1.3125, 0.1875);
	}

	protected void method_4089(PhantomEntity phantomEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(phantomEntity, matrixStack, f, g, h);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(phantomEntity.pitch, true));
	}
}

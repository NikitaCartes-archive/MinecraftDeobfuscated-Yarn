package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PhantomEntityRenderer extends MobEntityRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/phantom.png");

	public PhantomEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PhantomEntityModel<>(context.getPart(EntityModelLayers.PHANTOM)), 0.75F);
		this.addFeature(new PhantomEyesFeatureRenderer<>(this));
	}

	public Identifier getTexture(PhantomEntity phantomEntity) {
		return TEXTURE;
	}

	protected void scale(PhantomEntity phantomEntity, MatrixStack matrixStack, float f) {
		int i = phantomEntity.getPhantomSize();
		float g = 1.0F + 0.15F * (float)i;
		matrixStack.scale(g, g, g);
		matrixStack.translate(0.0F, 1.3125F, 0.1875F);
	}

	protected void setupTransforms(PhantomEntity phantomEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		super.setupTransforms(phantomEntity, matrixStack, f, g, h, i);
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(phantomEntity.getPitch()));
	}
}

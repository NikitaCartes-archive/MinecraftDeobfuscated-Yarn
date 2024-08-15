package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class EndermanEntityRenderer extends MobEntityRenderer<EndermanEntity, EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderman/enderman.png");
	private final Random random = Random.create();

	public EndermanEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new EndermanEntityModel<>(context.getPart(EntityModelLayers.ENDERMAN)), 0.5F);
		this.addFeature(new EndermanEyesFeatureRenderer(this));
		this.addFeature(new EndermanBlockFeatureRenderer(this, context.getBlockRenderManager()));
	}

	public Vec3d getPositionOffset(EndermanEntityRenderState endermanEntityRenderState) {
		Vec3d vec3d = super.getPositionOffset(endermanEntityRenderState);
		if (endermanEntityRenderState.angry) {
			double d = 0.02 * (double)endermanEntityRenderState.baseScale;
			return vec3d.add(this.random.nextGaussian() * d, 0.0, this.random.nextGaussian() * d);
		} else {
			return vec3d;
		}
	}

	public Identifier getTexture(EndermanEntityRenderState endermanEntityRenderState) {
		return TEXTURE;
	}

	public EndermanEntityRenderState getRenderState() {
		return new EndermanEntityRenderState();
	}

	public void updateRenderState(EndermanEntity endermanEntity, EndermanEntityRenderState endermanEntityRenderState, float f) {
		super.updateRenderState(endermanEntity, endermanEntityRenderState, f);
		BipedEntityRenderer.updateBipedRenderState(endermanEntity, endermanEntityRenderState, f);
		endermanEntityRenderState.angry = endermanEntity.isAngry();
		endermanEntityRenderState.carriedBlock = endermanEntity.getCarriedBlock();
	}
}

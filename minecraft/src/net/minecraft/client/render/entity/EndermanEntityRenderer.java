package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public class EndermanEntityRenderer extends MobEntityRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/enderman/enderman.png");
	private final AbstractRandom random = AbstractRandom.createAtomic();

	public EndermanEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new EndermanEntityModel<>(context.getPart(EntityModelLayers.ENDERMAN)), 0.5F);
		this.addFeature(new EndermanEyesFeatureRenderer<>(this));
		this.addFeature(new EndermanBlockFeatureRenderer(this));
	}

	public void render(EndermanEntity endermanEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		EndermanEntityModel<EndermanEntity> endermanEntityModel = this.getModel();
		endermanEntityModel.carryingBlock = blockState != null;
		endermanEntityModel.angry = endermanEntity.isAngry();
		super.render(endermanEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Vec3d getPositionOffset(EndermanEntity endermanEntity, float f) {
		if (endermanEntity.isAngry()) {
			double d = 0.02;
			return new Vec3d(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
		} else {
			return super.getPositionOffset(endermanEntity, f);
		}
	}

	public Identifier getTexture(EndermanEntity endermanEntity) {
		return TEXTURE;
	}
}

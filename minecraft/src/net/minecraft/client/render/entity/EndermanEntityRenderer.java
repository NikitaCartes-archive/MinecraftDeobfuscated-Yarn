package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EndermanEntityRenderer extends MobEntityRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/enderman/enderman.png");
	private final Random random = new Random();

	public EndermanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EndermanEntityModel<>(0.0F), 0.5F);
		this.addFeature(new EndermanEyesFeatureRenderer<>(this));
		this.addFeature(new EndermanBlockFeatureRenderer(this));
	}

	public void method_3911(EndermanEntity endermanEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		EndermanEntityModel<EndermanEntity> endermanEntityModel = this.getModel();
		endermanEntityModel.carryingBlock = blockState != null;
		endermanEntityModel.angry = endermanEntity.isAngry();
		super.method_4072(endermanEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Vec3d method_23160(EndermanEntity endermanEntity, float f) {
		if (endermanEntity.isAngry()) {
			double d = 0.02;
			return new Vec3d(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
		} else {
			return super.getPositionOffset(endermanEntity, f);
		}
	}

	public Identifier method_3912(EndermanEntity endermanEntity) {
		return TEXTURE;
	}
}

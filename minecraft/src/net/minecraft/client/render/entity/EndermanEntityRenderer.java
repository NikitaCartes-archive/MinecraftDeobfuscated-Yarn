package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EndermanEntityRenderer extends MobEntityRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderman/enderman.png");
	private final Random random = new Random();

	public EndermanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EndermanEntityModel<>(0.0F), 0.5F);
		this.addFeature(new EndermanEyesFeatureRenderer<>(this));
		this.addFeature(new EndermanBlockFeatureRenderer(this));
	}

	public void method_3911(
		EndermanEntity endermanEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		EndermanEntityModel<EndermanEntity> endermanEntityModel = this.getModel();
		endermanEntityModel.carryingBlock = blockState != null;
		endermanEntityModel.angry = endermanEntity.isAngry();
		super.method_4072(endermanEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Vec3d method_23160(EndermanEntity endermanEntity, double d, double e, double f, float g) {
		if (endermanEntity.isAngry()) {
			double h = 0.02;
			return new Vec3d(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
		} else {
			return super.getPositionOffset(endermanEntity, d, e, f, g);
		}
	}

	public Identifier method_3912(EndermanEntity endermanEntity) {
		return SKIN;
	}
}

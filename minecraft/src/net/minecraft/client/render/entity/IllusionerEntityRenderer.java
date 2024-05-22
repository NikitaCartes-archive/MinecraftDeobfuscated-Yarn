package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderer extends IllagerEntityRenderer<IllusionerEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/illusioner.png");

	public IllusionerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.ILLUSIONER)), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<IllusionerEntity, IllagerEntityModel<IllusionerEntity>>(this, context.getHeldItemRenderer()) {
				public void render(
					MatrixStack matrixStack,
					VertexConsumerProvider vertexConsumerProvider,
					int i,
					IllusionerEntity illusionerEntity,
					float f,
					float g,
					float h,
					float j,
					float k,
					float l
				) {
					if (illusionerEntity.isSpellcasting() || illusionerEntity.isAttacking()) {
						super.render(matrixStack, vertexConsumerProvider, i, illusionerEntity, f, g, h, j, k, l);
					}
				}
			}
		);
		this.model.getHat().visible = true;
	}

	public Identifier getTexture(IllusionerEntity illusionerEntity) {
		return TEXTURE;
	}

	public void render(IllusionerEntity illusionerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (illusionerEntity.isInvisible()) {
			Vec3d[] vec3ds = illusionerEntity.getMirrorCopyOffsets(g);
			float h = this.getAnimationProgress(illusionerEntity, g);

			for (int j = 0; j < vec3ds.length; j++) {
				matrixStack.push();
				matrixStack.translate(
					vec3ds[j].x + (double)MathHelper.cos((float)j + h * 0.5F) * 0.025,
					vec3ds[j].y + (double)MathHelper.cos((float)j + h * 0.75F) * 0.0125,
					vec3ds[j].z + (double)MathHelper.cos((float)j + h * 0.7F) * 0.025
				);
				super.render(illusionerEntity, f, g, matrixStack, vertexConsumerProvider, i);
				matrixStack.pop();
			}
		} else {
			super.render(illusionerEntity, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	protected boolean isVisible(IllusionerEntity illusionerEntity) {
		return true;
	}
}

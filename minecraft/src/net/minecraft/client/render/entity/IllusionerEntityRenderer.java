package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderer extends IllagerEntityRenderer<IllusionerEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/illusioner.png");

	public IllusionerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EvilVillagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<IllusionerEntity, EvilVillagerEntityModel<IllusionerEntity>>(this) {
				public void method_17149(
					MatrixStack matrixStack,
					LayeredVertexConsumerStorage layeredVertexConsumerStorage,
					int i,
					IllusionerEntity illusionerEntity,
					float f,
					float g,
					float h,
					float j,
					float k,
					float l,
					float m
				) {
					if (illusionerEntity.isSpellcasting() || illusionerEntity.isAttacking()) {
						super.method_17162(matrixStack, layeredVertexConsumerStorage, i, illusionerEntity, f, g, h, j, k, l, m);
					}
				}
			}
		);
		this.model.method_2812().visible = true;
	}

	public Identifier method_3990(IllusionerEntity illusionerEntity) {
		return SKIN;
	}

	public void method_3991(
		IllusionerEntity illusionerEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		if (illusionerEntity.isInvisible()) {
			Vec3d[] vec3ds = illusionerEntity.method_7065(h);
			float i = this.getAge(illusionerEntity, h);

			for (int j = 0; j < vec3ds.length; j++) {
				matrixStack.push();
				matrixStack.translate(
					vec3ds[j].x + (double)MathHelper.cos((float)j + i * 0.5F) * 0.025,
					vec3ds[j].y + (double)MathHelper.cos((float)j + i * 0.75F) * 0.0125,
					vec3ds[j].z + (double)MathHelper.cos((float)j + i * 0.7F) * 0.025
				);
				super.method_4072(illusionerEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
				matrixStack.pop();
			}
		} else {
			super.method_4072(illusionerEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		}
	}

	protected boolean method_3988(IllusionerEntity illusionerEntity, boolean bl) {
		return true;
	}
}

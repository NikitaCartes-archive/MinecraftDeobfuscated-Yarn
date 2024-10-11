package net.minecraft.client.render.entity;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderer extends IllagerEntityRenderer<IllusionerEntity, IllusionerEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/illusioner.png");

	public IllusionerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.ILLUSIONER)), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<IllusionerEntityRenderState, IllagerEntityModel<IllusionerEntityRenderState>>(this, context.getItemRenderer()) {
				public void render(
					MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IllusionerEntityRenderState illusionerEntityRenderState, float f, float g
				) {
					if (illusionerEntityRenderState.spellcasting || illusionerEntityRenderState.attacking) {
						super.render(matrixStack, vertexConsumerProvider, i, illusionerEntityRenderState, f, g);
					}
				}
			}
		);
		this.model.getHat().visible = true;
	}

	public Identifier getTexture(IllusionerEntityRenderState illusionerEntityRenderState) {
		return TEXTURE;
	}

	public IllusionerEntityRenderState createRenderState() {
		return new IllusionerEntityRenderState();
	}

	public void updateRenderState(IllusionerEntity illusionerEntity, IllusionerEntityRenderState illusionerEntityRenderState, float f) {
		super.updateRenderState(illusionerEntity, illusionerEntityRenderState, f);
		Vec3d[] vec3ds = illusionerEntity.getMirrorCopyOffsets(f);
		illusionerEntityRenderState.mirrorCopyOffsets = (Vec3d[])Arrays.copyOf(vec3ds, vec3ds.length);
		illusionerEntityRenderState.spellcasting = illusionerEntity.isSpellcasting();
	}

	public void render(IllusionerEntityRenderState illusionerEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (illusionerEntityRenderState.invisible) {
			Vec3d[] vec3ds = illusionerEntityRenderState.mirrorCopyOffsets;

			for (int j = 0; j < vec3ds.length; j++) {
				matrixStack.push();
				matrixStack.translate(
					vec3ds[j].x + (double)MathHelper.cos((float)j + illusionerEntityRenderState.age * 0.5F) * 0.025,
					vec3ds[j].y + (double)MathHelper.cos((float)j + illusionerEntityRenderState.age * 0.75F) * 0.0125,
					vec3ds[j].z + (double)MathHelper.cos((float)j + illusionerEntityRenderState.age * 0.7F) * 0.025
				);
				super.render(illusionerEntityRenderState, matrixStack, vertexConsumerProvider, i);
				matrixStack.pop();
			}
		} else {
			super.render(illusionerEntityRenderState, matrixStack, vertexConsumerProvider, i);
		}
	}

	protected boolean isVisible(IllusionerEntityRenderState illusionerEntityRenderState) {
		return true;
	}

	protected Box getBoundingBox(IllusionerEntity illusionerEntity) {
		return super.getBoundingBox(illusionerEntity).expand(3.0, 0.0, 3.0);
	}
}

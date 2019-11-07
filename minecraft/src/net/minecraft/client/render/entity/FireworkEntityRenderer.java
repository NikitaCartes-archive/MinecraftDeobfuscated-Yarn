package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity> {
	private final ItemRenderer itemRenderer;

	public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3968(FireworkEntity fireworkEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-this.renderManager.cameraYaw));
		float h = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch;
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(h));
		if (fireworkEntity.wasShotAtAngle()) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F));
		} else {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
		}

		this.itemRenderer.method_23178(fireworkEntity.getStack(), ModelTransformation.Type.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
		matrixStack.pop();
		super.render(fireworkEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier method_3969(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

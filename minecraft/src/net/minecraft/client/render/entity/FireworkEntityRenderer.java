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

	public void render(FireworkEntity fireworkEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(this.renderManager.method_24197());
		if (fireworkEntity.wasShotAtAngle()) {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		}

		this.itemRenderer.method_23178(fireworkEntity.getStack(), ModelTransformation.Type.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
		matrixStack.pop();
		super.render(fireworkEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

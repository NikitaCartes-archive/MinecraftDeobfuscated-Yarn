package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity> {
	private final ItemRenderer itemRenderer;

	public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3968(
		FireworkEntity fireworkEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-this.renderManager.cameraYaw, true));
		matrixStack.multiply(
			Vector3f.POSITIVE_X.getRotationQuaternion((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, true)
		);
		if (fireworkEntity.wasShotAtAngle()) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
		} else {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F, true));
		}

		this.itemRenderer
			.method_23178(fireworkEntity.getStack(), ModelTransformation.Type.GROUND, fireworkEntity.getLightmapCoordinates(), matrixStack, layeredVertexConsumerStorage);
		matrixStack.pop();
		super.render(fireworkEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_3969(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

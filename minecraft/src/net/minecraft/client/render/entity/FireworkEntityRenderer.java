package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkRocketEntity> {
	private final ItemRenderer itemRenderer;

	public FireworkEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	public void render(FireworkRocketEntity fireworkRocketEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(this.dispatcher.getRotation());
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		if (fireworkRocketEntity.wasShotAtAngle()) {
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		}

		this.itemRenderer
			.renderItem(
				fireworkRocketEntity.getStack(),
				ModelTransformation.Mode.GROUND,
				i,
				OverlayTexture.DEFAULT_UV,
				matrixStack,
				vertexConsumerProvider,
				fireworkRocketEntity.getId()
			);
		matrixStack.pop();
		super.render(fireworkRocketEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(FireworkRocketEntity fireworkRocketEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

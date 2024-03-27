package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class OminousItemSpawnerEntityRenderer extends EntityRenderer<OminousItemSpawnerEntity> {
	private static final float field_50231 = 40.0F;
	private static final int field_50232 = 50;
	private final ItemRenderer itemRenderer;

	protected OminousItemSpawnerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	public Identifier getTexture(OminousItemSpawnerEntity ominousItemSpawnerEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public void render(
		OminousItemSpawnerEntity ominousItemSpawnerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		ItemStack itemStack = ominousItemSpawnerEntity.getItem();
		if (!itemStack.isEmpty()) {
			matrixStack.push();
			if (ominousItemSpawnerEntity.age <= 50) {
				float h = Math.min((float)ominousItemSpawnerEntity.age + g, 50.0F) / 50.0F;
				matrixStack.scale(h, h, h);
			}

			World world = ominousItemSpawnerEntity.getWorld();
			float j = MathHelper.wrapDegrees((float)(world.getTime() - 1L)) * 40.0F;
			float k = MathHelper.wrapDegrees((float)world.getTime()) * 40.0F;
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerpAngleDegrees(g, j, k)));
			ItemEntityRenderer.renderStack(this.itemRenderer, matrixStack, vertexConsumerProvider, 15728880, itemStack, world.random, world);
			matrixStack.pop();
		}
	}
}

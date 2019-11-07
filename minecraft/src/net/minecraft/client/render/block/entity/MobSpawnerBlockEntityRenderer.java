package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer extends BlockEntityRenderer<MobSpawnerBlockEntity> {
	public MobSpawnerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3589(
		MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
	) {
		matrixStack.push();
		matrixStack.translate(0.5, 0.0, 0.5);
		MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
		Entity entity = mobSpawnerLogic.getRenderedEntity();
		if (entity != null) {
			float g = 0.53125F;
			float h = Math.max(entity.getWidth(), entity.getHeight());
			if ((double)h > 1.0) {
				g /= h;
			}

			matrixStack.translate(0.0, 0.4F, 0.0);
			matrixStack.multiply(
				Vector3f.POSITIVE_Y.getRotationQuaternion((float)MathHelper.lerp((double)f, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0F)
			);
			matrixStack.translate(0.0, -0.2F, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-30.0F));
			matrixStack.scale(g, g, g);
			MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0, 0.0, 0.0, 0.0F, f, matrixStack, vertexConsumerProvider, i);
		}

		matrixStack.pop();
	}
}

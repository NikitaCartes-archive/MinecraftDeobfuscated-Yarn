package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer implements BlockEntityRenderer<MobSpawnerBlockEntity> {
	private final EntityRenderDispatcher entityRenderDispatcher;

	public MobSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
	}

	public void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		World world = mobSpawnerBlockEntity.getWorld();
		if (world != null) {
			MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
			Entity entity = mobSpawnerLogic.getRenderedEntity(world, mobSpawnerBlockEntity.getPos());
			if (entity != null) {
				render(f, matrixStack, vertexConsumerProvider, i, entity, this.entityRenderDispatcher, mobSpawnerLogic.getLastRotation(), mobSpawnerLogic.getRotation());
			}
		}
	}

	public static void render(
		float tickDelta,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		Entity entity,
		EntityRenderDispatcher entityRenderDispatcher,
		double lastRotation,
		double rotation
	) {
		matrices.push();
		matrices.translate(0.5F, 0.0F, 0.5F);
		float f = 0.53125F;
		float g = Math.max(entity.getWidth(), entity.getHeight());
		if ((double)g > 1.0) {
			f /= g;
		}

		matrices.translate(0.0F, 0.4F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)MathHelper.lerp((double)tickDelta, lastRotation, rotation) * 10.0F));
		matrices.translate(0.0F, -0.2F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0F));
		matrices.scale(f, f, f);
		entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, tickDelta, matrices, vertexConsumers, light);
		matrices.pop();
	}
}

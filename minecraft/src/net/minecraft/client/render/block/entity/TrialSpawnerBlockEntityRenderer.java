package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class TrialSpawnerBlockEntityRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
	private final EntityRenderDispatcher entityRenderDispatcher;

	public TrialSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.entityRenderDispatcher = context.getEntityRenderDispatcher();
	}

	public void render(
		TrialSpawnerBlockEntity trialSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
	) {
		World world = trialSpawnerBlockEntity.getWorld();
		if (world != null) {
			TrialSpawnerLogic trialSpawnerLogic = trialSpawnerBlockEntity.getSpawner();
			TrialSpawnerData trialSpawnerData = trialSpawnerLogic.getData();
			Entity entity = trialSpawnerData.setDisplayEntity(trialSpawnerLogic, world, trialSpawnerLogic.getSpawnerState());
			if (entity != null) {
				MobSpawnerBlockEntityRenderer.render(
					f,
					matrixStack,
					vertexConsumerProvider,
					i,
					entity,
					this.entityRenderDispatcher,
					trialSpawnerData.getLastDisplayEntityRotation(),
					trialSpawnerData.getDisplayEntityRotation()
				);
			}
		}
	}
}

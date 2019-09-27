package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer extends BlockEntityRenderer<MobSpawnerBlockEntity> {
	public MobSpawnerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3589(MobSpawnerBlockEntity mobSpawnerBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		arg.method_22903();
		arg.method_22904(0.5, 0.0, 0.5);
		MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
		Entity entity = mobSpawnerLogic.getRenderedEntity();
		if (entity != null) {
			float h = 0.53125F;
			float j = Math.max(entity.getWidth(), entity.getHeight());
			if ((double)j > 1.0) {
				h /= j;
			}

			arg.method_22904(0.0, 0.4F, 0.0);
			arg.method_22907(
				Vector3f.field_20705.method_23214((float)MathHelper.lerp((double)g, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0F, true)
			);
			arg.method_22904(0.0, -0.2F, 0.0);
			arg.method_22907(Vector3f.field_20703.method_23214(-30.0F, true));
			arg.method_22905(h, h, h);
			entity.setPositionAndAngles(d, e, f, 0.0F, 0.0F);
			MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0, 0.0, 0.0, 0.0F, g, arg, arg2);
		}

		arg.method_22909();
	}
}

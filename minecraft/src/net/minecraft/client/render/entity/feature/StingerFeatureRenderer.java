package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StickingOutThingsFeatureRenderer<T, M> {
	private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

	public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected int getThingCount(T livingEntity) {
		return livingEntity.getStingerCount();
	}

	@Override
	protected void renderThing(class_4587 arg, class_4597 arg2, Entity entity, float f, float g, float h, float i) {
		float j = MathHelper.sqrt(f * f + h * h);
		float k = (float)(Math.atan2((double)f, (double)h) * 180.0F / (float)Math.PI);
		float l = (float)(Math.atan2((double)g, (double)j) * 180.0F / (float)Math.PI);
		arg.method_22904(0.0, 0.0, 0.0);
		arg.method_22907(Vector3f.field_20705.method_23214(k - 90.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(l, true));
		float m = 0.0F;
		float n = 0.125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.03125F;
		arg.method_22907(Vector3f.field_20703.method_23214(45.0F, true));
		arg.method_22905(0.03125F, 0.03125F, 0.03125F);
		arg.method_22904(2.5, 0.0, 0.0);
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(field_20529));
		class_4608.method_23211(lv);

		for (int r = 0; r < 4; r++) {
			arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
			Matrix4f matrix4f = arg.method_22910();
			lv.method_22918(matrix4f, -4.5F, -1.0F, 0.0F).texture(0.0F, 0.0F).next();
			lv.method_22918(matrix4f, 4.5F, -1.0F, 0.0F).texture(0.125F, 0.0F).next();
			lv.method_22918(matrix4f, 4.5F, 1.0F, 0.0F).texture(0.125F, 0.0625F).next();
			lv.method_22918(matrix4f, -4.5F, 1.0F, 0.0F).texture(0.0F, 0.0625F).next();
		}

		lv.method_22923();
	}
}

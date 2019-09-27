package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/shulker/spark.png");
	private final ShulkerBulletEntityModel<ShulkerBulletEntity> model = new ShulkerBulletEntityModel<>();

	public ShulkerBulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4103(ShulkerBulletEntity shulkerBulletEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		float i = MathHelper.method_22859(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
		float j = MathHelper.lerp(h, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
		float k = (float)shulkerBulletEntity.age + h;
		arg.method_22904(0.0, 0.15F, 0.0);
		arg.method_22907(Vector3f.field_20705.method_23214(MathHelper.sin(k * 0.1F) * 180.0F, true));
		arg.method_22907(Vector3f.field_20703.method_23214(MathHelper.cos(k * 0.1F) * 180.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(MathHelper.sin(k * 0.15F) * 360.0F, true));
		float l = 0.03125F;
		arg.method_22905(-1.0F, -1.0F, 1.0F);
		int m = shulkerBulletEntity.getLightmapCoordinates();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SKIN));
		class_4608.method_23211(lv);
		this.model.setAngles(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		this.model.method_22957(arg, lv, m);
		lv.method_22923();
		arg.method_22905(1.5F, 1.5F, 1.5F);
		class_4588 lv2 = arg2.getBuffer(BlockRenderLayer.method_23019(SKIN, true, true, false));
		class_4608.method_23211(lv);
		this.model.method_22957(arg, lv2, m);
		lv.method_22923();
		arg.method_22909();
		super.render(shulkerBulletEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_4105(ShulkerBulletEntity shulkerBulletEntity) {
		return SKIN;
	}
}

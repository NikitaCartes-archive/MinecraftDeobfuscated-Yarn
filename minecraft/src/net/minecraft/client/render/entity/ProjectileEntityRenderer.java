package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity> extends EntityRenderer<T> {
	public ProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3875(T projectileEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22907(Vector3f.field_20705.method_23214(MathHelper.lerp(h, projectileEntity.prevYaw, projectileEntity.yaw) - 90.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(MathHelper.lerp(h, projectileEntity.prevPitch, projectileEntity.pitch), true));
		int i = 0;
		float j = 0.0F;
		float k = 0.5F;
		float l = 0.0F;
		float m = 0.15625F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = 0.15625F;
		float q = 0.3125F;
		float r = 0.05625F;
		float s = (float)projectileEntity.shake - h;
		if (s > 0.0F) {
			float t = -MathHelper.sin(s * 3.0F) * s;
			arg.method_22907(Vector3f.field_20707.method_23214(t, true));
		}

		arg.method_22907(Vector3f.field_20703.method_23214(45.0F, true));
		arg.method_22905(0.05625F, 0.05625F, 0.05625F);
		arg.method_22904(-4.0, 0.0, 0.0);
		int u = projectileEntity.getLightmapCoordinates();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(this.getTexture(projectileEntity)));
		class_4608.method_23211(lv);
		Matrix4f matrix4f = arg.method_22910();
		this.method_23153(matrix4f, lv, -7, -2, -2, 0.0F, 0.15625F, 1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, -2, 2, 0.15625F, 0.15625F, 1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, 2, 2, 0.15625F, 0.3125F, 1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, 2, -2, 0.0F, 0.3125F, 1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, 2, -2, 0.0F, 0.15625F, -1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, 2, 2, 0.15625F, 0.15625F, -1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, -2, 2, 0.15625F, 0.3125F, -1, 0, 0, u);
		this.method_23153(matrix4f, lv, -7, -2, -2, 0.0F, 0.3125F, -1, 0, 0, u);

		for (int v = 0; v < 4; v++) {
			arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
			this.method_23153(matrix4f, lv, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, u);
			this.method_23153(matrix4f, lv, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, u);
			this.method_23153(matrix4f, lv, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, u);
			this.method_23153(matrix4f, lv, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, u);
		}

		lv.method_22923();
		arg.method_22909();
		super.render(projectileEntity, d, e, f, g, h, arg, arg2);
	}

	public void method_23153(Matrix4f matrix4f, class_4588 arg, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
		arg.method_22918(matrix4f, (float)i, (float)j, (float)k)
			.color(255, 255, 255, 255)
			.texture(f, g)
			.method_22916(o)
			.method_22914((float)l, (float)n, (float)m)
			.next();
	}
}

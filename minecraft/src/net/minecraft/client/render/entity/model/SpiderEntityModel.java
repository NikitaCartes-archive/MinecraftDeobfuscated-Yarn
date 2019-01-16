package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3583;
	private final Cuboid field_3585;
	private final Cuboid field_3584;
	private final Cuboid field_3580;
	private final Cuboid field_3578;
	private final Cuboid field_3586;
	private final Cuboid field_3577;
	private final Cuboid field_3579;
	private final Cuboid field_3581;
	private final Cuboid field_3576;
	private final Cuboid field_3582;

	public SpiderEntityModel() {
		float f = 0.0F;
		int i = 15;
		this.field_3583 = new Cuboid(this, 32, 4);
		this.field_3583.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		this.field_3583.setRotationPoint(0.0F, 15.0F, -3.0F);
		this.field_3585 = new Cuboid(this, 0, 0);
		this.field_3585.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
		this.field_3585.setRotationPoint(0.0F, 15.0F, 0.0F);
		this.field_3584 = new Cuboid(this, 0, 12);
		this.field_3584.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
		this.field_3584.setRotationPoint(0.0F, 15.0F, 9.0F);
		this.field_3580 = new Cuboid(this, 18, 0);
		this.field_3580.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3580.setRotationPoint(-4.0F, 15.0F, 2.0F);
		this.field_3578 = new Cuboid(this, 18, 0);
		this.field_3578.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3578.setRotationPoint(4.0F, 15.0F, 2.0F);
		this.field_3586 = new Cuboid(this, 18, 0);
		this.field_3586.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3586.setRotationPoint(-4.0F, 15.0F, 1.0F);
		this.field_3577 = new Cuboid(this, 18, 0);
		this.field_3577.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3577.setRotationPoint(4.0F, 15.0F, 1.0F);
		this.field_3579 = new Cuboid(this, 18, 0);
		this.field_3579.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3579.setRotationPoint(-4.0F, 15.0F, 0.0F);
		this.field_3581 = new Cuboid(this, 18, 0);
		this.field_3581.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3581.setRotationPoint(4.0F, 15.0F, 0.0F);
		this.field_3576 = new Cuboid(this, 18, 0);
		this.field_3576.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3576.setRotationPoint(-4.0F, 15.0F, -1.0F);
		this.field_3582 = new Cuboid(this, 18, 0);
		this.field_3582.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_3582.setRotationPoint(4.0F, 15.0F, -1.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3583.render(k);
		this.field_3585.render(k);
		this.field_3584.render(k);
		this.field_3580.render(k);
		this.field_3578.render(k);
		this.field_3586.render(k);
		this.field_3577.render(k);
		this.field_3579.render(k);
		this.field_3581.render(k);
		this.field_3576.render(k);
		this.field_3582.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3583.yaw = i * (float) (Math.PI / 180.0);
		this.field_3583.pitch = j * (float) (Math.PI / 180.0);
		float l = (float) (Math.PI / 4);
		this.field_3580.roll = (float) (-Math.PI / 4);
		this.field_3578.roll = (float) (Math.PI / 4);
		this.field_3586.roll = -0.58119464F;
		this.field_3577.roll = 0.58119464F;
		this.field_3579.roll = -0.58119464F;
		this.field_3581.roll = 0.58119464F;
		this.field_3576.roll = (float) (-Math.PI / 4);
		this.field_3582.roll = (float) (Math.PI / 4);
		float m = -0.0F;
		float n = (float) (Math.PI / 8);
		this.field_3580.yaw = (float) (Math.PI / 4);
		this.field_3578.yaw = (float) (-Math.PI / 4);
		this.field_3586.yaw = (float) (Math.PI / 8);
		this.field_3577.yaw = (float) (-Math.PI / 8);
		this.field_3579.yaw = (float) (-Math.PI / 8);
		this.field_3581.yaw = (float) (Math.PI / 8);
		this.field_3576.yaw = (float) (-Math.PI / 4);
		this.field_3582.yaw = (float) (Math.PI / 4);
		float o = -(MathHelper.cos(f * 0.6662F * 2.0F + 0.0F) * 0.4F) * g;
		float p = -(MathHelper.cos(f * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * g;
		float q = -(MathHelper.cos(f * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * g;
		float r = -(MathHelper.cos(f * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * g;
		float s = Math.abs(MathHelper.sin(f * 0.6662F + 0.0F) * 0.4F) * g;
		float t = Math.abs(MathHelper.sin(f * 0.6662F + (float) Math.PI) * 0.4F) * g;
		float u = Math.abs(MathHelper.sin(f * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * g;
		float v = Math.abs(MathHelper.sin(f * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * g;
		this.field_3580.yaw += o;
		this.field_3578.yaw += -o;
		this.field_3586.yaw += p;
		this.field_3577.yaw += -p;
		this.field_3579.yaw += q;
		this.field_3581.yaw += -q;
		this.field_3576.yaw += r;
		this.field_3582.yaw += -r;
		this.field_3580.roll += s;
		this.field_3578.roll += -s;
		this.field_3586.roll += t;
		this.field_3577.roll += -t;
		this.field_3579.roll += u;
		this.field_3581.roll += -u;
		this.field_3576.roll += v;
		this.field_3582.roll += -v;
	}
}

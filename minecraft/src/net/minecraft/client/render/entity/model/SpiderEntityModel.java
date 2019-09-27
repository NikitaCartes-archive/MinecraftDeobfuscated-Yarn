package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart field_3583;
	private final ModelPart field_3585;
	private final ModelPart field_3584;
	private final ModelPart field_3580;
	private final ModelPart field_3578;
	private final ModelPart field_3586;
	private final ModelPart field_3577;
	private final ModelPart field_3579;
	private final ModelPart field_3581;
	private final ModelPart field_3576;
	private final ModelPart field_3582;

	public SpiderEntityModel() {
		float f = 0.0F;
		int i = 15;
		this.field_3583 = new ModelPart(this, 32, 4);
		this.field_3583.addCuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F);
		this.field_3583.setRotationPoint(0.0F, 15.0F, -3.0F);
		this.field_3585 = new ModelPart(this, 0, 0);
		this.field_3585.addCuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F);
		this.field_3585.setRotationPoint(0.0F, 15.0F, 0.0F);
		this.field_3584 = new ModelPart(this, 0, 12);
		this.field_3584.addCuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, 0.0F);
		this.field_3584.setRotationPoint(0.0F, 15.0F, 9.0F);
		this.field_3580 = new ModelPart(this, 18, 0);
		this.field_3580.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3580.setRotationPoint(-4.0F, 15.0F, 2.0F);
		this.field_3578 = new ModelPart(this, 18, 0);
		this.field_3578.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3578.setRotationPoint(4.0F, 15.0F, 2.0F);
		this.field_3586 = new ModelPart(this, 18, 0);
		this.field_3586.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3586.setRotationPoint(-4.0F, 15.0F, 1.0F);
		this.field_3577 = new ModelPart(this, 18, 0);
		this.field_3577.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3577.setRotationPoint(4.0F, 15.0F, 1.0F);
		this.field_3579 = new ModelPart(this, 18, 0);
		this.field_3579.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3579.setRotationPoint(-4.0F, 15.0F, 0.0F);
		this.field_3581 = new ModelPart(this, 18, 0);
		this.field_3581.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3581.setRotationPoint(4.0F, 15.0F, 0.0F);
		this.field_3576 = new ModelPart(this, 18, 0);
		this.field_3576.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3576.setRotationPoint(-4.0F, 15.0F, -1.0F);
		this.field_3582 = new ModelPart(this, 18, 0);
		this.field_3582.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.field_3582.setRotationPoint(4.0F, 15.0F, -1.0F);
	}

	@Override
	public Iterable<ModelPart> method_22960() {
		return ImmutableList.<ModelPart>of(
			this.field_3583,
			this.field_3585,
			this.field_3584,
			this.field_3580,
			this.field_3578,
			this.field_3586,
			this.field_3577,
			this.field_3579,
			this.field_3581,
			this.field_3576,
			this.field_3582
		);
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

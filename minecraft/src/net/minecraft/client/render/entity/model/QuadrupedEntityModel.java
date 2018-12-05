package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel extends Model {
	protected Cuboid head;
	protected Cuboid body;
	protected Cuboid leg1;
	protected Cuboid leg2;
	protected Cuboid leg3;
	protected Cuboid leg4;
	protected float field_3540 = 8.0F;
	protected float field_3537 = 4.0F;

	public QuadrupedEntityModel(int i, float f) {
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
		this.head.setRotationPoint(0.0F, (float)(18 - i), -6.0F);
		this.body = new Cuboid(this, 28, 8);
		this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
		this.body.setRotationPoint(0.0F, (float)(17 - i), 2.0F);
		this.leg1 = new Cuboid(this, 0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.leg1.setRotationPoint(-3.0F, (float)(24 - i), 7.0F);
		this.leg2 = new Cuboid(this, 0, 16);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.leg2.setRotationPoint(3.0F, (float)(24 - i), 7.0F);
		this.leg3 = new Cuboid(this, 0, 16);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.leg3.setRotationPoint(-3.0F, (float)(24 - i), -5.0F);
		this.leg4 = new Cuboid(this, 0, 16);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.leg4.setRotationPoint(3.0F, (float)(24 - i), -5.0F);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.leg1.render(k);
			this.leg2.render(k);
			this.leg3.render(k);
			this.leg4.render(k);
			GlStateManager.popMatrix();
		} else {
			this.head.render(k);
			this.body.render(k);
			this.leg1.render(k);
			this.leg2.render(k);
			this.leg3.render(k);
			this.leg4.render(k);
		}
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.body.pitch = (float) (Math.PI / 2);
		this.leg1.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.leg2.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leg3.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leg4.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
	}
}

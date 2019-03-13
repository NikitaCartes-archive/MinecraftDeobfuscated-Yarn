package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity> extends EntityModel<T> {
	protected Cuboid field_3535;
	protected Cuboid field_3538;
	protected Cuboid field_3536;
	protected Cuboid field_3534;
	protected Cuboid field_3533;
	protected Cuboid field_3539;
	protected float field_3540 = 8.0F;
	protected float field_3537 = 4.0F;

	public QuadrupedEntityModel(int i, float f) {
		this.field_3535 = new Cuboid(this, 0, 0);
		this.field_3535.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
		this.field_3535.setRotationPoint(0.0F, (float)(18 - i), -6.0F);
		this.field_3538 = new Cuboid(this, 28, 8);
		this.field_3538.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
		this.field_3538.setRotationPoint(0.0F, (float)(17 - i), 2.0F);
		this.field_3536 = new Cuboid(this, 0, 16);
		this.field_3536.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3536.setRotationPoint(-3.0F, (float)(24 - i), 7.0F);
		this.field_3534 = new Cuboid(this, 0, 16);
		this.field_3534.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3534.setRotationPoint(3.0F, (float)(24 - i), 7.0F);
		this.field_3533 = new Cuboid(this, 0, 16);
		this.field_3533.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3533.setRotationPoint(-3.0F, (float)(24 - i), -5.0F);
		this.field_3539 = new Cuboid(this, 0, 16);
		this.field_3539.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3539.setRotationPoint(3.0F, (float)(24 - i), -5.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			this.field_3535.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3538.render(k);
			this.field_3536.render(k);
			this.field_3534.render(k);
			this.field_3533.render(k);
			this.field_3539.render(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3535.render(k);
			this.field_3538.render(k);
			this.field_3536.render(k);
			this.field_3534.render(k);
			this.field_3533.render(k);
			this.field_3539.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3535.pitch = j * (float) (Math.PI / 180.0);
		this.field_3535.yaw = i * (float) (Math.PI / 180.0);
		this.field_3538.pitch = (float) (Math.PI / 2);
		this.field_3536.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_3534.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3533.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3539.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
	}
}

package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaEntityModel<T extends PandaEntity> extends QuadrupedEntityModel<T> {
	private float field_3470;
	private float field_3469;
	private float field_3468;

	public PandaEntityModel(int i, float f) {
		super(i, f);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3535 = new Cuboid(this, 0, 6);
		this.field_3535.addBox(-6.5F, -5.0F, -4.0F, 13, 10, 9);
		this.field_3535.setRotationPoint(0.0F, 11.5F, -17.0F);
		this.field_3535.setTextureOffset(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7, 5, 2);
		this.field_3535.setTextureOffset(52, 25).addBox(-8.5F, -8.0F, -1.0F, 5, 4, 1);
		this.field_3535.setTextureOffset(52, 25).addBox(3.5F, -8.0F, -1.0F, 5, 4, 1);
		this.field_3538 = new Cuboid(this, 0, 25);
		this.field_3538.addBox(-9.5F, -13.0F, -6.5F, 19, 26, 13);
		this.field_3538.setRotationPoint(0.0F, 10.0F, 0.0F);
		int j = 9;
		int k = 6;
		this.field_3536 = new Cuboid(this, 40, 0);
		this.field_3536.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3536.setRotationPoint(-5.5F, 15.0F, 9.0F);
		this.field_3534 = new Cuboid(this, 40, 0);
		this.field_3534.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3534.setRotationPoint(5.5F, 15.0F, 9.0F);
		this.field_3533 = new Cuboid(this, 40, 0);
		this.field_3533.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3533.setRotationPoint(-5.5F, 15.0F, -9.0F);
		this.field_3539 = new Cuboid(this, 40, 0);
		this.field_3539.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3539.setRotationPoint(5.5F, 15.0F, -9.0F);
	}

	public void method_17102(T pandaEntity, float f, float g, float h) {
		super.animateModel(pandaEntity, f, g, h);
		this.field_3470 = pandaEntity.method_6534(h);
		this.field_3469 = pandaEntity.method_6555(h);
		this.field_3468 = pandaEntity.isChild() ? 0.0F : pandaEntity.method_6560(h);
	}

	public void method_17103(T pandaEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(pandaEntity, f, g, h, i, j, k);
		boolean bl = pandaEntity.method_6521() > 0;
		boolean bl2 = pandaEntity.method_6545();
		int l = pandaEntity.method_6532();
		boolean bl3 = pandaEntity.method_6527();
		boolean bl4 = pandaEntity.method_6524();
		if (bl) {
			this.field_3535.yaw = 0.35F * MathHelper.sin(0.6F * h);
			this.field_3535.roll = 0.35F * MathHelper.sin(0.6F * h);
			this.field_3533.pitch = -0.75F * MathHelper.sin(0.3F * h);
			this.field_3539.pitch = 0.75F * MathHelper.sin(0.3F * h);
		} else {
			this.field_3535.roll = 0.0F;
		}

		if (bl2) {
			if (l < 15) {
				this.field_3535.pitch = (float) (-Math.PI / 4) * (float)l / 14.0F;
			} else if (l < 20) {
				float m = (float)((l - 15) / 5);
				this.field_3535.pitch = (float) (-Math.PI / 4) + (float) (Math.PI / 4) * m;
			}
		}

		if (this.field_3470 > 0.0F) {
			this.field_3538.pitch = this.method_2822(this.field_3538.pitch, 1.7407963F, this.field_3470);
			this.field_3535.pitch = this.method_2822(this.field_3535.pitch, (float) (Math.PI / 2), this.field_3470);
			this.field_3533.roll = -0.27079642F;
			this.field_3539.roll = 0.27079642F;
			this.field_3536.roll = 0.5707964F;
			this.field_3534.roll = -0.5707964F;
			if (bl3) {
				this.field_3535.pitch = (float) (Math.PI / 2) + 0.2F * MathHelper.sin(h * 0.6F);
				this.field_3533.pitch = -0.4F - 0.2F * MathHelper.sin(h * 0.6F);
				this.field_3539.pitch = -0.4F - 0.2F * MathHelper.sin(h * 0.6F);
			}

			if (bl4) {
				this.field_3535.pitch = 2.1707964F;
				this.field_3533.pitch = -0.9F;
				this.field_3539.pitch = -0.9F;
			}
		} else {
			this.field_3536.roll = 0.0F;
			this.field_3534.roll = 0.0F;
			this.field_3533.roll = 0.0F;
			this.field_3539.roll = 0.0F;
		}

		if (this.field_3469 > 0.0F) {
			this.field_3536.pitch = -0.6F * MathHelper.sin(h * 0.15F);
			this.field_3534.pitch = 0.6F * MathHelper.sin(h * 0.15F);
			this.field_3533.pitch = 0.3F * MathHelper.sin(h * 0.25F);
			this.field_3539.pitch = -0.3F * MathHelper.sin(h * 0.25F);
			this.field_3535.pitch = this.method_2822(this.field_3535.pitch, (float) (Math.PI / 2), this.field_3469);
		}

		if (this.field_3468 > 0.0F) {
			this.field_3535.pitch = this.method_2822(this.field_3535.pitch, 2.0561945F, this.field_3468);
			this.field_3536.pitch = -0.5F * MathHelper.sin(h * 0.5F);
			this.field_3534.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.field_3533.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.field_3539.pitch = -0.5F * MathHelper.sin(h * 0.5F);
		}
	}

	protected float method_2822(float f, float g, float h) {
		float i = g - f;

		while (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		while (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}

	public void method_17104(T pandaEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17103(pandaEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 3.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.6F;
			GlStateManager.scalef(0.5555555F, 0.5555555F, 0.5555555F);
			GlStateManager.translatef(0.0F, 23.0F * k, 0.3F);
			this.field_3535.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.translatef(0.0F, 49.0F * k, 0.0F);
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
}

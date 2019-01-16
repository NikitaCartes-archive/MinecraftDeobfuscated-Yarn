package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends EntityModel<T> {
	protected final Cuboid field_3439;
	protected final Cuboid field_3441;
	protected final Cuboid field_3440;
	protected final Cuboid field_3438;
	protected final Cuboid field_3436;
	protected final Cuboid field_3442;
	protected final Cuboid field_3435;
	protected final Cuboid field_3437;
	protected int field_3434 = 1;

	public OcelotEntityModel(float f) {
		this.field_3435 = new Cuboid(this, "head");
		this.field_3435.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, f, 0, 0);
		this.field_3435.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, f, 0, 24);
		this.field_3435.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, f, 0, 10);
		this.field_3435.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, f, 6, 10);
		this.field_3435.setRotationPoint(0.0F, 15.0F, -9.0F);
		this.field_3437 = new Cuboid(this, 20, 0);
		this.field_3437.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, f);
		this.field_3437.setRotationPoint(0.0F, 12.0F, -10.0F);
		this.field_3436 = new Cuboid(this, 0, 15);
		this.field_3436.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, f);
		this.field_3436.pitch = 0.9F;
		this.field_3436.setRotationPoint(0.0F, 15.0F, 8.0F);
		this.field_3442 = new Cuboid(this, 4, 15);
		this.field_3442.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, f);
		this.field_3442.setRotationPoint(0.0F, 20.0F, 14.0F);
		this.field_3439 = new Cuboid(this, 8, 13);
		this.field_3439.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, f);
		this.field_3439.setRotationPoint(1.1F, 18.0F, 5.0F);
		this.field_3441 = new Cuboid(this, 8, 13);
		this.field_3441.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, f);
		this.field_3441.setRotationPoint(-1.1F, 18.0F, 5.0F);
		this.field_3440 = new Cuboid(this, 40, 0);
		this.field_3440.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, f);
		this.field_3440.setRotationPoint(1.2F, 13.8F, -5.0F);
		this.field_3438 = new Cuboid(this, 40, 0);
		this.field_3438.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, f);
		this.field_3438.setRotationPoint(-1.2F, 13.8F, -5.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 10.0F * k, 4.0F * k);
			this.field_3435.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3437.render(k);
			this.field_3439.render(k);
			this.field_3441.render(k);
			this.field_3440.render(k);
			this.field_3438.render(k);
			this.field_3436.render(k);
			this.field_3442.render(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3435.render(k);
			this.field_3437.render(k);
			this.field_3436.render(k);
			this.field_3442.render(k);
			this.field_3439.render(k);
			this.field_3441.render(k);
			this.field_3440.render(k);
			this.field_3438.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3435.pitch = j * (float) (Math.PI / 180.0);
		this.field_3435.yaw = i * (float) (Math.PI / 180.0);
		if (this.field_3434 != 3) {
			this.field_3437.pitch = (float) (Math.PI / 2);
			if (this.field_3434 == 2) {
				this.field_3439.pitch = MathHelper.cos(f * 0.6662F) * g;
				this.field_3441.pitch = MathHelper.cos(f * 0.6662F + 0.3F) * g;
				this.field_3440.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI + 0.3F) * g;
				this.field_3438.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.field_3442.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(f) * g;
			} else {
				this.field_3439.pitch = MathHelper.cos(f * 0.6662F) * g;
				this.field_3441.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.field_3440.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.field_3438.pitch = MathHelper.cos(f * 0.6662F) * g;
				if (this.field_3434 == 1) {
					this.field_3442.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(f) * g;
				} else {
					this.field_3442.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(f) * g;
				}
			}
		}
	}

	@Override
	public void animateModel(T entity, float f, float g, float h) {
		this.field_3437.rotationPointY = 12.0F;
		this.field_3437.rotationPointZ = -10.0F;
		this.field_3435.rotationPointY = 15.0F;
		this.field_3435.rotationPointZ = -9.0F;
		this.field_3436.rotationPointY = 15.0F;
		this.field_3436.rotationPointZ = 8.0F;
		this.field_3442.rotationPointY = 20.0F;
		this.field_3442.rotationPointZ = 14.0F;
		this.field_3440.rotationPointY = 13.8F;
		this.field_3440.rotationPointZ = -5.0F;
		this.field_3438.rotationPointY = 13.8F;
		this.field_3438.rotationPointZ = -5.0F;
		this.field_3439.rotationPointY = 18.0F;
		this.field_3439.rotationPointZ = 5.0F;
		this.field_3441.rotationPointY = 18.0F;
		this.field_3441.rotationPointZ = 5.0F;
		this.field_3436.pitch = 0.9F;
		if (entity.isSneaking()) {
			this.field_3437.rotationPointY++;
			this.field_3435.rotationPointY += 2.0F;
			this.field_3436.rotationPointY++;
			this.field_3442.rotationPointY += -4.0F;
			this.field_3442.rotationPointZ += 2.0F;
			this.field_3436.pitch = (float) (Math.PI / 2);
			this.field_3442.pitch = (float) (Math.PI / 2);
			this.field_3434 = 0;
		} else if (entity.isSprinting()) {
			this.field_3442.rotationPointY = this.field_3436.rotationPointY;
			this.field_3442.rotationPointZ += 2.0F;
			this.field_3436.pitch = (float) (Math.PI / 2);
			this.field_3442.pitch = (float) (Math.PI / 2);
			this.field_3434 = 2;
		} else {
			this.field_3434 = 1;
		}
	}
}

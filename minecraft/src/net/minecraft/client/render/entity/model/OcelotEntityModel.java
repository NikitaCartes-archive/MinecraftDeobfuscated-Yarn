package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends EntityModel<T> {
	protected final ModelPart frontLegLeft;
	protected final ModelPart frontLegRight;
	protected final ModelPart backLegLeft;
	protected final ModelPart backLegRight;
	protected final ModelPart tail1;
	protected final ModelPart tail2;
	protected final ModelPart head;
	protected final ModelPart body;
	protected int animationState = 1;

	public OcelotEntityModel(float f) {
		this.head = new ModelPart(this, "head");
		this.head.addCuboid("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, f, 0, 0);
		this.head.addCuboid("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, f, 0, 24);
		this.head.addCuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, f, 0, 10);
		this.head.addCuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, f, 6, 10);
		this.head.setRotationPoint(0.0F, 15.0F, -9.0F);
		this.body = new ModelPart(this, 20, 0);
		this.body.addCuboid(-2.0F, 3.0F, -8.0F, 4, 16, 6, f);
		this.body.setRotationPoint(0.0F, 12.0F, -10.0F);
		this.tail1 = new ModelPart(this, 0, 15);
		this.tail1.addCuboid(-0.5F, 0.0F, 0.0F, 1, 8, 1, f);
		this.tail1.pitch = 0.9F;
		this.tail1.setRotationPoint(0.0F, 15.0F, 8.0F);
		this.tail2 = new ModelPart(this, 4, 15);
		this.tail2.addCuboid(-0.5F, 0.0F, 0.0F, 1, 8, 1, f);
		this.tail2.setRotationPoint(0.0F, 20.0F, 14.0F);
		this.frontLegLeft = new ModelPart(this, 8, 13);
		this.frontLegLeft.addCuboid(-1.0F, 0.0F, 1.0F, 2, 6, 2, f);
		this.frontLegLeft.setRotationPoint(1.1F, 18.0F, 5.0F);
		this.frontLegRight = new ModelPart(this, 8, 13);
		this.frontLegRight.addCuboid(-1.0F, 0.0F, 1.0F, 2, 6, 2, f);
		this.frontLegRight.setRotationPoint(-1.1F, 18.0F, 5.0F);
		this.backLegLeft = new ModelPart(this, 40, 0);
		this.backLegLeft.addCuboid(-1.0F, 0.0F, 0.0F, 2, 10, 2, f);
		this.backLegLeft.setRotationPoint(1.2F, 14.1F, -5.0F);
		this.backLegRight = new ModelPart(this, 40, 0);
		this.backLegRight.addCuboid(-1.0F, 0.0F, 0.0F, 2, 10, 2, f);
		this.backLegRight.setRotationPoint(-1.2F, 14.1F, -5.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.75F, 0.75F, 0.75F);
			RenderSystem.translatef(0.0F, 10.0F * k, 4.0F * k);
			this.head.render(k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			RenderSystem.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.frontLegLeft.render(k);
			this.frontLegRight.render(k);
			this.backLegLeft.render(k);
			this.backLegRight.render(k);
			this.tail1.render(k);
			this.tail2.render(k);
			RenderSystem.popMatrix();
		} else {
			this.head.render(k);
			this.body.render(k);
			this.tail1.render(k);
			this.tail2.render(k);
			this.frontLegLeft.render(k);
			this.frontLegRight.render(k);
			this.backLegLeft.render(k);
			this.backLegRight.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		if (this.animationState != 3) {
			this.body.pitch = (float) (Math.PI / 2);
			if (this.animationState == 2) {
				this.frontLegLeft.pitch = MathHelper.cos(f * 0.6662F) * g;
				this.frontLegRight.pitch = MathHelper.cos(f * 0.6662F + 0.3F) * g;
				this.backLegLeft.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI + 0.3F) * g;
				this.backLegRight.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.tail2.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(f) * g;
			} else {
				this.frontLegLeft.pitch = MathHelper.cos(f * 0.6662F) * g;
				this.frontLegRight.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.backLegLeft.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * g;
				this.backLegRight.pitch = MathHelper.cos(f * 0.6662F) * g;
				if (this.animationState == 1) {
					this.tail2.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(f) * g;
				} else {
					this.tail2.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(f) * g;
				}
			}
		}
	}

	@Override
	public void animateModel(T entity, float f, float g, float h) {
		this.body.rotationPointY = 12.0F;
		this.body.rotationPointZ = -10.0F;
		this.head.rotationPointY = 15.0F;
		this.head.rotationPointZ = -9.0F;
		this.tail1.rotationPointY = 15.0F;
		this.tail1.rotationPointZ = 8.0F;
		this.tail2.rotationPointY = 20.0F;
		this.tail2.rotationPointZ = 14.0F;
		this.backLegLeft.rotationPointY = 14.1F;
		this.backLegLeft.rotationPointZ = -5.0F;
		this.backLegRight.rotationPointY = 14.1F;
		this.backLegRight.rotationPointZ = -5.0F;
		this.frontLegLeft.rotationPointY = 18.0F;
		this.frontLegLeft.rotationPointZ = 5.0F;
		this.frontLegRight.rotationPointY = 18.0F;
		this.frontLegRight.rotationPointZ = 5.0F;
		this.tail1.pitch = 0.9F;
		if (entity.isInSneakingPose()) {
			this.body.rotationPointY++;
			this.head.rotationPointY += 2.0F;
			this.tail1.rotationPointY++;
			this.tail2.rotationPointY += -4.0F;
			this.tail2.rotationPointZ += 2.0F;
			this.tail1.pitch = (float) (Math.PI / 2);
			this.tail2.pitch = (float) (Math.PI / 2);
			this.animationState = 0;
		} else if (entity.isSprinting()) {
			this.tail2.rotationPointY = this.tail1.rotationPointY;
			this.tail2.rotationPointZ += 2.0F;
			this.tail1.pitch = (float) (Math.PI / 2);
			this.tail2.pitch = (float) (Math.PI / 2);
			this.animationState = 2;
		} else {
			this.animationState = 1;
		}
	}
}

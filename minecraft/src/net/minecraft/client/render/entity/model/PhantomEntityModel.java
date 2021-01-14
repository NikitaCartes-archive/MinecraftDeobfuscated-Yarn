package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PhantomEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart body;
	private final ModelPart leftWingBase;
	private final ModelPart leftWingTip;
	private final ModelPart rightWingBase;
	private final ModelPart rightWingTip;
	private final ModelPart tailBase;
	private final ModelPart tailTip;

	public PhantomEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.body = new ModelPart(this, 0, 8);
		this.body.addCuboid(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F);
		this.tailBase = new ModelPart(this, 3, 20);
		this.tailBase.addCuboid(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F);
		this.tailBase.setPivot(0.0F, -2.0F, 1.0F);
		this.body.addChild(this.tailBase);
		this.tailTip = new ModelPart(this, 4, 29);
		this.tailTip.addCuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F);
		this.tailTip.setPivot(0.0F, 0.5F, 6.0F);
		this.tailBase.addChild(this.tailTip);
		this.leftWingBase = new ModelPart(this, 23, 12);
		this.leftWingBase.addCuboid(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
		this.leftWingBase.setPivot(2.0F, -2.0F, -8.0F);
		this.leftWingTip = new ModelPart(this, 16, 24);
		this.leftWingTip.addCuboid(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
		this.leftWingTip.setPivot(6.0F, 0.0F, 0.0F);
		this.leftWingBase.addChild(this.leftWingTip);
		this.rightWingBase = new ModelPart(this, 23, 12);
		this.rightWingBase.mirror = true;
		this.rightWingBase.addCuboid(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
		this.rightWingBase.setPivot(-3.0F, -2.0F, -8.0F);
		this.rightWingTip = new ModelPart(this, 16, 24);
		this.rightWingTip.mirror = true;
		this.rightWingTip.addCuboid(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
		this.rightWingTip.setPivot(-6.0F, 0.0F, 0.0F);
		this.rightWingBase.addChild(this.rightWingTip);
		this.leftWingBase.roll = 0.1F;
		this.leftWingTip.roll = 0.1F;
		this.rightWingBase.roll = -0.1F;
		this.rightWingTip.roll = -0.1F;
		this.body.pitch = -0.1F;
		ModelPart modelPart = new ModelPart(this, 0, 0);
		modelPart.addCuboid(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F);
		modelPart.setPivot(0.0F, 1.0F, -7.0F);
		modelPart.pitch = 0.2F;
		this.body.addChild(modelPart);
		this.body.addChild(this.leftWingBase);
		this.body.addChild(this.rightWingBase);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.body);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = ((float)(entity.getEntityId() * 3) + animationProgress) * 0.13F;
		float g = 16.0F;
		this.leftWingBase.roll = MathHelper.cos(f) * 16.0F * (float) (Math.PI / 180.0);
		this.leftWingTip.roll = MathHelper.cos(f) * 16.0F * (float) (Math.PI / 180.0);
		this.rightWingBase.roll = -this.leftWingBase.roll;
		this.rightWingTip.roll = -this.leftWingTip.roll;
		this.tailBase.pitch = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
		this.tailTip.pitch = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
	}
}

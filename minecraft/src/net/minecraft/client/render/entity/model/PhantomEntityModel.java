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
	private final ModelPart leftWing;
	private final ModelPart leftWingTip;
	private final ModelPart rightWing;
	private final ModelPart rightWingTip;
	private final ModelPart tail;
	private final ModelPart lowerTail;

	public PhantomEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.body = new ModelPart(this, 0, 8);
		this.body.addCuboid(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F);
		this.tail = new ModelPart(this, 3, 20);
		this.tail.addCuboid(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F);
		this.tail.setPivot(0.0F, -2.0F, 1.0F);
		this.body.addChild(this.tail);
		this.lowerTail = new ModelPart(this, 4, 29);
		this.lowerTail.addCuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F);
		this.lowerTail.setPivot(0.0F, 0.5F, 6.0F);
		this.tail.addChild(this.lowerTail);
		this.leftWing = new ModelPart(this, 23, 12);
		this.leftWing.addCuboid(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
		this.leftWing.setPivot(2.0F, -2.0F, -8.0F);
		this.leftWingTip = new ModelPart(this, 16, 24);
		this.leftWingTip.addCuboid(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
		this.leftWingTip.setPivot(6.0F, 0.0F, 0.0F);
		this.leftWing.addChild(this.leftWingTip);
		this.rightWing = new ModelPart(this, 23, 12);
		this.rightWing.mirror = true;
		this.rightWing.addCuboid(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
		this.rightWing.setPivot(-3.0F, -2.0F, -8.0F);
		this.rightWingTip = new ModelPart(this, 16, 24);
		this.rightWingTip.mirror = true;
		this.rightWingTip.addCuboid(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
		this.rightWingTip.setPivot(-6.0F, 0.0F, 0.0F);
		this.rightWing.addChild(this.rightWingTip);
		this.leftWing.roll = 0.1F;
		this.leftWingTip.roll = 0.1F;
		this.rightWing.roll = -0.1F;
		this.rightWingTip.roll = -0.1F;
		this.body.pitch = -0.1F;
		ModelPart modelPart = new ModelPart(this, 0, 0);
		modelPart.addCuboid(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F);
		modelPart.setPivot(0.0F, 1.0F, -7.0F);
		modelPart.pitch = 0.2F;
		this.body.addChild(modelPart);
		this.body.addChild(this.leftWing);
		this.body.addChild(this.rightWing);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.body);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		float f = ((float)(entity.getEntityId() * 3) + age) * 0.13F;
		float g = 16.0F;
		this.leftWing.roll = MathHelper.cos(f) * 16.0F * (float) (Math.PI / 180.0);
		this.leftWingTip.roll = MathHelper.cos(f) * 16.0F * (float) (Math.PI / 180.0);
		this.rightWing.roll = -this.leftWing.roll;
		this.rightWingTip.roll = -this.leftWingTip.roll;
		this.tail.pitch = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
		this.lowerTail.pitch = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
	}
}

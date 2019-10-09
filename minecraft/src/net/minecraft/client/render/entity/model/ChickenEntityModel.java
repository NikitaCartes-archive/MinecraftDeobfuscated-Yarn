package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4592;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity> extends class_4592<T> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart beak;
	private final ModelPart wattle;

	public ChickenEntityModel() {
		super(RenderLayer::getEntityCutoutNoCull, false, 5.0F, 2.0F, 2.0F, 2.0F, 24.0F);
		int i = 16;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, 0.0F);
		this.head.setPivot(0.0F, 15.0F, -4.0F);
		this.beak = new ModelPart(this, 14, 0);
		this.beak.addCuboid(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.0F);
		this.beak.setPivot(0.0F, 15.0F, -4.0F);
		this.wattle = new ModelPart(this, 14, 4);
		this.wattle.addCuboid(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F);
		this.wattle.setPivot(0.0F, 15.0F, -4.0F);
		this.body = new ModelPart(this, 0, 9);
		this.body.addCuboid(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
		this.body.setPivot(0.0F, 16.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 26, 0);
		this.leftLeg.addCuboid(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
		this.leftLeg.setPivot(-2.0F, 19.0F, 1.0F);
		this.rightLeg = new ModelPart(this, 26, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
		this.rightLeg.setPivot(1.0F, 19.0F, 1.0F);
		this.leftWing = new ModelPart(this, 24, 13);
		this.leftWing.addCuboid(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
		this.leftWing.setPivot(-4.0F, 13.0F, 0.0F);
		this.rightWing = new ModelPart(this, 24, 13);
		this.rightWing.addCuboid(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
		this.rightWing.setPivot(4.0F, 13.0F, 0.0F);
	}

	@Override
	protected Iterable<ModelPart> method_22946() {
		return ImmutableList.<ModelPart>of(this.head, this.beak, this.wattle);
	}

	@Override
	protected Iterable<ModelPart> method_22948() {
		return ImmutableList.<ModelPart>of(this.body, this.leftLeg, this.rightLeg, this.leftWing, this.rightWing);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.beak.pitch = this.head.pitch;
		this.beak.yaw = this.head.yaw;
		this.wattle.pitch = this.head.pitch;
		this.wattle.yaw = this.head.yaw;
		this.body.pitch = (float) (Math.PI / 2);
		this.leftLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.rightLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leftWing.roll = h;
		this.rightWing.roll = -h;
	}
}

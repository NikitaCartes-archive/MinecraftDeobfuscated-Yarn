package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CreeperEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart head;
	private final ModelPart helmet;
	private final ModelPart torso;
	private final ModelPart backLeftLeg;
	private final ModelPart backRightLeg;
	private final ModelPart frontLeftLeg;
	private final ModelPart frontRightLeg;

	public CreeperEntityModel() {
		this(0.0F);
	}

	public CreeperEntityModel(float scale) {
		int i = 6;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale);
		this.head.setPivot(0.0F, 6.0F, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale + 0.5F);
		this.helmet.setPivot(0.0F, 6.0F, 0.0F);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale);
		this.torso.setPivot(0.0F, 6.0F, 0.0F);
		this.backLeftLeg = new ModelPart(this, 0, 16);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.backLeftLeg.setPivot(-2.0F, 18.0F, 4.0F);
		this.backRightLeg = new ModelPart(this, 0, 16);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.backRightLeg.setPivot(2.0F, 18.0F, 4.0F);
		this.frontLeftLeg = new ModelPart(this, 0, 16);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.frontLeftLeg.setPivot(-2.0F, 18.0F, -4.0F);
		this.frontRightLeg = new ModelPart(this, 0, 16);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.frontRightLeg.setPivot(2.0F, 18.0F, -4.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.head, this.torso, this.backLeftLeg, this.backRightLeg, this.frontLeftLeg, this.frontRightLeg);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.backLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.backRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.frontLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.frontRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}

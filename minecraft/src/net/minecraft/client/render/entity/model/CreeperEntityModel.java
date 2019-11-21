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
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

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
		this.rightBackLeg = new ModelPart(this, 0, 16);
		this.rightBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.rightBackLeg.setPivot(-2.0F, 18.0F, 4.0F);
		this.leftBackLeg = new ModelPart(this, 0, 16);
		this.leftBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.leftBackLeg.setPivot(2.0F, 18.0F, 4.0F);
		this.rightFrontLeg = new ModelPart(this, 0, 16);
		this.rightFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.rightFrontLeg.setPivot(-2.0F, 18.0F, -4.0F);
		this.leftFrontLeg = new ModelPart(this, 0, 16);
		this.leftFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.leftFrontLeg.setPivot(2.0F, 18.0F, -4.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.head, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}

package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart head;
	private final ModelPart thorax;
	private final ModelPart abdomen;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightBackMiddleLeg;
	private final ModelPart leftBackMiddleLeg;
	private final ModelPart rightFrontMiddleLeg;
	private final ModelPart leftFrontMiddleLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public SpiderEntityModel() {
		float f = 0.0F;
		int i = 15;
		this.head = new ModelPart(this, 32, 4);
		this.head.addCuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F);
		this.head.setPivot(0.0F, 15.0F, -3.0F);
		this.thorax = new ModelPart(this, 0, 0);
		this.thorax.addCuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F);
		this.thorax.setPivot(0.0F, 15.0F, 0.0F);
		this.abdomen = new ModelPart(this, 0, 12);
		this.abdomen.addCuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, 0.0F);
		this.abdomen.setPivot(0.0F, 15.0F, 9.0F);
		this.rightBackLeg = new ModelPart(this, 18, 0);
		this.rightBackLeg.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.rightBackLeg.setPivot(-4.0F, 15.0F, 2.0F);
		this.leftBackLeg = new ModelPart(this, 18, 0);
		this.leftBackLeg.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.leftBackLeg.setPivot(4.0F, 15.0F, 2.0F);
		this.rightBackMiddleLeg = new ModelPart(this, 18, 0);
		this.rightBackMiddleLeg.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.rightBackMiddleLeg.setPivot(-4.0F, 15.0F, 1.0F);
		this.leftBackMiddleLeg = new ModelPart(this, 18, 0);
		this.leftBackMiddleLeg.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.leftBackMiddleLeg.setPivot(4.0F, 15.0F, 1.0F);
		this.rightFrontMiddleLeg = new ModelPart(this, 18, 0);
		this.rightFrontMiddleLeg.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.rightFrontMiddleLeg.setPivot(-4.0F, 15.0F, 0.0F);
		this.leftFrontMiddleLeg = new ModelPart(this, 18, 0);
		this.leftFrontMiddleLeg.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.leftFrontMiddleLeg.setPivot(4.0F, 15.0F, 0.0F);
		this.rightFrontLeg = new ModelPart(this, 18, 0);
		this.rightFrontLeg.addCuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.rightFrontLeg.setPivot(-4.0F, 15.0F, -1.0F);
		this.leftFrontLeg = new ModelPart(this, 18, 0);
		this.leftFrontLeg.addCuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
		this.leftFrontLeg.setPivot(4.0F, 15.0F, -1.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(
			this.head,
			this.thorax,
			this.abdomen,
			this.rightBackLeg,
			this.leftBackLeg,
			this.rightBackMiddleLeg,
			this.leftBackMiddleLeg,
			this.rightFrontMiddleLeg,
			this.leftFrontMiddleLeg,
			this.rightFrontLeg,
			this.leftFrontLeg
		);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		float f = (float) (Math.PI / 4);
		this.rightBackLeg.roll = (float) (-Math.PI / 4);
		this.leftBackLeg.roll = (float) (Math.PI / 4);
		this.rightBackMiddleLeg.roll = -0.58119464F;
		this.leftBackMiddleLeg.roll = 0.58119464F;
		this.rightFrontMiddleLeg.roll = -0.58119464F;
		this.leftFrontMiddleLeg.roll = 0.58119464F;
		this.rightFrontLeg.roll = (float) (-Math.PI / 4);
		this.leftFrontLeg.roll = (float) (Math.PI / 4);
		float g = -0.0F;
		float h = (float) (Math.PI / 8);
		this.rightBackLeg.yaw = (float) (Math.PI / 4);
		this.leftBackLeg.yaw = (float) (-Math.PI / 4);
		this.rightBackMiddleLeg.yaw = (float) (Math.PI / 8);
		this.leftBackMiddleLeg.yaw = (float) (-Math.PI / 8);
		this.rightFrontMiddleLeg.yaw = (float) (-Math.PI / 8);
		this.leftFrontMiddleLeg.yaw = (float) (Math.PI / 8);
		this.rightFrontLeg.yaw = (float) (-Math.PI / 4);
		this.leftFrontLeg.yaw = (float) (Math.PI / 4);
		float i = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance;
		float j = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance;
		float k = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float l = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		float m = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance;
		float n = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance;
		float o = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float p = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		this.rightBackLeg.yaw += i;
		this.leftBackLeg.yaw += -i;
		this.rightBackMiddleLeg.yaw += j;
		this.leftBackMiddleLeg.yaw += -j;
		this.rightFrontMiddleLeg.yaw += k;
		this.leftFrontMiddleLeg.yaw += -k;
		this.rightFrontLeg.yaw += l;
		this.leftFrontLeg.yaw += -l;
		this.rightBackLeg.roll += m;
		this.leftBackLeg.roll += -m;
		this.rightBackMiddleLeg.roll += n;
		this.leftBackMiddleLeg.roll += -n;
		this.rightFrontMiddleLeg.roll += o;
		this.leftFrontMiddleLeg.roll += -o;
		this.rightFrontLeg.roll += p;
		this.leftFrontLeg.roll += -p;
	}
}

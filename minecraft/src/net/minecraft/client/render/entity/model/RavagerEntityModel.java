package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RavagerEntityModel extends CompositeEntityModel<RavagerEntity> {
	private final ModelPart field_3386;
	private final ModelPart jaw;
	private final ModelPart torso;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart field_3384;

	public RavagerEntityModel() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		int i = 16;
		float f = 0.0F;
		this.field_3384 = new ModelPart(this);
		this.field_3384.setPivot(0.0F, -7.0F, -1.5F);
		this.field_3384.setTextureOffset(68, 73).addCuboid(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F, 0.0F);
		this.field_3386 = new ModelPart(this);
		this.field_3386.setPivot(0.0F, 16.0F, -17.0F);
		this.field_3386.setTextureOffset(0, 0).addCuboid(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F, 0.0F);
		this.field_3386.setTextureOffset(0, 0).addCuboid(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F, 0.0F);
		ModelPart modelPart = new ModelPart(this);
		modelPart.setPivot(-10.0F, -14.0F, -8.0F);
		modelPart.setTextureOffset(74, 55).addCuboid(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
		modelPart.pitch = 1.0995574F;
		this.field_3386.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this);
		modelPart2.mirror = true;
		modelPart2.setPivot(8.0F, -14.0F, -8.0F);
		modelPart2.setTextureOffset(74, 55).addCuboid(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
		modelPart2.pitch = 1.0995574F;
		this.field_3386.addChild(modelPart2);
		this.jaw = new ModelPart(this);
		this.jaw.setPivot(0.0F, -2.0F, 2.0F);
		this.jaw.setTextureOffset(0, 36).addCuboid(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F, 0.0F);
		this.field_3386.addChild(this.jaw);
		this.field_3384.addChild(this.field_3386);
		this.torso = new ModelPart(this);
		this.torso.setTextureOffset(0, 55).addCuboid(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F, 0.0F);
		this.torso.setTextureOffset(0, 91).addCuboid(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F, 0.0F);
		this.torso.setPivot(0.0F, 1.0F, 2.0F);
		this.rightBackLeg = new ModelPart(this, 96, 0);
		this.rightBackLeg.addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
		this.rightBackLeg.setPivot(-8.0F, -13.0F, 18.0F);
		this.leftBackLeg = new ModelPart(this, 96, 0);
		this.leftBackLeg.mirror = true;
		this.leftBackLeg.addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
		this.leftBackLeg.setPivot(8.0F, -13.0F, 18.0F);
		this.rightFrontLeg = new ModelPart(this, 64, 0);
		this.rightFrontLeg.addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
		this.rightFrontLeg.setPivot(-8.0F, -13.0F, -5.0F);
		this.leftFrontLeg = new ModelPart(this, 64, 0);
		this.leftFrontLeg.mirror = true;
		this.leftFrontLeg.addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
		this.leftFrontLeg.setPivot(8.0F, -13.0F, -5.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3384, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	public void method_17091(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
		this.field_3386.pitch = j * (float) (Math.PI / 180.0);
		this.field_3386.yaw = i * (float) (Math.PI / 180.0);
		this.torso.pitch = (float) (Math.PI / 2);
		float k = 0.4F * g;
		this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662F) * k;
		this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * k;
		this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * k;
		this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * k;
	}

	public void method_17089(RavagerEntity ravagerEntity, float f, float g, float h) {
		super.animateModel(ravagerEntity, f, g, h);
		int i = ravagerEntity.getStunTick();
		int j = ravagerEntity.getRoarTick();
		int k = 20;
		int l = ravagerEntity.getAttackTick();
		int m = 10;
		if (l > 0) {
			float n = MathHelper.method_24504((float)l - h, 10.0F);
			float o = (1.0F + n) * 0.5F;
			float p = o * o * o * 12.0F;
			float q = p * MathHelper.sin(this.field_3384.pitch);
			this.field_3384.pivotZ = -6.5F + p;
			this.field_3384.pivotY = -7.0F - q;
			float r = MathHelper.sin(((float)l - h) / 10.0F * (float) Math.PI * 0.25F);
			this.jaw.pitch = (float) (Math.PI / 2) * r;
			if (l > 5) {
				this.jaw.pitch = MathHelper.sin(((float)(-4 + l) - h) / 4.0F) * (float) Math.PI * 0.4F;
			} else {
				this.jaw.pitch = (float) (Math.PI / 20) * MathHelper.sin((float) Math.PI * ((float)l - h) / 10.0F);
			}
		} else {
			float n = -1.0F;
			float o = -1.0F * MathHelper.sin(this.field_3384.pitch);
			this.field_3384.pivotX = 0.0F;
			this.field_3384.pivotY = -7.0F - o;
			this.field_3384.pivotZ = 5.5F;
			boolean bl = i > 0;
			this.field_3384.pitch = bl ? 0.21991149F : 0.0F;
			this.jaw.pitch = (float) Math.PI * (bl ? 0.05F : 0.01F);
			if (bl) {
				double d = (double)i / 40.0;
				this.field_3384.pivotX = (float)Math.sin(d * 10.0) * 3.0F;
			} else if (j > 0) {
				float q = MathHelper.sin(((float)(20 - j) - h) / 20.0F * (float) Math.PI * 0.25F);
				this.jaw.pitch = (float) (Math.PI / 2) * q;
			}
		}
	}
}

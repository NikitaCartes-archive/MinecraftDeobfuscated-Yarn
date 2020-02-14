package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_4840<T extends class_4836> extends BipedEntityModel<T> {
	public final ModelPart field_22404;
	public final ModelPart field_22405;

	public class_4840(float f, int i, int j) {
		super(f, 0.0F, i, j);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f);
		this.head = new ModelPart(this);
		this.head.setTextureOffset(0, 0).addCuboid(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, f);
		this.head.setTextureOffset(31, 1).addCuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, f);
		this.head.setTextureOffset(2, 4).addCuboid(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, f);
		this.head.setTextureOffset(2, 0).addCuboid(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, f);
		this.field_22404 = new ModelPart(this);
		this.field_22404.setPivot(4.5F, -6.0F, 0.0F);
		this.field_22404.setTextureOffset(57, 38).addCuboid(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, f);
		this.head.addChild(this.field_22404);
		this.field_22405 = new ModelPart(this);
		this.field_22405.setPivot(-4.5F, -6.0F, 0.0F);
		this.head.addChild(this.field_22405);
		this.field_22405.setTextureOffset(57, 22).addCuboid(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, f);
		this.helmet = new ModelPart(this);
		this.helmet.setPivot(0.0F, 0.0F, 0.0F);
		this.rightArm = new ModelPart(this);
		this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		this.rightArm.setTextureOffset(40, 16).addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.leftArm = new ModelPart(this);
		this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
		this.leftArm.setTextureOffset(40, 16).addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.rightLeg = new ModelPart(this);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.rightLeg.setTextureOffset(0, 16).addCuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.leftLeg = new ModelPart(this);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.leftLeg.setTextureOffset(0, 16).addCuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
	}

	public void setAngles(T arg, float f, float g, float h, float i, float j) {
		super.setAngles(arg, f, g, h, i, j);
		float k = (float) (Math.PI / 6);
		float l = h * 0.1F + f * 0.5F;
		float m = 0.08F + g * 0.4F;
		this.field_22404.roll = (float) (-Math.PI / 6) - MathHelper.cos(l * 1.2F) * m;
		this.field_22405.roll = (float) (Math.PI / 6) + MathHelper.cos(l) * m;
		class_4836.class_4837 lv = arg.method_24705();
		if (lv == class_4836.class_4837.field_22383) {
			this.rightArm.yaw = -0.3F + this.head.yaw;
			this.leftArm.yaw = 0.6F + this.head.yaw;
			this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch + 0.1F;
			this.leftArm.pitch = -1.5F + this.head.pitch;
		} else if (lv == class_4836.class_4837.field_22384) {
			this.rightArm.yaw = -0.8F;
			this.rightArm.pitch = -0.97079635F;
			this.leftArm.pitch = -0.97079635F;
			float n = (float)MathHelper.clamp(arg.getItemUseTime(), 0, 25);
			float o = n / 25.0F;
			this.leftArm.yaw = MathHelper.lerp(o, 0.4F, 0.85F);
			this.leftArm.pitch = MathHelper.lerp(o, this.leftArm.pitch, (float) (-Math.PI / 2));
		} else if (lv == class_4836.class_4837.field_22385) {
			this.leftArm.yaw = 0.5F;
			this.leftArm.pitch = -0.9F;
			this.head.pitch = 0.5F;
			this.head.yaw = 0.0F;
		}
	}
}

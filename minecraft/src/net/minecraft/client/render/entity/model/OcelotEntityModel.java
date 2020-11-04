package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends AnimalModel<T> {
	protected final ModelPart field_27454;
	protected final ModelPart field_27455;
	protected final ModelPart field_27456;
	protected final ModelPart field_27457;
	protected final ModelPart upperTail;
	protected final ModelPart lowerTail;
	protected final ModelPart head;
	protected final ModelPart torso;
	protected int animationState = 1;

	public OcelotEntityModel(ModelPart modelPart) {
		super(true, 10.0F, 4.0F);
		this.head = modelPart.method_32086("head");
		this.torso = modelPart.method_32086("body");
		this.upperTail = modelPart.method_32086("tail1");
		this.lowerTail = modelPart.method_32086("tail2");
		this.field_27454 = modelPart.method_32086("left_hind_leg");
		this.field_27455 = modelPart.method_32086("right_hind_leg");
		this.field_27456 = modelPart.method_32086("left_front_leg");
		this.field_27457 = modelPart.method_32086("left_front_leg");
	}

	public static class_5609 method_32021(class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			class_5606.method_32108()
				.method_32103("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, arg)
				.method_32105("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, arg, 0, 24)
				.method_32105("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, arg, 0, 10)
				.method_32105("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, arg, 6, 10),
			class_5603.method_32090(0.0F, 15.0F, -9.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(20, 0).method_32098(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, arg),
			class_5603.method_32091(0.0F, 12.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"tail1",
			class_5606.method_32108().method_32101(0, 15).method_32098(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, arg),
			class_5603.method_32091(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F)
		);
		lv2.method_32117(
			"tail2", class_5606.method_32108().method_32101(4, 15).method_32098(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, arg), class_5603.method_32090(0.0F, 20.0F, 14.0F)
		);
		class_5606 lv3 = class_5606.method_32108().method_32101(8, 13).method_32098(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, arg);
		lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(1.1F, 18.0F, 5.0F));
		lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-1.1F, 18.0F, 5.0F));
		class_5606 lv4 = class_5606.method_32108().method_32101(40, 0).method_32098(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, arg);
		lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(1.2F, 14.1F, -5.0F));
		lv2.method_32117("right_front_leg", lv4, class_5603.method_32090(-1.2F, 14.1F, -5.0F));
		return lv;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.field_27454, this.field_27455, this.field_27456, this.field_27457, this.upperTail, this.lowerTail);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		if (this.animationState != 3) {
			this.torso.pitch = (float) (Math.PI / 2);
			if (this.animationState == 2) {
				this.field_27454.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.field_27455.pitch = MathHelper.cos(limbAngle * 0.6662F + 0.3F) * limbDistance;
				this.field_27456.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI + 0.3F) * limbDistance;
				this.field_27457.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(limbAngle) * limbDistance;
			} else {
				this.field_27454.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				this.field_27455.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.field_27456.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
				this.field_27457.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
				if (this.animationState == 1) {
					this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(limbAngle) * limbDistance;
				} else {
					this.lowerTail.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(limbAngle) * limbDistance;
				}
			}
		}
	}

	@Override
	public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
		this.torso.pivotY = 12.0F;
		this.torso.pivotZ = -10.0F;
		this.head.pivotY = 15.0F;
		this.head.pivotZ = -9.0F;
		this.upperTail.pivotY = 15.0F;
		this.upperTail.pivotZ = 8.0F;
		this.lowerTail.pivotY = 20.0F;
		this.lowerTail.pivotZ = 14.0F;
		this.field_27456.pivotY = 14.1F;
		this.field_27456.pivotZ = -5.0F;
		this.field_27457.pivotY = 14.1F;
		this.field_27457.pivotZ = -5.0F;
		this.field_27454.pivotY = 18.0F;
		this.field_27454.pivotZ = 5.0F;
		this.field_27455.pivotY = 18.0F;
		this.field_27455.pivotZ = 5.0F;
		this.upperTail.pitch = 0.9F;
		if (entity.isInSneakingPose()) {
			this.torso.pivotY++;
			this.head.pivotY += 2.0F;
			this.upperTail.pivotY++;
			this.lowerTail.pivotY += -4.0F;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
			this.animationState = 0;
		} else if (entity.isSprinting()) {
			this.lowerTail.pivotY = this.upperTail.pivotY;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
			this.animationState = 2;
		} else {
			this.animationState = 1;
		}
	}
}

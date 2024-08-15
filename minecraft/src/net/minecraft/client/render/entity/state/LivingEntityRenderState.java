package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class LivingEntityRenderState extends EntityRenderState {
	public float bodyYaw;
	public float yawDegrees;
	public float pitch;
	public float deathTime;
	public float limbFrequency;
	public float limbAmplitudeMultiplier;
	public float headItemAnimationProgress;
	public float baseScale = 1.0F;
	public float ageScale = 1.0F;
	public boolean flipUpsideDown;
	public boolean shaking;
	public boolean baby;
	public boolean touchingWater;
	public boolean usingRiptide;
	public boolean hurt;
	public boolean invisibleToPlayer;
	public boolean hasOutline;
	@Nullable
	public Direction sleepingDirection;
	@Nullable
	public Text customName;
	public EntityPose pose = EntityPose.STANDING;
	@Nullable
	public BakedModel headEquippedItemModel;
	public ItemStack headEquippedStack = ItemStack.EMPTY;
	public Arm mainArm = Arm.RIGHT;
	@Nullable
	public BakedModel rightHandItemModel;
	public ItemStack rightHandStack = ItemStack.EMPTY;
	@Nullable
	public BakedModel leftHandItemModel;
	public ItemStack leftHandStack = ItemStack.EMPTY;

	public ItemStack getMainHandStack() {
		return this.mainArm == Arm.RIGHT ? this.rightHandStack : this.leftHandStack;
	}

	@Nullable
	public BakedModel getMainHandItemModel() {
		return this.mainArm == Arm.RIGHT ? this.rightHandItemModel : this.leftHandItemModel;
	}

	public boolean isInPose(EntityPose pose) {
		return this.pose == pose;
	}
}

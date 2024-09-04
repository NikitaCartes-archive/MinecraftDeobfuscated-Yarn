package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class BipedEntityRenderState extends LivingEntityRenderState {
	public float leaningPitch;
	public float handSwingProgress;
	public float limbAmplitudeInverse = 1.0F;
	public float crossbowPullTime;
	public int itemUseTime;
	public Arm preferredArm = Arm.RIGHT;
	public Hand activeHand = Hand.MAIN_HAND;
	public boolean isInSneakingPose;
	public boolean isGliding;
	public boolean isSwimming;
	public boolean hasVehicle;
	public boolean isUsingItem;
	public float leftWingPitch;
	public float leftWingYaw;
	public float leftWingRoll;
	public ItemStack equippedChestStack = ItemStack.EMPTY;
	public ItemStack equippedLegsStack = ItemStack.EMPTY;
	public ItemStack equippedFeetStack = ItemStack.EMPTY;
}

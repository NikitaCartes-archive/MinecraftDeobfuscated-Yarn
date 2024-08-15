package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityRenderState extends LivingEntityRenderState {
	private static final Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/wolf/wolf.png");
	public boolean angerTime;
	public boolean inSittingPose;
	public float tailAngle = (float) (Math.PI / 5);
	public float begAnimationProgress;
	public float shakeProgress;
	public float furWetBrightnessMultiplier = 1.0F;
	public Identifier texture = DEFAULT_TEXTURE;
	@Nullable
	public DyeColor collarColor;
	public ItemStack bodyArmor = ItemStack.EMPTY;

	public float getRoll(float shakeOffset) {
		float f = (this.shakeProgress + shakeOffset) / 1.8F;
		if (f < 0.0F) {
			f = 0.0F;
		} else if (f > 1.0F) {
			f = 1.0F;
		}

		return MathHelper.sin(f * (float) Math.PI) * MathHelper.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
	}
}

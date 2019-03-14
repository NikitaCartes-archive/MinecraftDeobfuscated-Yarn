package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OtherClientPlayerEntity extends AbstractClientPlayerEntity {
	public OtherClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
		super(clientWorld, gameProfile);
		this.stepHeight = 1.0F;
		this.noClip = true;
	}

	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension() * 10.0;
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * getRenderDistanceMultiplier();
		return d < e * e;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return true;
	}

	@Override
	public void update() {
		super.update();
		this.field_6211 = this.field_6225;
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float f = MathHelper.sqrt(d * d + e * e) * 4.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		this.field_6225 = this.field_6225 + (f - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	@Override
	public void updateMovement() {
		if (this.field_6210 > 0) {
			double d = this.x + (this.field_6224 - this.x) / (double)this.field_6210;
			double e = this.y + (this.field_6245 - this.y) / (double)this.field_6210;
			double f = this.z + (this.field_6263 - this.z) / (double)this.field_6210;
			this.yaw = (float)((double)this.yaw + MathHelper.wrapDegrees(this.field_6284 - (double)this.yaw) / (double)this.field_6210);
			this.pitch = (float)((double)this.pitch + (this.field_6221 - (double)this.pitch) / (double)this.field_6210);
			this.field_6210--;
			this.setPosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		}

		if (this.field_6265 > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.field_6242 - (double)this.headYaw) / (double)this.field_6265);
			this.field_6265--;
		}

		this.field_7505 = this.field_7483;
		this.method_6119();
		float g;
		if (this.onGround && !(this.getHealth() <= 0.0F)) {
			g = Math.min(0.1F, MathHelper.sqrt(squaredHorizontalLength(this.getVelocity())));
		} else {
			g = 0.0F;
		}

		float h;
		if (!this.onGround && !(this.getHealth() <= 0.0F)) {
			h = (float)Math.atan(-this.getVelocity().y * 0.2F) * 15.0F;
		} else {
			h = 0.0F;
		}

		this.field_7483 = this.field_7483 + (g - this.field_7483) * 0.4F;
		this.field_6223 = this.field_6223 + (h - this.field_6223) * 0.8F;
		this.world.getProfiler().push("push");
		this.doPushLogic();
		this.world.getProfiler().pop();
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(textComponent);
	}
}

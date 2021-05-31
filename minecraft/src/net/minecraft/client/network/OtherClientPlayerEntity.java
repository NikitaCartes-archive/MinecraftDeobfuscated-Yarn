package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Represents a player entity that is present on the client but is not the client's own player.
 */
@Environment(EnvType.CLIENT)
public class OtherClientPlayerEntity extends AbstractClientPlayerEntity {
	public OtherClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
		super(clientWorld, gameProfile);
		this.stepHeight = 1.0F;
		this.noClip = true;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 10.0;
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		this.updateLimbs(this, false);
	}

	@Override
	public void tickMovement() {
		if (this.bodyTrackingIncrements > 0) {
			double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
			double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
			double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
			this.setYaw(this.getYaw() + (float)MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw()) / (float)this.bodyTrackingIncrements);
			this.setPitch(this.getPitch() + (float)(this.serverPitch - (double)this.getPitch()) / (float)this.bodyTrackingIncrements);
			this.bodyTrackingIncrements--;
			this.setPosition(d, e, f);
			this.setRotation(this.getYaw(), this.getPitch());
		}

		if (this.headTrackingIncrements > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
			this.headTrackingIncrements--;
		}

		this.prevStrideDistance = this.strideDistance;
		this.tickHandSwing();
		float g;
		if (this.onGround && !this.isDead()) {
			g = (float)Math.min(0.1, this.getVelocity().horizontalLength());
		} else {
			g = 0.0F;
		}

		this.strideDistance = this.strideDistance + (g - this.strideDistance) * 0.4F;
		this.world.getProfiler().push("push");
		this.tickCramming();
		this.world.getProfiler().pop();
	}

	@Override
	protected void updatePose() {
	}

	@Override
	public void sendSystemMessage(Text message, UUID sender) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (!minecraftClient.shouldBlockMessages(sender)) {
			minecraftClient.inGameHud.getChatHud().addMessage(message);
		}
	}
}

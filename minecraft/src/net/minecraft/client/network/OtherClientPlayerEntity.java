package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * Represents a player entity that is present on the client but is not the client's own player.
 */
@Environment(EnvType.CLIENT)
public class OtherClientPlayerEntity extends AbstractClientPlayerEntity {
	private Vec3d clientVelocity = Vec3d.ZERO;
	private int velocityLerpDivisor;

	public OtherClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
		super(clientWorld, gameProfile);
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
		this.updateLimbs(false);
	}

	@Override
	public void tickMovement() {
		if (this.bodyTrackingIncrements > 0) {
			this.lerpPosAndRotation(this.bodyTrackingIncrements, this.serverX, this.serverY, this.serverZ, this.serverYaw, this.serverPitch);
			this.bodyTrackingIncrements--;
		}

		if (this.headTrackingIncrements > 0) {
			this.lerpHeadYaw(this.headTrackingIncrements, this.serverHeadYaw);
			this.headTrackingIncrements--;
		}

		if (this.velocityLerpDivisor > 0) {
			this.addVelocityInternal(
				new Vec3d(
					(this.clientVelocity.x - this.getVelocity().x) / (double)this.velocityLerpDivisor,
					(this.clientVelocity.y - this.getVelocity().y) / (double)this.velocityLerpDivisor,
					(this.clientVelocity.z - this.getVelocity().z) / (double)this.velocityLerpDivisor
				)
			);
			this.velocityLerpDivisor--;
		}

		this.prevStrideDistance = this.strideDistance;
		this.tickHandSwing();
		float f;
		if (this.isOnGround() && !this.isDead()) {
			f = (float)Math.min(0.1, this.getVelocity().horizontalLength());
		} else {
			f = 0.0F;
		}

		this.strideDistance = this.strideDistance + (f - this.strideDistance) * 0.4F;
		this.getWorld().getProfiler().push("push");
		this.tickCramming();
		this.getWorld().getProfiler().pop();
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.clientVelocity = new Vec3d(x, y, z);
		this.velocityLerpDivisor = this.getType().getTrackTickInterval() + 1;
	}

	@Override
	protected void updatePose() {
	}

	@Override
	public void sendMessage(Text message) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.inGameHud.getChatHud().addMessage(message);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		this.resetPosition();
	}
}

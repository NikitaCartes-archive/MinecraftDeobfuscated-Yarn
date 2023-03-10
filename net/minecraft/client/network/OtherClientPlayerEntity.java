/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Represents a player entity that is present on the client but is not the client's own player.
 */
@Environment(value=EnvType.CLIENT)
public class OtherClientPlayerEntity
extends AbstractClientPlayerEntity {
    private Vec3d clientVelocity = Vec3d.ZERO;
    private int velocityLerpDivisor;

    public OtherClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
        this.setStepHeight(1.0f);
        this.noClip = true;
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * OtherClientPlayerEntity.getRenderDistanceMultiplier()) * d;
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
            double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
            double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
            double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
            this.setYaw(this.getYaw() + (float)MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw()) / (float)this.bodyTrackingIncrements);
            this.setPitch(this.getPitch() + (float)(this.serverPitch - (double)this.getPitch()) / (float)this.bodyTrackingIncrements);
            --this.bodyTrackingIncrements;
            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        }
        if (this.headTrackingIncrements > 0) {
            this.headYaw += (float)(MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
            --this.headTrackingIncrements;
        }
        if (this.velocityLerpDivisor > 0) {
            this.addVelocity(new Vec3d((this.clientVelocity.x - this.getVelocity().x) / (double)this.velocityLerpDivisor, (this.clientVelocity.y - this.getVelocity().y) / (double)this.velocityLerpDivisor, (this.clientVelocity.z - this.getVelocity().z) / (double)this.velocityLerpDivisor));
            --this.velocityLerpDivisor;
        }
        this.prevStrideDistance = this.strideDistance;
        this.tickHandSwing();
        float g = !this.onGround || this.isDead() ? 0.0f : (float)Math.min(0.1, this.getVelocity().horizontalLength());
        this.strideDistance += (g - this.strideDistance) * 0.4f;
        this.world.getProfiler().push("push");
        this.tickCramming();
        this.world.getProfiler().pop();
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
}


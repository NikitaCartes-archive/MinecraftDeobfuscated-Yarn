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

@Environment(value=EnvType.CLIENT)
public class OtherClientPlayerEntity
extends AbstractClientPlayerEntity {
    public OtherClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
        this.stepHeight = 1.0f;
        this.noClip = true;
    }

    @Override
    public boolean shouldRenderAtDistance(double d) {
        double e = this.getBoundingBox().averageDimension() * 10.0;
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return d < (e *= 64.0 * OtherClientPlayerEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.lastLimbDistance = this.limbDistance;
        double d = this.x - this.prevX;
        double e = this.z - this.prevZ;
        float f = MathHelper.sqrt(d * d + e * e) * 4.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.limbDistance += (f - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    @Override
    public void tickMovement() {
        if (this.bodyTrackingIncrements > 0) {
            double d = this.x + (this.serverX - this.x) / (double)this.bodyTrackingIncrements;
            double e = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
            double f = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
            this.yaw = (float)((double)this.yaw + MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw) / (double)this.bodyTrackingIncrements);
            this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
            --this.bodyTrackingIncrements;
            this.setPosition(d, e, f);
            this.setRotation(this.yaw, this.pitch);
        }
        if (this.headTrackingIncrements > 0) {
            this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
            --this.headTrackingIncrements;
        }
        this.field_7505 = this.field_7483;
        this.tickHandSwing();
        float g = !this.onGround || this.getHealth() <= 0.0f ? 0.0f : Math.min(0.1f, MathHelper.sqrt(OtherClientPlayerEntity.squaredHorizontalLength(this.getVelocity())));
        if (this.onGround || this.getHealth() <= 0.0f) {
            float h = 0.0f;
        } else {
            float h = (float)Math.atan(-this.getVelocity().y * (double)0.2f) * 15.0f;
        }
        this.field_7483 += (g - this.field_7483) * 0.4f;
        this.world.getProfiler().push("push");
        this.tickCramming();
        this.world.getProfiler().pop();
    }

    @Override
    protected void updateSize() {
    }

    @Override
    public void sendMessage(Text text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }
}


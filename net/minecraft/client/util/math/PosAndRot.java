/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class PosAndRot {
    private final Vec3d pos;
    private final float pitch;
    private final float yaw;

    public PosAndRot(Vec3d pos, float pitch, float yaw) {
        this.pos = pos;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        PosAndRot posAndRot = (PosAndRot)object;
        return Float.compare(posAndRot.pitch, this.pitch) == 0 && Float.compare(posAndRot.yaw, this.yaw) == 0 && Objects.equals(this.pos, posAndRot.pos);
    }

    public int hashCode() {
        return Objects.hash(this.pos, Float.valueOf(this.pitch), Float.valueOf(this.yaw));
    }

    public String toString() {
        return "PosAndRot[" + this.pos + " (" + this.pitch + ", " + this.yaw + ")]";
    }
}


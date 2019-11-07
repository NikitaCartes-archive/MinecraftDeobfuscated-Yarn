/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class Listener {
    private float volume = 1.0f;

    public void setPosition(Vec3d vec3d) {
        AL10.alListener3f(4100, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
    }

    public void setOrientation(Vector3f vector3f, Vector3f vector3f2) {
        AL10.alListenerfv(4111, new float[]{vector3f.getX(), vector3f.getY(), vector3f.getZ(), vector3f2.getX(), vector3f2.getY(), vector3f2.getZ()});
    }

    public void setVolume(float f) {
        AL10.alListenerf(4106, f);
        this.volume = f;
    }

    public float getVolume() {
        return this.volume;
    }

    public void init() {
        this.setPosition(Vec3d.ZERO);
        this.setOrientation(Vector3f.NEGATIVE_Z, Vector3f.POSITIVE_Y);
    }
}


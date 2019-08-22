package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL10;

@Environment(EnvType.CLIENT)
public class Listener {
	public static final Vec3d field_18905 = new Vec3d(0.0, 1.0, 0.0);
	private float volume = 1.0F;

	public void setPosition(Vec3d vec3d) {
		AL10.alListener3f(4100, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
	}

	public void setOrientation(Vec3d vec3d, Vec3d vec3d2) {
		AL10.alListenerfv(4111, new float[]{(float)vec3d.x, (float)vec3d.y, (float)vec3d.z, (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z});
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
		this.setOrientation(new Vec3d(0.0, 0.0, -1.0), field_18905);
	}
}

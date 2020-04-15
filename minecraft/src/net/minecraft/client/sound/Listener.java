package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL10;

@Environment(EnvType.CLIENT)
public class Listener {
	private float volume = 1.0F;
	private Vec3d field_24051 = Vec3d.ZERO;

	public void setPosition(Vec3d position) {
		this.field_24051 = position;
		AL10.alListener3f(4100, (float)position.x, (float)position.y, (float)position.z);
	}

	public Vec3d method_27268() {
		return this.field_24051;
	}

	public void setOrientation(Vector3f vector3f, Vector3f vector3f2) {
		AL10.alListenerfv(4111, new float[]{vector3f.getX(), vector3f.getY(), vector3f.getZ(), vector3f2.getX(), vector3f2.getY(), vector3f2.getZ()});
	}

	public void setVolume(float volume) {
		AL10.alListenerf(4106, volume);
		this.volume = volume;
	}

	public float getVolume() {
		return this.volume;
	}

	public void init() {
		this.setPosition(Vec3d.ZERO);
		this.setOrientation(Vector3f.NEGATIVE_Z, Vector3f.POSITIVE_Y);
	}
}

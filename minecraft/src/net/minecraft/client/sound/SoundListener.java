package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

@Environment(EnvType.CLIENT)
public class SoundListener {
	private float volume = 1.0F;
	private Vec3d pos = Vec3d.ZERO;

	public void setPosition(Vec3d position) {
		this.pos = position;
		AL10.alListener3f(4100, (float)position.x, (float)position.y, (float)position.z);
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public void setOrientation(Vector3f at, Vector3f up) {
		AL10.alListenerfv(4111, new float[]{at.x(), at.y(), at.z(), up.x(), up.y(), up.z()});
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
		this.setOrientation(new Vector3f(0.0F, 0.0F, -1.0F), new Vector3f(0.0F, 1.0F, 0.0F));
	}
}

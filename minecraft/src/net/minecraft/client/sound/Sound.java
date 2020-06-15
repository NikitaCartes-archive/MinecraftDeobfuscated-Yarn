package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Sound implements SoundContainer<Sound> {
	private final Identifier id;
	private final float volume;
	private final float pitch;
	private final int weight;
	private final Sound.RegistrationType registrationType;
	private final boolean stream;
	private final boolean preload;
	private final int attenuation;

	public Sound(String id, float volume, float pitch, int weight, Sound.RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
		this.id = new Identifier(id);
		this.volume = volume;
		this.pitch = pitch;
		this.weight = weight;
		this.registrationType = registrationType;
		this.stream = stream;
		this.preload = preload;
		this.attenuation = attenuation;
	}

	public Identifier getIdentifier() {
		return this.id;
	}

	public Identifier getLocation() {
		return new Identifier(this.id.getNamespace(), "sounds/" + this.id.getPath() + ".ogg");
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	public Sound getSound() {
		return this;
	}

	@Override
	public void preload(SoundSystem soundSystem) {
		if (this.preload) {
			soundSystem.addPreloadedSound(this);
		}
	}

	public Sound.RegistrationType getRegistrationType() {
		return this.registrationType;
	}

	public boolean isStreamed() {
		return this.stream;
	}

	public boolean isPreloaded() {
		return this.preload;
	}

	public int getAttenuation() {
		return this.attenuation;
	}

	public String toString() {
		return "Sound[" + this.id + "]";
	}

	@Environment(EnvType.CLIENT)
	public static enum RegistrationType {
		FILE("file"),
		SOUND_EVENT("event");

		private final String name;

		private RegistrationType(String name) {
			this.name = name;
		}

		public static Sound.RegistrationType getByName(String string) {
			for (Sound.RegistrationType registrationType : values()) {
				if (registrationType.name.equals(string)) {
					return registrationType;
				}
			}

			return null;
		}
	}
}

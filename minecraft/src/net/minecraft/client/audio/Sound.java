package net.minecraft.client.audio;

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

	public Sound(String string, float f, float g, int i, Sound.RegistrationType registrationType, boolean bl, boolean bl2, int j) {
		this.id = new Identifier(string);
		this.volume = f;
		this.pitch = g;
		this.weight = i;
		this.registrationType = registrationType;
		this.stream = bl;
		this.preload = bl2;
		this.attenuation = j;
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

	public Sound method_4765() {
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

	@Environment(EnvType.CLIENT)
	public static enum RegistrationType {
		FILE("file"),
		EVENT("event");

		private final String name;

		private RegistrationType(String string2) {
			this.name = string2;
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

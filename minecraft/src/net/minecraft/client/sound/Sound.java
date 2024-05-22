package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class Sound implements SoundContainer<Sound> {
	public static final ResourceFinder FINDER = new ResourceFinder("sounds", ".ogg");
	private final Identifier id;
	private final FloatSupplier volume;
	private final FloatSupplier pitch;
	private final int weight;
	private final Sound.RegistrationType registrationType;
	private final boolean stream;
	private final boolean preload;
	private final int attenuation;

	public Sound(
		Identifier id,
		FloatSupplier volume,
		FloatSupplier pitch,
		int weight,
		Sound.RegistrationType registrationType,
		boolean stream,
		boolean preload,
		int attenuation
	) {
		this.id = id;
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
		return FINDER.toResourcePath(this.id);
	}

	public FloatSupplier getVolume() {
		return this.volume;
	}

	public FloatSupplier getPitch() {
		return this.pitch;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	public Sound getSound(Random random) {
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

		private RegistrationType(final String name) {
			this.name = name;
		}

		@Nullable
		public static Sound.RegistrationType getByName(String name) {
			for (Sound.RegistrationType registrationType : values()) {
				if (registrationType.name.equals(name)) {
					return registrationType;
				}
			}

			return null;
		}
	}
}

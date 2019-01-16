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
	private final boolean field_5465;
	private final int field_5463;

	public Sound(String string, float f, float g, int i, Sound.RegistrationType registrationType, boolean bl, boolean bl2, int j) {
		this.id = new Identifier(string);
		this.volume = f;
		this.pitch = g;
		this.weight = i;
		this.registrationType = registrationType;
		this.stream = bl;
		this.field_5465 = bl2;
		this.field_5463 = j;
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

	public Sound.RegistrationType getRegistrationType() {
		return this.registrationType;
	}

	public boolean isStreamed() {
		return this.stream;
	}

	public boolean method_4764() {
		return this.field_5465;
	}

	public int method_4770() {
		return this.field_5463;
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

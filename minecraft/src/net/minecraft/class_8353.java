package net.minecraft;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public class class_8353 extends class_8285<SoundEvent> {
	private final Map<SoundEvent, SoundEvent> field_43935 = new HashMap();

	public class_8353() {
		super(RegistryKeys.SOUND_EVENT);
	}

	public SoundEvent method_50434(SoundEvent soundEvent) {
		return (SoundEvent)this.field_43935.computeIfAbsent(soundEvent, soundEvent2 -> {
			RegistryKey<SoundEvent> registryKey = (RegistryKey<SoundEvent>)Registries.SOUND_EVENT.getKey(soundEvent2).orElse(null);
			if (registryKey != null) {
				RegistryKey<SoundEvent> registryKey2 = (RegistryKey<SoundEvent>)this.field_43489.get(registryKey);
				if (registryKey2 != null) {
					SoundEvent soundEvent3 = Registries.SOUND_EVENT.get(registryKey2);
					if (soundEvent3 != null) {
						return soundEvent3;
					}
				}
			}

			return soundEvent;
		});
	}

	@Override
	protected void method_50138(RegistryKey<SoundEvent> registryKey, RegistryKey<SoundEvent> registryKey2) {
		super.method_50138(registryKey, registryKey2);
		this.field_43935.clear();
	}

	@Override
	protected void method_50136(RegistryKey<SoundEvent> registryKey) {
		super.method_50136(registryKey);
		this.field_43935.clear();
	}

	protected Text method_50162(RegistryKey<SoundEvent> registryKey, RegistryKey<SoundEvent> registryKey2) {
		return Text.translatable(
			"rule.replace_sound", Text.literal(registryKey.getValue().toShortTranslationKey()), Text.literal(registryKey2.getValue().toShortTranslationKey())
		);
	}
}

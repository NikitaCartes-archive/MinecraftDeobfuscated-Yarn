package net.minecraft.potion;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class Potion {
	@Nullable
	private final String baseName;
	private final List<StatusEffectInstance> effects;

	public static RegistryEntry<Potion> byId(String id) {
		Identifier identifier = Identifier.tryParse(id);
		return identifier == null ? Potions.EMPTY : (RegistryEntry)Registries.POTION.getEntry(identifier).map(Function.identity()).orElse(Potions.EMPTY);
	}

	public Potion(StatusEffectInstance... effects) {
		this(null, effects);
	}

	public Potion(@Nullable String baseName, StatusEffectInstance... effects) {
		this.baseName = baseName;
		this.effects = List.of(effects);
	}

	public static String finishTranslationKey(RegistryEntry<Potion> potion, String prefix) {
		String string = potion.value().baseName;
		if (string != null) {
			return prefix + string;
		} else {
			RegistryKey<Potion> registryKey = (RegistryKey<Potion>)potion.getKey().orElse(Potions.EMPTY_KEY);
			return prefix + registryKey.getValue().getPath();
		}
	}

	public List<StatusEffectInstance> getEffects() {
		return this.effects;
	}

	public boolean hasInstantEffect() {
		if (!this.effects.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance : this.effects) {
				if (statusEffectInstance.getEffectType().value().isInstant()) {
					return true;
				}
			}
		}

		return false;
	}
}

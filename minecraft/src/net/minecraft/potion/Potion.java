package net.minecraft.potion;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class Potion {
	@Nullable
	private final String baseName;
	private final List<StatusEffectInstance> effects;

	public Potion(StatusEffectInstance... effects) {
		this(null, effects);
	}

	public Potion(@Nullable String baseName, StatusEffectInstance... effects) {
		this.baseName = baseName;
		this.effects = List.of(effects);
	}

	public static String finishTranslationKey(Optional<RegistryEntry<Potion>> potion, String prefix) {
		if (potion.isPresent()) {
			String string = ((Potion)((RegistryEntry)potion.get()).value()).baseName;
			if (string != null) {
				return prefix + string;
			}
		}

		String string = (String)potion.flatMap(RegistryEntry::getKey).map(key -> key.getValue().getPath()).orElse("empty");
		return prefix + string;
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

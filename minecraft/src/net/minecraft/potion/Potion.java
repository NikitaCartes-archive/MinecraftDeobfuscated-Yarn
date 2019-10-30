package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Potion {
	private final String name;
	private final ImmutableList<StatusEffectInstance> effectList;

	public static Potion byId(String id) {
		return Registry.POTION.get(Identifier.tryParse(id));
	}

	public Potion(StatusEffectInstance... effects) {
		this(null, effects);
	}

	public Potion(@Nullable String name, StatusEffectInstance... effects) {
		this.name = name;
		this.effectList = ImmutableList.copyOf(effects);
	}

	public String getName(String string) {
		return string + (this.name == null ? Registry.POTION.getId(this).getPath() : this.name);
	}

	public List<StatusEffectInstance> getEffects() {
		return this.effectList;
	}

	public boolean hasInstantEffect() {
		if (!this.effectList.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance : this.effectList) {
				if (statusEffectInstance.getEffectType().isInstant()) {
					return true;
				}
			}
		}

		return false;
	}
}

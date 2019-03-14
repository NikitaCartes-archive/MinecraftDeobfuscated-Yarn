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

	public static Potion byId(String string) {
		return Registry.POTION.get(Identifier.create(string));
	}

	public Potion(StatusEffectInstance... statusEffectInstances) {
		this(null, statusEffectInstances);
	}

	public Potion(@Nullable String string, StatusEffectInstance... statusEffectInstances) {
		this.name = string;
		this.effectList = ImmutableList.copyOf(statusEffectInstances);
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

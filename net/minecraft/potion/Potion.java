/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class Potion {
    private final String baseName;
    private final ImmutableList<StatusEffectInstance> effects;

    public static Potion byId(String id) {
        return Registry.POTION.get(Identifier.tryParse(id));
    }

    public Potion(StatusEffectInstance ... effects) {
        this((String)null, effects);
    }

    public Potion(@Nullable String baseName, StatusEffectInstance ... effects) {
        this.baseName = baseName;
        this.effects = ImmutableList.copyOf(effects);
    }

    public String finishTranslationKey(String prefix) {
        return prefix + (this.baseName == null ? Registry.POTION.getId(this).getPath() : this.baseName);
    }

    public List<StatusEffectInstance> getEffects() {
        return this.effects;
    }

    public boolean hasInstantEffect() {
        if (!this.effects.isEmpty()) {
            for (StatusEffectInstance statusEffectInstance : this.effects) {
                if (!statusEffectInstance.getEffectType().isInstant()) continue;
                return true;
            }
        }
        return false;
    }
}


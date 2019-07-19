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

    public static Potion byId(String string) {
        return Registry.POTION.get(Identifier.tryParse(string));
    }

    public Potion(StatusEffectInstance ... statusEffectInstances) {
        this((String)null, statusEffectInstances);
    }

    public Potion(@Nullable String string, StatusEffectInstance ... statusEffectInstances) {
        this.baseName = string;
        this.effects = ImmutableList.copyOf(statusEffectInstances);
    }

    public String finishTranslationKey(String string) {
        return string + (this.baseName == null ? Registry.POTION.getId(this).getPath() : this.baseName);
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


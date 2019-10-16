/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.context;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextParameter;

public class LootContextType {
    private final Set<LootContextParameter<?>> required;
    private final Set<LootContextParameter<?>> allowed;

    private LootContextType(Set<LootContextParameter<?>> set, Set<LootContextParameter<?>> set2) {
        this.required = ImmutableSet.copyOf(set);
        this.allowed = ImmutableSet.copyOf(Sets.union(set, set2));
    }

    public Set<LootContextParameter<?>> getRequired() {
        return this.required;
    }

    public Set<LootContextParameter<?>> getAllowed() {
        return this.allowed;
    }

    public String toString() {
        return "[" + Joiner.on(", ").join(this.allowed.stream().map(lootContextParameter -> (this.required.contains(lootContextParameter) ? "!" : "") + lootContextParameter.getIdentifier()).iterator()) + "]";
    }

    public void check(LootTableReporter lootTableReporter, LootContextAware lootContextAware) {
        Set<LootContextParameter<?>> set = lootContextAware.getRequiredParameters();
        Sets.SetView<LootContextParameter<?>> set2 = Sets.difference(set, this.allowed);
        if (!set2.isEmpty()) {
            lootTableReporter.report("Parameters " + set2 + " are not provided in this context");
        }
    }

    public static class Builder {
        private final Set<LootContextParameter<?>> required = Sets.newIdentityHashSet();
        private final Set<LootContextParameter<?>> allowed = Sets.newIdentityHashSet();

        public Builder require(LootContextParameter<?> lootContextParameter) {
            if (this.allowed.contains(lootContextParameter)) {
                throw new IllegalArgumentException("Parameter " + lootContextParameter.getIdentifier() + " is already optional");
            }
            this.required.add(lootContextParameter);
            return this;
        }

        public Builder allow(LootContextParameter<?> lootContextParameter) {
            if (this.required.contains(lootContextParameter)) {
                throw new IllegalArgumentException("Parameter " + lootContextParameter.getIdentifier() + " is already required");
            }
            this.allowed.add(lootContextParameter);
            return this;
        }

        public LootContextType build() {
            return new LootContextType(this.required, this.allowed);
        }
    }
}


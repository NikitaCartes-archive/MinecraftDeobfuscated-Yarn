/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootTable;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContextAware;
import net.minecraft.world.loot.context.LootContextType;
import org.jetbrains.annotations.Nullable;

public class LootTableReporter {
    private final Multimap<String, String> messages;
    private final Supplier<String> nameFactory;
    private final LootContextType contextType;
    private final Function<Identifier, LootCondition> conditionGetter;
    private final Set<Identifier> conditions;
    private final Function<Identifier, LootTable> supplierGetter;
    private final Set<Identifier> suppliers;
    private String name;

    public LootTableReporter(LootContextType lootContextType, Function<Identifier, LootCondition> function, Function<Identifier, LootTable> function2) {
        this(HashMultimap.create(), () -> "", lootContextType, function, ImmutableSet.of(), function2, ImmutableSet.of());
    }

    public LootTableReporter(Multimap<String, String> multimap, Supplier<String> supplier, LootContextType lootContextType, Function<Identifier, LootCondition> function, Set<Identifier> set, Function<Identifier, LootTable> function2, Set<Identifier> set2) {
        this.messages = multimap;
        this.nameFactory = supplier;
        this.contextType = lootContextType;
        this.conditionGetter = function;
        this.conditions = set;
        this.supplierGetter = function2;
        this.suppliers = set2;
    }

    private String getContext() {
        if (this.name == null) {
            this.name = this.nameFactory.get();
        }
        return this.name;
    }

    public void report(String string) {
        this.messages.put(this.getContext(), string);
    }

    public LootTableReporter makeChild(String string) {
        return new LootTableReporter(this.messages, () -> this.getContext() + string, this.contextType, this.conditionGetter, this.conditions, this.supplierGetter, this.suppliers);
    }

    public LootTableReporter withSupplier(String string, Identifier identifier) {
        ImmutableCollection immutableSet = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().addAll(this.suppliers)).add(identifier)).build();
        return new LootTableReporter(this.messages, () -> this.getContext() + string, this.contextType, this.conditionGetter, this.conditions, this.supplierGetter, (Set<Identifier>)((Object)immutableSet));
    }

    public LootTableReporter withCondition(String string, Identifier identifier) {
        ImmutableCollection immutableSet = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().addAll(this.conditions)).add(identifier)).build();
        return new LootTableReporter(this.messages, () -> this.getContext() + string, this.contextType, this.conditionGetter, (Set<Identifier>)((Object)immutableSet), this.supplierGetter, this.suppliers);
    }

    public boolean hasSupplier(Identifier identifier) {
        return this.suppliers.contains(identifier);
    }

    public boolean hasCondition(Identifier identifier) {
        return this.conditions.contains(identifier);
    }

    public Multimap<String, String> getMessages() {
        return ImmutableMultimap.copyOf(this.messages);
    }

    public void checkContext(LootContextAware lootContextAware) {
        this.contextType.check(this, lootContextAware);
    }

    @Nullable
    public LootTable getSupplier(Identifier identifier) {
        return this.supplierGetter.apply(identifier);
    }

    @Nullable
    public LootCondition getCondition(Identifier identifier) {
        return this.conditionGetter.apply(identifier);
    }

    public LootTableReporter withContextType(LootContextType lootContextType) {
        return new LootTableReporter(this.messages, this.nameFactory, lootContextType, this.conditionGetter, this.conditions, this.supplierGetter, this.suppliers);
    }
}


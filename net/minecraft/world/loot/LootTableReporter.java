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
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.context.ParameterConsumer;
import org.jetbrains.annotations.Nullable;

public class LootTableReporter {
    private final Multimap<String, String> messages;
    private final Supplier<String> nameFactory;
    private final LootContextType field_20756;
    private final Function<Identifier, class_4570> field_20757;
    private final Set<Identifier> field_20758;
    private final Function<Identifier, LootSupplier> field_20759;
    private final Set<Identifier> field_20760;
    private String name;

    public LootTableReporter(LootContextType lootContextType, Function<Identifier, class_4570> function, Function<Identifier, LootSupplier> function2) {
        this(HashMultimap.create(), () -> "", lootContextType, function, ImmutableSet.of(), function2, ImmutableSet.of());
    }

    public LootTableReporter(Multimap<String, String> multimap, Supplier<String> supplier, LootContextType lootContextType, Function<Identifier, class_4570> function, Set<Identifier> set, Function<Identifier, LootSupplier> function2, Set<Identifier> set2) {
        this.messages = multimap;
        this.nameFactory = supplier;
        this.field_20756 = lootContextType;
        this.field_20757 = function;
        this.field_20758 = set;
        this.field_20759 = function2;
        this.field_20760 = set2;
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
        return new LootTableReporter(this.messages, () -> this.getContext() + string, this.field_20756, this.field_20757, this.field_20758, this.field_20759, this.field_20760);
    }

    public LootTableReporter method_22569(String string, Identifier identifier) {
        ImmutableCollection immutableSet = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().addAll(this.field_20760)).add(identifier)).build();
        return new LootTableReporter(this.messages, () -> this.getContext() + string, this.field_20756, this.field_20757, this.field_20758, this.field_20759, (Set<Identifier>)((Object)immutableSet));
    }

    public LootTableReporter method_22571(String string, Identifier identifier) {
        ImmutableCollection immutableSet = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().addAll(this.field_20758)).add(identifier)).build();
        return new LootTableReporter(this.messages, () -> this.getContext() + string, this.field_20756, this.field_20757, (Set<Identifier>)((Object)immutableSet), this.field_20759, this.field_20760);
    }

    public boolean method_22570(Identifier identifier) {
        return this.field_20760.contains(identifier);
    }

    public boolean method_22572(Identifier identifier) {
        return this.field_20758.contains(identifier);
    }

    public Multimap<String, String> getMessages() {
        return ImmutableMultimap.copyOf(this.messages);
    }

    public void method_22567(ParameterConsumer parameterConsumer) {
        this.field_20756.check(this, parameterConsumer);
    }

    @Nullable
    public LootSupplier method_22574(Identifier identifier) {
        return this.field_20759.apply(identifier);
    }

    @Nullable
    public class_4570 method_22576(Identifier identifier) {
        return this.field_20757.apply(identifier);
    }

    public LootTableReporter method_22568(LootContextType lootContextType) {
        return new LootTableReporter(this.messages, this.nameFactory, lootContextType, this.field_20757, this.field_20758, this.field_20759, this.field_20760);
    }
}


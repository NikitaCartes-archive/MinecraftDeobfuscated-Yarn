package net.minecraft.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

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

	public LootTableReporter(
		Multimap<String, String> multimap,
		Supplier<String> supplier,
		LootContextType lootContextType,
		Function<Identifier, LootCondition> function,
		Set<Identifier> set,
		Function<Identifier, LootTable> function2,
		Set<Identifier> set2
	) {
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
			this.name = (String)this.nameFactory.get();
		}

		return this.name;
	}

	public void report(String message) {
		this.messages.put(this.getContext(), message);
	}

	public LootTableReporter makeChild(String name) {
		return new LootTableReporter(
			this.messages, () -> this.getContext() + name, this.contextType, this.conditionGetter, this.conditions, this.supplierGetter, this.suppliers
		);
	}

	public LootTableReporter withSupplier(String name, Identifier id) {
		ImmutableSet<Identifier> immutableSet = ImmutableSet.<Identifier>builder().addAll(this.suppliers).add(id).build();
		return new LootTableReporter(
			this.messages, () -> this.getContext() + name, this.contextType, this.conditionGetter, this.conditions, this.supplierGetter, immutableSet
		);
	}

	public LootTableReporter withCondition(String name, Identifier id) {
		ImmutableSet<Identifier> immutableSet = ImmutableSet.<Identifier>builder().addAll(this.conditions).add(id).build();
		return new LootTableReporter(
			this.messages, () -> this.getContext() + name, this.contextType, this.conditionGetter, immutableSet, this.supplierGetter, this.suppliers
		);
	}

	public boolean hasSupplier(Identifier id) {
		return this.suppliers.contains(id);
	}

	public boolean hasCondition(Identifier id) {
		return this.conditions.contains(id);
	}

	public Multimap<String, String> getMessages() {
		return ImmutableMultimap.copyOf(this.messages);
	}

	public void checkContext(LootContextAware contextAware) {
		this.contextType.check(this, contextAware);
	}

	@Nullable
	public LootTable getSupplier(Identifier id) {
		return (LootTable)this.supplierGetter.apply(id);
	}

	@Nullable
	public LootCondition getCondition(Identifier id) {
		return (LootCondition)this.conditionGetter.apply(id);
	}

	public LootTableReporter withContextType(LootContextType contextType) {
		return new LootTableReporter(this.messages, this.nameFactory, contextType, this.conditionGetter, this.conditions, this.supplierGetter, this.suppliers);
	}
}

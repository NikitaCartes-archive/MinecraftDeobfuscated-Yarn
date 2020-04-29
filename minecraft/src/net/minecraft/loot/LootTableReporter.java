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
	private final Function<Identifier, LootTable> tableGetter;
	private final Set<Identifier> tables;
	private String name;

	public LootTableReporter(LootContextType contextType, Function<Identifier, LootCondition> conditionGetter, Function<Identifier, LootTable> tableFactory) {
		this(HashMultimap.create(), () -> "", contextType, conditionGetter, ImmutableSet.of(), tableFactory, ImmutableSet.of());
	}

	public LootTableReporter(
		Multimap<String, String> messages,
		Supplier<String> nameFactory,
		LootContextType contextType,
		Function<Identifier, LootCondition> conditionGetter,
		Set<Identifier> conditions,
		Function<Identifier, LootTable> tableGetter,
		Set<Identifier> tables
	) {
		this.messages = messages;
		this.nameFactory = nameFactory;
		this.contextType = contextType;
		this.conditionGetter = conditionGetter;
		this.conditions = conditions;
		this.tableGetter = tableGetter;
		this.tables = tables;
	}

	private String getName() {
		if (this.name == null) {
			this.name = (String)this.nameFactory.get();
		}

		return this.name;
	}

	public void report(String message) {
		this.messages.put(this.getName(), message);
	}

	public LootTableReporter makeChild(String name) {
		return new LootTableReporter(
			this.messages, () -> this.getName() + name, this.contextType, this.conditionGetter, this.conditions, this.tableGetter, this.tables
		);
	}

	public LootTableReporter withTable(String name, Identifier id) {
		ImmutableSet<Identifier> immutableSet = ImmutableSet.<Identifier>builder().addAll(this.tables).add(id).build();
		return new LootTableReporter(
			this.messages, () -> this.getName() + name, this.contextType, this.conditionGetter, this.conditions, this.tableGetter, immutableSet
		);
	}

	public LootTableReporter withCondition(String name, Identifier id) {
		ImmutableSet<Identifier> immutableSet = ImmutableSet.<Identifier>builder().addAll(this.conditions).add(id).build();
		return new LootTableReporter(this.messages, () -> this.getName() + name, this.contextType, this.conditionGetter, immutableSet, this.tableGetter, this.tables);
	}

	public boolean hasTable(Identifier id) {
		return this.tables.contains(id);
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
	public LootTable getTable(Identifier id) {
		return (LootTable)this.tableGetter.apply(id);
	}

	@Nullable
	public LootCondition getCondition(Identifier id) {
		return (LootCondition)this.conditionGetter.apply(id);
	}

	public LootTableReporter withContextType(LootContextType contextType) {
		return new LootTableReporter(this.messages, this.nameFactory, contextType, this.conditionGetter, this.conditions, this.tableGetter, this.tables);
	}
}

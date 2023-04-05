package net.minecraft.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextType;

public class LootTableReporter {
	private final Multimap<String, String> messages;
	private final Supplier<String> nameFactory;
	private final LootContextType contextType;
	private final LootDataLookup dataLookup;
	private final Set<LootDataKey<?>> referenceStack;
	@Nullable
	private String name;

	public LootTableReporter(LootContextType contextType, LootDataLookup dataLookup) {
		this(HashMultimap.create(), () -> "", contextType, dataLookup, ImmutableSet.of());
	}

	public LootTableReporter(
		Multimap<String, String> messages, Supplier<String> nameFactory, LootContextType contextType, LootDataLookup dataLookup, Set<LootDataKey<?>> referenceStack
	) {
		this.messages = messages;
		this.nameFactory = nameFactory;
		this.contextType = contextType;
		this.dataLookup = dataLookup;
		this.referenceStack = referenceStack;
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
		return new LootTableReporter(this.messages, () -> this.getName() + name, this.contextType, this.dataLookup, this.referenceStack);
	}

	public LootTableReporter makeChild(String name, LootDataKey<?> currentKey) {
		ImmutableSet<LootDataKey<?>> immutableSet = ImmutableSet.<LootDataKey<?>>builder().addAll(this.referenceStack).add(currentKey).build();
		return new LootTableReporter(this.messages, () -> this.getName() + name, this.contextType, this.dataLookup, immutableSet);
	}

	public boolean isInStack(LootDataKey<?> key) {
		return this.referenceStack.contains(key);
	}

	public Multimap<String, String> getMessages() {
		return ImmutableMultimap.copyOf(this.messages);
	}

	public void validateContext(LootContextAware contextAware) {
		this.contextType.validate(this, contextAware);
	}

	public LootDataLookup getDataLookup() {
		return this.dataLookup;
	}

	public LootTableReporter withContextType(LootContextType contextType) {
		return new LootTableReporter(this.messages, this.nameFactory, contextType, this.dataLookup, this.referenceStack);
	}
}

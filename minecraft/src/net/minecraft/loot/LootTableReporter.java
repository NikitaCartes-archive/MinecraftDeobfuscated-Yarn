package net.minecraft.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;

public class LootTableReporter {
	private final ErrorReporter errorReporter;
	private final LootContextType contextType;
	private final RegistryEntryLookup.RegistryLookup dataLookup;
	private final Set<RegistryKey<?>> referenceStack;

	public LootTableReporter(ErrorReporter errorReporter, LootContextType contextType, RegistryEntryLookup.RegistryLookup dataLookup) {
		this(errorReporter, contextType, dataLookup, Set.of());
	}

	private LootTableReporter(
		ErrorReporter errorReporter, LootContextType contextType, RegistryEntryLookup.RegistryLookup dataLookup, Set<RegistryKey<?>> referenceStack
	) {
		this.errorReporter = errorReporter;
		this.contextType = contextType;
		this.dataLookup = dataLookup;
		this.referenceStack = referenceStack;
	}

	public LootTableReporter makeChild(String name) {
		return new LootTableReporter(this.errorReporter.makeChild(name), this.contextType, this.dataLookup, this.referenceStack);
	}

	public LootTableReporter makeChild(String name, RegistryKey<?> key) {
		Set<RegistryKey<?>> set = ImmutableSet.<RegistryKey<?>>builder().addAll(this.referenceStack).add(key).build();
		return new LootTableReporter(this.errorReporter.makeChild(name), this.contextType, this.dataLookup, set);
	}

	public boolean isInStack(RegistryKey<?> key) {
		return this.referenceStack.contains(key);
	}

	public void report(String message) {
		this.errorReporter.report(message);
	}

	public void validateContext(LootContextAware contextAware) {
		this.contextType.validate(this, contextAware);
	}

	public RegistryEntryLookup.RegistryLookup getDataLookup() {
		return this.dataLookup;
	}

	public LootTableReporter withContextType(LootContextType contextType) {
		return new LootTableReporter(this.errorReporter, contextType, this.dataLookup, this.referenceStack);
	}
}

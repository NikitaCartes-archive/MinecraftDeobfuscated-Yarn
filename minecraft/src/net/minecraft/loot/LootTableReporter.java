package net.minecraft.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;

public class LootTableReporter {
	private final ErrorReporter errorReporter;
	private final ContextType contextType;
	private final Optional<RegistryEntryLookup.RegistryLookup> dataLookup;
	private final Set<RegistryKey<?>> referenceStack;

	public LootTableReporter(ErrorReporter errorReporter, ContextType contextType, RegistryEntryLookup.RegistryLookup dataLookup) {
		this(errorReporter, contextType, Optional.of(dataLookup), Set.of());
	}

	public LootTableReporter(ErrorReporter errorReporter, ContextType contextType) {
		this(errorReporter, contextType, Optional.empty(), Set.of());
	}

	private LootTableReporter(
		ErrorReporter errorReporter, ContextType contextType, Optional<RegistryEntryLookup.RegistryLookup> dataLookup, Set<RegistryKey<?>> referenceStack
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
		Set<ContextParameter<?>> set = contextAware.getAllowedParameters();
		Set<ContextParameter<?>> set2 = Sets.<ContextParameter<?>>difference(set, this.contextType.getAllowed());
		if (!set2.isEmpty()) {
			this.errorReporter.report("Parameters " + set2 + " are not provided in this context");
		}
	}

	public RegistryEntryLookup.RegistryLookup getDataLookup() {
		return (RegistryEntryLookup.RegistryLookup)this.dataLookup.orElseThrow(() -> new UnsupportedOperationException("References not allowed"));
	}

	public boolean canUseReferences() {
		return this.dataLookup.isPresent();
	}

	public LootTableReporter withContextType(ContextType contextType) {
		return new LootTableReporter(this.errorReporter, contextType, this.dataLookup, this.referenceStack);
	}

	public ErrorReporter getErrorReporter() {
		return this.errorReporter;
	}
}

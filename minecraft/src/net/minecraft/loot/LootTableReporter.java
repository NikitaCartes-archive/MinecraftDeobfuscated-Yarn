package net.minecraft.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.ErrorReporter;

public class LootTableReporter {
	private final ErrorReporter errorReporter;
	private final LootContextType contextType;
	private final LootDataLookup dataLookup;
	private final Set<LootDataKey<?>> referenceStack;

	public LootTableReporter(ErrorReporter errorReporter, LootContextType contextType, LootDataLookup dataLookup) {
		this(errorReporter, contextType, dataLookup, Set.of());
	}

	private LootTableReporter(ErrorReporter errorReporter, LootContextType contextType, LootDataLookup dataLookup, Set<LootDataKey<?>> referenceStack) {
		this.errorReporter = errorReporter;
		this.contextType = contextType;
		this.dataLookup = dataLookup;
		this.referenceStack = referenceStack;
	}

	public LootTableReporter makeChild(String name) {
		return new LootTableReporter(this.errorReporter.makeChild(name), this.contextType, this.dataLookup, this.referenceStack);
	}

	public LootTableReporter makeChild(String name, LootDataKey<?> currentKey) {
		ImmutableSet<LootDataKey<?>> immutableSet = ImmutableSet.<LootDataKey<?>>builder().addAll(this.referenceStack).add(currentKey).build();
		return new LootTableReporter(this.errorReporter.makeChild(name), this.contextType, this.dataLookup, immutableSet);
	}

	public boolean isInStack(LootDataKey<?> key) {
		return this.referenceStack.contains(key);
	}

	public void report(String message) {
		this.errorReporter.report(message);
	}

	public void validateContext(LootContextAware contextAware) {
		this.contextType.validate(this, contextAware);
	}

	public LootDataLookup getDataLookup() {
		return this.dataLookup;
	}

	public LootTableReporter withContextType(LootContextType contextType) {
		return new LootTableReporter(this.errorReporter, contextType, this.dataLookup, this.referenceStack);
	}
}

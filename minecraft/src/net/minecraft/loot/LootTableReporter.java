package net.minecraft.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;

public class LootTableReporter {
	private final Multimap<String, String> messages;
	private final Supplier<String> nameFactory;
	private String name;

	public LootTableReporter() {
		this(HashMultimap.create(), () -> "");
	}

	public LootTableReporter(Multimap<String, String> messages, Supplier<String> nameFactory) {
		this.messages = messages;
		this.nameFactory = nameFactory;
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
		return new LootTableReporter(this.messages, () -> this.getContext() + name);
	}

	public Multimap<String, String> getMessages() {
		return ImmutableMultimap.copyOf(this.messages);
	}
}

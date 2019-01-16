package net.minecraft.world.loot;

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

	public LootTableReporter(Multimap<String, String> multimap, Supplier<String> supplier) {
		this.messages = multimap;
		this.nameFactory = supplier;
	}

	private String getContext() {
		if (this.name == null) {
			this.name = (String)this.nameFactory.get();
		}

		return this.name;
	}

	public void report(String string) {
		this.messages.put(this.getContext(), string);
	}

	public LootTableReporter makeChild(String string) {
		return new LootTableReporter(this.messages, () -> this.getContext() + string);
	}

	public Multimap<String, String> getMessages() {
		return ImmutableMultimap.copyOf(this.messages);
	}
}

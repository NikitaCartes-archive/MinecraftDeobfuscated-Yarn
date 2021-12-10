package net.minecraft.loot.context;

import net.minecraft.util.Identifier;

public class LootContextParameter<T> {
	private final Identifier id;

	public LootContextParameter(Identifier id) {
		this.id = id;
	}

	public Identifier getId() {
		return this.id;
	}

	public String toString() {
		return "<parameter " + this.id + ">";
	}
}

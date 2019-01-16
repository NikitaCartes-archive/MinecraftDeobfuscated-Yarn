package net.minecraft.world.loot.context;

import net.minecraft.util.Identifier;

public class Parameter<T> {
	private final Identifier id;

	public Parameter(Identifier identifier) {
		this.id = identifier;
	}

	public Identifier getIdentifier() {
		return this.id;
	}

	public String toString() {
		return "<parameter " + this.id + ">";
	}
}

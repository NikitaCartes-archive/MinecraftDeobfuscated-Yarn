package net.minecraft.world.loot.context;

import net.minecraft.util.Identifier;

public class LootContextParameter<T> {
	private final Identifier field_1162;

	public LootContextParameter(Identifier identifier) {
		this.field_1162 = identifier;
	}

	public Identifier method_746() {
		return this.field_1162;
	}

	public String toString() {
		return "<parameter " + this.field_1162 + ">";
	}
}

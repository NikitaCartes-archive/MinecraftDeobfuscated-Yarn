package net.minecraft.util.context;

import net.minecraft.util.Identifier;

public class ContextParameter<T> {
	private final Identifier id;

	public ContextParameter(Identifier id) {
		this.id = id;
	}

	public static <T> ContextParameter<T> of(String id) {
		return new ContextParameter<>(Identifier.ofVanilla(id));
	}

	public Identifier getId() {
		return this.id;
	}

	public String toString() {
		return "<parameter " + this.id + ">";
	}
}

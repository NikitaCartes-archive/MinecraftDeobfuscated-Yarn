package net.minecraft.advancement.criterion;

import net.minecraft.util.Identifier;

public class AbstractCriterionConditions implements CriterionConditions {
	private final Identifier id;

	public AbstractCriterionConditions(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public String toString() {
		return "AbstractCriterionInstance{criterion=" + this.id + '}';
	}
}

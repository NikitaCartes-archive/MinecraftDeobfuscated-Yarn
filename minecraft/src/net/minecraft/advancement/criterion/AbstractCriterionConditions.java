package net.minecraft.advancement.criterion;

import net.minecraft.util.Identifier;

public class AbstractCriterionConditions implements CriterionConditions {
	private final Identifier field_1270;

	public AbstractCriterionConditions(Identifier identifier) {
		this.field_1270 = identifier;
	}

	@Override
	public Identifier getId() {
		return this.field_1270;
	}

	public String toString() {
		return "AbstractCriterionInstance{criterion=" + this.field_1270 + '}';
	}
}

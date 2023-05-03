package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.util.Identifier;

public abstract class AbstractCriterionConditions implements CriterionConditions {
	private final Identifier id;
	private final LootContextPredicate playerPredicate;

	public AbstractCriterionConditions(Identifier id, LootContextPredicate entity) {
		this.id = id;
		this.playerPredicate = entity;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	protected LootContextPredicate getPlayerPredicate() {
		return this.playerPredicate;
	}

	@Override
	public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("player", this.playerPredicate.toJson(predicateSerializer));
		return jsonObject;
	}

	public String toString() {
		return "AbstractCriterionInstance{criterion=" + this.id + "}";
	}
}

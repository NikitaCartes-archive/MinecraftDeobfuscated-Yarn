package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.predicate.entity.LootContextPredicate;

public abstract class AbstractCriterionConditions implements AbstractCriterion.Conditions {
	private final Optional<LootContextPredicate> playerPredicate;

	public AbstractCriterionConditions(Optional<LootContextPredicate> playerPredicate) {
		this.playerPredicate = playerPredicate;
	}

	@Override
	public Optional<LootContextPredicate> getPlayerPredicate() {
		return this.playerPredicate;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		this.playerPredicate.ifPresent(predicate -> jsonObject.add("player", predicate.toJson()));
		return jsonObject;
	}
}

package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.util.Identifier;

public abstract class AbstractCriterionConditions implements CriterionConditions {
	private final Identifier id;
	private final Optional<LootContextPredicate> playerPredicate;

	public AbstractCriterionConditions(Identifier id, Optional<LootContextPredicate> playerPredicate) {
		this.id = id;
		this.playerPredicate = playerPredicate;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	protected Optional<LootContextPredicate> getPlayerPredicate() {
		return this.playerPredicate;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		this.playerPredicate.ifPresent(lootContextPredicate -> jsonObject.add("player", lootContextPredicate.toJson()));
		return jsonObject;
	}

	public String toString() {
		return "AbstractCriterionInstance{criterion=" + this.id + "}";
	}
}

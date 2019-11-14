package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BredAnimalsCriterion extends AbstractCriterion<BredAnimalsCriterion.Conditions> {
	private static final Identifier ID = new Identifier("bred_animals");

	@Override
	public Identifier getId() {
		return ID;
	}

	public BredAnimalsCriterion.Conditions method_854(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("parent"));
		EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("partner"));
		EntityPredicate entityPredicate3 = EntityPredicate.fromJson(jsonObject.get("child"));
		return new BredAnimalsCriterion.Conditions(entityPredicate, entityPredicate2, entityPredicate3);
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity parent, @Nullable AnimalEntity partner, @Nullable PassiveEntity child) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, parent, partner, child));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate parent;
		private final EntityPredicate partner;
		private final EntityPredicate child;

		public Conditions(EntityPredicate parent, EntityPredicate partner, EntityPredicate child) {
			super(BredAnimalsCriterion.ID);
			this.parent = parent;
			this.partner = partner;
			this.child = child;
		}

		public static BredAnimalsCriterion.Conditions any() {
			return new BredAnimalsCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public static BredAnimalsCriterion.Conditions create(EntityPredicate.Builder builder) {
			return new BredAnimalsCriterion.Conditions(builder.build(), EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity player, AnimalEntity parent, @Nullable AnimalEntity partner, @Nullable PassiveEntity child) {
			return !this.child.test(player, child)
				? false
				: this.parent.test(player, parent) && this.partner.test(player, partner) || this.parent.test(player, partner) && this.partner.test(player, parent);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("parent", this.parent.serialize());
			jsonObject.add("partner", this.partner.serialize());
			jsonObject.add("child", this.child.serialize());
			return jsonObject;
		}
	}
}

package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RecipeUnlockedCriterion extends AbstractCriterion<RecipeUnlockedCriterion.Conditions> {
	@Override
	public Codec<RecipeUnlockedCriterion.Conditions> getConditionsCodec() {
		return RecipeUnlockedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, RecipeEntry<?> recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public static AdvancementCriterion<RecipeUnlockedCriterion.Conditions> create(Identifier id) {
		return Criteria.RECIPE_UNLOCKED.create(new RecipeUnlockedCriterion.Conditions(Optional.empty(), id));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Identifier recipe) implements AbstractCriterion.Conditions {
		public static final Codec<RecipeUnlockedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(RecipeUnlockedCriterion.Conditions::player),
						Identifier.CODEC.fieldOf("recipe").forGetter(RecipeUnlockedCriterion.Conditions::recipe)
					)
					.apply(instance, RecipeUnlockedCriterion.Conditions::new)
		);

		public boolean matches(RecipeEntry<?> recipe) {
			return this.recipe.equals(recipe.id());
		}
	}
}

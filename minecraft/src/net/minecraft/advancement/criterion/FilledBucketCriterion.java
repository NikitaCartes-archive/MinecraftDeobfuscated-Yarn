package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class FilledBucketCriterion extends AbstractCriterion<FilledBucketCriterion.Conditions> {
	@Override
	public Codec<FilledBucketCriterion.Conditions> getConditionsCodec() {
		return FilledBucketCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions {
		public static final Codec<FilledBucketCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(FilledBucketCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FilledBucketCriterion.Conditions::item)
					)
					.apply(instance, FilledBucketCriterion.Conditions::new)
		);

		public static AdvancementCriterion<FilledBucketCriterion.Conditions> create(ItemPredicate.Builder item) {
			return Criteria.FILLED_BUCKET.create(new FilledBucketCriterion.Conditions(Optional.empty(), Optional.of(item.build())));
		}

		public boolean matches(ItemStack stack) {
			return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
		}
	}
}

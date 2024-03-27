package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConsumeItemCriterion extends AbstractCriterion<ConsumeItemCriterion.Conditions> {
	@Override
	public Codec<ConsumeItemCriterion.Conditions> getConditionsCodec() {
		return ConsumeItemCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions {
		public static final Codec<ConsumeItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ConsumeItemCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ConsumeItemCriterion.Conditions::item)
					)
					.apply(instance, ConsumeItemCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ConsumeItemCriterion.Conditions> any() {
			return Criteria.CONSUME_ITEM.create(new ConsumeItemCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<ConsumeItemCriterion.Conditions> item(ItemConvertible item) {
			return predicate(ItemPredicate.Builder.create().items(item.asItem()));
		}

		public static AdvancementCriterion<ConsumeItemCriterion.Conditions> predicate(ItemPredicate.Builder predicate) {
			return Criteria.CONSUME_ITEM.create(new ConsumeItemCriterion.Conditions(Optional.empty(), Optional.of(predicate.build())));
		}

		public boolean matches(ItemStack stack) {
			return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test(stack);
		}
	}
}

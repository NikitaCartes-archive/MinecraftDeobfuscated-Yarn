package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ItemDurabilityChangedCriterion extends AbstractCriterion<ItemDurabilityChangedCriterion.Conditions> {
	@Override
	public Codec<ItemDurabilityChangedCriterion.Conditions> getConditionsCodec() {
		return ItemDurabilityChangedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int durability) {
		this.trigger(player, conditions -> conditions.matches(stack, durability));
	}

	public static record Conditions(
		Optional<LootContextPredicate> player, Optional<ItemPredicate> item, NumberRange.IntRange durability, NumberRange.IntRange delta
	) implements AbstractCriterion.Conditions {
		public static final Codec<ItemDurabilityChangedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ItemDurabilityChangedCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ItemDurabilityChangedCriterion.Conditions::item),
						NumberRange.IntRange.CODEC.optionalFieldOf("durability", NumberRange.IntRange.ANY).forGetter(ItemDurabilityChangedCriterion.Conditions::durability),
						NumberRange.IntRange.CODEC.optionalFieldOf("delta", NumberRange.IntRange.ANY).forGetter(ItemDurabilityChangedCriterion.Conditions::delta)
					)
					.apply(instance, ItemDurabilityChangedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ItemDurabilityChangedCriterion.Conditions> create(Optional<ItemPredicate> item, NumberRange.IntRange durability) {
			return create(Optional.empty(), item, durability);
		}

		public static AdvancementCriterion<ItemDurabilityChangedCriterion.Conditions> create(
			Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, NumberRange.IntRange durability
		) {
			return Criteria.ITEM_DURABILITY_CHANGED.create(new ItemDurabilityChangedCriterion.Conditions(playerPredicate, item, durability, NumberRange.IntRange.ANY));
		}

		public boolean matches(ItemStack stack, int durability) {
			if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)) {
				return false;
			} else {
				return !this.durability.test(stack.getMaxDamage() - durability) ? false : this.delta.test(stack.getDamage() - durability);
			}
		}
	}
}

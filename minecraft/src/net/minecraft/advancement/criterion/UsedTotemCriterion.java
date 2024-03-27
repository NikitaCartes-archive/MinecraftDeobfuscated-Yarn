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

public class UsedTotemCriterion extends AbstractCriterion<UsedTotemCriterion.Conditions> {
	@Override
	public Codec<UsedTotemCriterion.Conditions> getConditionsCodec() {
		return UsedTotemCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions {
		public static final Codec<UsedTotemCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsedTotemCriterion.Conditions::player),
						ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsedTotemCriterion.Conditions::item)
					)
					.apply(instance, UsedTotemCriterion.Conditions::new)
		);

		public static AdvancementCriterion<UsedTotemCriterion.Conditions> create(ItemPredicate itemPredicate) {
			return Criteria.USED_TOTEM.create(new UsedTotemCriterion.Conditions(Optional.empty(), Optional.of(itemPredicate)));
		}

		public static AdvancementCriterion<UsedTotemCriterion.Conditions> create(ItemConvertible item) {
			return Criteria.USED_TOTEM.create(new UsedTotemCriterion.Conditions(Optional.empty(), Optional.of(ItemPredicate.Builder.create().items(item).build())));
		}

		public boolean matches(ItemStack stack) {
			return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test(stack);
		}
	}
}

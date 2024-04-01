package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LubricationComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

public class ThrowLubricatedCriterion extends AbstractCriterion<ThrowLubricatedCriterion.Conditions> {
	@Override
	public Codec<ThrowLubricatedCriterion.Conditions> getConditionsCodec() {
		return ThrowLubricatedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.test(stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> itemPredicate, int minLubrication)
		implements AbstractCriterion.Conditions {
		public static final Codec<ThrowLubricatedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(ThrowLubricatedCriterion.Conditions::player),
						Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "item_predicate").forGetter(ThrowLubricatedCriterion.Conditions::itemPredicate),
						Codec.INT.fieldOf("min_lubrication").forGetter(ThrowLubricatedCriterion.Conditions::minLubrication)
					)
					.apply(instance, ThrowLubricatedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ThrowLubricatedCriterion.Conditions> create(int minLubrication) {
			return Criteria.THROW_LUBRICATED.create(new ThrowLubricatedCriterion.Conditions(Optional.empty(), Optional.empty(), minLubrication));
		}

		public static AdvancementCriterion<ThrowLubricatedCriterion.Conditions> create(ItemPredicate itemPredicate, int minLubrication) {
			return Criteria.THROW_LUBRICATED.create(new ThrowLubricatedCriterion.Conditions(Optional.empty(), Optional.of(itemPredicate), minLubrication));
		}

		public boolean test(ItemStack stack) {
			if (this.itemPredicate.isPresent() && !((ItemPredicate)this.itemPredicate.get()).test(stack)) {
				return false;
			} else {
				LubricationComponent lubricationComponent = stack.get(DataComponentTypes.LUBRICATION);
				return lubricationComponent == null ? false : lubricationComponent.getLevel() >= this.minLubrication;
			}
		}
	}
}

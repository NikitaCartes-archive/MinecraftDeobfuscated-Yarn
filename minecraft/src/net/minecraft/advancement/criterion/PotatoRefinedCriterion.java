package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LubricationComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public class PotatoRefinedCriterion extends AbstractCriterion<PotatoRefinedCriterion.Conditions> {
	static final Codec<PotatoRefinedCriterion.ResultPredicate> RESULT_PREDICATE_CODEC = StringIdentifiable.createCodec(PotatoRefinedCriterion.Type::values)
		.dispatch(PotatoRefinedCriterion.ResultPredicate::getType, PotatoRefinedCriterion.Type::getCodec);

	@Override
	public Codec<PotatoRefinedCriterion.Conditions> getConditionsCodec() {
		return PotatoRefinedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.test(stack));
	}

	public static record Conditions(Optional<LootContextPredicate> player, PotatoRefinedCriterion.ResultPredicate resultPredicate)
		implements AbstractCriterion.Conditions {
		public static final Codec<PotatoRefinedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(PotatoRefinedCriterion.Conditions::player),
						PotatoRefinedCriterion.RESULT_PREDICATE_CODEC.fieldOf("result_predicate").forGetter(PotatoRefinedCriterion.Conditions::resultPredicate)
					)
					.apply(instance, PotatoRefinedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<PotatoRefinedCriterion.Conditions> createStandard(Item item) {
			return Criteria.POTATO_REFINED
				.create(new PotatoRefinedCriterion.Conditions(Optional.empty(), new PotatoRefinedCriterion.Standard(ItemPredicate.Builder.create().items(item).build())));
		}

		public static AdvancementCriterion<PotatoRefinedCriterion.Conditions> createLubrication(int minLubricationLevel) {
			return Criteria.POTATO_REFINED
				.create(
					new PotatoRefinedCriterion.Conditions(
						Optional.empty(), new PotatoRefinedCriterion.Lubrication(ItemPredicate.Builder.create().build(), minLubricationLevel)
					)
				);
		}

		public static AdvancementCriterion<PotatoRefinedCriterion.Conditions> createLubrication(ItemPredicate itemPredicate, int minLubricationLevel) {
			return Criteria.POTATO_REFINED
				.create(new PotatoRefinedCriterion.Conditions(Optional.empty(), new PotatoRefinedCriterion.Lubrication(itemPredicate, minLubricationLevel)));
		}

		public boolean test(ItemStack stack) {
			return this.resultPredicate.test(stack);
		}
	}

	static record Lubrication(ItemPredicate itemPredicate, int minLubricationLevel) implements PotatoRefinedCriterion.ResultPredicate {
		public static final Codec<PotatoRefinedCriterion.Lubrication> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						ItemPredicate.CODEC.fieldOf("item_predicate").forGetter(PotatoRefinedCriterion.Lubrication::itemPredicate),
						Codec.INT.fieldOf("min_lubrication").forGetter(PotatoRefinedCriterion.Lubrication::minLubricationLevel)
					)
					.apply(instance, PotatoRefinedCriterion.Lubrication::new)
		);

		@Override
		public PotatoRefinedCriterion.Type getType() {
			return PotatoRefinedCriterion.Type.LUBRICATION;
		}

		public boolean test(ItemStack itemStack) {
			if (this.itemPredicate.test(itemStack)) {
				LubricationComponent lubricationComponent = itemStack.get(DataComponentTypes.LUBRICATION);
				if (lubricationComponent != null) {
					return lubricationComponent.getLevel() >= this.minLubricationLevel;
				}
			}

			return false;
		}
	}

	public interface ResultPredicate extends Predicate<ItemStack> {
		PotatoRefinedCriterion.Type getType();
	}

	static record Standard(ItemPredicate itemPredicate) implements PotatoRefinedCriterion.ResultPredicate {
		public static final Codec<PotatoRefinedCriterion.Standard> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(ItemPredicate.CODEC.fieldOf("item_predicate").forGetter(PotatoRefinedCriterion.Standard::itemPredicate))
					.apply(instance, PotatoRefinedCriterion.Standard::new)
		);

		@Override
		public PotatoRefinedCriterion.Type getType() {
			return PotatoRefinedCriterion.Type.STANDARD;
		}

		public boolean test(ItemStack itemStack) {
			return this.itemPredicate.test(itemStack);
		}
	}

	static enum Type implements StringIdentifiable {
		STANDARD("standard", () -> PotatoRefinedCriterion.Standard.CODEC),
		LUBRICATION("lubrication", () -> PotatoRefinedCriterion.Lubrication.CODEC);

		private final String name;
		private final Supplier<Codec<? extends PotatoRefinedCriterion.ResultPredicate>> codecSupplier;

		private Type(String name, Supplier<Codec<? extends PotatoRefinedCriterion.ResultPredicate>> codecSupplier) {
			this.name = name;
			this.codecSupplier = codecSupplier;
		}

		private Codec<? extends PotatoRefinedCriterion.ResultPredicate> getCodec() {
			return (Codec<? extends PotatoRefinedCriterion.ResultPredicate>)this.codecSupplier.get();
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}

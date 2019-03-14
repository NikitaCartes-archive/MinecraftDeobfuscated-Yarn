package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;

public class HusbandryTabAdvancementGenerator implements Consumer<Consumer<SimpleAdvancement>> {
	private static final EntityType<?>[] field_11296 = new EntityType[]{
		EntityType.HORSE,
		EntityType.SHEEP,
		EntityType.COW,
		EntityType.MOOSHROOM,
		EntityType.PIG,
		EntityType.CHICKEN,
		EntityType.WOLF,
		EntityType.OCELOT,
		EntityType.RABBIT,
		EntityType.LLAMA,
		EntityType.TURTLE,
		EntityType.CAT,
		EntityType.PANDA
	};
	private static final Item[] field_11295 = new Item[]{Items.field_8429, Items.field_8846, Items.field_8323, Items.field_8209};
	private static final Item[] field_11297 = new Item[]{Items.field_8666, Items.field_8478, Items.field_8108, Items.field_8714};
	private static final Item[] field_11298 = new Item[]{
		Items.field_8279,
		Items.field_8208,
		Items.field_8229,
		Items.field_8389,
		Items.field_8261,
		Items.field_8463,
		Items.field_8367,
		Items.field_8429,
		Items.field_8209,
		Items.field_8846,
		Items.field_8323,
		Items.field_8373,
		Items.field_8509,
		Items.field_8423,
		Items.field_8497,
		Items.field_8046,
		Items.field_8176,
		Items.field_8726,
		Items.field_8544,
		Items.field_8511,
		Items.field_8680,
		Items.field_8179,
		Items.field_8567,
		Items.field_8512,
		Items.field_8635,
		Items.field_8071,
		Items.field_8741,
		Items.field_8504,
		Items.field_8752,
		Items.field_8308,
		Items.field_8748,
		Items.field_8347,
		Items.field_8233,
		Items.field_8186,
		Items.field_8515,
		Items.field_8551,
		Items.field_8766,
		Items.field_16998
	};

	public void method_10338(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.Builder.create()
			.display(
				Blocks.field_10359,
				new TranslatableTextComponent("advancements.husbandry.root.title"),
				new TranslatableTextComponent("advancements.husbandry.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/husbandry.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("consumed_item", ConsumeItemCriterion.Conditions.any())
			.build(consumer, "husbandry/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8861,
				new TranslatableTextComponent("advancements.husbandry.plant_seed.title"),
				new TranslatableTextComponent("advancements.husbandry.plant_seed.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriteriaMerger.OR)
			.criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.field_10293))
			.criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.field_9984))
			.criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.field_10168))
			.criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.field_10341))
			.criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.field_9974))
			.build(consumer, "husbandry/plant_seed");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8861,
				new TranslatableTextComponent("advancements.husbandry.breed_an_animal.title"),
				new TranslatableTextComponent("advancements.husbandry.breed_an_animal.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriteriaMerger.OR)
			.criterion("bred", BredAnimalsCriterion.Conditions.any())
			.build(consumer, "husbandry/breed_an_animal");
		SimpleAdvancement simpleAdvancement4 = this.method_10341(SimpleAdvancement.Builder.create())
			.parent(simpleAdvancement2)
			.display(
				Items.field_8279,
				new TranslatableTextComponent("advancements.husbandry.balanced_diet.title"),
				new TranslatableTextComponent("advancements.husbandry.balanced_diet.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "husbandry/balanced_diet");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement2)
			.display(
				Items.field_8527,
				new TranslatableTextComponent("advancements.husbandry.break_diamond_hoe.title"),
				new TranslatableTextComponent("advancements.husbandry.break_diamond_hoe.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"broke_hoe",
				ItemDurabilityChangedCriterion.Conditions.method_8967(ItemPredicate.Builder.create().item(Items.field_8527).build(), NumberRange.Integer.exactly(0))
			)
			.build(consumer, "husbandry/break_diamond_hoe");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8719,
				new TranslatableTextComponent("advancements.husbandry.tame_an_animal.title"),
				new TranslatableTextComponent("advancements.husbandry.tame_an_animal.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("tamed_animal", TameAnimalCriterion.Conditions.any())
			.build(consumer, "husbandry/tame_an_animal");
		SimpleAdvancement simpleAdvancement7 = this.method_10342(SimpleAdvancement.Builder.create())
			.parent(simpleAdvancement3)
			.display(
				Items.field_8071,
				new TranslatableTextComponent("advancements.husbandry.breed_all_animals.title"),
				new TranslatableTextComponent("advancements.husbandry.breed_all_animals.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "husbandry/bred_all_animals");
		SimpleAdvancement simpleAdvancement8 = this.method_10339(SimpleAdvancement.Builder.create())
			.parent(simpleAdvancement)
			.criteriaMerger(CriteriaMerger.OR)
			.display(
				Items.field_8378,
				new TranslatableTextComponent("advancements.husbandry.fishy_business.title"),
				new TranslatableTextComponent("advancements.husbandry.fishy_business.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/fishy_business");
		SimpleAdvancement simpleAdvancement9 = this.method_10340(SimpleAdvancement.Builder.create())
			.parent(simpleAdvancement8)
			.criteriaMerger(CriteriaMerger.OR)
			.display(
				Items.field_8108,
				new TranslatableTextComponent("advancements.husbandry.tactical_fishing.title"),
				new TranslatableTextComponent("advancements.husbandry.tactical_fishing.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/tactical_fishing");
		SimpleAdvancement simpleAdvancement10 = this.method_16118(SimpleAdvancement.Builder.create())
			.parent(simpleAdvancement6)
			.display(
				Items.field_8429,
				new TranslatableTextComponent("advancements.husbandry.complete_catalogue.title"),
				new TranslatableTextComponent("advancements.husbandry.complete_catalogue.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.build(consumer, "husbandry/complete_catalogue");
	}

	private SimpleAdvancement.Builder method_10341(SimpleAdvancement.Builder builder) {
		for (Item item : field_11298) {
			builder.criterion(Registry.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
		}

		return builder;
	}

	private SimpleAdvancement.Builder method_10342(SimpleAdvancement.Builder builder) {
		for (EntityType<?> entityType : field_11296) {
			builder.criterion(EntityType.getId(entityType).toString(), BredAnimalsCriterion.Conditions.method_861(EntityPredicate.Builder.create().type(entityType)));
		}

		return builder;
	}

	private SimpleAdvancement.Builder method_10340(SimpleAdvancement.Builder builder) {
		for (Item item : field_11297) {
			builder.criterion(Registry.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().item(item).build()));
		}

		return builder;
	}

	private SimpleAdvancement.Builder method_10339(SimpleAdvancement.Builder builder) {
		for (Item item : field_11295) {
			builder.criterion(
				Registry.ITEM.getId(item).getPath(),
				FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(item).build())
			);
		}

		return builder;
	}

	private SimpleAdvancement.Builder method_16118(SimpleAdvancement.Builder builder) {
		CatEntity.field_16283
			.forEach(
				(integer, identifier) -> builder.criterion(
						identifier.getPath(), TameAnimalCriterion.Conditions.method_16114(EntityPredicate.Builder.create().type(identifier).build())
					)
			);
		return builder;
	}
}

package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemUsedOnBlockCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HusbandryTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{
		EntityType.field_6139,
		EntityType.field_6067,
		EntityType.field_6057,
		EntityType.field_6115,
		EntityType.field_6085,
		EntityType.field_6143,
		EntityType.field_6093,
		EntityType.field_6132,
		EntityType.field_6055,
		EntityType.field_6081,
		EntityType.field_6140,
		EntityType.field_6074,
		EntityType.field_16281,
		EntityType.field_6146,
		EntityType.field_17943,
		EntityType.field_20346,
		EntityType.field_21973,
		EntityType.field_23214
	};
	private static final Item[] FISH_ITEMS = new Item[]{Items.field_8429, Items.field_8846, Items.field_8323, Items.field_8209};
	private static final Item[] FISH_BUCKET_ITEMS = new Item[]{Items.field_8666, Items.field_8478, Items.field_8108, Items.field_8714};
	private static final Item[] FOOD_ITEMS = new Item[]{
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
		Items.field_16998,
		Items.field_20417
	};

	public void method_10338(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Blocks.field_10359,
				new TranslatableText("advancements.husbandry.root.title"),
				new TranslatableText("advancements.husbandry.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/husbandry.png"),
				AdvancementFrame.field_1254,
				false,
				false,
				false
			)
			.criterion("consumed_item", ConsumeItemCriterion.Conditions.any())
			.build(consumer, "husbandry/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8861,
				new TranslatableText("advancements.husbandry.plant_seed.title"),
				new TranslatableText("advancements.husbandry.plant_seed.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.field_10293))
			.criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.field_9984))
			.criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.field_10168))
			.criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.field_10341))
			.criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.field_9974))
			.build(consumer, "husbandry/plant_seed");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8861,
				new TranslatableText("advancements.husbandry.breed_an_animal.title"),
				new TranslatableText("advancements.husbandry.breed_an_animal.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("bred", BredAnimalsCriterion.Conditions.any())
			.build(consumer, "husbandry/breed_an_animal");
		this.requireFoodItemsEaten(Advancement.Task.create())
			.parent(advancement2)
			.display(
				Items.field_8279,
				new TranslatableText("advancements.husbandry.balanced_diet.title"),
				new TranslatableText("advancements.husbandry.balanced_diet.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "husbandry/balanced_diet");
		Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_22026,
				new TranslatableText("advancements.husbandry.netherite_hoe.title"),
				new TranslatableText("advancements.husbandry.netherite_hoe.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("netherite_hoe", InventoryChangedCriterion.Conditions.items(Items.field_22026))
			.build(consumer, "husbandry/obtain_netherite_hoe");
		Advancement advancement4 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8719,
				new TranslatableText("advancements.husbandry.tame_an_animal.title"),
				new TranslatableText("advancements.husbandry.tame_an_animal.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("tamed_animal", TameAnimalCriterion.Conditions.any())
			.build(consumer, "husbandry/tame_an_animal");
		this.requireListedAnimalsBred(Advancement.Task.create())
			.parent(advancement3)
			.display(
				Items.field_8071,
				new TranslatableText("advancements.husbandry.breed_all_animals.title"),
				new TranslatableText("advancements.husbandry.breed_all_animals.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "husbandry/bred_all_animals");
		Advancement advancement5 = this.requireListedFishCaught(Advancement.Task.create())
			.parent(advancement)
			.criteriaMerger(CriterionMerger.OR)
			.display(
				Items.field_8378,
				new TranslatableText("advancements.husbandry.fishy_business.title"),
				new TranslatableText("advancements.husbandry.fishy_business.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/fishy_business");
		this.requireListedFishBucketsFilled(Advancement.Task.create())
			.parent(advancement5)
			.criteriaMerger(CriterionMerger.OR)
			.display(
				Items.field_8108,
				new TranslatableText("advancements.husbandry.tactical_fishing.title"),
				new TranslatableText("advancements.husbandry.tactical_fishing.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/tactical_fishing");
		this.requireAllCatsTamed(Advancement.Task.create())
			.parent(advancement4)
			.display(
				Items.field_8429,
				new TranslatableText("advancements.husbandry.complete_catalogue.title"),
				new TranslatableText("advancements.husbandry.complete_catalogue.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.build(consumer, "husbandry/complete_catalogue");
		Advancement.Task.create()
			.parent(advancement)
			.criterion(
				"safely_harvest_honey",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().method_29233(BlockTags.field_20340).build()).smokey(true),
					ItemPredicate.Builder.create().item(Items.field_8469)
				)
			)
			.display(
				Items.field_20417,
				new TranslatableText("advancements.husbandry.safely_harvest_honey.title"),
				new TranslatableText("advancements.husbandry.safely_harvest_honey.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/safely_harvest_honey");
		Advancement.Task.create()
			.parent(advancement)
			.criterion(
				"silk_touch_nest",
				BeeNestDestroyedCriterion.Conditions.create(
					Blocks.field_20421,
					ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.field_9099, NumberRange.IntRange.atLeast(1))),
					NumberRange.IntRange.exactly(3)
				)
			)
			.display(
				Blocks.field_20421,
				new TranslatableText("advancements.husbandry.silk_touch_nest.title"),
				new TranslatableText("advancements.husbandry.silk_touch_nest.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/silk_touch_nest");
	}

	private Advancement.Task requireFoodItemsEaten(Advancement.Task task) {
		for (Item item : FOOD_ITEMS) {
			task.criterion(Registry.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
		}

		return task;
	}

	private Advancement.Task requireListedAnimalsBred(Advancement.Task task) {
		for (EntityType<?> entityType : BREEDABLE_ANIMALS) {
			task.criterion(EntityType.getId(entityType).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(entityType)));
		}

		task.criterion(
			EntityType.getId(EntityType.field_6113).toString(),
			BredAnimalsCriterion.Conditions.method_29918(
				EntityPredicate.Builder.create().type(EntityType.field_6113).build(),
				EntityPredicate.Builder.create().type(EntityType.field_6113).build(),
				EntityPredicate.ANY
			)
		);
		return task;
	}

	private Advancement.Task requireListedFishBucketsFilled(Advancement.Task task) {
		for (Item item : FISH_BUCKET_ITEMS) {
			task.criterion(Registry.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().item(item).build()));
		}

		return task;
	}

	private Advancement.Task requireListedFishCaught(Advancement.Task task) {
		for (Item item : FISH_ITEMS) {
			task.criterion(
				Registry.ITEM.getId(item).getPath(),
				FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(item).build())
			);
		}

		return task;
	}

	private Advancement.Task requireAllCatsTamed(Advancement.Task task) {
		CatEntity.TEXTURES
			.forEach(
				(integer, identifier) -> task.criterion(
						identifier.getPath(), TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().type(identifier).build())
					)
			);
		return task;
	}
}

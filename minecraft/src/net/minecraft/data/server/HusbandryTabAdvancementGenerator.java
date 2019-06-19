package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;

public class HusbandryTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	private static final EntityType<?>[] field_11296 = new EntityType[]{
		EntityType.field_6139,
		EntityType.field_6115,
		EntityType.field_6085,
		EntityType.field_6143,
		EntityType.field_6093,
		EntityType.field_6132,
		EntityType.field_6055,
		EntityType.field_6081,
		EntityType.field_6140,
		EntityType.field_6074,
		EntityType.field_6113,
		EntityType.field_16281,
		EntityType.field_6146,
		EntityType.field_17943
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
			.criteriaMerger(CriteriaMerger.OR)
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
			.criteriaMerger(CriteriaMerger.OR)
			.criterion("bred", BredAnimalsCriterion.Conditions.any())
			.build(consumer, "husbandry/breed_an_animal");
		Advancement advancement4 = this.method_10341(Advancement.Task.create())
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
		Advancement advancement5 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_8527,
				new TranslatableText("advancements.husbandry.break_diamond_hoe.title"),
				new TranslatableText("advancements.husbandry.break_diamond_hoe.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"broke_hoe",
				ItemDurabilityChangedCriterion.Conditions.create(ItemPredicate.Builder.create().item(Items.field_8527).build(), NumberRange.IntRange.exactly(0))
			)
			.build(consumer, "husbandry/break_diamond_hoe");
		Advancement advancement6 = Advancement.Task.create()
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
		Advancement advancement7 = this.method_10342(Advancement.Task.create())
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
		Advancement advancement8 = this.method_10339(Advancement.Task.create())
			.parent(advancement)
			.criteriaMerger(CriteriaMerger.OR)
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
		Advancement advancement9 = this.method_10340(Advancement.Task.create())
			.parent(advancement8)
			.criteriaMerger(CriteriaMerger.OR)
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
		Advancement advancement10 = this.method_16118(Advancement.Task.create())
			.parent(advancement6)
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
	}

	private Advancement.Task method_10341(Advancement.Task task) {
		for (Item item : field_11298) {
			task.criterion(Registry.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
		}

		return task;
	}

	private Advancement.Task method_10342(Advancement.Task task) {
		for (EntityType<?> entityType : field_11296) {
			task.criterion(EntityType.getId(entityType).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(entityType)));
		}

		return task;
	}

	private Advancement.Task method_10340(Advancement.Task task) {
		for (Item item : field_11297) {
			task.criterion(Registry.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().item(item).build()));
		}

		return task;
	}

	private Advancement.Task method_10339(Advancement.Task task) {
		for (Item item : field_11295) {
			task.criterion(
				Registry.ITEM.getId(item).getPath(),
				FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(item).build())
			);
		}

		return task;
	}

	private Advancement.Task method_16118(Advancement.Task task) {
		CatEntity.TEXTURES
			.forEach(
				(integer, identifier) -> task.criterion(
						identifier.getPath(), TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().type(identifier).build())
					)
			);
		return task;
	}
}

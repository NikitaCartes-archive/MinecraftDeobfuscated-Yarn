package net.minecraft.data.server;

import com.google.common.collect.BiMap;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemUsedOnBlockCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.HoneycombItem;
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
		EntityType.HORSE,
		EntityType.DONKEY,
		EntityType.MULE,
		EntityType.SHEEP,
		EntityType.COW,
		EntityType.MOOSHROOM,
		EntityType.PIG,
		EntityType.CHICKEN,
		EntityType.WOLF,
		EntityType.OCELOT,
		EntityType.RABBIT,
		EntityType.LLAMA,
		EntityType.CAT,
		EntityType.PANDA,
		EntityType.FOX,
		EntityType.BEE,
		EntityType.HOGLIN,
		EntityType.STRIDER,
		EntityType.GOAT,
		EntityType.AXOLOTL
	};
	private static final Item[] FISH_ITEMS = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
	private static final Item[] FISH_BUCKET_ITEMS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
	private static final Item[] FOOD_ITEMS = new Item[]{
		Items.APPLE,
		Items.MUSHROOM_STEW,
		Items.BREAD,
		Items.PORKCHOP,
		Items.COOKED_PORKCHOP,
		Items.GOLDEN_APPLE,
		Items.ENCHANTED_GOLDEN_APPLE,
		Items.COD,
		Items.SALMON,
		Items.TROPICAL_FISH,
		Items.PUFFERFISH,
		Items.COOKED_COD,
		Items.COOKED_SALMON,
		Items.COOKIE,
		Items.MELON_SLICE,
		Items.BEEF,
		Items.COOKED_BEEF,
		Items.CHICKEN,
		Items.COOKED_CHICKEN,
		Items.ROTTEN_FLESH,
		Items.SPIDER_EYE,
		Items.CARROT,
		Items.POTATO,
		Items.BAKED_POTATO,
		Items.POISONOUS_POTATO,
		Items.GOLDEN_CARROT,
		Items.PUMPKIN_PIE,
		Items.RABBIT,
		Items.COOKED_RABBIT,
		Items.RABBIT_STEW,
		Items.MUTTON,
		Items.COOKED_MUTTON,
		Items.CHORUS_FRUIT,
		Items.BEETROOT,
		Items.BEETROOT_SOUP,
		Items.DRIED_KELP,
		Items.SUSPICIOUS_STEW,
		Items.SWEET_BERRIES,
		Items.HONEY_BOTTLE,
		Items.GLOW_BERRIES
	};
	private static final Item[] field_33964 = new Item[]{
		Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE
	};

	public void accept(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Blocks.HAY_BLOCK,
				new TranslatableText("advancements.husbandry.root.title"),
				new TranslatableText("advancements.husbandry.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/husbandry.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("consumed_item", ConsumeItemCriterion.Conditions.any())
			.build(consumer, "husbandry/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.WHEAT,
				new TranslatableText("advancements.husbandry.plant_seed.title"),
				new TranslatableText("advancements.husbandry.plant_seed.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.WHEAT))
			.criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.PUMPKIN_STEM))
			.criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.MELON_STEM))
			.criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.BEETROOTS))
			.criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.NETHER_WART))
			.build(consumer, "husbandry/plant_seed");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.WHEAT,
				new TranslatableText("advancements.husbandry.breed_an_animal.title"),
				new TranslatableText("advancements.husbandry.breed_an_animal.description"),
				null,
				AdvancementFrame.TASK,
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
				Items.APPLE,
				new TranslatableText("advancements.husbandry.balanced_diet.title"),
				new TranslatableText("advancements.husbandry.balanced_diet.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "husbandry/balanced_diet");
		Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.NETHERITE_HOE,
				new TranslatableText("advancements.husbandry.netherite_hoe.title"),
				new TranslatableText("advancements.husbandry.netherite_hoe.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("netherite_hoe", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HOE))
			.build(consumer, "husbandry/obtain_netherite_hoe");
		Advancement advancement4 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.LEAD,
				new TranslatableText("advancements.husbandry.tame_an_animal.title"),
				new TranslatableText("advancements.husbandry.tame_an_animal.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("tamed_animal", TameAnimalCriterion.Conditions.any())
			.build(consumer, "husbandry/tame_an_animal");
		this.requireListedAnimalsBred(Advancement.Task.create())
			.parent(advancement3)
			.display(
				Items.GOLDEN_CARROT,
				new TranslatableText("advancements.husbandry.breed_all_animals.title"),
				new TranslatableText("advancements.husbandry.breed_all_animals.description"),
				null,
				AdvancementFrame.CHALLENGE,
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
				Items.FISHING_ROD,
				new TranslatableText("advancements.husbandry.fishy_business.title"),
				new TranslatableText("advancements.husbandry.fishy_business.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/fishy_business");
		Advancement advancement6 = this.requireListedFishBucketsFilled(Advancement.Task.create())
			.parent(advancement5)
			.criteriaMerger(CriterionMerger.OR)
			.display(
				Items.PUFFERFISH_BUCKET,
				new TranslatableText("advancements.husbandry.tactical_fishing.title"),
				new TranslatableText("advancements.husbandry.tactical_fishing.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/tactical_fishing");
		Advancement advancement7 = Advancement.Task.create()
			.parent(advancement6)
			.criteriaMerger(CriterionMerger.OR)
			.criterion(
				Registry.ITEM.getId(Items.AXOLOTL_BUCKET).getPath(),
				FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(Items.AXOLOTL_BUCKET).build())
			)
			.display(
				Items.AXOLOTL_BUCKET,
				new TranslatableText("advancements.husbandry.axolotl_in_a_bucket.title"),
				new TranslatableText("advancements.husbandry.axolotl_in_a_bucket.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/axolotl_in_a_bucket");
		Advancement.Task.create()
			.parent(advancement7)
			.criterion("kill_axolotl_target", EffectsChangedCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.AXOLOTL).build()))
			.display(
				Items.TROPICAL_FISH_BUCKET,
				new TranslatableText("advancements.husbandry.kill_axolotl_target.title"),
				new TranslatableText("advancements.husbandry.kill_axolotl_target.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/kill_axolotl_target");
		this.requireAllCatsTamed(Advancement.Task.create())
			.parent(advancement4)
			.display(
				Items.COD,
				new TranslatableText("advancements.husbandry.complete_catalogue.title"),
				new TranslatableText("advancements.husbandry.complete_catalogue.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.build(consumer, "husbandry/complete_catalogue");
		Advancement advancement8 = Advancement.Task.create()
			.parent(advancement)
			.criterion(
				"safely_harvest_honey",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().tag(BlockTags.BEEHIVES).build()).smokey(true),
					ItemPredicate.Builder.create().items(Items.GLASS_BOTTLE)
				)
			)
			.display(
				Items.HONEY_BOTTLE,
				new TranslatableText("advancements.husbandry.safely_harvest_honey.title"),
				new TranslatableText("advancements.husbandry.safely_harvest_honey.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/safely_harvest_honey");
		Advancement advancement9 = Advancement.Task.create()
			.parent(advancement8)
			.display(
				Items.HONEYCOMB,
				new TranslatableText("advancements.husbandry.wax_on.title"),
				new TranslatableText("advancements.husbandry.wax_on.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"wax_on",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(((BiMap)HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get()).keySet()).build()),
					ItemPredicate.Builder.create().items(Items.HONEYCOMB)
				)
			)
			.build(consumer, "husbandry/wax_on");
		Advancement.Task.create()
			.parent(advancement9)
			.display(
				Items.STONE_AXE,
				new TranslatableText("advancements.husbandry.wax_off.title"),
				new TranslatableText("advancements.husbandry.wax_off.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"wax_off",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).keySet()).build()),
					ItemPredicate.Builder.create().items(field_33964)
				)
			)
			.build(consumer, "husbandry/wax_off");
		Advancement.Task.create()
			.parent(advancement)
			.criterion(
				"silk_touch_nest",
				BeeNestDestroyedCriterion.Conditions.create(
					Blocks.BEE_NEST,
					ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))),
					NumberRange.IntRange.exactly(3)
				)
			)
			.display(
				Blocks.BEE_NEST,
				new TranslatableText("advancements.husbandry.silk_touch_nest.title"),
				new TranslatableText("advancements.husbandry.silk_touch_nest.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(consumer, "husbandry/silk_touch_nest");
		Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.OAK_BOAT,
				new TranslatableText("advancements.husbandry.ride_a_boat_with_a_goat.title"),
				new TranslatableText("advancements.husbandry.ride_a_boat_with_a_goat.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"ride_a_boat_with_a_goat",
				StartedRidingCriterion.Conditions.create(
					EntityPredicate.Builder.create()
						.vehicle(EntityPredicate.Builder.create().type(EntityType.BOAT).passenger(EntityPredicate.Builder.create().type(EntityType.GOAT).build()).build())
				)
			)
			.build(consumer, "husbandry/ride_a_boat_with_a_goat");
		Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.GLOW_INK_SAC,
				new TranslatableText("advancements.husbandry.make_a_sign_glow.title"),
				new TranslatableText("advancements.husbandry.make_a_sign_glow.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"make_a_sign_glow",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().tag(BlockTags.SIGNS).build()),
					ItemPredicate.Builder.create().items(Items.GLOW_INK_SAC)
				)
			)
			.build(consumer, "husbandry/make_a_sign_glow");
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
			EntityType.getId(EntityType.TURTLE).toString(),
			BredAnimalsCriterion.Conditions.create(
				EntityPredicate.Builder.create().type(EntityType.TURTLE).build(), EntityPredicate.Builder.create().type(EntityType.TURTLE).build(), EntityPredicate.ANY
			)
		);
		return task;
	}

	private Advancement.Task requireListedFishBucketsFilled(Advancement.Task task) {
		for (Item item : FISH_BUCKET_ITEMS) {
			task.criterion(Registry.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(item).build()));
		}

		return task;
	}

	private Advancement.Task requireListedFishCaught(Advancement.Task task) {
		for (Item item : FISH_ITEMS) {
			task.criterion(
				Registry.ITEM.getId(item).getPath(),
				FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().items(item).build())
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

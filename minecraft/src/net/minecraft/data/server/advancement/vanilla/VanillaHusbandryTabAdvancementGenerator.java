package net.minecraft.data.server.advancement.vanilla;

import com.google.common.collect.BiMap;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VanillaHusbandryTabAdvancementGenerator implements AdvancementTabGenerator {
	public static final List<EntityType<?>> BREEDABLE_ANIMALS = List.of(
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
		EntityType.AXOLOTL,
		EntityType.CAMEL,
		EntityType.ARMADILLO
	);
	public static final List<EntityType<?>> EGG_LAYING_ANIMALS = List.of(EntityType.TURTLE, EntityType.FROG, EntityType.SNIFFER);
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
	public static final Item[] AXE_ITEMS = new Item[]{Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE};

	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
		RegistryWrapper.Impl<Enchantment> impl = lookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		AdvancementEntry advancementEntry = Advancement.Builder.create()
			.display(
				Blocks.HAY_BLOCK,
				Text.translatable("advancements.husbandry.root.title"),
				Text.translatable("advancements.husbandry.root.description"),
				Identifier.ofVanilla("textures/gui/advancements/backgrounds/husbandry.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("consumed_item", ConsumeItemCriterion.Conditions.any())
			.build(exporter, "husbandry/root");
		AdvancementEntry advancementEntry2 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.WHEAT,
				Text.translatable("advancements.husbandry.plant_seed.title"),
				Text.translatable("advancements.husbandry.plant_seed.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion("wheat", ItemCriterion.Conditions.createPlacedBlock(Blocks.WHEAT))
			.criterion("pumpkin_stem", ItemCriterion.Conditions.createPlacedBlock(Blocks.PUMPKIN_STEM))
			.criterion("melon_stem", ItemCriterion.Conditions.createPlacedBlock(Blocks.MELON_STEM))
			.criterion("beetroots", ItemCriterion.Conditions.createPlacedBlock(Blocks.BEETROOTS))
			.criterion("nether_wart", ItemCriterion.Conditions.createPlacedBlock(Blocks.NETHER_WART))
			.criterion("torchflower", ItemCriterion.Conditions.createPlacedBlock(Blocks.TORCHFLOWER_CROP))
			.criterion("pitcher_pod", ItemCriterion.Conditions.createPlacedBlock(Blocks.PITCHER_CROP))
			.build(exporter, "husbandry/plant_seed");
		AdvancementEntry advancementEntry3 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.WHEAT,
				Text.translatable("advancements.husbandry.breed_an_animal.title"),
				Text.translatable("advancements.husbandry.breed_an_animal.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion("bred", BredAnimalsCriterion.Conditions.any())
			.build(exporter, "husbandry/breed_an_animal");
		createBreedAllAnimalsAdvancement(advancementEntry3, exporter, BREEDABLE_ANIMALS.stream(), EGG_LAYING_ANIMALS.stream());
		requireFoodItemsEaten(Advancement.Builder.create())
			.parent(advancementEntry2)
			.display(
				Items.APPLE,
				Text.translatable("advancements.husbandry.balanced_diet.title"),
				Text.translatable("advancements.husbandry.balanced_diet.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(exporter, "husbandry/balanced_diet");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.NETHERITE_HOE,
				Text.translatable("advancements.husbandry.netherite_hoe.title"),
				Text.translatable("advancements.husbandry.netherite_hoe.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("netherite_hoe", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HOE))
			.build(exporter, "husbandry/obtain_netherite_hoe");
		AdvancementEntry advancementEntry4 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.LEAD,
				Text.translatable("advancements.husbandry.tame_an_animal.title"),
				Text.translatable("advancements.husbandry.tame_an_animal.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("tamed_animal", TameAnimalCriterion.Conditions.any())
			.build(exporter, "husbandry/tame_an_animal");
		AdvancementEntry advancementEntry5 = requireListedFishCaught(Advancement.Builder.create())
			.parent(advancementEntry)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.display(
				Items.FISHING_ROD,
				Text.translatable("advancements.husbandry.fishy_business.title"),
				Text.translatable("advancements.husbandry.fishy_business.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/fishy_business");
		AdvancementEntry advancementEntry6 = requireListedFishBucketsFilled(Advancement.Builder.create())
			.parent(advancementEntry5)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.display(
				Items.PUFFERFISH_BUCKET,
				Text.translatable("advancements.husbandry.tactical_fishing.title"),
				Text.translatable("advancements.husbandry.tactical_fishing.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/tactical_fishing");
		AdvancementEntry advancementEntry7 = Advancement.Builder.create()
			.parent(advancementEntry6)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion(
				Registries.ITEM.getId(Items.AXOLOTL_BUCKET).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(Items.AXOLOTL_BUCKET))
			)
			.display(
				Items.AXOLOTL_BUCKET,
				Text.translatable("advancements.husbandry.axolotl_in_a_bucket.title"),
				Text.translatable("advancements.husbandry.axolotl_in_a_bucket.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/axolotl_in_a_bucket");
		Advancement.Builder.create()
			.parent(advancementEntry7)
			.criterion("kill_axolotl_target", EffectsChangedCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.AXOLOTL)))
			.display(
				Items.TROPICAL_FISH_BUCKET,
				Text.translatable("advancements.husbandry.kill_axolotl_target.title"),
				Text.translatable("advancements.husbandry.kill_axolotl_target.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/kill_axolotl_target");
		requireAllCatsTamed(Advancement.Builder.create())
			.parent(advancementEntry4)
			.display(
				Items.COD,
				Text.translatable("advancements.husbandry.complete_catalogue.title"),
				Text.translatable("advancements.husbandry.complete_catalogue.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.build(exporter, "husbandry/complete_catalogue");
		requireAllWolvesTamed(Advancement.Builder.create(), lookup)
			.parent(advancementEntry4)
			.display(
				Items.BONE,
				Text.translatable("advancements.husbandry.whole_pack.title"),
				Text.translatable("advancements.husbandry.whole_pack.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.build(exporter, "husbandry/whole_pack");
		AdvancementEntry advancementEntry8 = Advancement.Builder.create()
			.parent(advancementEntry)
			.criterion(
				"safely_harvest_honey",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().tag(BlockTags.BEEHIVES)).smokey(true),
					ItemPredicate.Builder.create().items(Items.GLASS_BOTTLE)
				)
			)
			.display(
				Items.HONEY_BOTTLE,
				Text.translatable("advancements.husbandry.safely_harvest_honey.title"),
				Text.translatable("advancements.husbandry.safely_harvest_honey.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/safely_harvest_honey");
		AdvancementEntry advancementEntry9 = Advancement.Builder.create()
			.parent(advancementEntry8)
			.display(
				Items.HONEYCOMB,
				Text.translatable("advancements.husbandry.wax_on.title"),
				Text.translatable("advancements.husbandry.wax_on.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"wax_on",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(((BiMap)HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get()).keySet())),
					ItemPredicate.Builder.create().items(Items.HONEYCOMB)
				)
			)
			.build(exporter, "husbandry/wax_on");
		Advancement.Builder.create()
			.parent(advancementEntry9)
			.display(
				Items.STONE_AXE,
				Text.translatable("advancements.husbandry.wax_off.title"),
				Text.translatable("advancements.husbandry.wax_off.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"wax_off",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).keySet())),
					ItemPredicate.Builder.create().items(AXE_ITEMS)
				)
			)
			.build(exporter, "husbandry/wax_off");
		AdvancementEntry advancementEntry10 = Advancement.Builder.create()
			.parent(advancementEntry)
			.criterion(
				Registries.ITEM.getId(Items.TADPOLE_BUCKET).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(Items.TADPOLE_BUCKET))
			)
			.display(
				Items.TADPOLE_BUCKET,
				Text.translatable("advancements.husbandry.tadpole_in_a_bucket.title"),
				Text.translatable("advancements.husbandry.tadpole_in_a_bucket.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/tadpole_in_a_bucket");
		AdvancementEntry advancementEntry11 = requireAllFrogsOnLeads(Advancement.Builder.create())
			.parent(advancementEntry10)
			.display(
				Items.LEAD,
				Text.translatable("advancements.husbandry.leash_all_frog_variants.title"),
				Text.translatable("advancements.husbandry.leash_all_frog_variants.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/leash_all_frog_variants");
		Advancement.Builder.create()
			.parent(advancementEntry11)
			.display(
				Items.VERDANT_FROGLIGHT,
				Text.translatable("advancements.husbandry.froglights.title"),
				Text.translatable("advancements.husbandry.froglights.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.criterion("froglights", InventoryChangedCriterion.Conditions.items(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT))
			.build(exporter, "husbandry/froglights");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.criterion(
				"silk_touch_nest",
				BeeNestDestroyedCriterion.Conditions.create(
					Blocks.BEE_NEST,
					ItemPredicate.Builder.create()
						.subPredicate(
							ItemSubPredicateTypes.ENCHANTMENTS,
							EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(Enchantments.SILK_TOUCH), NumberRange.IntRange.atLeast(1))))
						),
					NumberRange.IntRange.exactly(3)
				)
			)
			.display(
				Blocks.BEE_NEST,
				Text.translatable("advancements.husbandry.silk_touch_nest.title"),
				Text.translatable("advancements.husbandry.silk_touch_nest.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "husbandry/silk_touch_nest");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.OAK_BOAT,
				Text.translatable("advancements.husbandry.ride_a_boat_with_a_goat.title"),
				Text.translatable("advancements.husbandry.ride_a_boat_with_a_goat.description"),
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
						.vehicle(EntityPredicate.Builder.create().type(EntityType.BOAT).passenger(EntityPredicate.Builder.create().type(EntityType.GOAT)))
				)
			)
			.build(exporter, "husbandry/ride_a_boat_with_a_goat");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.GLOW_INK_SAC,
				Text.translatable("advancements.husbandry.make_a_sign_glow.title"),
				Text.translatable("advancements.husbandry.make_a_sign_glow.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"make_a_sign_glow",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().tag(BlockTags.ALL_SIGNS)),
					ItemPredicate.Builder.create().items(Items.GLOW_INK_SAC)
				)
			)
			.build(exporter, "husbandry/make_a_sign_glow");
		AdvancementEntry advancementEntry12 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.COOKIE,
				Text.translatable("advancements.husbandry.allay_deliver_item_to_player.title"),
				Text.translatable("advancements.husbandry.allay_deliver_item_to_player.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				true
			)
			.criterion(
				"allay_deliver_item_to_player",
				ThrownItemPickedUpByEntityCriterion.Conditions.createThrownItemPickedUpByPlayer(
					Optional.empty(),
					Optional.empty(),
					Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().type(EntityType.ALLAY)))
				)
			)
			.build(exporter, "husbandry/allay_deliver_item_to_player");
		Advancement.Builder.create()
			.parent(advancementEntry12)
			.display(
				Items.NOTE_BLOCK,
				Text.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.title"),
				Text.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				true
			)
			.criterion(
				"allay_deliver_cake_to_note_block",
				ItemCriterion.Conditions.createAllayDropItemOnBlock(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(Blocks.NOTE_BLOCK)), ItemPredicate.Builder.create().items(Items.CAKE)
				)
			)
			.build(exporter, "husbandry/allay_deliver_cake_to_note_block");
		AdvancementEntry advancementEntry13 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.SNIFFER_EGG,
				Text.translatable("advancements.husbandry.obtain_sniffer_egg.title"),
				Text.translatable("advancements.husbandry.obtain_sniffer_egg.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				true
			)
			.criterion("obtain_sniffer_egg", InventoryChangedCriterion.Conditions.items(Items.SNIFFER_EGG))
			.build(exporter, "husbandry/obtain_sniffer_egg");
		AdvancementEntry advancementEntry14 = Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Items.TORCHFLOWER_SEEDS,
				Text.translatable("advancements.husbandry.feed_snifflet.title"),
				Text.translatable("advancements.husbandry.feed_snifflet.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				true
			)
			.criterion(
				"feed_snifflet",
				PlayerInteractedWithEntityCriterion.Conditions.create(
					ItemPredicate.Builder.create().tag(ItemTags.SNIFFER_FOOD),
					Optional.of(
						EntityPredicate.contextPredicateFromEntityPredicate(
							EntityPredicate.Builder.create().type(EntityType.SNIFFER).flags(EntityFlagsPredicate.Builder.create().isBaby(true))
						)
					)
				)
			)
			.build(exporter, "husbandry/feed_snifflet");
		Advancement.Builder.create()
			.parent(advancementEntry14)
			.display(
				Items.PITCHER_POD,
				Text.translatable("advancements.husbandry.plant_any_sniffer_seed.title"),
				Text.translatable("advancements.husbandry.plant_any_sniffer_seed.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				true
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion("torchflower", ItemCriterion.Conditions.createPlacedBlock(Blocks.TORCHFLOWER_CROP))
			.criterion("pitcher_pod", ItemCriterion.Conditions.createPlacedBlock(Blocks.PITCHER_CROP))
			.build(exporter, "husbandry/plant_any_sniffer_seed");
		Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Items.SHEARS,
				Text.translatable("advancements.husbandry.remove_wolf_armor.title"),
				Text.translatable("advancements.husbandry.remove_wolf_armor.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"remove_wolf_armor",
				PlayerInteractedWithEntityCriterion.Conditions.create(
					ItemPredicate.Builder.create().items(Items.SHEARS),
					Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().type(EntityType.WOLF)))
				)
			)
			.build(exporter, "husbandry/remove_wolf_armor");
		Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Items.WOLF_ARMOR,
				Text.translatable("advancements.husbandry.repair_wolf_armor.title"),
				Text.translatable("advancements.husbandry.repair_wolf_armor.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"repair_wolf_armor",
				PlayerInteractedWithEntityCriterion.Conditions.create(
					ItemPredicate.Builder.create().items(Items.ARMADILLO_SCUTE),
					Optional.of(
						EntityPredicate.contextPredicateFromEntityPredicate(
							EntityPredicate.Builder.create()
								.type(EntityType.WOLF)
								.equipment(
									EntityEquipmentPredicate.Builder.create()
										.body(ItemPredicate.Builder.create().items(Items.WOLF_ARMOR).component(ComponentPredicate.builder().add(DataComponentTypes.DAMAGE, 0).build()))
								)
						)
					)
				)
			)
			.build(exporter, "husbandry/repair_wolf_armor");
	}

	public static AdvancementEntry createBreedAllAnimalsAdvancement(
		AdvancementEntry parent, Consumer<AdvancementEntry> exporter, Stream<EntityType<?>> breedableAnimals, Stream<EntityType<?>> eggLayingAnimals
	) {
		return requireListedAnimalsBred(Advancement.Builder.create(), breedableAnimals, eggLayingAnimals)
			.parent(parent)
			.display(
				Items.GOLDEN_CARROT,
				Text.translatable("advancements.husbandry.breed_all_animals.title"),
				Text.translatable("advancements.husbandry.breed_all_animals.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(exporter, "husbandry/bred_all_animals");
	}

	private static Advancement.Builder requireAllFrogsOnLeads(Advancement.Builder builder) {
		Registries.FROG_VARIANT
			.streamEntries()
			.forEach(
				variant -> builder.criterion(
						variant.registryKey().getValue().toString(),
						PlayerInteractedWithEntityCriterion.Conditions.create(
							ItemPredicate.Builder.create().items(Items.LEAD),
							Optional.of(
								EntityPredicate.contextPredicateFromEntityPredicate(
									EntityPredicate.Builder.create().type(EntityType.FROG).typeSpecific(EntitySubPredicateTypes.frogVariant(variant))
								)
							)
						)
					)
			);
		return builder;
	}

	private static Advancement.Builder requireFoodItemsEaten(Advancement.Builder builder) {
		for (Item item : FOOD_ITEMS) {
			builder.criterion(Registries.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
		}

		return builder;
	}

	private static Advancement.Builder requireListedAnimalsBred(
		Advancement.Builder advancementBuilder, Stream<EntityType<?>> breedableAnimals, Stream<EntityType<?>> eggLayingAnimals
	) {
		breedableAnimals.forEach(
			type -> advancementBuilder.criterion(EntityType.getId(type).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(type)))
		);
		eggLayingAnimals.forEach(
			type -> advancementBuilder.criterion(
					EntityType.getId(type).toString(),
					BredAnimalsCriterion.Conditions.create(
						Optional.of(EntityPredicate.Builder.create().type(type).build()), Optional.of(EntityPredicate.Builder.create().type(type).build()), Optional.empty()
					)
				)
		);
		return advancementBuilder;
	}

	private static Advancement.Builder requireListedFishBucketsFilled(Advancement.Builder builder) {
		for (Item item : FISH_BUCKET_ITEMS) {
			builder.criterion(Registries.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(item)));
		}

		return builder;
	}

	private static Advancement.Builder requireListedFishCaught(Advancement.Builder builder) {
		for (Item item : FISH_ITEMS) {
			builder.criterion(
				Registries.ITEM.getId(item).getPath(),
				FishingRodHookedCriterion.Conditions.create(Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.create().items(item).build()))
			);
		}

		return builder;
	}

	private static Advancement.Builder requireAllCatsTamed(Advancement.Builder builder) {
		Registries.CAT_VARIANT
			.streamEntries()
			.sorted(Comparator.comparing(entry -> entry.registryKey().getValue()))
			.forEach(
				entry -> builder.criterion(
						entry.registryKey().getValue().toString(),
						TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().typeSpecific(EntitySubPredicateTypes.catVariant(entry)))
					)
			);
		return builder;
	}

	private static Advancement.Builder requireAllWolvesTamed(Advancement.Builder builder, RegistryWrapper.WrapperLookup registryLookup) {
		RegistryWrapper.Impl<WolfVariant> impl = registryLookup.getWrapperOrThrow(RegistryKeys.WOLF_VARIANT);
		impl.streamKeys()
			.sorted(Comparator.comparing(RegistryKey::getValue))
			.forEach(
				key -> {
					RegistryEntry<WolfVariant> registryEntry = impl.getOrThrow(key);
					builder.criterion(
						key.getValue().toString(),
						TameAnimalCriterion.Conditions.create(
							EntityPredicate.Builder.create().typeSpecific(EntitySubPredicateTypes.wolfVariant(RegistryEntryList.of(registryEntry)))
						)
					);
				}
			);
		return builder;
	}
}

package net.minecraft.data.server.advancement;

import com.google.common.collect.BiMap;
import java.util.Comparator;
import java.util.Map.Entry;
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
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HusbandryTabAdvancementGenerator implements AdvancementTabGenerator {
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
	private static final EntityType<?>[] EGG_LAYING_ANIMALS = new EntityType[]{EntityType.TURTLE, EntityType.FROG};
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
	private static final Item[] AXE_ITEMS = new Item[]{
		Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE
	};

	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<Advancement> exporter) {
		Advancement advancement = this.createRootAdvancement(exporter);
		Advancement advancement2 = Advancement.Builder.create()
			.parent(advancement)
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
			.criteriaMerger(CriterionMerger.OR)
			.criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.WHEAT))
			.criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.PUMPKIN_STEM))
			.criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.MELON_STEM))
			.criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.BEETROOTS))
			.criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.NETHER_WART))
			.build(exporter, "husbandry/plant_seed");
		Advancement advancement3 = this.createBreedAnimalAdvancement(advancement, exporter);
		this.createBreedAllAnimalsAdvancement(advancement3, exporter);
		this.requireFoodItemsEaten(Advancement.Builder.create())
			.parent(advancement2)
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
			.parent(advancement2)
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
		Advancement advancement4 = Advancement.Builder.create()
			.parent(advancement)
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
		Advancement advancement5 = this.requireListedFishCaught(Advancement.Builder.create())
			.parent(advancement)
			.criteriaMerger(CriterionMerger.OR)
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
		Advancement advancement6 = this.requireListedFishBucketsFilled(Advancement.Builder.create())
			.parent(advancement5)
			.criteriaMerger(CriterionMerger.OR)
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
		Advancement advancement7 = Advancement.Builder.create()
			.parent(advancement6)
			.criteriaMerger(CriterionMerger.OR)
			.criterion(
				Registries.ITEM.getId(Items.AXOLOTL_BUCKET).getPath(),
				FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(Items.AXOLOTL_BUCKET).build())
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
			.parent(advancement7)
			.criterion("kill_axolotl_target", EffectsChangedCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.AXOLOTL).build()))
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
		this.requireAllCatsTamed(Advancement.Builder.create())
			.parent(advancement4)
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
		Advancement advancement8 = Advancement.Builder.create()
			.parent(advancement)
			.criterion(
				"safely_harvest_honey",
				ItemCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().tag(BlockTags.BEEHIVES).build()).smokey(true),
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
		Advancement advancement9 = Advancement.Builder.create()
			.parent(advancement8)
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
				ItemCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(((BiMap)HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get()).keySet()).build()),
					ItemPredicate.Builder.create().items(Items.HONEYCOMB)
				)
			)
			.build(exporter, "husbandry/wax_on");
		Advancement.Builder.create()
			.parent(advancement9)
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
				ItemCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).keySet()).build()),
					ItemPredicate.Builder.create().items(AXE_ITEMS)
				)
			)
			.build(exporter, "husbandry/wax_off");
		Advancement advancement10 = Advancement.Builder.create()
			.parent(advancement)
			.criterion(
				Registries.ITEM.getId(Items.TADPOLE_BUCKET).getPath(),
				FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(Items.TADPOLE_BUCKET).build())
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
		Advancement advancement11 = this.requireAllFrogsOnLeads(Advancement.Builder.create())
			.parent(advancement10)
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
			.parent(advancement11)
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
			.parent(advancement)
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
						.vehicle(EntityPredicate.Builder.create().type(EntityType.BOAT).passenger(EntityPredicate.Builder.create().type(EntityType.GOAT).build()).build())
				)
			)
			.build(exporter, "husbandry/ride_a_boat_with_a_goat");
		Advancement.Builder.create()
			.parent(advancement)
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
				ItemCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().tag(BlockTags.ALL_SIGNS).build()),
					ItemPredicate.Builder.create().items(Items.GLOW_INK_SAC)
				)
			)
			.build(exporter, "husbandry/make_a_sign_glow");
		Advancement advancement12 = Advancement.Builder.create()
			.parent(advancement)
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
					EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().type(EntityType.ALLAY).build())
				)
			)
			.build(exporter, "husbandry/allay_deliver_item_to_player");
		Advancement.Builder.create()
			.parent(advancement12)
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
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(Blocks.NOTE_BLOCK).build()),
					ItemPredicate.Builder.create().items(Items.CAKE)
				)
			)
			.build(exporter, "husbandry/allay_deliver_cake_to_note_block");
	}

	Advancement createRootAdvancement(Consumer<Advancement> exporter) {
		return Advancement.Builder.create()
			.display(
				Blocks.HAY_BLOCK,
				Text.translatable("advancements.husbandry.root.title"),
				Text.translatable("advancements.husbandry.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/husbandry.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("consumed_item", ConsumeItemCriterion.Conditions.any())
			.build(exporter, "husbandry/root");
	}

	Advancement createBreedAnimalAdvancement(Advancement parent, Consumer<Advancement> exporter) {
		return Advancement.Builder.create()
			.parent(parent)
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
			.criteriaMerger(CriterionMerger.OR)
			.criterion("bred", BredAnimalsCriterion.Conditions.any())
			.build(exporter, "husbandry/breed_an_animal");
	}

	Advancement createBreedAllAnimalsAdvancement(Advancement parent, Consumer<Advancement> exporter) {
		return this.requireListedAnimalsBred(Advancement.Builder.create())
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

	private Advancement.Builder requireAllFrogsOnLeads(Advancement.Builder builder) {
		Registries.FROG_VARIANT
			.streamEntries()
			.forEach(
				variant -> builder.criterion(
						variant.registryKey().getValue().toString(),
						PlayerInteractedWithEntityCriterion.Conditions.create(
							ItemPredicate.Builder.create().items(Items.LEAD),
							EntityPredicate.Extended.ofLegacy(
								EntityPredicate.Builder.create().type(EntityType.FROG).typeSpecific(TypeSpecificPredicate.frog((FrogVariant)variant.value())).build()
							)
						)
					)
			);
		return builder;
	}

	private Advancement.Builder requireFoodItemsEaten(Advancement.Builder builder) {
		for (Item item : FOOD_ITEMS) {
			builder.criterion(Registries.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
		}

		return builder;
	}

	Advancement.Builder requireListedAnimalsBred(Advancement.Builder builder) {
		for (EntityType<?> entityType : this.getBreedableAnimals()) {
			builder.criterion(EntityType.getId(entityType).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(entityType)));
		}

		for (EntityType<?> entityType : this.getEggLayingAnimals()) {
			builder.criterion(
				EntityType.getId(entityType).toString(),
				BredAnimalsCriterion.Conditions.create(
					EntityPredicate.Builder.create().type(entityType).build(), EntityPredicate.Builder.create().type(entityType).build(), EntityPredicate.ANY
				)
			);
		}

		return builder;
	}

	private Advancement.Builder requireListedFishBucketsFilled(Advancement.Builder builder) {
		for (Item item : FISH_BUCKET_ITEMS) {
			builder.criterion(Registries.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().items(item).build()));
		}

		return builder;
	}

	private Advancement.Builder requireListedFishCaught(Advancement.Builder builder) {
		for (Item item : FISH_ITEMS) {
			builder.criterion(
				Registries.ITEM.getId(item).getPath(),
				FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().items(item).build())
			);
		}

		return builder;
	}

	private Advancement.Builder requireAllCatsTamed(Advancement.Builder builder) {
		Registries.CAT_VARIANT
			.getEntrySet()
			.stream()
			.sorted(Entry.comparingByKey(Comparator.comparing(RegistryKey::getValue)))
			.forEach(
				entry -> builder.criterion(
						((RegistryKey)entry.getKey()).getValue().toString(),
						TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().typeSpecific(TypeSpecificPredicate.cat((CatVariant)entry.getValue())).build())
					)
			);
		return builder;
	}

	public EntityType<?>[] getBreedableAnimals() {
		return BREEDABLE_ANIMALS;
	}

	public EntityType<?>[] getEggLayingAnimals() {
		return EGG_LAYING_ANIMALS;
	}
}

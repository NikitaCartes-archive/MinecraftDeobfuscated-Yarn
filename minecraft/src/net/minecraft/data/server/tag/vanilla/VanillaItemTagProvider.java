package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

public class VanillaItemTagProvider extends ItemTagProvider {
	public VanillaItemTagProvider(
		DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, CompletableFuture<TagProvider.TagLookup<Block>> completableFuture2
	) {
		super(dataOutput, completableFuture, completableFuture2);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.copy(BlockTags.WOOL, ItemTags.WOOL);
		this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
		this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
		this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		this.copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
		this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
		this.copy(BlockTags.WOOL_CARPETS, ItemTags.WOOL_CARPETS);
		this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
		this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		this.copy(BlockTags.DOORS, ItemTags.DOORS);
		this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		this.copy(BlockTags.BAMBOO_BLOCKS, ItemTags.BAMBOO_BLOCKS);
		this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
		this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
		this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
		this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
		this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
		this.copy(BlockTags.MANGROVE_LOGS, ItemTags.MANGROVE_LOGS);
		this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
		this.copy(BlockTags.CHERRY_LOGS, ItemTags.CHERRY_LOGS);
		this.copy(BlockTags.CRIMSON_STEMS, ItemTags.CRIMSON_STEMS);
		this.copy(BlockTags.WARPED_STEMS, ItemTags.WARPED_STEMS);
		this.copy(BlockTags.WART_BLOCKS, ItemTags.WART_BLOCKS);
		this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		this.copy(BlockTags.LOGS, ItemTags.LOGS);
		this.copy(BlockTags.SAND, ItemTags.SAND);
		this.copy(BlockTags.SMELTS_TO_GLASS, ItemTags.SMELTS_TO_GLASS);
		this.copy(BlockTags.SLABS, ItemTags.SLABS);
		this.copy(BlockTags.WALLS, ItemTags.WALLS);
		this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
		this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
		this.copy(BlockTags.RAILS, ItemTags.RAILS);
		this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
		this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
		this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
		this.copy(BlockTags.BEDS, ItemTags.BEDS);
		this.copy(BlockTags.FENCES, ItemTags.FENCES);
		this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
		this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
		this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);
		this.copy(BlockTags.CANDLES, ItemTags.CANDLES);
		this.copy(BlockTags.DAMPENS_VIBRATIONS, ItemTags.DAMPENS_VIBRATIONS);
		this.copy(BlockTags.GOLD_ORES, ItemTags.GOLD_ORES);
		this.copy(BlockTags.IRON_ORES, ItemTags.IRON_ORES);
		this.copy(BlockTags.DIAMOND_ORES, ItemTags.DIAMOND_ORES);
		this.copy(BlockTags.REDSTONE_ORES, ItemTags.REDSTONE_ORES);
		this.copy(BlockTags.LAPIS_ORES, ItemTags.LAPIS_ORES);
		this.copy(BlockTags.COAL_ORES, ItemTags.COAL_ORES);
		this.copy(BlockTags.EMERALD_ORES, ItemTags.EMERALD_ORES);
		this.copy(BlockTags.COPPER_ORES, ItemTags.COPPER_ORES);
		this.copy(BlockTags.DIRT, ItemTags.DIRT);
		this.copy(BlockTags.TERRACOTTA, ItemTags.TERRACOTTA);
		this.copy(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
		this.getOrCreateTagBuilder(ItemTags.BANNERS)
			.add(
				Items.WHITE_BANNER,
				Items.ORANGE_BANNER,
				Items.MAGENTA_BANNER,
				Items.LIGHT_BLUE_BANNER,
				Items.YELLOW_BANNER,
				Items.LIME_BANNER,
				Items.PINK_BANNER,
				Items.GRAY_BANNER,
				Items.LIGHT_GRAY_BANNER,
				Items.CYAN_BANNER,
				Items.PURPLE_BANNER,
				Items.BLUE_BANNER,
				Items.BROWN_BANNER,
				Items.GREEN_BANNER,
				Items.RED_BANNER,
				Items.BLACK_BANNER
			);
		this.getOrCreateTagBuilder(ItemTags.BOATS)
			.add(
				Items.OAK_BOAT,
				Items.SPRUCE_BOAT,
				Items.BIRCH_BOAT,
				Items.JUNGLE_BOAT,
				Items.ACACIA_BOAT,
				Items.DARK_OAK_BOAT,
				Items.MANGROVE_BOAT,
				Items.BAMBOO_RAFT,
				Items.CHERRY_BOAT
			)
			.addTag(ItemTags.CHEST_BOATS);
		this.getOrCreateTagBuilder(ItemTags.CHEST_BOATS)
			.add(
				Items.OAK_CHEST_BOAT,
				Items.SPRUCE_CHEST_BOAT,
				Items.BIRCH_CHEST_BOAT,
				Items.JUNGLE_CHEST_BOAT,
				Items.ACACIA_CHEST_BOAT,
				Items.DARK_OAK_CHEST_BOAT,
				Items.MANGROVE_CHEST_BOAT,
				Items.BAMBOO_CHEST_RAFT,
				Items.CHERRY_CHEST_BOAT
			);
		this.getOrCreateTagBuilder(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
		this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
		this.getOrCreateTagBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS)
			.add(
				Items.MUSIC_DISC_13,
				Items.MUSIC_DISC_CAT,
				Items.MUSIC_DISC_BLOCKS,
				Items.MUSIC_DISC_CHIRP,
				Items.MUSIC_DISC_FAR,
				Items.MUSIC_DISC_MALL,
				Items.MUSIC_DISC_MELLOHI,
				Items.MUSIC_DISC_STAL,
				Items.MUSIC_DISC_STRAD,
				Items.MUSIC_DISC_WARD,
				Items.MUSIC_DISC_11,
				Items.MUSIC_DISC_WAIT
			);
		this.getOrCreateTagBuilder(ItemTags.COALS).add(Items.COAL, Items.CHARCOAL);
		this.getOrCreateTagBuilder(ItemTags.ARROWS).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
		this.getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).add(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK);
		this.getOrCreateTagBuilder(ItemTags.BEACON_PAYMENT_ITEMS).add(Items.NETHERITE_INGOT, Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT);
		this.getOrCreateTagBuilder(ItemTags.PIGLIN_REPELLENTS).add(Items.SOUL_TORCH).add(Items.SOUL_LANTERN).add(Items.SOUL_CAMPFIRE);
		this.getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED)
			.addTag(ItemTags.GOLD_ORES)
			.add(
				Items.GOLD_BLOCK,
				Items.GILDED_BLACKSTONE,
				Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
				Items.GOLD_INGOT,
				Items.BELL,
				Items.CLOCK,
				Items.GOLDEN_CARROT,
				Items.GLISTERING_MELON_SLICE,
				Items.GOLDEN_APPLE,
				Items.ENCHANTED_GOLDEN_APPLE,
				Items.GOLDEN_HELMET,
				Items.GOLDEN_CHESTPLATE,
				Items.GOLDEN_LEGGINGS,
				Items.GOLDEN_BOOTS,
				Items.GOLDEN_HORSE_ARMOR,
				Items.GOLDEN_SWORD,
				Items.GOLDEN_PICKAXE,
				Items.GOLDEN_SHOVEL,
				Items.GOLDEN_AXE,
				Items.GOLDEN_HOE,
				Items.RAW_GOLD,
				Items.RAW_GOLD_BLOCK
			);
		this.getOrCreateTagBuilder(ItemTags.IGNORED_BY_PIGLIN_BABIES).add(Items.LEATHER);
		this.getOrCreateTagBuilder(ItemTags.PIGLIN_FOOD).add(Items.PORKCHOP, Items.COOKED_PORKCHOP);
		this.getOrCreateTagBuilder(ItemTags.FOX_FOOD).add(Items.SWEET_BERRIES, Items.GLOW_BERRIES);
		this.getOrCreateTagBuilder(ItemTags.NON_FLAMMABLE_WOOD)
			.add(
				Items.WARPED_STEM,
				Items.STRIPPED_WARPED_STEM,
				Items.WARPED_HYPHAE,
				Items.STRIPPED_WARPED_HYPHAE,
				Items.CRIMSON_STEM,
				Items.STRIPPED_CRIMSON_STEM,
				Items.CRIMSON_HYPHAE,
				Items.STRIPPED_CRIMSON_HYPHAE,
				Items.CRIMSON_PLANKS,
				Items.WARPED_PLANKS,
				Items.CRIMSON_SLAB,
				Items.WARPED_SLAB,
				Items.CRIMSON_PRESSURE_PLATE,
				Items.WARPED_PRESSURE_PLATE,
				Items.CRIMSON_FENCE,
				Items.WARPED_FENCE,
				Items.CRIMSON_TRAPDOOR,
				Items.WARPED_TRAPDOOR,
				Items.CRIMSON_FENCE_GATE,
				Items.WARPED_FENCE_GATE,
				Items.CRIMSON_STAIRS,
				Items.WARPED_STAIRS,
				Items.CRIMSON_BUTTON,
				Items.WARPED_BUTTON,
				Items.CRIMSON_DOOR,
				Items.WARPED_DOOR,
				Items.CRIMSON_SIGN,
				Items.WARPED_SIGN,
				Items.WARPED_HANGING_SIGN,
				Items.CRIMSON_HANGING_SIGN
			);
		this.getOrCreateTagBuilder(ItemTags.STONE_TOOL_MATERIALS).add(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE);
		this.getOrCreateTagBuilder(ItemTags.STONE_CRAFTING_MATERIALS).add(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE);
		this.getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES)
			.add(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.AXOLOTL_FOOD).add(Items.TROPICAL_FISH_BUCKET);
		this.getOrCreateTagBuilder(ItemTags.CLUSTER_MAX_HARVESTABLES)
			.add(Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE);
		this.getOrCreateTagBuilder(ItemTags.COMPASSES).add(Items.COMPASS).add(Items.RECOVERY_COMPASS);
		this.getOrCreateTagBuilder(ItemTags.CREEPER_IGNITERS).add(Items.FLINT_AND_STEEL).add(Items.FIRE_CHARGE);
		this.getOrCreateTagBuilder(ItemTags.SWORDS)
			.add(Items.DIAMOND_SWORD)
			.add(Items.STONE_SWORD)
			.add(Items.GOLDEN_SWORD)
			.add(Items.NETHERITE_SWORD)
			.add(Items.WOODEN_SWORD)
			.add(Items.IRON_SWORD);
		this.getOrCreateTagBuilder(ItemTags.AXES)
			.add(Items.DIAMOND_AXE)
			.add(Items.STONE_AXE)
			.add(Items.GOLDEN_AXE)
			.add(Items.NETHERITE_AXE)
			.add(Items.WOODEN_AXE)
			.add(Items.IRON_AXE);
		this.getOrCreateTagBuilder(ItemTags.PICKAXES)
			.add(Items.DIAMOND_PICKAXE)
			.add(Items.STONE_PICKAXE)
			.add(Items.GOLDEN_PICKAXE)
			.add(Items.NETHERITE_PICKAXE)
			.add(Items.WOODEN_PICKAXE)
			.add(Items.IRON_PICKAXE);
		this.getOrCreateTagBuilder(ItemTags.SHOVELS)
			.add(Items.DIAMOND_SHOVEL)
			.add(Items.STONE_SHOVEL)
			.add(Items.GOLDEN_SHOVEL)
			.add(Items.NETHERITE_SHOVEL)
			.add(Items.WOODEN_SHOVEL)
			.add(Items.IRON_SHOVEL);
		this.getOrCreateTagBuilder(ItemTags.HOES)
			.add(Items.DIAMOND_HOE)
			.add(Items.STONE_HOE)
			.add(Items.GOLDEN_HOE)
			.add(Items.NETHERITE_HOE)
			.add(Items.WOODEN_HOE)
			.add(Items.IRON_HOE);
		this.getOrCreateTagBuilder(ItemTags.BREAKS_DECORATED_POTS)
			.addTag(ItemTags.SWORDS)
			.addTag(ItemTags.AXES)
			.addTag(ItemTags.PICKAXES)
			.addTag(ItemTags.SHOVELS)
			.addTag(ItemTags.HOES)
			.add(Items.TRIDENT)
			.add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
			.add(
				Items.ANGLER_POTTERY_SHERD,
				Items.ARCHER_POTTERY_SHERD,
				Items.ARMS_UP_POTTERY_SHERD,
				Items.BLADE_POTTERY_SHERD,
				Items.BREWER_POTTERY_SHERD,
				Items.BURN_POTTERY_SHERD,
				Items.DANGER_POTTERY_SHERD,
				Items.EXPLORER_POTTERY_SHERD,
				Items.FRIEND_POTTERY_SHERD,
				Items.HEART_POTTERY_SHERD,
				Items.HEARTBREAK_POTTERY_SHERD,
				Items.HOWL_POTTERY_SHERD,
				Items.MINER_POTTERY_SHERD,
				Items.MOURNER_POTTERY_SHERD,
				Items.PLENTY_POTTERY_SHERD,
				Items.PRIZE_POTTERY_SHERD,
				Items.SHEAF_POTTERY_SHERD,
				Items.SHELTER_POTTERY_SHERD,
				Items.SKULL_POTTERY_SHERD,
				Items.SNORT_POTTERY_SHERD,
				Items.FLOW_POTTERY_SHERD,
				Items.GUSTER_POTTERY_SHERD,
				Items.SCRAPE_POTTERY_SHERD
			);
		this.getOrCreateTagBuilder(ItemTags.DECORATED_POT_INGREDIENTS).add(Items.BRICK).addTag(ItemTags.DECORATED_POT_SHERDS);
		this.getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
			.add(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
		this.getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
			.add(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
		this.getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
			.add(
				Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE
			);
		this.getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
			.add(Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET);
		this.getOrCreateTagBuilder(ItemTags.SKULLS)
			.add(Items.PLAYER_HEAD, Items.CREEPER_HEAD, Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.DRAGON_HEAD, Items.PIGLIN_HEAD);
		this.getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
			.addTag(ItemTags.FOOT_ARMOR)
			.addTag(ItemTags.LEG_ARMOR)
			.addTag(ItemTags.CHEST_ARMOR)
			.addTag(ItemTags.HEAD_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
			.add(Items.IRON_INGOT)
			.add(Items.COPPER_INGOT)
			.add(Items.GOLD_INGOT)
			.add(Items.LAPIS_LAZULI)
			.add(Items.EMERALD)
			.add(Items.DIAMOND)
			.add(Items.NETHERITE_INGOT)
			.add(Items.REDSTONE)
			.add(Items.QUARTZ)
			.add(Items.AMETHYST_SHARD);
		this.getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES)
			.add(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE)
			.add(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
		this.getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS).add(Items.BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK, Items.WRITABLE_BOOK, Items.KNOWLEDGE_BOOK);
		this.getOrCreateTagBuilder(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS)
			.add(Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD);
		this.getOrCreateTagBuilder(ItemTags.SNIFFER_FOOD).add(Items.TORCHFLOWER_SEEDS);
		this.getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS)
			.add(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
		this.getOrCreateTagBuilder(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.HEAD_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.ARMOR_ENCHANTABLE)
			.addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
			.addTag(ItemTags.LEG_ARMOR_ENCHANTABLE)
			.addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
			.addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
		this.getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE).addTag(ItemTags.SWORDS);
		this.getOrCreateTagBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(ItemTags.SWORD_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES);
		this.getOrCreateTagBuilder(ItemTags.WEAPON_ENCHANTABLE).addTag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.MACE_ENCHANTABLE).add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.MINING_ENCHANTABLE)
			.addTag(ItemTags.AXES)
			.addTag(ItemTags.PICKAXES)
			.addTag(ItemTags.SHOVELS)
			.addTag(ItemTags.HOES)
			.add(Items.SHEARS);
		this.getOrCreateTagBuilder(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES);
		this.getOrCreateTagBuilder(ItemTags.FISHING_ENCHANTABLE).add(Items.FISHING_ROD);
		this.getOrCreateTagBuilder(ItemTags.TRIDENT_ENCHANTABLE).add(Items.TRIDENT);
		this.getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
			.addTag(ItemTags.FOOT_ARMOR)
			.addTag(ItemTags.LEG_ARMOR)
			.addTag(ItemTags.CHEST_ARMOR)
			.addTag(ItemTags.HEAD_ARMOR)
			.add(Items.ELYTRA)
			.add(Items.SHIELD)
			.addTag(ItemTags.SWORDS)
			.addTag(ItemTags.AXES)
			.addTag(ItemTags.PICKAXES)
			.addTag(ItemTags.SHOVELS)
			.addTag(ItemTags.HOES)
			.add(Items.BOW)
			.add(Items.CROSSBOW)
			.add(Items.TRIDENT)
			.add(Items.FLINT_AND_STEEL)
			.add(Items.SHEARS)
			.add(Items.BRUSH)
			.add(Items.FISHING_ROD)
			.add(Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK)
			.add(Items.MACE);
		this.getOrCreateTagBuilder(ItemTags.BOW_ENCHANTABLE).add(Items.BOW);
		this.getOrCreateTagBuilder(ItemTags.EQUIPPABLE_ENCHANTABLE)
			.addTag(ItemTags.FOOT_ARMOR)
			.addTag(ItemTags.LEG_ARMOR)
			.addTag(ItemTags.CHEST_ARMOR)
			.addTag(ItemTags.HEAD_ARMOR)
			.add(Items.ELYTRA)
			.addTag(ItemTags.SKULLS)
			.add(Items.CARVED_PUMPKIN);
		this.getOrCreateTagBuilder(ItemTags.CROSSBOW_ENCHANTABLE).add(Items.CROSSBOW);
		this.getOrCreateTagBuilder(ItemTags.VANISHING_ENCHANTABLE)
			.addTag(ItemTags.DURABILITY_ENCHANTABLE)
			.add(Items.COMPASS)
			.add(Items.CARVED_PUMPKIN)
			.addTag(ItemTags.SKULLS);
		this.getOrCreateTagBuilder(ItemTags.DYEABLE)
			.add(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR);
		this.getOrCreateTagBuilder(ItemTags.MEAT)
			.add(
				Items.BEEF,
				Items.CHICKEN,
				Items.COOKED_BEEF,
				Items.COOKED_CHICKEN,
				Items.COOKED_MUTTON,
				Items.COOKED_PORKCHOP,
				Items.COOKED_RABBIT,
				Items.MUTTON,
				Items.PORKCHOP,
				Items.RABBIT,
				Items.ROTTEN_FLESH
			);
		this.getOrCreateTagBuilder(ItemTags.WOLF_FOOD).addTag(ItemTags.MEAT);
		this.getOrCreateTagBuilder(ItemTags.OCELOT_FOOD).add(Items.COD, Items.SALMON);
		this.getOrCreateTagBuilder(ItemTags.CAT_FOOD).add(Items.COD, Items.SALMON);
		this.getOrCreateTagBuilder(ItemTags.HORSE_FOOD)
			.add(Items.WHEAT, Items.SUGAR, Items.HAY_BLOCK, Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
		this.getOrCreateTagBuilder(ItemTags.HORSE_TEMPT_ITEMS).add(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
		this.getOrCreateTagBuilder(ItemTags.CAMEL_FOOD).add(Items.CACTUS);
		this.getOrCreateTagBuilder(ItemTags.ARMADILLO_FOOD).add(Items.SPIDER_EYE);
		this.getOrCreateTagBuilder(ItemTags.BEE_FOOD).addTag(ItemTags.FLOWERS);
		this.getOrCreateTagBuilder(ItemTags.CHICKEN_FOOD)
			.add(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
		this.getOrCreateTagBuilder(ItemTags.FROG_FOOD).add(Items.SLIME_BALL);
		this.getOrCreateTagBuilder(ItemTags.HOGLIN_FOOD).add(Items.CRIMSON_FUNGUS);
		this.getOrCreateTagBuilder(ItemTags.LLAMA_FOOD).add(Items.WHEAT, Items.HAY_BLOCK);
		this.getOrCreateTagBuilder(ItemTags.LLAMA_TEMPT_ITEMS).add(Items.HAY_BLOCK);
		this.getOrCreateTagBuilder(ItemTags.PANDA_FOOD).add(Items.BAMBOO);
		this.getOrCreateTagBuilder(ItemTags.PIG_FOOD).add(Items.CARROT, Items.POTATO, Items.BEETROOT);
		this.getOrCreateTagBuilder(ItemTags.RABBIT_FOOD).add(Items.CARROT, Items.GOLDEN_CARROT, Items.DANDELION);
		this.getOrCreateTagBuilder(ItemTags.STRIDER_FOOD).add(Items.WARPED_FUNGUS);
		this.getOrCreateTagBuilder(ItemTags.STRIDER_TEMPT_ITEMS).addTag(ItemTags.STRIDER_FOOD).add(Items.WARPED_FUNGUS_ON_A_STICK);
		this.getOrCreateTagBuilder(ItemTags.TURTLE_FOOD).add(Items.SEAGRASS);
		this.getOrCreateTagBuilder(ItemTags.PARROT_FOOD)
			.add(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
		this.getOrCreateTagBuilder(ItemTags.PARROT_POISONOUS_FOOD).add(Items.COOKIE);
		this.getOrCreateTagBuilder(ItemTags.COW_FOOD).add(Items.WHEAT);
		this.getOrCreateTagBuilder(ItemTags.SHEEP_FOOD).add(Items.WHEAT);
		this.getOrCreateTagBuilder(ItemTags.GOAT_FOOD).add(Items.WHEAT);
	}
}

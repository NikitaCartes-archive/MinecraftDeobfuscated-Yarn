package net.minecraft.data.client;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.Texture;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ItemModelGenerator {
	private final BiConsumer<Identifier, Supplier<JsonElement>> writer;

	public ItemModelGenerator(BiConsumer<Identifier, Supplier<JsonElement>> writer) {
		this.writer = writer;
	}

	private void register(Item item, Model model) {
		model.upload(ModelIds.getItemModelId(item), Texture.layer0(item), this.writer);
	}

	private void register(Item item, String suffix, Model model) {
		model.upload(ModelIds.getItemSubModelId(item, suffix), Texture.layer0(Texture.getSubId(item, suffix)), this.writer);
	}

	private void register(Item item, Item texture, Model model) {
		model.upload(ModelIds.getItemModelId(item), Texture.layer0(texture), this.writer);
	}

	public void register() {
		this.register(Items.ACACIA_BOAT, Models.GENERATED);
		this.register(Items.APPLE, Models.GENERATED);
		this.register(Items.ARMOR_STAND, Models.GENERATED);
		this.register(Items.ARROW, Models.GENERATED);
		this.register(Items.BAKED_POTATO, Models.GENERATED);
		this.register(Items.BAMBOO, Models.HANDHELD);
		this.register(Items.BEEF, Models.GENERATED);
		this.register(Items.BEETROOT, Models.GENERATED);
		this.register(Items.BEETROOT_SOUP, Models.GENERATED);
		this.register(Items.BIRCH_BOAT, Models.GENERATED);
		this.register(Items.BLACK_DYE, Models.GENERATED);
		this.register(Items.BLAZE_POWDER, Models.GENERATED);
		this.register(Items.BLAZE_ROD, Models.HANDHELD);
		this.register(Items.BLUE_DYE, Models.GENERATED);
		this.register(Items.BONE_MEAL, Models.GENERATED);
		this.register(Items.BOOK, Models.GENERATED);
		this.register(Items.BOWL, Models.GENERATED);
		this.register(Items.BREAD, Models.GENERATED);
		this.register(Items.BRICK, Models.GENERATED);
		this.register(Items.BROWN_DYE, Models.GENERATED);
		this.register(Items.BUCKET, Models.GENERATED);
		this.register(Items.CARROT_ON_A_STICK, Models.HANDHELD_ROD);
		this.register(Items.WARPED_FUNGUS_ON_A_STICK, Models.HANDHELD_ROD);
		this.register(Items.CHAINMAIL_BOOTS, Models.GENERATED);
		this.register(Items.CHAINMAIL_CHESTPLATE, Models.GENERATED);
		this.register(Items.CHAINMAIL_HELMET, Models.GENERATED);
		this.register(Items.CHAINMAIL_LEGGINGS, Models.GENERATED);
		this.register(Items.CHARCOAL, Models.GENERATED);
		this.register(Items.CHEST_MINECART, Models.GENERATED);
		this.register(Items.CHICKEN, Models.GENERATED);
		this.register(Items.CHORUS_FRUIT, Models.GENERATED);
		this.register(Items.CLAY_BALL, Models.GENERATED);

		for (int i = 1; i < 64; i++) {
			this.register(Items.CLOCK, String.format("_%02d", i), Models.GENERATED);
		}

		this.register(Items.COAL, Models.GENERATED);
		this.register(Items.COD_BUCKET, Models.GENERATED);
		this.register(Items.COMMAND_BLOCK_MINECART, Models.GENERATED);

		for (int i = 0; i < 32; i++) {
			if (i != 16) {
				this.register(Items.COMPASS, String.format("_%02d", i), Models.GENERATED);
			}
		}

		this.register(Items.COOKED_BEEF, Models.GENERATED);
		this.register(Items.COOKED_CHICKEN, Models.GENERATED);
		this.register(Items.COOKED_COD, Models.GENERATED);
		this.register(Items.COOKED_MUTTON, Models.GENERATED);
		this.register(Items.COOKED_PORKCHOP, Models.GENERATED);
		this.register(Items.COOKED_RABBIT, Models.GENERATED);
		this.register(Items.COOKED_SALMON, Models.GENERATED);
		this.register(Items.COOKIE, Models.GENERATED);
		this.register(Items.CREEPER_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.CYAN_DYE, Models.GENERATED);
		this.register(Items.DARK_OAK_BOAT, Models.GENERATED);
		this.register(Items.DIAMOND, Models.GENERATED);
		this.register(Items.DIAMOND_AXE, Models.HANDHELD);
		this.register(Items.DIAMOND_BOOTS, Models.GENERATED);
		this.register(Items.DIAMOND_CHESTPLATE, Models.GENERATED);
		this.register(Items.DIAMOND_HELMET, Models.GENERATED);
		this.register(Items.DIAMOND_HOE, Models.HANDHELD);
		this.register(Items.DIAMOND_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.DIAMOND_LEGGINGS, Models.GENERATED);
		this.register(Items.DIAMOND_PICKAXE, Models.HANDHELD);
		this.register(Items.DIAMOND_SHOVEL, Models.HANDHELD);
		this.register(Items.DIAMOND_SWORD, Models.HANDHELD);
		this.register(Items.DRAGON_BREATH, Models.GENERATED);
		this.register(Items.DRIED_KELP, Models.GENERATED);
		this.register(Items.EGG, Models.GENERATED);
		this.register(Items.EMERALD, Models.GENERATED);
		this.register(Items.ENCHANTED_BOOK, Models.GENERATED);
		this.register(Items.ENDER_EYE, Models.GENERATED);
		this.register(Items.ENDER_PEARL, Models.GENERATED);
		this.register(Items.END_CRYSTAL, Models.GENERATED);
		this.register(Items.EXPERIENCE_BOTTLE, Models.GENERATED);
		this.register(Items.FERMENTED_SPIDER_EYE, Models.GENERATED);
		this.register(Items.FIREWORK_ROCKET, Models.GENERATED);
		this.register(Items.FIRE_CHARGE, Models.GENERATED);
		this.register(Items.FLINT, Models.GENERATED);
		this.register(Items.FLINT_AND_STEEL, Models.GENERATED);
		this.register(Items.FLOWER_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.FURNACE_MINECART, Models.GENERATED);
		this.register(Items.GHAST_TEAR, Models.GENERATED);
		this.register(Items.GLASS_BOTTLE, Models.GENERATED);
		this.register(Items.GLISTERING_MELON_SLICE, Models.GENERATED);
		this.register(Items.GLOBE_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.GLOWSTONE_DUST, Models.GENERATED);
		this.register(Items.GOLDEN_APPLE, Models.GENERATED);
		this.register(Items.GOLDEN_AXE, Models.HANDHELD);
		this.register(Items.GOLDEN_BOOTS, Models.GENERATED);
		this.register(Items.GOLDEN_CARROT, Models.GENERATED);
		this.register(Items.GOLDEN_CHESTPLATE, Models.GENERATED);
		this.register(Items.GOLDEN_HELMET, Models.GENERATED);
		this.register(Items.GOLDEN_HOE, Models.HANDHELD);
		this.register(Items.GOLDEN_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.GOLDEN_LEGGINGS, Models.GENERATED);
		this.register(Items.GOLDEN_PICKAXE, Models.HANDHELD);
		this.register(Items.GOLDEN_SHOVEL, Models.HANDHELD);
		this.register(Items.GOLDEN_SWORD, Models.HANDHELD);
		this.register(Items.GOLD_INGOT, Models.GENERATED);
		this.register(Items.GOLD_NUGGET, Models.GENERATED);
		this.register(Items.GRAY_DYE, Models.GENERATED);
		this.register(Items.GREEN_DYE, Models.GENERATED);
		this.register(Items.GUNPOWDER, Models.GENERATED);
		this.register(Items.HEART_OF_THE_SEA, Models.GENERATED);
		this.register(Items.HONEYCOMB, Models.GENERATED);
		this.register(Items.HONEY_BOTTLE, Models.GENERATED);
		this.register(Items.HOPPER_MINECART, Models.GENERATED);
		this.register(Items.INK_SAC, Models.GENERATED);
		this.register(Items.IRON_AXE, Models.HANDHELD);
		this.register(Items.IRON_BOOTS, Models.GENERATED);
		this.register(Items.IRON_CHESTPLATE, Models.GENERATED);
		this.register(Items.IRON_HELMET, Models.GENERATED);
		this.register(Items.IRON_HOE, Models.HANDHELD);
		this.register(Items.IRON_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.IRON_INGOT, Models.GENERATED);
		this.register(Items.IRON_LEGGINGS, Models.GENERATED);
		this.register(Items.IRON_NUGGET, Models.GENERATED);
		this.register(Items.IRON_PICKAXE, Models.HANDHELD);
		this.register(Items.IRON_SHOVEL, Models.HANDHELD);
		this.register(Items.IRON_SWORD, Models.HANDHELD);
		this.register(Items.ITEM_FRAME, Models.GENERATED);
		this.register(Items.JUNGLE_BOAT, Models.GENERATED);
		this.register(Items.KNOWLEDGE_BOOK, Models.GENERATED);
		this.register(Items.LAPIS_LAZULI, Models.GENERATED);
		this.register(Items.LAVA_BUCKET, Models.GENERATED);
		this.register(Items.LEATHER, Models.GENERATED);
		this.register(Items.LEATHER_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.LIGHT_BLUE_DYE, Models.GENERATED);
		this.register(Items.LIGHT_GRAY_DYE, Models.GENERATED);
		this.register(Items.LIME_DYE, Models.GENERATED);
		this.register(Items.MAGENTA_DYE, Models.GENERATED);
		this.register(Items.MAGMA_CREAM, Models.GENERATED);
		this.register(Items.MAP, Models.GENERATED);
		this.register(Items.MELON_SLICE, Models.GENERATED);
		this.register(Items.MILK_BUCKET, Models.GENERATED);
		this.register(Items.MINECART, Models.GENERATED);
		this.register(Items.MOJANG_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.MUSHROOM_STEW, Models.GENERATED);
		this.register(Items.MUSIC_DISC_11, Models.GENERATED);
		this.register(Items.MUSIC_DISC_13, Models.GENERATED);
		this.register(Items.MUSIC_DISC_BLOCKS, Models.GENERATED);
		this.register(Items.MUSIC_DISC_CAT, Models.GENERATED);
		this.register(Items.MUSIC_DISC_CHIRP, Models.GENERATED);
		this.register(Items.MUSIC_DISC_FAR, Models.GENERATED);
		this.register(Items.MUSIC_DISC_MALL, Models.GENERATED);
		this.register(Items.MUSIC_DISC_MELLOHI, Models.GENERATED);
		this.register(Items.MUSIC_DISC_STAL, Models.GENERATED);
		this.register(Items.MUSIC_DISC_STRAD, Models.GENERATED);
		this.register(Items.MUSIC_DISC_WAIT, Models.GENERATED);
		this.register(Items.MUSIC_DISC_WARD, Models.GENERATED);
		this.register(Items.MUTTON, Models.GENERATED);
		this.register(Items.NAME_TAG, Models.GENERATED);
		this.register(Items.NAUTILUS_SHELL, Models.GENERATED);
		this.register(Items.NETHERITE_AXE, Models.HANDHELD);
		this.register(Items.NETHERITE_BOOTS, Models.GENERATED);
		this.register(Items.NETHERITE_CHESTPLATE, Models.GENERATED);
		this.register(Items.NETHERITE_HELMET, Models.GENERATED);
		this.register(Items.NETHERITE_HOE, Models.HANDHELD);
		this.register(Items.NETHERITE_INGOT, Models.GENERATED);
		this.register(Items.NETHERITE_LEGGINGS, Models.GENERATED);
		this.register(Items.NETHERITE_PICKAXE, Models.HANDHELD);
		this.register(Items.NETHERITE_SCRAP, Models.GENERATED);
		this.register(Items.NETHERITE_SHOVEL, Models.HANDHELD);
		this.register(Items.NETHERITE_SWORD, Models.HANDHELD);
		this.register(Items.NETHER_BRICK, Models.GENERATED);
		this.register(Items.NETHER_STAR, Models.GENERATED);
		this.register(Items.OAK_BOAT, Models.GENERATED);
		this.register(Items.ORANGE_DYE, Models.GENERATED);
		this.register(Items.PAINTING, Models.GENERATED);
		this.register(Items.PAPER, Models.GENERATED);
		this.register(Items.PHANTOM_MEMBRANE, Models.GENERATED);
		this.register(Items.PINK_DYE, Models.GENERATED);
		this.register(Items.POISONOUS_POTATO, Models.GENERATED);
		this.register(Items.POPPED_CHORUS_FRUIT, Models.GENERATED);
		this.register(Items.PORKCHOP, Models.GENERATED);
		this.register(Items.PRISMARINE_CRYSTALS, Models.GENERATED);
		this.register(Items.PRISMARINE_SHARD, Models.GENERATED);
		this.register(Items.PUFFERFISH, Models.GENERATED);
		this.register(Items.PUFFERFISH_BUCKET, Models.GENERATED);
		this.register(Items.PUMPKIN_PIE, Models.GENERATED);
		this.register(Items.PURPLE_DYE, Models.GENERATED);
		this.register(Items.QUARTZ, Models.GENERATED);
		this.register(Items.RABBIT, Models.GENERATED);
		this.register(Items.RABBIT_FOOT, Models.GENERATED);
		this.register(Items.RABBIT_HIDE, Models.GENERATED);
		this.register(Items.RABBIT_STEW, Models.GENERATED);
		this.register(Items.RED_DYE, Models.GENERATED);
		this.register(Items.ROTTEN_FLESH, Models.GENERATED);
		this.register(Items.SADDLE, Models.GENERATED);
		this.register(Items.SALMON, Models.GENERATED);
		this.register(Items.SALMON_BUCKET, Models.GENERATED);
		this.register(Items.SCUTE, Models.GENERATED);
		this.register(Items.SHEARS, Models.GENERATED);
		this.register(Items.SHULKER_SHELL, Models.GENERATED);
		this.register(Items.SKULL_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.SLIME_BALL, Models.GENERATED);
		this.register(Items.SNOWBALL, Models.GENERATED);
		this.register(Items.SPECTRAL_ARROW, Models.GENERATED);
		this.register(Items.SPIDER_EYE, Models.GENERATED);
		this.register(Items.SPRUCE_BOAT, Models.GENERATED);
		this.register(Items.STICK, Models.HANDHELD);
		this.register(Items.STONE_AXE, Models.HANDHELD);
		this.register(Items.STONE_HOE, Models.HANDHELD);
		this.register(Items.STONE_PICKAXE, Models.HANDHELD);
		this.register(Items.STONE_SHOVEL, Models.HANDHELD);
		this.register(Items.STONE_SWORD, Models.HANDHELD);
		this.register(Items.SUGAR, Models.GENERATED);
		this.register(Items.SUSPICIOUS_STEW, Models.GENERATED);
		this.register(Items.TNT_MINECART, Models.GENERATED);
		this.register(Items.TOTEM_OF_UNDYING, Models.GENERATED);
		this.register(Items.TRIDENT, Models.GENERATED);
		this.register(Items.TROPICAL_FISH, Models.GENERATED);
		this.register(Items.TROPICAL_FISH_BUCKET, Models.GENERATED);
		this.register(Items.TURTLE_HELMET, Models.GENERATED);
		this.register(Items.WATER_BUCKET, Models.GENERATED);
		this.register(Items.WHEAT, Models.GENERATED);
		this.register(Items.WHITE_DYE, Models.GENERATED);
		this.register(Items.WOODEN_AXE, Models.HANDHELD);
		this.register(Items.WOODEN_HOE, Models.HANDHELD);
		this.register(Items.WOODEN_PICKAXE, Models.HANDHELD);
		this.register(Items.WOODEN_SHOVEL, Models.HANDHELD);
		this.register(Items.WOODEN_SWORD, Models.HANDHELD);
		this.register(Items.WRITABLE_BOOK, Models.GENERATED);
		this.register(Items.WRITTEN_BOOK, Models.GENERATED);
		this.register(Items.YELLOW_DYE, Models.GENERATED);
		this.register(Items.DEBUG_STICK, Items.STICK, Models.HANDHELD);
		this.register(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, Models.GENERATED);
	}
}

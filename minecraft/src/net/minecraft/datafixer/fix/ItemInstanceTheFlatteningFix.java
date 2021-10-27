package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemInstanceTheFlatteningFix extends DataFix {
	private static final Map<String, String> FLATTENING_MAP = DataFixUtils.make(Maps.<String, String>newHashMap(), map -> {
		map.put("minecraft:stone.0", "minecraft:stone");
		map.put("minecraft:stone.1", "minecraft:granite");
		map.put("minecraft:stone.2", "minecraft:polished_granite");
		map.put("minecraft:stone.3", "minecraft:diorite");
		map.put("minecraft:stone.4", "minecraft:polished_diorite");
		map.put("minecraft:stone.5", "minecraft:andesite");
		map.put("minecraft:stone.6", "minecraft:polished_andesite");
		map.put("minecraft:dirt.0", "minecraft:dirt");
		map.put("minecraft:dirt.1", "minecraft:coarse_dirt");
		map.put("minecraft:dirt.2", "minecraft:podzol");
		map.put("minecraft:leaves.0", "minecraft:oak_leaves");
		map.put("minecraft:leaves.1", "minecraft:spruce_leaves");
		map.put("minecraft:leaves.2", "minecraft:birch_leaves");
		map.put("minecraft:leaves.3", "minecraft:jungle_leaves");
		map.put("minecraft:leaves2.0", "minecraft:acacia_leaves");
		map.put("minecraft:leaves2.1", "minecraft:dark_oak_leaves");
		map.put("minecraft:log.0", "minecraft:oak_log");
		map.put("minecraft:log.1", "minecraft:spruce_log");
		map.put("minecraft:log.2", "minecraft:birch_log");
		map.put("minecraft:log.3", "minecraft:jungle_log");
		map.put("minecraft:log2.0", "minecraft:acacia_log");
		map.put("minecraft:log2.1", "minecraft:dark_oak_log");
		map.put("minecraft:sapling.0", "minecraft:oak_sapling");
		map.put("minecraft:sapling.1", "minecraft:spruce_sapling");
		map.put("minecraft:sapling.2", "minecraft:birch_sapling");
		map.put("minecraft:sapling.3", "minecraft:jungle_sapling");
		map.put("minecraft:sapling.4", "minecraft:acacia_sapling");
		map.put("minecraft:sapling.5", "minecraft:dark_oak_sapling");
		map.put("minecraft:planks.0", "minecraft:oak_planks");
		map.put("minecraft:planks.1", "minecraft:spruce_planks");
		map.put("minecraft:planks.2", "minecraft:birch_planks");
		map.put("minecraft:planks.3", "minecraft:jungle_planks");
		map.put("minecraft:planks.4", "minecraft:acacia_planks");
		map.put("minecraft:planks.5", "minecraft:dark_oak_planks");
		map.put("minecraft:sand.0", "minecraft:sand");
		map.put("minecraft:sand.1", "minecraft:red_sand");
		map.put("minecraft:quartz_block.0", "minecraft:quartz_block");
		map.put("minecraft:quartz_block.1", "minecraft:chiseled_quartz_block");
		map.put("minecraft:quartz_block.2", "minecraft:quartz_pillar");
		map.put("minecraft:anvil.0", "minecraft:anvil");
		map.put("minecraft:anvil.1", "minecraft:chipped_anvil");
		map.put("minecraft:anvil.2", "minecraft:damaged_anvil");
		map.put("minecraft:wool.0", "minecraft:white_wool");
		map.put("minecraft:wool.1", "minecraft:orange_wool");
		map.put("minecraft:wool.2", "minecraft:magenta_wool");
		map.put("minecraft:wool.3", "minecraft:light_blue_wool");
		map.put("minecraft:wool.4", "minecraft:yellow_wool");
		map.put("minecraft:wool.5", "minecraft:lime_wool");
		map.put("minecraft:wool.6", "minecraft:pink_wool");
		map.put("minecraft:wool.7", "minecraft:gray_wool");
		map.put("minecraft:wool.8", "minecraft:light_gray_wool");
		map.put("minecraft:wool.9", "minecraft:cyan_wool");
		map.put("minecraft:wool.10", "minecraft:purple_wool");
		map.put("minecraft:wool.11", "minecraft:blue_wool");
		map.put("minecraft:wool.12", "minecraft:brown_wool");
		map.put("minecraft:wool.13", "minecraft:green_wool");
		map.put("minecraft:wool.14", "minecraft:red_wool");
		map.put("minecraft:wool.15", "minecraft:black_wool");
		map.put("minecraft:carpet.0", "minecraft:white_carpet");
		map.put("minecraft:carpet.1", "minecraft:orange_carpet");
		map.put("minecraft:carpet.2", "minecraft:magenta_carpet");
		map.put("minecraft:carpet.3", "minecraft:light_blue_carpet");
		map.put("minecraft:carpet.4", "minecraft:yellow_carpet");
		map.put("minecraft:carpet.5", "minecraft:lime_carpet");
		map.put("minecraft:carpet.6", "minecraft:pink_carpet");
		map.put("minecraft:carpet.7", "minecraft:gray_carpet");
		map.put("minecraft:carpet.8", "minecraft:light_gray_carpet");
		map.put("minecraft:carpet.9", "minecraft:cyan_carpet");
		map.put("minecraft:carpet.10", "minecraft:purple_carpet");
		map.put("minecraft:carpet.11", "minecraft:blue_carpet");
		map.put("minecraft:carpet.12", "minecraft:brown_carpet");
		map.put("minecraft:carpet.13", "minecraft:green_carpet");
		map.put("minecraft:carpet.14", "minecraft:red_carpet");
		map.put("minecraft:carpet.15", "minecraft:black_carpet");
		map.put("minecraft:hardened_clay.0", "minecraft:terracotta");
		map.put("minecraft:stained_hardened_clay.0", "minecraft:white_terracotta");
		map.put("minecraft:stained_hardened_clay.1", "minecraft:orange_terracotta");
		map.put("minecraft:stained_hardened_clay.2", "minecraft:magenta_terracotta");
		map.put("minecraft:stained_hardened_clay.3", "minecraft:light_blue_terracotta");
		map.put("minecraft:stained_hardened_clay.4", "minecraft:yellow_terracotta");
		map.put("minecraft:stained_hardened_clay.5", "minecraft:lime_terracotta");
		map.put("minecraft:stained_hardened_clay.6", "minecraft:pink_terracotta");
		map.put("minecraft:stained_hardened_clay.7", "minecraft:gray_terracotta");
		map.put("minecraft:stained_hardened_clay.8", "minecraft:light_gray_terracotta");
		map.put("minecraft:stained_hardened_clay.9", "minecraft:cyan_terracotta");
		map.put("minecraft:stained_hardened_clay.10", "minecraft:purple_terracotta");
		map.put("minecraft:stained_hardened_clay.11", "minecraft:blue_terracotta");
		map.put("minecraft:stained_hardened_clay.12", "minecraft:brown_terracotta");
		map.put("minecraft:stained_hardened_clay.13", "minecraft:green_terracotta");
		map.put("minecraft:stained_hardened_clay.14", "minecraft:red_terracotta");
		map.put("minecraft:stained_hardened_clay.15", "minecraft:black_terracotta");
		map.put("minecraft:silver_glazed_terracotta.0", "minecraft:light_gray_glazed_terracotta");
		map.put("minecraft:stained_glass.0", "minecraft:white_stained_glass");
		map.put("minecraft:stained_glass.1", "minecraft:orange_stained_glass");
		map.put("minecraft:stained_glass.2", "minecraft:magenta_stained_glass");
		map.put("minecraft:stained_glass.3", "minecraft:light_blue_stained_glass");
		map.put("minecraft:stained_glass.4", "minecraft:yellow_stained_glass");
		map.put("minecraft:stained_glass.5", "minecraft:lime_stained_glass");
		map.put("minecraft:stained_glass.6", "minecraft:pink_stained_glass");
		map.put("minecraft:stained_glass.7", "minecraft:gray_stained_glass");
		map.put("minecraft:stained_glass.8", "minecraft:light_gray_stained_glass");
		map.put("minecraft:stained_glass.9", "minecraft:cyan_stained_glass");
		map.put("minecraft:stained_glass.10", "minecraft:purple_stained_glass");
		map.put("minecraft:stained_glass.11", "minecraft:blue_stained_glass");
		map.put("minecraft:stained_glass.12", "minecraft:brown_stained_glass");
		map.put("minecraft:stained_glass.13", "minecraft:green_stained_glass");
		map.put("minecraft:stained_glass.14", "minecraft:red_stained_glass");
		map.put("minecraft:stained_glass.15", "minecraft:black_stained_glass");
		map.put("minecraft:stained_glass_pane.0", "minecraft:white_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.1", "minecraft:orange_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.2", "minecraft:magenta_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.3", "minecraft:light_blue_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.4", "minecraft:yellow_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.5", "minecraft:lime_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.6", "minecraft:pink_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.7", "minecraft:gray_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.8", "minecraft:light_gray_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.9", "minecraft:cyan_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.10", "minecraft:purple_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.11", "minecraft:blue_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.12", "minecraft:brown_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.13", "minecraft:green_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.14", "minecraft:red_stained_glass_pane");
		map.put("minecraft:stained_glass_pane.15", "minecraft:black_stained_glass_pane");
		map.put("minecraft:prismarine.0", "minecraft:prismarine");
		map.put("minecraft:prismarine.1", "minecraft:prismarine_bricks");
		map.put("minecraft:prismarine.2", "minecraft:dark_prismarine");
		map.put("minecraft:concrete.0", "minecraft:white_concrete");
		map.put("minecraft:concrete.1", "minecraft:orange_concrete");
		map.put("minecraft:concrete.2", "minecraft:magenta_concrete");
		map.put("minecraft:concrete.3", "minecraft:light_blue_concrete");
		map.put("minecraft:concrete.4", "minecraft:yellow_concrete");
		map.put("minecraft:concrete.5", "minecraft:lime_concrete");
		map.put("minecraft:concrete.6", "minecraft:pink_concrete");
		map.put("minecraft:concrete.7", "minecraft:gray_concrete");
		map.put("minecraft:concrete.8", "minecraft:light_gray_concrete");
		map.put("minecraft:concrete.9", "minecraft:cyan_concrete");
		map.put("minecraft:concrete.10", "minecraft:purple_concrete");
		map.put("minecraft:concrete.11", "minecraft:blue_concrete");
		map.put("minecraft:concrete.12", "minecraft:brown_concrete");
		map.put("minecraft:concrete.13", "minecraft:green_concrete");
		map.put("minecraft:concrete.14", "minecraft:red_concrete");
		map.put("minecraft:concrete.15", "minecraft:black_concrete");
		map.put("minecraft:concrete_powder.0", "minecraft:white_concrete_powder");
		map.put("minecraft:concrete_powder.1", "minecraft:orange_concrete_powder");
		map.put("minecraft:concrete_powder.2", "minecraft:magenta_concrete_powder");
		map.put("minecraft:concrete_powder.3", "minecraft:light_blue_concrete_powder");
		map.put("minecraft:concrete_powder.4", "minecraft:yellow_concrete_powder");
		map.put("minecraft:concrete_powder.5", "minecraft:lime_concrete_powder");
		map.put("minecraft:concrete_powder.6", "minecraft:pink_concrete_powder");
		map.put("minecraft:concrete_powder.7", "minecraft:gray_concrete_powder");
		map.put("minecraft:concrete_powder.8", "minecraft:light_gray_concrete_powder");
		map.put("minecraft:concrete_powder.9", "minecraft:cyan_concrete_powder");
		map.put("minecraft:concrete_powder.10", "minecraft:purple_concrete_powder");
		map.put("minecraft:concrete_powder.11", "minecraft:blue_concrete_powder");
		map.put("minecraft:concrete_powder.12", "minecraft:brown_concrete_powder");
		map.put("minecraft:concrete_powder.13", "minecraft:green_concrete_powder");
		map.put("minecraft:concrete_powder.14", "minecraft:red_concrete_powder");
		map.put("minecraft:concrete_powder.15", "minecraft:black_concrete_powder");
		map.put("minecraft:cobblestone_wall.0", "minecraft:cobblestone_wall");
		map.put("minecraft:cobblestone_wall.1", "minecraft:mossy_cobblestone_wall");
		map.put("minecraft:sandstone.0", "minecraft:sandstone");
		map.put("minecraft:sandstone.1", "minecraft:chiseled_sandstone");
		map.put("minecraft:sandstone.2", "minecraft:cut_sandstone");
		map.put("minecraft:red_sandstone.0", "minecraft:red_sandstone");
		map.put("minecraft:red_sandstone.1", "minecraft:chiseled_red_sandstone");
		map.put("minecraft:red_sandstone.2", "minecraft:cut_red_sandstone");
		map.put("minecraft:stonebrick.0", "minecraft:stone_bricks");
		map.put("minecraft:stonebrick.1", "minecraft:mossy_stone_bricks");
		map.put("minecraft:stonebrick.2", "minecraft:cracked_stone_bricks");
		map.put("minecraft:stonebrick.3", "minecraft:chiseled_stone_bricks");
		map.put("minecraft:monster_egg.0", "minecraft:infested_stone");
		map.put("minecraft:monster_egg.1", "minecraft:infested_cobblestone");
		map.put("minecraft:monster_egg.2", "minecraft:infested_stone_bricks");
		map.put("minecraft:monster_egg.3", "minecraft:infested_mossy_stone_bricks");
		map.put("minecraft:monster_egg.4", "minecraft:infested_cracked_stone_bricks");
		map.put("minecraft:monster_egg.5", "minecraft:infested_chiseled_stone_bricks");
		map.put("minecraft:yellow_flower.0", "minecraft:dandelion");
		map.put("minecraft:red_flower.0", "minecraft:poppy");
		map.put("minecraft:red_flower.1", "minecraft:blue_orchid");
		map.put("minecraft:red_flower.2", "minecraft:allium");
		map.put("minecraft:red_flower.3", "minecraft:azure_bluet");
		map.put("minecraft:red_flower.4", "minecraft:red_tulip");
		map.put("minecraft:red_flower.5", "minecraft:orange_tulip");
		map.put("minecraft:red_flower.6", "minecraft:white_tulip");
		map.put("minecraft:red_flower.7", "minecraft:pink_tulip");
		map.put("minecraft:red_flower.8", "minecraft:oxeye_daisy");
		map.put("minecraft:double_plant.0", "minecraft:sunflower");
		map.put("minecraft:double_plant.1", "minecraft:lilac");
		map.put("minecraft:double_plant.2", "minecraft:tall_grass");
		map.put("minecraft:double_plant.3", "minecraft:large_fern");
		map.put("minecraft:double_plant.4", "minecraft:rose_bush");
		map.put("minecraft:double_plant.5", "minecraft:peony");
		map.put("minecraft:deadbush.0", "minecraft:dead_bush");
		map.put("minecraft:tallgrass.0", "minecraft:dead_bush");
		map.put("minecraft:tallgrass.1", "minecraft:grass");
		map.put("minecraft:tallgrass.2", "minecraft:fern");
		map.put("minecraft:sponge.0", "minecraft:sponge");
		map.put("minecraft:sponge.1", "minecraft:wet_sponge");
		map.put("minecraft:purpur_slab.0", "minecraft:purpur_slab");
		map.put("minecraft:stone_slab.0", "minecraft:stone_slab");
		map.put("minecraft:stone_slab.1", "minecraft:sandstone_slab");
		map.put("minecraft:stone_slab.2", "minecraft:petrified_oak_slab");
		map.put("minecraft:stone_slab.3", "minecraft:cobblestone_slab");
		map.put("minecraft:stone_slab.4", "minecraft:brick_slab");
		map.put("minecraft:stone_slab.5", "minecraft:stone_brick_slab");
		map.put("minecraft:stone_slab.6", "minecraft:nether_brick_slab");
		map.put("minecraft:stone_slab.7", "minecraft:quartz_slab");
		map.put("minecraft:stone_slab2.0", "minecraft:red_sandstone_slab");
		map.put("minecraft:wooden_slab.0", "minecraft:oak_slab");
		map.put("minecraft:wooden_slab.1", "minecraft:spruce_slab");
		map.put("minecraft:wooden_slab.2", "minecraft:birch_slab");
		map.put("minecraft:wooden_slab.3", "minecraft:jungle_slab");
		map.put("minecraft:wooden_slab.4", "minecraft:acacia_slab");
		map.put("minecraft:wooden_slab.5", "minecraft:dark_oak_slab");
		map.put("minecraft:coal.0", "minecraft:coal");
		map.put("minecraft:coal.1", "minecraft:charcoal");
		map.put("minecraft:fish.0", "minecraft:cod");
		map.put("minecraft:fish.1", "minecraft:salmon");
		map.put("minecraft:fish.2", "minecraft:clownfish");
		map.put("minecraft:fish.3", "minecraft:pufferfish");
		map.put("minecraft:cooked_fish.0", "minecraft:cooked_cod");
		map.put("minecraft:cooked_fish.1", "minecraft:cooked_salmon");
		map.put("minecraft:skull.0", "minecraft:skeleton_skull");
		map.put("minecraft:skull.1", "minecraft:wither_skeleton_skull");
		map.put("minecraft:skull.2", "minecraft:zombie_head");
		map.put("minecraft:skull.3", "minecraft:player_head");
		map.put("minecraft:skull.4", "minecraft:creeper_head");
		map.put("minecraft:skull.5", "minecraft:dragon_head");
		map.put("minecraft:golden_apple.0", "minecraft:golden_apple");
		map.put("minecraft:golden_apple.1", "minecraft:enchanted_golden_apple");
		map.put("minecraft:fireworks.0", "minecraft:firework_rocket");
		map.put("minecraft:firework_charge.0", "minecraft:firework_star");
		map.put("minecraft:dye.0", "minecraft:ink_sac");
		map.put("minecraft:dye.1", "minecraft:rose_red");
		map.put("minecraft:dye.2", "minecraft:cactus_green");
		map.put("minecraft:dye.3", "minecraft:cocoa_beans");
		map.put("minecraft:dye.4", "minecraft:lapis_lazuli");
		map.put("minecraft:dye.5", "minecraft:purple_dye");
		map.put("minecraft:dye.6", "minecraft:cyan_dye");
		map.put("minecraft:dye.7", "minecraft:light_gray_dye");
		map.put("minecraft:dye.8", "minecraft:gray_dye");
		map.put("minecraft:dye.9", "minecraft:pink_dye");
		map.put("minecraft:dye.10", "minecraft:lime_dye");
		map.put("minecraft:dye.11", "minecraft:dandelion_yellow");
		map.put("minecraft:dye.12", "minecraft:light_blue_dye");
		map.put("minecraft:dye.13", "minecraft:magenta_dye");
		map.put("minecraft:dye.14", "minecraft:orange_dye");
		map.put("minecraft:dye.15", "minecraft:bone_meal");
		map.put("minecraft:silver_shulker_box.0", "minecraft:light_gray_shulker_box");
		map.put("minecraft:fence.0", "minecraft:oak_fence");
		map.put("minecraft:fence_gate.0", "minecraft:oak_fence_gate");
		map.put("minecraft:wooden_door.0", "minecraft:oak_door");
		map.put("minecraft:boat.0", "minecraft:oak_boat");
		map.put("minecraft:lit_pumpkin.0", "minecraft:jack_o_lantern");
		map.put("minecraft:pumpkin.0", "minecraft:carved_pumpkin");
		map.put("minecraft:trapdoor.0", "minecraft:oak_trapdoor");
		map.put("minecraft:nether_brick.0", "minecraft:nether_bricks");
		map.put("minecraft:red_nether_brick.0", "minecraft:red_nether_bricks");
		map.put("minecraft:netherbrick.0", "minecraft:nether_brick");
		map.put("minecraft:wooden_button.0", "minecraft:oak_button");
		map.put("minecraft:wooden_pressure_plate.0", "minecraft:oak_pressure_plate");
		map.put("minecraft:noteblock.0", "minecraft:note_block");
		map.put("minecraft:bed.0", "minecraft:white_bed");
		map.put("minecraft:bed.1", "minecraft:orange_bed");
		map.put("minecraft:bed.2", "minecraft:magenta_bed");
		map.put("minecraft:bed.3", "minecraft:light_blue_bed");
		map.put("minecraft:bed.4", "minecraft:yellow_bed");
		map.put("minecraft:bed.5", "minecraft:lime_bed");
		map.put("minecraft:bed.6", "minecraft:pink_bed");
		map.put("minecraft:bed.7", "minecraft:gray_bed");
		map.put("minecraft:bed.8", "minecraft:light_gray_bed");
		map.put("minecraft:bed.9", "minecraft:cyan_bed");
		map.put("minecraft:bed.10", "minecraft:purple_bed");
		map.put("minecraft:bed.11", "minecraft:blue_bed");
		map.put("minecraft:bed.12", "minecraft:brown_bed");
		map.put("minecraft:bed.13", "minecraft:green_bed");
		map.put("minecraft:bed.14", "minecraft:red_bed");
		map.put("minecraft:bed.15", "minecraft:black_bed");
		map.put("minecraft:banner.15", "minecraft:white_banner");
		map.put("minecraft:banner.14", "minecraft:orange_banner");
		map.put("minecraft:banner.13", "minecraft:magenta_banner");
		map.put("minecraft:banner.12", "minecraft:light_blue_banner");
		map.put("minecraft:banner.11", "minecraft:yellow_banner");
		map.put("minecraft:banner.10", "minecraft:lime_banner");
		map.put("minecraft:banner.9", "minecraft:pink_banner");
		map.put("minecraft:banner.8", "minecraft:gray_banner");
		map.put("minecraft:banner.7", "minecraft:light_gray_banner");
		map.put("minecraft:banner.6", "minecraft:cyan_banner");
		map.put("minecraft:banner.5", "minecraft:purple_banner");
		map.put("minecraft:banner.4", "minecraft:blue_banner");
		map.put("minecraft:banner.3", "minecraft:brown_banner");
		map.put("minecraft:banner.2", "minecraft:green_banner");
		map.put("minecraft:banner.1", "minecraft:red_banner");
		map.put("minecraft:banner.0", "minecraft:black_banner");
		map.put("minecraft:grass.0", "minecraft:grass_block");
		map.put("minecraft:brick_block.0", "minecraft:bricks");
		map.put("minecraft:end_bricks.0", "minecraft:end_stone_bricks");
		map.put("minecraft:golden_rail.0", "minecraft:powered_rail");
		map.put("minecraft:magma.0", "minecraft:magma_block");
		map.put("minecraft:quartz_ore.0", "minecraft:nether_quartz_ore");
		map.put("minecraft:reeds.0", "minecraft:sugar_cane");
		map.put("minecraft:slime.0", "minecraft:slime_block");
		map.put("minecraft:stone_stairs.0", "minecraft:cobblestone_stairs");
		map.put("minecraft:waterlily.0", "minecraft:lily_pad");
		map.put("minecraft:web.0", "minecraft:cobweb");
		map.put("minecraft:snow.0", "minecraft:snow_block");
		map.put("minecraft:snow_layer.0", "minecraft:snow");
		map.put("minecraft:record_11.0", "minecraft:music_disc_11");
		map.put("minecraft:record_13.0", "minecraft:music_disc_13");
		map.put("minecraft:record_blocks.0", "minecraft:music_disc_blocks");
		map.put("minecraft:record_cat.0", "minecraft:music_disc_cat");
		map.put("minecraft:record_chirp.0", "minecraft:music_disc_chirp");
		map.put("minecraft:record_far.0", "minecraft:music_disc_far");
		map.put("minecraft:record_mall.0", "minecraft:music_disc_mall");
		map.put("minecraft:record_mellohi.0", "minecraft:music_disc_mellohi");
		map.put("minecraft:record_stal.0", "minecraft:music_disc_stal");
		map.put("minecraft:record_strad.0", "minecraft:music_disc_strad");
		map.put("minecraft:record_wait.0", "minecraft:music_disc_wait");
		map.put("minecraft:record_ward.0", "minecraft:music_disc_ward");
	});
	private static final Set<String> ORIGINAL_ITEM_NAMES = (Set<String>)FLATTENING_MAP.keySet()
		.stream()
		.map(string -> string.substring(0, string.indexOf(46)))
		.collect(Collectors.toSet());
	private static final Set<String> DAMAGEABLE_ITEMS = Sets.<String>newHashSet(
		"minecraft:bow",
		"minecraft:carrot_on_a_stick",
		"minecraft:chainmail_boots",
		"minecraft:chainmail_chestplate",
		"minecraft:chainmail_helmet",
		"minecraft:chainmail_leggings",
		"minecraft:diamond_axe",
		"minecraft:diamond_boots",
		"minecraft:diamond_chestplate",
		"minecraft:diamond_helmet",
		"minecraft:diamond_hoe",
		"minecraft:diamond_leggings",
		"minecraft:diamond_pickaxe",
		"minecraft:diamond_shovel",
		"minecraft:diamond_sword",
		"minecraft:elytra",
		"minecraft:fishing_rod",
		"minecraft:flint_and_steel",
		"minecraft:golden_axe",
		"minecraft:golden_boots",
		"minecraft:golden_chestplate",
		"minecraft:golden_helmet",
		"minecraft:golden_hoe",
		"minecraft:golden_leggings",
		"minecraft:golden_pickaxe",
		"minecraft:golden_shovel",
		"minecraft:golden_sword",
		"minecraft:iron_axe",
		"minecraft:iron_boots",
		"minecraft:iron_chestplate",
		"minecraft:iron_helmet",
		"minecraft:iron_hoe",
		"minecraft:iron_leggings",
		"minecraft:iron_pickaxe",
		"minecraft:iron_shovel",
		"minecraft:iron_sword",
		"minecraft:leather_boots",
		"minecraft:leather_chestplate",
		"minecraft:leather_helmet",
		"minecraft:leather_leggings",
		"minecraft:shears",
		"minecraft:shield",
		"minecraft:stone_axe",
		"minecraft:stone_hoe",
		"minecraft:stone_pickaxe",
		"minecraft:stone_shovel",
		"minecraft:stone_sword",
		"minecraft:wooden_axe",
		"minecraft:wooden_hoe",
		"minecraft:wooden_pickaxe",
		"minecraft:wooden_shovel",
		"minecraft:wooden_sword"
	);

	public ItemInstanceTheFlatteningFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped("ItemInstanceTheFlatteningFix", type, typed -> {
			Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
			if (!optional.isPresent()) {
				return typed;
			} else {
				Typed<?> typed2 = typed;
				Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
				int i = dynamic.get("Damage").asInt(0);
				String string = getItem((String)((Pair)optional.get()).getSecond(), i);
				if (string != null) {
					typed2 = typed.set(opticFinder, Pair.of(TypeReferences.ITEM_NAME.typeName(), string));
				}

				if (DAMAGEABLE_ITEMS.contains(((Pair)optional.get()).getSecond())) {
					Typed<?> typed3 = typed.getOrCreateTyped(opticFinder2);
					Dynamic<?> dynamic2 = typed3.get(DSL.remainderFinder());
					dynamic2 = dynamic2.set("Damage", dynamic2.createInt(i));
					typed2 = typed2.set(opticFinder2, typed3.set(DSL.remainderFinder(), dynamic2));
				}

				return typed2.set(DSL.remainderFinder(), dynamic.remove("Damage"));
			}
		});
	}

	@Nullable
	public static String getItem(@Nullable String originalName, int damage) {
		if (ORIGINAL_ITEM_NAMES.contains(originalName)) {
			String string = (String)FLATTENING_MAP.get(originalName + "." + damage);
			return string == null ? (String)FLATTENING_MAP.get(originalName + ".0") : string;
		} else {
			return null;
		}
	}
}

package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class EntityBlockStateFix extends DataFix {
	private static final Map<String, Integer> BLOCK_NAME_TO_ID = DataFixUtils.make(Maps.<String, Integer>newHashMap(), map -> {
		map.put("minecraft:air", 0);
		map.put("minecraft:stone", 1);
		map.put("minecraft:grass", 2);
		map.put("minecraft:dirt", 3);
		map.put("minecraft:cobblestone", 4);
		map.put("minecraft:planks", 5);
		map.put("minecraft:sapling", 6);
		map.put("minecraft:bedrock", 7);
		map.put("minecraft:flowing_water", 8);
		map.put("minecraft:water", 9);
		map.put("minecraft:flowing_lava", 10);
		map.put("minecraft:lava", 11);
		map.put("minecraft:sand", 12);
		map.put("minecraft:gravel", 13);
		map.put("minecraft:gold_ore", 14);
		map.put("minecraft:iron_ore", 15);
		map.put("minecraft:coal_ore", 16);
		map.put("minecraft:log", 17);
		map.put("minecraft:leaves", 18);
		map.put("minecraft:sponge", 19);
		map.put("minecraft:glass", 20);
		map.put("minecraft:lapis_ore", 21);
		map.put("minecraft:lapis_block", 22);
		map.put("minecraft:dispenser", 23);
		map.put("minecraft:sandstone", 24);
		map.put("minecraft:noteblock", 25);
		map.put("minecraft:bed", 26);
		map.put("minecraft:golden_rail", 27);
		map.put("minecraft:detector_rail", 28);
		map.put("minecraft:sticky_piston", 29);
		map.put("minecraft:web", 30);
		map.put("minecraft:tallgrass", 31);
		map.put("minecraft:deadbush", 32);
		map.put("minecraft:piston", 33);
		map.put("minecraft:piston_head", 34);
		map.put("minecraft:wool", 35);
		map.put("minecraft:piston_extension", 36);
		map.put("minecraft:yellow_flower", 37);
		map.put("minecraft:red_flower", 38);
		map.put("minecraft:brown_mushroom", 39);
		map.put("minecraft:red_mushroom", 40);
		map.put("minecraft:gold_block", 41);
		map.put("minecraft:iron_block", 42);
		map.put("minecraft:double_stone_slab", 43);
		map.put("minecraft:stone_slab", 44);
		map.put("minecraft:brick_block", 45);
		map.put("minecraft:tnt", 46);
		map.put("minecraft:bookshelf", 47);
		map.put("minecraft:mossy_cobblestone", 48);
		map.put("minecraft:obsidian", 49);
		map.put("minecraft:torch", 50);
		map.put("minecraft:fire", 51);
		map.put("minecraft:mob_spawner", 52);
		map.put("minecraft:oak_stairs", 53);
		map.put("minecraft:chest", 54);
		map.put("minecraft:redstone_wire", 55);
		map.put("minecraft:diamond_ore", 56);
		map.put("minecraft:diamond_block", 57);
		map.put("minecraft:crafting_table", 58);
		map.put("minecraft:wheat", 59);
		map.put("minecraft:farmland", 60);
		map.put("minecraft:furnace", 61);
		map.put("minecraft:lit_furnace", 62);
		map.put("minecraft:standing_sign", 63);
		map.put("minecraft:wooden_door", 64);
		map.put("minecraft:ladder", 65);
		map.put("minecraft:rail", 66);
		map.put("minecraft:stone_stairs", 67);
		map.put("minecraft:wall_sign", 68);
		map.put("minecraft:lever", 69);
		map.put("minecraft:stone_pressure_plate", 70);
		map.put("minecraft:iron_door", 71);
		map.put("minecraft:wooden_pressure_plate", 72);
		map.put("minecraft:redstone_ore", 73);
		map.put("minecraft:lit_redstone_ore", 74);
		map.put("minecraft:unlit_redstone_torch", 75);
		map.put("minecraft:redstone_torch", 76);
		map.put("minecraft:stone_button", 77);
		map.put("minecraft:snow_layer", 78);
		map.put("minecraft:ice", 79);
		map.put("minecraft:snow", 80);
		map.put("minecraft:cactus", 81);
		map.put("minecraft:clay", 82);
		map.put("minecraft:reeds", 83);
		map.put("minecraft:jukebox", 84);
		map.put("minecraft:fence", 85);
		map.put("minecraft:pumpkin", 86);
		map.put("minecraft:netherrack", 87);
		map.put("minecraft:soul_sand", 88);
		map.put("minecraft:glowstone", 89);
		map.put("minecraft:portal", 90);
		map.put("minecraft:lit_pumpkin", 91);
		map.put("minecraft:cake", 92);
		map.put("minecraft:unpowered_repeater", 93);
		map.put("minecraft:powered_repeater", 94);
		map.put("minecraft:stained_glass", 95);
		map.put("minecraft:trapdoor", 96);
		map.put("minecraft:monster_egg", 97);
		map.put("minecraft:stonebrick", 98);
		map.put("minecraft:brown_mushroom_block", 99);
		map.put("minecraft:red_mushroom_block", 100);
		map.put("minecraft:iron_bars", 101);
		map.put("minecraft:glass_pane", 102);
		map.put("minecraft:melon_block", 103);
		map.put("minecraft:pumpkin_stem", 104);
		map.put("minecraft:melon_stem", 105);
		map.put("minecraft:vine", 106);
		map.put("minecraft:fence_gate", 107);
		map.put("minecraft:brick_stairs", 108);
		map.put("minecraft:stone_brick_stairs", 109);
		map.put("minecraft:mycelium", 110);
		map.put("minecraft:waterlily", 111);
		map.put("minecraft:nether_brick", 112);
		map.put("minecraft:nether_brick_fence", 113);
		map.put("minecraft:nether_brick_stairs", 114);
		map.put("minecraft:nether_wart", 115);
		map.put("minecraft:enchanting_table", 116);
		map.put("minecraft:brewing_stand", 117);
		map.put("minecraft:cauldron", 118);
		map.put("minecraft:end_portal", 119);
		map.put("minecraft:end_portal_frame", 120);
		map.put("minecraft:end_stone", 121);
		map.put("minecraft:dragon_egg", 122);
		map.put("minecraft:redstone_lamp", 123);
		map.put("minecraft:lit_redstone_lamp", 124);
		map.put("minecraft:double_wooden_slab", 125);
		map.put("minecraft:wooden_slab", 126);
		map.put("minecraft:cocoa", 127);
		map.put("minecraft:sandstone_stairs", 128);
		map.put("minecraft:emerald_ore", 129);
		map.put("minecraft:ender_chest", 130);
		map.put("minecraft:tripwire_hook", 131);
		map.put("minecraft:tripwire", 132);
		map.put("minecraft:emerald_block", 133);
		map.put("minecraft:spruce_stairs", 134);
		map.put("minecraft:birch_stairs", 135);
		map.put("minecraft:jungle_stairs", 136);
		map.put("minecraft:command_block", 137);
		map.put("minecraft:beacon", 138);
		map.put("minecraft:cobblestone_wall", 139);
		map.put("minecraft:flower_pot", 140);
		map.put("minecraft:carrots", 141);
		map.put("minecraft:potatoes", 142);
		map.put("minecraft:wooden_button", 143);
		map.put("minecraft:skull", 144);
		map.put("minecraft:anvil", 145);
		map.put("minecraft:trapped_chest", 146);
		map.put("minecraft:light_weighted_pressure_plate", 147);
		map.put("minecraft:heavy_weighted_pressure_plate", 148);
		map.put("minecraft:unpowered_comparator", 149);
		map.put("minecraft:powered_comparator", 150);
		map.put("minecraft:daylight_detector", 151);
		map.put("minecraft:redstone_block", 152);
		map.put("minecraft:quartz_ore", 153);
		map.put("minecraft:hopper", 154);
		map.put("minecraft:quartz_block", 155);
		map.put("minecraft:quartz_stairs", 156);
		map.put("minecraft:activator_rail", 157);
		map.put("minecraft:dropper", 158);
		map.put("minecraft:stained_hardened_clay", 159);
		map.put("minecraft:stained_glass_pane", 160);
		map.put("minecraft:leaves2", 161);
		map.put("minecraft:log2", 162);
		map.put("minecraft:acacia_stairs", 163);
		map.put("minecraft:dark_oak_stairs", 164);
		map.put("minecraft:slime", 165);
		map.put("minecraft:barrier", 166);
		map.put("minecraft:iron_trapdoor", 167);
		map.put("minecraft:prismarine", 168);
		map.put("minecraft:sea_lantern", 169);
		map.put("minecraft:hay_block", 170);
		map.put("minecraft:carpet", 171);
		map.put("minecraft:hardened_clay", 172);
		map.put("minecraft:coal_block", 173);
		map.put("minecraft:packed_ice", 174);
		map.put("minecraft:double_plant", 175);
		map.put("minecraft:standing_banner", 176);
		map.put("minecraft:wall_banner", 177);
		map.put("minecraft:daylight_detector_inverted", 178);
		map.put("minecraft:red_sandstone", 179);
		map.put("minecraft:red_sandstone_stairs", 180);
		map.put("minecraft:double_stone_slab2", 181);
		map.put("minecraft:stone_slab2", 182);
		map.put("minecraft:spruce_fence_gate", 183);
		map.put("minecraft:birch_fence_gate", 184);
		map.put("minecraft:jungle_fence_gate", 185);
		map.put("minecraft:dark_oak_fence_gate", 186);
		map.put("minecraft:acacia_fence_gate", 187);
		map.put("minecraft:spruce_fence", 188);
		map.put("minecraft:birch_fence", 189);
		map.put("minecraft:jungle_fence", 190);
		map.put("minecraft:dark_oak_fence", 191);
		map.put("minecraft:acacia_fence", 192);
		map.put("minecraft:spruce_door", 193);
		map.put("minecraft:birch_door", 194);
		map.put("minecraft:jungle_door", 195);
		map.put("minecraft:acacia_door", 196);
		map.put("minecraft:dark_oak_door", 197);
		map.put("minecraft:end_rod", 198);
		map.put("minecraft:chorus_plant", 199);
		map.put("minecraft:chorus_flower", 200);
		map.put("minecraft:purpur_block", 201);
		map.put("minecraft:purpur_pillar", 202);
		map.put("minecraft:purpur_stairs", 203);
		map.put("minecraft:purpur_double_slab", 204);
		map.put("minecraft:purpur_slab", 205);
		map.put("minecraft:end_bricks", 206);
		map.put("minecraft:beetroots", 207);
		map.put("minecraft:grass_path", 208);
		map.put("minecraft:end_gateway", 209);
		map.put("minecraft:repeating_command_block", 210);
		map.put("minecraft:chain_command_block", 211);
		map.put("minecraft:frosted_ice", 212);
		map.put("minecraft:magma", 213);
		map.put("minecraft:nether_wart_block", 214);
		map.put("minecraft:red_nether_brick", 215);
		map.put("minecraft:bone_block", 216);
		map.put("minecraft:structure_void", 217);
		map.put("minecraft:observer", 218);
		map.put("minecraft:white_shulker_box", 219);
		map.put("minecraft:orange_shulker_box", 220);
		map.put("minecraft:magenta_shulker_box", 221);
		map.put("minecraft:light_blue_shulker_box", 222);
		map.put("minecraft:yellow_shulker_box", 223);
		map.put("minecraft:lime_shulker_box", 224);
		map.put("minecraft:pink_shulker_box", 225);
		map.put("minecraft:gray_shulker_box", 226);
		map.put("minecraft:silver_shulker_box", 227);
		map.put("minecraft:cyan_shulker_box", 228);
		map.put("minecraft:purple_shulker_box", 229);
		map.put("minecraft:blue_shulker_box", 230);
		map.put("minecraft:brown_shulker_box", 231);
		map.put("minecraft:green_shulker_box", 232);
		map.put("minecraft:red_shulker_box", 233);
		map.put("minecraft:black_shulker_box", 234);
		map.put("minecraft:white_glazed_terracotta", 235);
		map.put("minecraft:orange_glazed_terracotta", 236);
		map.put("minecraft:magenta_glazed_terracotta", 237);
		map.put("minecraft:light_blue_glazed_terracotta", 238);
		map.put("minecraft:yellow_glazed_terracotta", 239);
		map.put("minecraft:lime_glazed_terracotta", 240);
		map.put("minecraft:pink_glazed_terracotta", 241);
		map.put("minecraft:gray_glazed_terracotta", 242);
		map.put("minecraft:silver_glazed_terracotta", 243);
		map.put("minecraft:cyan_glazed_terracotta", 244);
		map.put("minecraft:purple_glazed_terracotta", 245);
		map.put("minecraft:blue_glazed_terracotta", 246);
		map.put("minecraft:brown_glazed_terracotta", 247);
		map.put("minecraft:green_glazed_terracotta", 248);
		map.put("minecraft:red_glazed_terracotta", 249);
		map.put("minecraft:black_glazed_terracotta", 250);
		map.put("minecraft:concrete", 251);
		map.put("minecraft:concrete_powder", 252);
		map.put("minecraft:structure_block", 255);
	});

	public EntityBlockStateFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	public static int getNumericalBlockId(String blockId) {
		Integer integer = (Integer)BLOCK_NAME_TO_ID.get(blockId);
		return integer == null ? 0 : integer;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		Schema schema2 = this.getOutputSchema();
		Function<Typed<?>, Typed<?>> function = minecart -> this.mergeIdAndData(minecart, "DisplayTile", "DisplayData", "DisplayState");
		Function<Typed<?>, Typed<?>> function2 = arrow -> this.mergeIdAndData(arrow, "inTile", "inData", "inBlockState");
		Type<Pair<Either<Pair<String, Either<Integer, String>>, Unit>, Dynamic<?>>> type = DSL.and(
			DSL.optional(DSL.field("inTile", DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), IdentifierNormalizingSchema.getIdentifierType())))),
			DSL.remainderType()
		);
		Function<Typed<?>, Typed<?>> function3 = projectile -> projectile.update(type.finder(), DSL.remainderType(), Pair::getSecond);
		return this.fixTypeEverywhereTyped("EntityBlockStateFix", schema.getType(TypeReferences.ENTITY), schema2.getType(TypeReferences.ENTITY), entity -> {
			entity = this.useFunction(entity, "minecraft:falling_block", this::fixFallingBlock);
			entity = this.useFunction(entity, "minecraft:enderman", enderman -> this.mergeIdAndData(enderman, "carried", "carriedData", "carriedBlockState"));
			entity = this.useFunction(entity, "minecraft:arrow", function2);
			entity = this.useFunction(entity, "minecraft:spectral_arrow", function2);
			entity = this.useFunction(entity, "minecraft:egg", function3);
			entity = this.useFunction(entity, "minecraft:ender_pearl", function3);
			entity = this.useFunction(entity, "minecraft:fireball", function3);
			entity = this.useFunction(entity, "minecraft:potion", function3);
			entity = this.useFunction(entity, "minecraft:small_fireball", function3);
			entity = this.useFunction(entity, "minecraft:snowball", function3);
			entity = this.useFunction(entity, "minecraft:wither_skull", function3);
			entity = this.useFunction(entity, "minecraft:xp_bottle", function3);
			entity = this.useFunction(entity, "minecraft:commandblock_minecart", function);
			entity = this.useFunction(entity, "minecraft:minecart", function);
			entity = this.useFunction(entity, "minecraft:chest_minecart", function);
			entity = this.useFunction(entity, "minecraft:furnace_minecart", function);
			entity = this.useFunction(entity, "minecraft:tnt_minecart", function);
			entity = this.useFunction(entity, "minecraft:hopper_minecart", function);
			return this.useFunction(entity, "minecraft:spawner_minecart", function);
		});
	}

	private Typed<?> fixFallingBlock(Typed<?> fallingBlock) {
		Type<Either<Pair<String, Either<Integer, String>>, Unit>> type = DSL.optional(
			DSL.field("Block", DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), IdentifierNormalizingSchema.getIdentifierType())))
		);
		Type<Either<Pair<String, Dynamic<?>>, Unit>> type2 = DSL.optional(
			DSL.field("BlockState", DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType()))
		);
		Dynamic<?> dynamic = fallingBlock.get(DSL.remainderFinder());
		return fallingBlock.update(type.finder(), type2, state -> {
			int i = state.<Integer>map(pair -> ((Either)pair.getSecond()).map(id -> id, EntityBlockStateFix::getNumericalBlockId), unit -> {
				Optional<Number> optional = dynamic.get("TileID").asNumber().result();
				return (Integer)optional.map(Number::intValue).orElseGet(() -> dynamic.get("Tile").asByte((byte)0) & 0xFF);
			});
			int j = dynamic.get("Data").asInt(0) & 15;
			return Either.left(Pair.of(TypeReferences.BLOCK_STATE.typeName(), BlockStateFlattening.lookupState(i << 4 | j)));
		}).set(DSL.remainderFinder(), dynamic.remove("Data").remove("TileID").remove("Tile"));
	}

	private Typed<?> mergeIdAndData(Typed<?> entity, String oldIdKey, String oldDataKey, String newStateKey) {
		Type<Pair<String, Either<Integer, String>>> type = DSL.field(
			oldIdKey, DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), IdentifierNormalizingSchema.getIdentifierType()))
		);
		Type<Pair<String, Dynamic<?>>> type2 = DSL.field(newStateKey, DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType()));
		Dynamic<?> dynamic = entity.getOrCreate(DSL.remainderFinder());
		return entity.update(type.finder(), type2, state -> {
			int i = ((Either)state.getSecond()).<Integer>map(id -> id, EntityBlockStateFix::getNumericalBlockId);
			int j = dynamic.get(oldDataKey).asInt(0) & 15;
			return Pair.of(TypeReferences.BLOCK_STATE.typeName(), BlockStateFlattening.lookupState(i << 4 | j));
		}).set(DSL.remainderFinder(), dynamic.remove(oldDataKey));
	}

	private Typed<?> useFunction(Typed<?> entity, String entityId, Function<Typed<?>, Typed<?>> function) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, entityId);
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, entityId);
		return entity.updateTyped(DSL.namedChoice(entityId, type), type2, function);
	}
}

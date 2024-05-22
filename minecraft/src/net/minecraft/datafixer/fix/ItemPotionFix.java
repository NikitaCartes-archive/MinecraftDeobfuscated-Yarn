package net.minecraft.datafixer.fix;

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
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemPotionFix extends DataFix {
	private static final int SPLASH_POTION_FLAG = 16384;
	private static final String[] ID_TO_POTIONS = DataFixUtils.make(new String[128], potions -> {
		potions[0] = "minecraft:water";
		potions[1] = "minecraft:regeneration";
		potions[2] = "minecraft:swiftness";
		potions[3] = "minecraft:fire_resistance";
		potions[4] = "minecraft:poison";
		potions[5] = "minecraft:healing";
		potions[6] = "minecraft:night_vision";
		potions[7] = null;
		potions[8] = "minecraft:weakness";
		potions[9] = "minecraft:strength";
		potions[10] = "minecraft:slowness";
		potions[11] = "minecraft:leaping";
		potions[12] = "minecraft:harming";
		potions[13] = "minecraft:water_breathing";
		potions[14] = "minecraft:invisibility";
		potions[15] = null;
		potions[16] = "minecraft:awkward";
		potions[17] = "minecraft:regeneration";
		potions[18] = "minecraft:swiftness";
		potions[19] = "minecraft:fire_resistance";
		potions[20] = "minecraft:poison";
		potions[21] = "minecraft:healing";
		potions[22] = "minecraft:night_vision";
		potions[23] = null;
		potions[24] = "minecraft:weakness";
		potions[25] = "minecraft:strength";
		potions[26] = "minecraft:slowness";
		potions[27] = "minecraft:leaping";
		potions[28] = "minecraft:harming";
		potions[29] = "minecraft:water_breathing";
		potions[30] = "minecraft:invisibility";
		potions[31] = null;
		potions[32] = "minecraft:thick";
		potions[33] = "minecraft:strong_regeneration";
		potions[34] = "minecraft:strong_swiftness";
		potions[35] = "minecraft:fire_resistance";
		potions[36] = "minecraft:strong_poison";
		potions[37] = "minecraft:strong_healing";
		potions[38] = "minecraft:night_vision";
		potions[39] = null;
		potions[40] = "minecraft:weakness";
		potions[41] = "minecraft:strong_strength";
		potions[42] = "minecraft:slowness";
		potions[43] = "minecraft:strong_leaping";
		potions[44] = "minecraft:strong_harming";
		potions[45] = "minecraft:water_breathing";
		potions[46] = "minecraft:invisibility";
		potions[47] = null;
		potions[48] = null;
		potions[49] = "minecraft:strong_regeneration";
		potions[50] = "minecraft:strong_swiftness";
		potions[51] = "minecraft:fire_resistance";
		potions[52] = "minecraft:strong_poison";
		potions[53] = "minecraft:strong_healing";
		potions[54] = "minecraft:night_vision";
		potions[55] = null;
		potions[56] = "minecraft:weakness";
		potions[57] = "minecraft:strong_strength";
		potions[58] = "minecraft:slowness";
		potions[59] = "minecraft:strong_leaping";
		potions[60] = "minecraft:strong_harming";
		potions[61] = "minecraft:water_breathing";
		potions[62] = "minecraft:invisibility";
		potions[63] = null;
		potions[64] = "minecraft:mundane";
		potions[65] = "minecraft:long_regeneration";
		potions[66] = "minecraft:long_swiftness";
		potions[67] = "minecraft:long_fire_resistance";
		potions[68] = "minecraft:long_poison";
		potions[69] = "minecraft:healing";
		potions[70] = "minecraft:long_night_vision";
		potions[71] = null;
		potions[72] = "minecraft:long_weakness";
		potions[73] = "minecraft:long_strength";
		potions[74] = "minecraft:long_slowness";
		potions[75] = "minecraft:long_leaping";
		potions[76] = "minecraft:harming";
		potions[77] = "minecraft:long_water_breathing";
		potions[78] = "minecraft:long_invisibility";
		potions[79] = null;
		potions[80] = "minecraft:awkward";
		potions[81] = "minecraft:long_regeneration";
		potions[82] = "minecraft:long_swiftness";
		potions[83] = "minecraft:long_fire_resistance";
		potions[84] = "minecraft:long_poison";
		potions[85] = "minecraft:healing";
		potions[86] = "minecraft:long_night_vision";
		potions[87] = null;
		potions[88] = "minecraft:long_weakness";
		potions[89] = "minecraft:long_strength";
		potions[90] = "minecraft:long_slowness";
		potions[91] = "minecraft:long_leaping";
		potions[92] = "minecraft:harming";
		potions[93] = "minecraft:long_water_breathing";
		potions[94] = "minecraft:long_invisibility";
		potions[95] = null;
		potions[96] = "minecraft:thick";
		potions[97] = "minecraft:regeneration";
		potions[98] = "minecraft:swiftness";
		potions[99] = "minecraft:long_fire_resistance";
		potions[100] = "minecraft:poison";
		potions[101] = "minecraft:strong_healing";
		potions[102] = "minecraft:long_night_vision";
		potions[103] = null;
		potions[104] = "minecraft:long_weakness";
		potions[105] = "minecraft:strength";
		potions[106] = "minecraft:long_slowness";
		potions[107] = "minecraft:leaping";
		potions[108] = "minecraft:strong_harming";
		potions[109] = "minecraft:long_water_breathing";
		potions[110] = "minecraft:long_invisibility";
		potions[111] = null;
		potions[112] = null;
		potions[113] = "minecraft:regeneration";
		potions[114] = "minecraft:swiftness";
		potions[115] = "minecraft:long_fire_resistance";
		potions[116] = "minecraft:poison";
		potions[117] = "minecraft:strong_healing";
		potions[118] = "minecraft:long_night_vision";
		potions[119] = null;
		potions[120] = "minecraft:long_weakness";
		potions[121] = "minecraft:strength";
		potions[122] = "minecraft:long_slowness";
		potions[123] = "minecraft:leaping";
		potions[124] = "minecraft:strong_harming";
		potions[125] = "minecraft:long_water_breathing";
		potions[126] = "minecraft:long_invisibility";
		potions[127] = null;
	});
	public static final String WATER = "minecraft:water";

	public ItemPotionFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemPotionFix",
			type,
			itemStack -> {
				Optional<Pair<String, String>> optional = itemStack.getOptional(opticFinder);
				if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:potion")) {
					Dynamic<?> dynamic = itemStack.get(DSL.remainderFinder());
					Optional<? extends Typed<?>> optional2 = itemStack.getOptionalTyped(opticFinder2);
					short s = dynamic.get("Damage").asShort((short)0);
					if (optional2.isPresent()) {
						Typed<?> typed = itemStack;
						Dynamic<?> dynamic2 = ((Typed)optional2.get()).get(DSL.remainderFinder());
						Optional<String> optional3 = dynamic2.get("Potion").asString().result();
						if (optional3.isEmpty()) {
							String string = ID_TO_POTIONS[s & 127];
							Typed<?> typed2 = ((Typed)optional2.get())
								.set(DSL.remainderFinder(), dynamic2.set("Potion", dynamic2.createString(string == null ? "minecraft:water" : string)));
							typed = itemStack.set(opticFinder2, typed2);
							if ((s & 16384) == 16384) {
								typed = typed.set(opticFinder, Pair.of(TypeReferences.ITEM_NAME.typeName(), "minecraft:splash_potion"));
							}
						}

						if (s != 0) {
							dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
						}

						return typed.set(DSL.remainderFinder(), dynamic);
					}
				}

				return itemStack;
			}
		);
	}
}

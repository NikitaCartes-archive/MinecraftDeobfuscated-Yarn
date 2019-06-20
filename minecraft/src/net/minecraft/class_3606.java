package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_3606 extends DataFix {
	private static final int[][] field_15897 = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

	public class_3606(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_15719(Dynamic<?> dynamic, boolean bl, boolean bl2) {
		if ((bl || bl2) && !dynamic.get("Facing").asNumber().isPresent()) {
			int i;
			if (dynamic.get("Direction").asNumber().isPresent()) {
				i = dynamic.get("Direction").asByte((byte)0) % field_15897.length;
				int[] is = field_15897[i];
				dynamic = dynamic.set("TileX", dynamic.createInt(dynamic.get("TileX").asInt(0) + is[0]));
				dynamic = dynamic.set("TileY", dynamic.createInt(dynamic.get("TileY").asInt(0) + is[1]));
				dynamic = dynamic.set("TileZ", dynamic.createInt(dynamic.get("TileZ").asInt(0) + is[2]));
				dynamic = dynamic.remove("Direction");
				if (bl2 && dynamic.get("ItemRotation").asNumber().isPresent()) {
					dynamic = dynamic.set("ItemRotation", dynamic.createByte((byte)(dynamic.get("ItemRotation").asByte((byte)0) * 2)));
				}
			} else {
				i = dynamic.get("Dir").asByte((byte)0) % field_15897.length;
				dynamic = dynamic.remove("Dir");
			}

			dynamic = dynamic.set("Facing", dynamic.createByte((byte)i));
		}

		return dynamic;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(class_1208.field_5729, "Painting");
		OpticFinder<?> opticFinder = DSL.namedChoice("Painting", type);
		Type<?> type2 = this.getInputSchema().getChoiceType(class_1208.field_5729, "ItemFrame");
		OpticFinder<?> opticFinder2 = DSL.namedChoice("ItemFrame", type2);
		Type<?> type3 = this.getInputSchema().getType(class_1208.field_5729);
		TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped(
			"EntityPaintingFix",
			type3,
			typed -> typed.updateTyped(opticFinder, type, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> this.method_15719(dynamic, true, false)))
		);
		TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
			"EntityItemFrameFix",
			type3,
			typed -> typed.updateTyped(opticFinder2, type2, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> this.method_15719(dynamic, false, true)))
		);
		return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
	}
}

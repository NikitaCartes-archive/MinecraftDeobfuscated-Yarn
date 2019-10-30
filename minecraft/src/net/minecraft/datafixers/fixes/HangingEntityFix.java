package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;

public class HangingEntityFix extends DataFix {
	private static final int[][] OFFSETS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

	public HangingEntityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private Dynamic<?> fixDecorationPosition(Dynamic<?> tag, boolean isPainting, boolean isItemFrame) {
		if ((isPainting || isItemFrame) && !tag.get("Facing").asNumber().isPresent()) {
			int i;
			if (tag.get("Direction").asNumber().isPresent()) {
				i = tag.get("Direction").asByte((byte)0) % OFFSETS.length;
				int[] is = OFFSETS[i];
				tag = tag.set("TileX", tag.createInt(tag.get("TileX").asInt(0) + is[0]));
				tag = tag.set("TileY", tag.createInt(tag.get("TileY").asInt(0) + is[1]));
				tag = tag.set("TileZ", tag.createInt(tag.get("TileZ").asInt(0) + is[2]));
				tag = tag.remove("Direction");
				if (isItemFrame && tag.get("ItemRotation").asNumber().isPresent()) {
					tag = tag.set("ItemRotation", tag.createByte((byte)(tag.get("ItemRotation").asByte((byte)0) * 2)));
				}
			} else {
				i = tag.get("Dir").asByte((byte)0) % OFFSETS.length;
				tag = tag.remove("Dir");
			}

			tag = tag.set("Facing", tag.createByte((byte)i));
		}

		return tag;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "Painting");
		OpticFinder<?> opticFinder = DSL.namedChoice("Painting", type);
		Type<?> type2 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "ItemFrame");
		OpticFinder<?> opticFinder2 = DSL.namedChoice("ItemFrame", type2);
		Type<?> type3 = this.getInputSchema().getType(TypeReferences.ENTITY);
		TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped(
			"EntityPaintingFix",
			type3,
			typed -> typed.updateTyped(opticFinder, type, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> this.fixDecorationPosition(dynamic, true, false)))
		);
		TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
			"EntityItemFrameFix",
			type3,
			typed -> typed.updateTyped(opticFinder2, type2, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> this.fixDecorationPosition(dynamic, false, true)))
		);
		return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
	}
}

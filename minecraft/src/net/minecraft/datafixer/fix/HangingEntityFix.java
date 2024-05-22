package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class HangingEntityFix extends DataFix {
	private static final int[][] OFFSETS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

	public HangingEntityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private Dynamic<?> fixDecorationPosition(Dynamic<?> entityDynamic, boolean isPainting, boolean isItemFrame) {
		if ((isPainting || isItemFrame) && entityDynamic.get("Facing").asNumber().result().isEmpty()) {
			int i;
			if (entityDynamic.get("Direction").asNumber().result().isPresent()) {
				i = entityDynamic.get("Direction").asByte((byte)0) % OFFSETS.length;
				int[] is = OFFSETS[i];
				entityDynamic = entityDynamic.set("TileX", entityDynamic.createInt(entityDynamic.get("TileX").asInt(0) + is[0]));
				entityDynamic = entityDynamic.set("TileY", entityDynamic.createInt(entityDynamic.get("TileY").asInt(0) + is[1]));
				entityDynamic = entityDynamic.set("TileZ", entityDynamic.createInt(entityDynamic.get("TileZ").asInt(0) + is[2]));
				entityDynamic = entityDynamic.remove("Direction");
				if (isItemFrame && entityDynamic.get("ItemRotation").asNumber().result().isPresent()) {
					entityDynamic = entityDynamic.set("ItemRotation", entityDynamic.createByte((byte)(entityDynamic.get("ItemRotation").asByte((byte)0) * 2)));
				}
			} else {
				i = entityDynamic.get("Dir").asByte((byte)0) % OFFSETS.length;
				entityDynamic = entityDynamic.remove("Dir");
			}

			entityDynamic = entityDynamic.set("Facing", entityDynamic.createByte((byte)i));
		}

		return entityDynamic;
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
			entityTyped -> entityTyped.updateTyped(
					opticFinder,
					type,
					paintingTyped -> paintingTyped.update(DSL.remainderFinder(), paintingDynamic -> this.fixDecorationPosition(paintingDynamic, true, false))
				)
		);
		TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
			"EntityItemFrameFix",
			type3,
			entityTyped -> entityTyped.updateTyped(
					opticFinder2,
					type2,
					itemFrameTyped -> itemFrameTyped.update(DSL.remainderFinder(), itemFrameDynamic -> this.fixDecorationPosition(itemFrameDynamic, false, true))
				)
		);
		return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
	}
}

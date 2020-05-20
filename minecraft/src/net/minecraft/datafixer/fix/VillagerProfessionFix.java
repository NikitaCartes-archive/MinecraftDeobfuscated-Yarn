package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class VillagerProfessionFix extends ChoiceFix {
	public VillagerProfessionFix(Schema outputSchema, String entity) {
		super(outputSchema, false, "Villager profession data fix (" + entity + ")", TypeReferences.ENTITY, entity);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		Dynamic<?> dynamic = inputType.get(DSL.remainderFinder());
		return inputType.set(
			DSL.remainderFinder(),
			dynamic.remove("Profession")
				.remove("Career")
				.remove("CareerLevel")
				.set(
					"VillagerData",
					dynamic.createMap(
						ImmutableMap.of(
							dynamic.createString("type"),
							dynamic.createString("minecraft:plains"),
							dynamic.createString("profession"),
							dynamic.createString(convertProfessionId(dynamic.get("Profession").asInt(0), dynamic.get("Career").asInt(0))),
							dynamic.createString("level"),
							DataFixUtils.orElse(dynamic.get("CareerLevel").result(), dynamic.createInt(1))
						)
					)
				)
		);
	}

	private static String convertProfessionId(int professionId, int careerId) {
		if (professionId == 0) {
			if (careerId == 2) {
				return "minecraft:fisherman";
			} else if (careerId == 3) {
				return "minecraft:shepherd";
			} else {
				return careerId == 4 ? "minecraft:fletcher" : "minecraft:farmer";
			}
		} else if (professionId == 1) {
			return careerId == 2 ? "minecraft:cartographer" : "minecraft:librarian";
		} else if (professionId == 2) {
			return "minecraft:cleric";
		} else if (professionId == 3) {
			if (careerId == 2) {
				return "minecraft:weaponsmith";
			} else {
				return careerId == 3 ? "minecraft:toolsmith" : "minecraft:armorer";
			}
		} else if (professionId == 4) {
			return careerId == 2 ? "minecraft:leatherworker" : "minecraft:butcher";
		} else {
			return professionId == 5 ? "minecraft:nitwit" : "minecraft:none";
		}
	}
}

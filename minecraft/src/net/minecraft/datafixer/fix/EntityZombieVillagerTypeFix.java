package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;
import net.minecraft.datafixer.TypeReferences;

public class EntityZombieVillagerTypeFix extends ChoiceFix {
	private static final Random RANDOM = new Random();

	public EntityZombieVillagerTypeFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityZombieVillagerTypeFix", TypeReferences.ENTITY, "Zombie");
	}

	public Dynamic<?> fixZombieType(Dynamic<?> tag) {
		if (tag.get("IsVillager").asBoolean(false)) {
			if (!tag.get("ZombieType").get().isPresent()) {
				int i = this.clampType(tag.get("VillagerProfession").asInt(-1));
				if (i == -1) {
					i = this.clampType(RANDOM.nextInt(6));
				}

				tag = tag.set("ZombieType", tag.createInt(i));
			}

			tag = tag.remove("IsVillager");
		}

		return tag;
	}

	private int clampType(int type) {
		return type >= 0 && type < 6 ? type : -1;
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::fixZombieType);
	}
}

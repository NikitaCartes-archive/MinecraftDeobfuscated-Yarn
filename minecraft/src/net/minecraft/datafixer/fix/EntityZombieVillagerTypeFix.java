package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Random;
import net.minecraft.datafixer.TypeReferences;

public class EntityZombieVillagerTypeFix extends ChoiceFix {
	private static final Random RANDOM = new Random();

	public EntityZombieVillagerTypeFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityZombieVillagerTypeFix", TypeReferences.ENTITY, "Zombie");
	}

	public Dynamic<?> fixZombieType(Dynamic<?> dynamic) {
		if (dynamic.get("IsVillager").asBoolean(false)) {
			if (!dynamic.get("ZombieType").result().isPresent()) {
				int i = this.clampType(dynamic.get("VillagerProfession").asInt(-1));
				if (i == -1) {
					i = this.clampType(RANDOM.nextInt(6));
				}

				dynamic = dynamic.set("ZombieType", dynamic.createInt(i));
			}

			dynamic = dynamic.remove("IsVillager");
		}

		return dynamic;
	}

	private int clampType(int type) {
		return type >= 0 && type < 6 ? type : -1;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixZombieType);
	}
}

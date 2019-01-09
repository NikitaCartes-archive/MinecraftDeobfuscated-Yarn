package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;

public class class_1174 extends class_1197 {
	private static final Random field_5674 = new Random();

	public class_1174(Schema schema, boolean bl) {
		super(schema, bl, "EntityZombieVillagerTypeFix", class_1208.field_5729, "Zombie");
	}

	public Dynamic<?> method_4990(Dynamic<?> dynamic) {
		if (dynamic.get("IsVillager").asBoolean(false)) {
			if (!dynamic.get("ZombieType").get().isPresent()) {
				int i = this.method_4991(dynamic.get("VillagerProfession").asInt(-1));
				if (i == -1) {
					i = this.method_4991(field_5674.nextInt(6));
				}

				dynamic = dynamic.set("ZombieType", dynamic.createInt(i));
			}

			dynamic = dynamic.remove("IsVillager");
		}

		return dynamic;
	}

	private int method_4991(int i) {
		return i >= 0 && i < 6 ? i : -1;
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_4990);
	}
}

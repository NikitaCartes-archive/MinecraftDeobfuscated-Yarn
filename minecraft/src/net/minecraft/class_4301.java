package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;

public class class_4301 extends class_1197 {
	public class_4301(Schema schema, boolean bl) {
		super(schema, bl, "Zombie Villager XP rebuild", class_1208.field_5729, "minecraft:zombie_villager");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> {
			Optional<Number> optional = dynamic.get("Xp").asNumber();
			if (!optional.isPresent()) {
				int i = ((Number)dynamic.get("VillagerData").get("level").asNumber().orElse(1)).intValue();
				return dynamic.set("Xp", dynamic.createInt(class_4300.method_20482(i)));
			} else {
				return dynamic;
			}
		});
	}
}

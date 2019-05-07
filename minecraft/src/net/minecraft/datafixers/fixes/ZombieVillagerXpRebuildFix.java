package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class ZombieVillagerXpRebuildFix extends ChoiceFix {
	public ZombieVillagerXpRebuildFix(Schema schema, boolean bl) {
		super(schema, bl, "Zombie Villager XP rebuild", TypeReferences.ENTITY, "minecraft:zombie_villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> {
			Optional<Number> optional = dynamic.get("Xp").asNumber();
			if (!optional.isPresent()) {
				int i = ((Number)dynamic.get("VillagerData").get("level").asNumber().orElse(1)).intValue();
				return dynamic.set("Xp", dynamic.createInt(VillagerXpRebuildFix.levelToXp(i)));
			} else {
				return dynamic;
			}
		});
	}
}

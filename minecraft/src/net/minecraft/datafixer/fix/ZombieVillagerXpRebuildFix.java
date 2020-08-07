package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class ZombieVillagerXpRebuildFix extends ChoiceFix {
	public ZombieVillagerXpRebuildFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "Zombie Villager XP rebuild", TypeReferences.ENTITY, "minecraft:zombie_villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), dynamic -> {
			Optional<Number> optional = dynamic.get("Xp").asNumber().result();
			if (!optional.isPresent()) {
				int i = dynamic.get("VillagerData").get("level").asInt(1);
				return dynamic.set("Xp", dynamic.createInt(VillagerXpRebuildFix.levelToXp(i)));
			} else {
				return dynamic;
			}
		});
	}
}

package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityZombieSplitFix extends EntitySimpleTransformFix {
	public EntityZombieSplitFix(Schema outputSchema, boolean changesType) {
		super("EntityZombieSplitFix", outputSchema, changesType);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
		if (Objects.equals("Zombie", choice)) {
			String string = "Zombie";
			int i = tag.get("ZombieType").asInt(0);
			switch (i) {
				case 0:
				default:
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					string = "ZombieVillager";
					tag = tag.set("Profession", tag.createInt(i - 1));
					break;
				case 6:
					string = "Husk";
			}

			tag = tag.remove("ZombieType");
			return Pair.of(string, tag);
		} else {
			return Pair.of(choice, tag);
		}
	}
}

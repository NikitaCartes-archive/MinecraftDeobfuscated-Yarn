package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityZombieSplitFix extends EntitySimpleTransformFix {
	public EntityZombieSplitFix(Schema schema, boolean bl) {
		super("EntityZombieSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
		if (Objects.equals("Zombie", string)) {
			String string2 = "Zombie";
			int i = dynamic.getInt("ZombieType");
			switch (i) {
				case 0:
				default:
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					string2 = "ZombieVillager";
					dynamic = dynamic.set("Profession", dynamic.createInt(i - 1));
					break;
				case 6:
					string2 = "Husk";
			}

			dynamic = dynamic.remove("ZombieType");
			return Pair.of(string2, dynamic);
		} else {
			return Pair.of(string, dynamic);
		}
	}
}

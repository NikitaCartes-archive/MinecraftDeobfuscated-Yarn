package net.minecraft.datafixer.fix;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class EntityZombieSplitFix extends EntityTransformFix {
	private final Supplier<Type<?>> ZOMBIE_VILLAGER_TYPE = Suppliers.memoize(() -> this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, "ZombieVillager"));

	public EntityZombieSplitFix(Schema outputSchema) {
		super("EntityZombieSplitFix", outputSchema, true);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> entityTyped) {
		if (!choice.equals("Zombie")) {
			return Pair.of(choice, entityTyped);
		} else {
			Dynamic<?> dynamic = (Dynamic<?>)entityTyped.getOptional(DSL.remainderFinder()).orElseThrow();
			int i = dynamic.get("ZombieType").asInt(0);
			String string;
			Typed<?> typed;
			switch (i) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					string = "ZombieVillager";
					typed = this.setZombieVillagerProfession(entityTyped, i - 1);
					break;
				case 6:
					string = "Husk";
					typed = entityTyped;
					break;
				default:
					string = "Zombie";
					typed = entityTyped;
			}

			return Pair.of(string, typed.update(DSL.remainderFinder(), entityDynamic -> entityDynamic.remove("ZombieType")));
		}
	}

	private Typed<?> setZombieVillagerProfession(Typed<?> entityTyped, int variant) {
		return Util.apply(
			entityTyped,
			(Type)this.ZOMBIE_VILLAGER_TYPE.get(),
			zombieVillagerDynamic -> zombieVillagerDynamic.set("Profession", zombieVillagerDynamic.createInt(variant))
		);
	}
}

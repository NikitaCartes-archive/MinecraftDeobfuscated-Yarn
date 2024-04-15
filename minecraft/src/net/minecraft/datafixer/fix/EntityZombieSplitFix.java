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
	private final Supplier<Type<?>> field_51480 = Suppliers.memoize(() -> this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, "ZombieVillager"));

	public EntityZombieSplitFix(Schema schema) {
		super("EntityZombieSplitFix", schema, true);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
		if (!choice.equals("Zombie")) {
			return Pair.of(choice, typed);
		} else {
			Dynamic<?> dynamic = (Dynamic<?>)typed.getOptional(DSL.remainderFinder()).orElseThrow();
			int i = dynamic.get("ZombieType").asInt(0);
			String string;
			Typed<?> typed2;
			switch (i) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					string = "ZombieVillager";
					typed2 = this.method_59812(typed, i - 1);
					break;
				case 6:
					string = "Husk";
					typed2 = typed;
					break;
				default:
					string = "Zombie";
					typed2 = typed;
			}

			return Pair.of(string, typed2.update(DSL.remainderFinder(), dynamicx -> dynamicx.remove("ZombieType")));
		}
	}

	private Typed<?> method_59812(Typed<?> typed, int i) {
		return Util.apply(typed, (Type)this.field_51480.get(), dynamic -> dynamic.set("Profession", dynamic.createInt(i)));
	}
}

package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class EntityMinecartIdentifiersFix extends EntityTransformFix {
	public EntityMinecartIdentifiersFix(Schema schema) {
		super("EntityMinecartIdentifiersFix", schema, true);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
		if (!choice.equals("Minecart")) {
			return Pair.of(choice, typed);
		} else {
			int i = typed.getOrCreate(DSL.remainderFinder()).get("Type").asInt(0);

			String string = switch (i) {
				case 1 -> "MinecartChest";
				case 2 -> "MinecartFurnace";
				default -> "MinecartRideable";
			};
			Type<?> type = (Type<?>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
			return Pair.of(string, Util.apply(typed, type, dynamic -> dynamic.remove("Type")));
		}
	}
}

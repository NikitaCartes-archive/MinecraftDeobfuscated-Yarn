package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class class_7407 extends ChoiceFix {
	public class_7407(Schema schema) {
		super(schema, false, "EntityPaintingFieldsRenameFix", TypeReferences.ENTITY, "minecraft:painting");
	}

	public Dynamic<?> method_43386(Dynamic<?> dynamic) {
		return this.method_43387(this.method_43387(dynamic, "Motive", "variant"), "Facing", "facing");
	}

	private Dynamic<?> method_43387(Dynamic<?> dynamic, String string, String string2) {
		Optional<? extends Dynamic<?>> optional = dynamic.get(string).result();
		Optional<? extends Dynamic<?>> optional2 = optional.map(dynamic2 -> dynamic.remove(string).set(string2, dynamic2));
		return DataFixUtils.orElse(optional2, dynamic);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::method_43386);
	}
}

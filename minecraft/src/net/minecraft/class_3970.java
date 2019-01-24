package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class class_3970 extends DataFix {
	public class_3970(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.SAVED_DATA);
		OpticFinder<?> opticFinder = type.findField("data");
		return this.fixTypeEverywhereTyped("Map id fix", type, typed -> {
			Optional<? extends Typed<?>> optional = typed.getOptionalTyped(opticFinder);
			return optional.isPresent() ? typed : typed.update(DSL.remainderFinder(), dynamic -> dynamic.emptyMap().merge(dynamic.createString("data"), dynamic));
		});
	}
}

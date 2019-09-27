package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixers.TypeReferences;

public class PoiRebuildFix extends DataFix {
	public PoiRebuildFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
			throw new IllegalStateException("Poi type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("POI rebuild", type, dynamicOps -> pair -> pair.mapSecond(PoiRebuildFix::method_22376));
		}
	}

	private static <T> Dynamic<T> method_22376(Dynamic<T> dynamic) {
		return dynamic.update("Sections", dynamicx -> dynamicx.updateMapValues(pair -> pair.mapSecond(dynamicxx -> dynamicxx.remove("Valid"))));
	}
}

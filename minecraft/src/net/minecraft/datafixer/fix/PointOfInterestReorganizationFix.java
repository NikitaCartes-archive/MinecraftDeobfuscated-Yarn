package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class PointOfInterestReorganizationFix extends DataFix {
	public PointOfInterestReorganizationFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
			throw new IllegalStateException("Poi type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("POI reorganization", type, dynamicOps -> pair -> pair.mapSecond(PointOfInterestReorganizationFix::reorganize));
		}
	}

	private static <T> Dynamic<T> reorganize(Dynamic<T> tag) {
		Map<Dynamic<T>, Dynamic<T>> map = Maps.<Dynamic<T>, Dynamic<T>>newHashMap();

		for (int i = 0; i < 16; i++) {
			String string = String.valueOf(i);
			Optional<Dynamic<T>> optional = tag.get(string).get();
			if (optional.isPresent()) {
				Dynamic<T> dynamic = (Dynamic<T>)optional.get();
				Dynamic<T> dynamic2 = tag.createMap(ImmutableMap.of(tag.createString("Records"), dynamic));
				map.put(tag.createInt(i), dynamic2);
				tag = tag.remove(string);
			}
		}

		return tag.set("Sections", tag.createMap(map));
	}
}

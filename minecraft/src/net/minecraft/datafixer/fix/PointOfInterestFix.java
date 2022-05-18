package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public abstract class PointOfInterestFix extends DataFix {
	private final String name;

	public PointOfInterestFix(Schema schema, String name) {
		super(schema, false);
		this.name = name;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
			throw new IllegalStateException("Poi type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::fixSections));
		}
	}

	private <T> Dynamic<T> fixSections(Dynamic<T> dynamic) {
		return dynamic.update("Sections", dynamicx -> dynamicx.updateMapValues(pair -> pair.mapSecond(this::fixRecords)));
	}

	private Dynamic<?> fixRecords(Dynamic<?> dynamic) {
		return dynamic.update("Records", this::method_44188);
	}

	private <T> Dynamic<T> method_44188(Dynamic<T> dynamic) {
		return DataFixUtils.orElse(dynamic.asStreamOpt().result().map(stream -> dynamic.createList(this.method_44186(stream))), dynamic);
	}

	protected abstract <T> Stream<Dynamic<T>> method_44186(Stream<Dynamic<T>> stream);
}

package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public abstract class PointOfInterestRenameFIx extends DataFix {
	public PointOfInterestRenameFIx(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
			throw new IllegalStateException("Poi type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("POI rename", type, dynamicOps -> pair -> pair.mapSecond(this::method_23299));
		}
	}

	private <T> Dynamic<T> method_23299(Dynamic<T> dynamic) {
		return dynamic.update(
			"Sections",
			dynamicx -> dynamicx.updateMapValues(
					pair -> pair.mapSecond(dynamicxx -> dynamicxx.update("Records", dynamicxxx -> DataFixUtils.orElse(this.method_23304(dynamicxxx), dynamicxxx)))
				)
		);
	}

	private <T> Optional<Dynamic<T>> method_23304(Dynamic<T> dynamic) {
		return dynamic.asStreamOpt()
			.map(
				stream -> dynamic.createList(
						stream.map(
							dynamicxx -> dynamicxx.update(
									"type", dynamicxxx -> DataFixUtils.orElse(dynamicxxx.asString().map(this::rename).map(dynamicxxx::createString), dynamicxxx)
								)
						)
					)
			);
	}

	protected abstract String rename(String string);
}

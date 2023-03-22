package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;

public class RemoveFeatureTogglesFix extends DataFix {
	private final String name;
	private final Set<String> featureToggleIds;

	public RemoveFeatureTogglesFix(Schema outputSchema, String name, Set<String> featureToggleIds) {
		super(outputSchema, false);
		this.name = name;
		this.featureToggleIds = featureToggleIds;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			this.name, this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), this::removeFeatureToggles)
		);
	}

	private <T> Dynamic<T> removeFeatureToggles(Dynamic<T> dynamic) {
		List<Dynamic<T>> list = (List<Dynamic<T>>)dynamic.get("removed_features").asStream().collect(Collectors.toCollection(ArrayList::new));
		Dynamic<T> dynamic2 = dynamic.update(
			"enabled_features", enabledFeatures -> DataFixUtils.orElse(enabledFeatures.asStreamOpt().result().map(stream -> stream.filter(enabledFeature -> {
						Optional<String> optional = enabledFeature.asString().result();
						if (optional.isEmpty()) {
							return true;
						} else {
							boolean bl = this.featureToggleIds.contains(optional.get());
							if (bl) {
								list.add(dynamic.createString((String)optional.get()));
							}

							return !bl;
						}
					})).map(dynamic::createList), enabledFeatures)
		);
		if (!list.isEmpty()) {
			dynamic2 = dynamic2.set("removed_features", dynamic.createList(list.stream()));
		}

		return dynamic2;
	}
}

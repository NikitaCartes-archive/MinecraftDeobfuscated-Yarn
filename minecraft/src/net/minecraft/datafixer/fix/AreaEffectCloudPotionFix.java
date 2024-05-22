package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class AreaEffectCloudPotionFix extends ChoiceFix {
	public AreaEffectCloudPotionFix(Schema outputSchema) {
		super(outputSchema, false, "AreaEffectCloudPotionFix", TypeReferences.ENTITY, "minecraft:area_effect_cloud");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::update);
	}

	private <T> Dynamic<T> update(Dynamic<T> areaEffectCloudDynamic) {
		Optional<Dynamic<T>> optional = areaEffectCloudDynamic.get("Color").result();
		Optional<Dynamic<T>> optional2 = areaEffectCloudDynamic.get("effects").result();
		Optional<Dynamic<T>> optional3 = areaEffectCloudDynamic.get("Potion").result();
		areaEffectCloudDynamic = areaEffectCloudDynamic.remove("Color").remove("effects").remove("Potion");
		if (optional.isEmpty() && optional2.isEmpty() && optional3.isEmpty()) {
			return areaEffectCloudDynamic;
		} else {
			Dynamic<T> dynamic = areaEffectCloudDynamic.emptyMap();
			if (optional.isPresent()) {
				dynamic = dynamic.set("custom_color", (Dynamic<?>)optional.get());
			}

			if (optional2.isPresent()) {
				dynamic = dynamic.set("custom_effects", (Dynamic<?>)optional2.get());
			}

			if (optional3.isPresent()) {
				dynamic = dynamic.set("potion", (Dynamic<?>)optional3.get());
			}

			return areaEffectCloudDynamic.set("potion_contents", dynamic);
		}
	}
}

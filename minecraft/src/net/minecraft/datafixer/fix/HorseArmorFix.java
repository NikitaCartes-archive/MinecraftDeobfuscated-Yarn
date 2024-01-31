package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class HorseArmorFix extends ChoiceWriteReadFix {
	private final String oldNbtKey;

	public HorseArmorFix(Schema outputSchema, String entityId, String oldNbtKey) {
		super(outputSchema, true, "Horse armor fix for " + entityId, TypeReferences.ENTITY, entityId);
		this.oldNbtKey = oldNbtKey;
	}

	@Override
	protected <T> Dynamic<T> transform(Dynamic<T> data) {
		Optional<? extends Dynamic<?>> optional = data.get(this.oldNbtKey).result();
		if (optional.isPresent()) {
			Dynamic<?> dynamic = (Dynamic<?>)optional.get();
			Dynamic<T> dynamic2 = data.remove(this.oldNbtKey);
			dynamic2 = dynamic2.set("body_armor_item", dynamic);
			return dynamic2.set("body_armor_drop_chance", data.createFloat(2.0F));
		} else {
			return data;
		}
	}
}

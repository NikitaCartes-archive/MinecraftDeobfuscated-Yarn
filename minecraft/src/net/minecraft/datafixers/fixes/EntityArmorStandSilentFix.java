package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class EntityArmorStandSilentFix extends ChoiceFix {
	public EntityArmorStandSilentFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
	}

	public Dynamic<?> fixSilent(Dynamic<?> tag) {
		return tag.get("Silent").asBoolean(false) && !tag.get("Marker").asBoolean(false) ? tag.remove("Silent") : tag;
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::fixSilent);
	}
}

package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class EntityArmorStandSilentFix extends ChoiceFix {
	public EntityArmorStandSilentFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
	}

	public Dynamic<?> fixSilent(Dynamic<?> tag) {
		return tag.get("Silent").asBoolean(false) && !tag.get("Marker").asBoolean(false) ? tag.remove("Silent") : tag;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixSilent);
	}
}

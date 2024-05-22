package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class EntityPaintingMotiveFix extends ChoiceFix {
	private static final Map<String, String> RENAMED_MOTIVES = DataFixUtils.make(Maps.<String, String>newHashMap(), map -> {
		map.put("donkeykong", "donkey_kong");
		map.put("burningskull", "burning_skull");
		map.put("skullandroses", "skull_and_roses");
	});

	public EntityPaintingMotiveFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityPaintingMotiveFix", TypeReferences.ENTITY, "minecraft:painting");
	}

	public Dynamic<?> renameMotive(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("Motive").asString().result();
		if (optional.isPresent()) {
			String string = ((String)optional.get()).toLowerCase(Locale.ROOT);
			return dynamic.set("Motive", dynamic.createString(IdentifierNormalizingSchema.normalize((String)RENAMED_MOTIVES.getOrDefault(string, string))));
		} else {
			return dynamic;
		}
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::renameMotive);
	}
}

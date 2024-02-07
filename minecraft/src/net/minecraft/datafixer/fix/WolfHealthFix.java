package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WolfHealthFix extends ChoiceFix {
	private static final String WOLF_ENTITY_ID = "minecraft:wolf";
	private static final Identifier MAX_HEALTH_ATTRIBUTE_ID = new Identifier("generic.max_health");

	public WolfHealthFix(Schema outputSchema) {
		super(outputSchema, false, "FixWolfHealth", TypeReferences.ENTITY, "minecraft:wolf");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(),
			dynamic -> {
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				dynamic = dynamic.update(
					"Attributes",
					dynamicx -> dynamicx.createList(
							dynamicx.asStream()
								.map(
									dynamicxx -> Objects.equals(Identifier.tryParse(dynamicxx.get("Name").asString("")), MAX_HEALTH_ATTRIBUTE_ID)
											? dynamicxx.update("Base", dynamicxxx -> {
												if (dynamicxxx.asDouble(0.0) == 20.0) {
													mutableBoolean.setTrue();
													return dynamicxxx.createDouble(40.0);
												} else {
													return dynamicxxx;
												}
											})
											: dynamicxx
								)
						)
				);
				if (mutableBoolean.isTrue()) {
					dynamic = dynamic.update("Health", dynamicx -> dynamicx.createFloat(dynamicx.asFloat(0.0F) * 2.0F));
				}

				return dynamic;
			}
		);
	}
}

package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import org.slf4j.Logger;

public class PersistentStateUuidFix extends AbstractUuidFix {
	private static final Logger LOGGER = LogUtils.getLogger();

	public PersistentStateUuidFix(Schema outputSchema) {
		super(outputSchema, TypeReferences.SAVED_DATA);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"SavedDataUUIDFix",
			this.getInputSchema().getType(this.typeReference),
			typed -> typed.updateTyped(
					typed.getType().findField("data"),
					typedx -> typedx.update(
							DSL.remainderFinder(),
							dynamic -> dynamic.update(
									"Raids",
									dynamicx -> dynamicx.createList(
											dynamicx.asStream()
												.map(
													dynamicxx -> dynamicxx.update(
															"HeroesOfTheVillage",
															dynamicxxx -> dynamicxxx.createList(
																	dynamicxxx.asStream().map(dynamicxxxx -> (Dynamic)createArrayFromMostLeastTags(dynamicxxxx, "UUIDMost", "UUIDLeast").orElseGet(() -> {
																			LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
																			return dynamicxxxx;
																		}))
																)
														)
												)
										)
								)
						)
				)
		);
	}
}

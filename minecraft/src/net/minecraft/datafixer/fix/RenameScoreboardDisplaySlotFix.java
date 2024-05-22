package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;

public class RenameScoreboardDisplaySlotFix extends DataFix {
	private static final Map<String, String> OLD_TO_NEW_SLOT_NAMES = ImmutableMap.<String, String>builder()
		.put("slot_0", "list")
		.put("slot_1", "sidebar")
		.put("slot_2", "below_name")
		.put("slot_3", "sidebar.team.black")
		.put("slot_4", "sidebar.team.dark_blue")
		.put("slot_5", "sidebar.team.dark_green")
		.put("slot_6", "sidebar.team.dark_aqua")
		.put("slot_7", "sidebar.team.dark_red")
		.put("slot_8", "sidebar.team.dark_purple")
		.put("slot_9", "sidebar.team.gold")
		.put("slot_10", "sidebar.team.gray")
		.put("slot_11", "sidebar.team.dark_gray")
		.put("slot_12", "sidebar.team.blue")
		.put("slot_13", "sidebar.team.green")
		.put("slot_14", "sidebar.team.aqua")
		.put("slot_15", "sidebar.team.red")
		.put("slot_16", "sidebar.team.light_purple")
		.put("slot_17", "sidebar.team.yellow")
		.put("slot_18", "sidebar.team.white")
		.build();

	public RenameScoreboardDisplaySlotFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Nullable
	private static String getUpdatedName(String oldName) {
		return (String)OLD_TO_NEW_SLOT_NAMES.get(oldName);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.SAVED_DATA_SCOREBOARD);
		OpticFinder<?> opticFinder = type.findField("data");
		return this.fixTypeEverywhereTyped(
			"Scoreboard DisplaySlot rename",
			type,
			scoreboardTyped -> scoreboardTyped.updateTyped(
					opticFinder,
					scoreboardDataTyped -> scoreboardDataTyped.update(
							DSL.remainderFinder(),
							scoreboardDataDynamic -> scoreboardDataDynamic.update(
									"DisplaySlots",
									displaySlotsDynamic -> displaySlotsDynamic.updateMapValues(
											entry -> entry.mapFirst(
													key -> DataFixUtils.orElse(key.asString().result().map(RenameScoreboardDisplaySlotFix::getUpdatedName).map(key::createString), key)
												)
										)
								)
						)
				)
		);
	}
}

package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class JigsawRotationFix extends DataFix {
	private static final Map<String, String> ORIENTATION_UPDATES = ImmutableMap.<String, String>builder()
		.put("down", "down_south")
		.put("up", "up_north")
		.put("north", "north_up")
		.put("south", "south_up")
		.put("west", "west_up")
		.put("east", "east_up")
		.build();

	public JigsawRotationFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private static Dynamic<?> updateBlockState(Dynamic<?> blockStateData) {
		Optional<String> optional = blockStateData.get("Name").asString();
		return optional.equals(Optional.of("minecraft:jigsaw")) ? blockStateData.update("Properties", dynamic -> {
			String string = dynamic.get("facing").asString("north");
			return dynamic.remove("facing").set("orientation", dynamic.createString((String)ORIENTATION_UPDATES.getOrDefault(string, string)));
		}) : blockStateData;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"jigsaw_rotation_fix",
			this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
			typed -> typed.update(DSL.remainderFinder(), JigsawRotationFix::updateBlockState)
		);
	}
}

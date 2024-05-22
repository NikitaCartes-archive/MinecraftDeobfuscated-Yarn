package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
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

	public JigsawRotationFix(Schema outputSchema, boolean changesTyped) {
		super(outputSchema, changesTyped);
	}

	private static Dynamic<?> updateBlockState(Dynamic<?> blockStateDynamic) {
		Optional<String> optional = blockStateDynamic.get("Name").asString().result();
		return optional.equals(Optional.of("minecraft:jigsaw")) ? blockStateDynamic.update("Properties", propertiesDynamic -> {
			String string = propertiesDynamic.get("facing").asString("north");
			return propertiesDynamic.remove("facing").set("orientation", propertiesDynamic.createString((String)ORIENTATION_UPDATES.getOrDefault(string, string)));
		}) : blockStateDynamic;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"jigsaw_rotation_fix",
			this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
			blockStateTyped -> blockStateTyped.update(DSL.remainderFinder(), JigsawRotationFix::updateBlockState)
		);
	}
}

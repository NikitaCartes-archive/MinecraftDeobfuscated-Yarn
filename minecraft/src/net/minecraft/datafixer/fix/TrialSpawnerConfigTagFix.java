package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class TrialSpawnerConfigTagFix extends ChoiceWriteReadFix {
	public TrialSpawnerConfigTagFix(Schema outputSchema) {
		super(outputSchema, true, "Trial Spawner config tag fixer", TypeReferences.BLOCK_ENTITY, "minecraft:trial_spawner");
	}

	private static <T> Dynamic<T> fix(Dynamic<T> data) {
		List<String> list = List.of(
			"spawn_range",
			"total_mobs",
			"simultaneous_mobs",
			"total_mobs_added_per_player",
			"simultaneous_mobs_added_per_player",
			"ticks_between_spawn",
			"spawn_potentials",
			"loot_tables_to_eject",
			"items_to_drop_when_ominous"
		);
		Map<Dynamic<T>, Dynamic<T>> map = new HashMap(list.size());

		for (String string : list) {
			Optional<Dynamic<T>> optional = data.get(string).get().result();
			if (optional.isPresent()) {
				map.put(data.createString(string), (Dynamic)optional.get());
				data = data.remove(string);
			}
		}

		return map.isEmpty() ? data : data.set("normal_config", data.createMap(map));
	}

	@Override
	protected <T> Dynamic<T> transform(Dynamic<T> data) {
		return fix(data);
	}
}

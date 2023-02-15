package net.minecraft.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class OneTwentyLootTables {
	private static final Set<Identifier> LOOT_TABLES = Sets.<Identifier>newHashSet();
	private static final Set<Identifier> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
	public static final Identifier DESERT_WELL_ARCHAEOLOGY = register("archaeology/desert_well");
	public static final Identifier DESERT_PYRAMID_ARCHAEOLOGY = register("archaeology/desert_pyramid");

	private static Identifier register(String id) {
		return registerLootTable(new Identifier(id));
	}

	private static Identifier registerLootTable(Identifier id) {
		if (LOOT_TABLES.add(id)) {
			return id;
		} else {
			throw new IllegalArgumentException(id + " is already a registered built-in loot table");
		}
	}

	public static Set<Identifier> getAll() {
		return LOOT_TABLES_READ_ONLY;
	}
}

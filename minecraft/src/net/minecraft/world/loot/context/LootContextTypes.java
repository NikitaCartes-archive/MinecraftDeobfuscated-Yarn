package net.minecraft.world.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class LootContextTypes {
	private static final BiMap<Identifier, LootContextType> MAP = HashBiMap.create();
	public static final LootContextType EMPTY = register("empty", builder -> {
	});
	public static final LootContextType CHEST = register("chest", builder -> builder.require(Parameters.field_1232).allow(Parameters.field_1226));
	public static final LootContextType FISHING = register("fishing", builder -> builder.require(Parameters.field_1232).require(Parameters.field_1229));
	public static final LootContextType ENTITY = register(
		"entity",
		builder -> builder.require(Parameters.field_1226)
				.require(Parameters.field_1232)
				.require(Parameters.field_1231)
				.allow(Parameters.field_1230)
				.allow(Parameters.field_1227)
				.allow(Parameters.field_1233)
	);
	public static final LootContextType GIFT = register("gift", builder -> builder.require(Parameters.field_1232).require(Parameters.field_1226));
	public static final LootContextType ADVANCEMENT_REWARD = register(
		"advancement_reward", builder -> builder.require(Parameters.field_1226).require(Parameters.field_1232)
	);
	public static final LootContextType GENERIC = register(
		"generic",
		builder -> builder.require(Parameters.field_1226)
				.require(Parameters.field_1233)
				.require(Parameters.field_1231)
				.require(Parameters.field_1230)
				.require(Parameters.field_1227)
				.require(Parameters.field_1232)
				.require(Parameters.field_1224)
				.require(Parameters.field_1228)
				.require(Parameters.field_1229)
				.require(Parameters.field_1225)
	);
	public static final LootContextType BLOCK = register(
		"block",
		builder -> builder.require(Parameters.field_1224)
				.require(Parameters.field_1232)
				.require(Parameters.field_1229)
				.allow(Parameters.field_1226)
				.allow(Parameters.field_1228)
				.allow(Parameters.field_1225)
	);

	private static LootContextType register(String string, Consumer<LootContextType.Builder> consumer) {
		LootContextType.Builder builder = new LootContextType.Builder();
		consumer.accept(builder);
		LootContextType lootContextType = builder.build();
		Identifier identifier = new Identifier(string);
		LootContextType lootContextType2 = MAP.put(identifier, lootContextType);
		if (lootContextType2 != null) {
			throw new IllegalStateException("Loot table parameter set " + identifier + " is already registered");
		} else {
			return lootContextType;
		}
	}

	@Nullable
	public static LootContextType get(Identifier identifier) {
		return (LootContextType)MAP.get(identifier);
	}

	@Nullable
	public static Identifier getId(LootContextType lootContextType) {
		return (Identifier)MAP.inverse().get(lootContextType);
	}
}

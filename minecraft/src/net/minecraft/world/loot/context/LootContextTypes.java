package net.minecraft.world.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class LootContextTypes {
	private static final BiMap<Identifier, LootContextType> MAP = HashBiMap.create();
	public static final LootContextType field_1175 = register("empty", builder -> {
	});
	public static final LootContextType field_1179 = register(
		"chest", builder -> builder.require(LootContextParameters.field_1232).allow(LootContextParameters.field_1226)
	);
	public static final LootContextType field_1176 = register(
		"fishing", builder -> builder.require(LootContextParameters.field_1232).require(LootContextParameters.field_1229)
	);
	public static final LootContextType field_1173 = register(
		"entity",
		builder -> builder.require(LootContextParameters.field_1226)
				.require(LootContextParameters.field_1232)
				.require(LootContextParameters.field_1231)
				.allow(LootContextParameters.field_1230)
				.allow(LootContextParameters.field_1227)
				.allow(LootContextParameters.field_1233)
	);
	public static final LootContextType field_16235 = register(
		"gift", builder -> builder.require(LootContextParameters.field_1232).require(LootContextParameters.field_1226)
	);
	public static final LootContextType field_1174 = register(
		"advancement_reward", builder -> builder.require(LootContextParameters.field_1226).require(LootContextParameters.field_1232)
	);
	public static final LootContextType field_1177 = register(
		"generic",
		builder -> builder.require(LootContextParameters.field_1226)
				.require(LootContextParameters.field_1233)
				.require(LootContextParameters.field_1231)
				.require(LootContextParameters.field_1230)
				.require(LootContextParameters.field_1227)
				.require(LootContextParameters.field_1232)
				.require(LootContextParameters.field_1224)
				.require(LootContextParameters.field_1228)
				.require(LootContextParameters.field_1229)
				.require(LootContextParameters.field_1225)
	);
	public static final LootContextType field_1172 = register(
		"block",
		builder -> builder.require(LootContextParameters.field_1224)
				.require(LootContextParameters.field_1232)
				.require(LootContextParameters.field_1229)
				.allow(LootContextParameters.field_1226)
				.allow(LootContextParameters.field_1228)
				.allow(LootContextParameters.field_1225)
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

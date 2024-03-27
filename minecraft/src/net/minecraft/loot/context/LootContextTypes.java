package net.minecraft.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;

public class LootContextTypes {
	private static final BiMap<Identifier, LootContextType> MAP = HashBiMap.create();
	public static final Codec<LootContextType> CODEC = Identifier.CODEC
		.comapFlatMap(
			id -> (DataResult)Optional.ofNullable((LootContextType)MAP.get(id))
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + id + "'")),
			MAP.inverse()::get
		);
	public static final LootContextType EMPTY = register("empty", builder -> {
	});
	public static final LootContextType CHEST = register(
		"chest", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType COMMAND = register(
		"command", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType SELECTOR = register(
		"selector", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType FISHING = register(
		"fishing", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType ENTITY = register(
		"entity",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.allow(LootContextParameters.KILLER_ENTITY)
				.allow(LootContextParameters.DIRECT_KILLER_ENTITY)
				.allow(LootContextParameters.LAST_DAMAGE_PLAYER)
	);
	public static final LootContextType EQUIPMENT = register(
		"equipment", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType ARCHAEOLOGY = register(
		"archaeology", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType GIFT = register(
		"gift", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType BARTER = register("barter", builder -> builder.require(LootContextParameters.THIS_ENTITY));
	public static final LootContextType VAULT = register(
		"vault", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType ADVANCEMENT_REWARD = register(
		"advancement_reward", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN)
	);
	public static final LootContextType ADVANCEMENT_ENTITY = register(
		"advancement_entity", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN)
	);
	public static final LootContextType ADVANCEMENT_LOCATION = register(
		"advancement_location",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.TOOL)
				.require(LootContextParameters.BLOCK_STATE)
	);
	public static final LootContextType BLOCK_USE = register(
		"block_use", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE)
	);
	public static final LootContextType GENERIC = register(
		"generic",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.LAST_DAMAGE_PLAYER)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.require(LootContextParameters.KILLER_ENTITY)
				.require(LootContextParameters.DIRECT_KILLER_ENTITY)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.BLOCK_STATE)
				.require(LootContextParameters.BLOCK_ENTITY)
				.require(LootContextParameters.TOOL)
				.require(LootContextParameters.EXPLOSION_RADIUS)
	);
	public static final LootContextType BLOCK = register(
		"block",
		builder -> builder.require(LootContextParameters.BLOCK_STATE)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.TOOL)
				.allow(LootContextParameters.THIS_ENTITY)
				.allow(LootContextParameters.BLOCK_ENTITY)
				.allow(LootContextParameters.EXPLOSION_RADIUS)
	);
	public static final LootContextType SHEARING = register(
		"shearing", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY)
	);

	private static LootContextType register(String name, Consumer<LootContextType.Builder> type) {
		LootContextType.Builder builder = new LootContextType.Builder();
		type.accept(builder);
		LootContextType lootContextType = builder.build();
		Identifier identifier = new Identifier(name);
		LootContextType lootContextType2 = MAP.put(identifier, lootContextType);
		if (lootContextType2 != null) {
			throw new IllegalStateException("Loot table parameter set " + identifier + " is already registered");
		} else {
			return lootContextType;
		}
	}
}

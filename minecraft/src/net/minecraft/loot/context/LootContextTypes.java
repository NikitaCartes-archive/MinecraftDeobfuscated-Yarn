package net.minecraft.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextType;

public class LootContextTypes {
	private static final BiMap<Identifier, ContextType> MAP = HashBiMap.create();
	public static final Codec<ContextType> CODEC = Identifier.CODEC
		.comapFlatMap(
			id -> (DataResult)Optional.ofNullable((ContextType)MAP.get(id))
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + id + "'")),
			MAP.inverse()::get
		);
	public static final ContextType EMPTY = register("empty", builder -> {
	});
	public static final ContextType CHEST = register("chest", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY));
	public static final ContextType COMMAND = register(
		"command", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final ContextType SELECTOR = register(
		"selector", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY)
	);
	public static final ContextType FISHING = register(
		"fishing", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final ContextType ENTITY = register(
		"entity",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.allow(LootContextParameters.ATTACKING_ENTITY)
				.allow(LootContextParameters.DIRECT_ATTACKING_ENTITY)
				.allow(LootContextParameters.LAST_DAMAGE_PLAYER)
	);
	public static final ContextType EQUIPMENT = register(
		"equipment", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY)
	);
	public static final ContextType ARCHAEOLOGY = register(
		"archaeology", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.TOOL)
	);
	public static final ContextType GIFT = register("gift", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY));
	public static final ContextType BARTER = register("barter", builder -> builder.require(LootContextParameters.THIS_ENTITY));
	public static final ContextType VAULT = register(
		"vault", builder -> builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.TOOL)
	);
	public static final ContextType ADVANCEMENT_REWARD = register(
		"advancement_reward", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN)
	);
	public static final ContextType ADVANCEMENT_ENTITY = register(
		"advancement_entity", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN)
	);
	public static final ContextType ADVANCEMENT_LOCATION = register(
		"advancement_location",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.TOOL)
				.require(LootContextParameters.BLOCK_STATE)
	);
	public static final ContextType BLOCK_USE = register(
		"block_use", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE)
	);
	public static final ContextType GENERIC = register(
		"generic",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.LAST_DAMAGE_PLAYER)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.require(LootContextParameters.ATTACKING_ENTITY)
				.require(LootContextParameters.DIRECT_ATTACKING_ENTITY)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.BLOCK_STATE)
				.require(LootContextParameters.BLOCK_ENTITY)
				.require(LootContextParameters.TOOL)
				.require(LootContextParameters.EXPLOSION_RADIUS)
	);
	public static final ContextType BLOCK = register(
		"block",
		builder -> builder.require(LootContextParameters.BLOCK_STATE)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.TOOL)
				.allow(LootContextParameters.THIS_ENTITY)
				.allow(LootContextParameters.BLOCK_ENTITY)
				.allow(LootContextParameters.EXPLOSION_RADIUS)
	);
	public static final ContextType SHEARING = register(
		"shearing", builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.TOOL)
	);
	public static final ContextType ENCHANTED_DAMAGE = register(
		"enchanted_damage",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ENCHANTMENT_LEVEL)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.allow(LootContextParameters.DIRECT_ATTACKING_ENTITY)
				.allow(LootContextParameters.ATTACKING_ENTITY)
	);
	public static final ContextType ENCHANTED_ITEM = register(
		"enchanted_item", builder -> builder.require(LootContextParameters.TOOL).require(LootContextParameters.ENCHANTMENT_LEVEL)
	);
	public static final ContextType ENCHANTED_LOCATION = register(
		"enchanted_location",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ENCHANTMENT_LEVEL)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.ENCHANTMENT_ACTIVE)
	);
	public static final ContextType ENCHANTED_ENTITY = register(
		"enchanted_entity",
		builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN)
	);
	public static final ContextType HIT_BLOCK = register(
		"hit_block",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.ENCHANTMENT_LEVEL)
				.require(LootContextParameters.ORIGIN)
				.require(LootContextParameters.BLOCK_STATE)
	);

	private static ContextType register(String name, Consumer<ContextType.Builder> type) {
		ContextType.Builder builder = new ContextType.Builder();
		type.accept(builder);
		ContextType contextType = builder.build();
		Identifier identifier = Identifier.ofVanilla(name);
		ContextType contextType2 = MAP.put(identifier, contextType);
		if (contextType2 != null) {
			throw new IllegalStateException("Loot table parameter set " + identifier + " is already registered");
		} else {
			return contextType;
		}
	}
}

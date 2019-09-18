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
	public static final LootContextType CHEST = register(
		"chest", builder -> builder.require(LootContextParameters.POSITION).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType COMMAND = register(
		"command", builder -> builder.require(LootContextParameters.POSITION).allow(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType SELECTOR = register(
		"selector", builder -> builder.require(LootContextParameters.POSITION).require(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType FISHING = register(
		"fishing", builder -> builder.require(LootContextParameters.POSITION).require(LootContextParameters.TOOL)
	);
	public static final LootContextType ENTITY = register(
		"entity",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.POSITION)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.allow(LootContextParameters.KILLER_ENTITY)
				.allow(LootContextParameters.DIRECT_KILLER_ENTITY)
				.allow(LootContextParameters.LAST_DAMAGE_PLAYER)
	);
	public static final LootContextType GIFT = register(
		"gift", builder -> builder.require(LootContextParameters.POSITION).require(LootContextParameters.THIS_ENTITY)
	);
	public static final LootContextType ADVANCEMENT_REWARD = register(
		"advancement_reward", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.POSITION)
	);
	public static final LootContextType GENERIC = register(
		"generic",
		builder -> builder.require(LootContextParameters.THIS_ENTITY)
				.require(LootContextParameters.LAST_DAMAGE_PLAYER)
				.require(LootContextParameters.DAMAGE_SOURCE)
				.require(LootContextParameters.KILLER_ENTITY)
				.require(LootContextParameters.DIRECT_KILLER_ENTITY)
				.require(LootContextParameters.POSITION)
				.require(LootContextParameters.BLOCK_STATE)
				.require(LootContextParameters.BLOCK_ENTITY)
				.require(LootContextParameters.TOOL)
				.require(LootContextParameters.EXPLOSION_RADIUS)
	);
	public static final LootContextType BLOCK = register(
		"block",
		builder -> builder.require(LootContextParameters.BLOCK_STATE)
				.require(LootContextParameters.POSITION)
				.require(LootContextParameters.TOOL)
				.allow(LootContextParameters.THIS_ENTITY)
				.allow(LootContextParameters.BLOCK_ENTITY)
				.allow(LootContextParameters.EXPLOSION_RADIUS)
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

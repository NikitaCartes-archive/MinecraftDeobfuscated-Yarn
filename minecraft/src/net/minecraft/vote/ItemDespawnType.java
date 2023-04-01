package net.minecraft.vote;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum ItemDespawnType implements StringIdentifiable {
	DESPAWN_ALL("despawn_all"),
	KEEP_PLAYER_DROPS("keep_player_drops"),
	DESPAWN_NONE("despawn_none");

	private final String id;
	private final Text name;
	public static final com.mojang.serialization.Codec<ItemDespawnType> codec = StringIdentifiable.createCodec(ItemDespawnType::values);

	private ItemDespawnType(String id) {
		this.id = id;
		this.name = Text.translatable("rule.item_despawn." + id);
	}

	@Override
	public String asString() {
		return this.id;
	}

	public Text getName() {
		return this.name;
	}
}

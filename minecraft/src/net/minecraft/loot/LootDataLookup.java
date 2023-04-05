package net.minecraft.loot;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootDataLookup {
	@Nullable
	<T> T getElement(LootDataKey<T> key);

	@Nullable
	default <T> T getElement(LootDataType<T> type, Identifier id) {
		return this.getElement(new LootDataKey<>(type, id));
	}

	default <T> Optional<T> getElementOptional(LootDataKey<T> key) {
		return Optional.ofNullable(this.getElement(key));
	}

	default <T> Optional<T> getElementOptional(LootDataType<T> type, Identifier id) {
		return this.getElementOptional(new LootDataKey<>(type, id));
	}

	default LootTable getLootTable(Identifier id) {
		return (LootTable)this.getElementOptional(LootDataType.LOOT_TABLES, id).orElse(LootTable.EMPTY);
	}
}

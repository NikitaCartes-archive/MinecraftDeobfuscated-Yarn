package net.minecraft.loot.provider.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class StorageLootNbtProvider implements LootNbtProvider {
	private final Identifier source;

	private StorageLootNbtProvider(Identifier source) {
		this.source = source;
	}

	@Override
	public LootNbtProviderType getType() {
		return LootNbtProviderTypes.STORAGE;
	}

	@Nullable
	@Override
	public Tag method_32440(LootContext lootContext) {
		return lootContext.getWorld().getServer().getDataCommandStorage().get(this.source);
	}

	@Override
	public Set<LootContextParameter<?>> method_32441() {
		return ImmutableSet.of();
	}

	public static class Serializer implements JsonSerializer<StorageLootNbtProvider> {
		public void toJson(JsonObject jsonObject, StorageLootNbtProvider storageLootNbtProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("source", storageLootNbtProvider.source.toString());
		}

		public StorageLootNbtProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			String string = JsonHelper.getString(jsonObject, "source");
			return new StorageLootNbtProvider(new Identifier(string));
		}
	}
}

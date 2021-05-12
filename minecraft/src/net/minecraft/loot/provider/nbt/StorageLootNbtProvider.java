package net.minecraft.loot.provider.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class StorageLootNbtProvider implements LootNbtProvider {
	final Identifier source;

	StorageLootNbtProvider(Identifier identifier) {
		this.source = identifier;
	}

	@Override
	public LootNbtProviderType getType() {
		return LootNbtProviderTypes.STORAGE;
	}

	@Nullable
	@Override
	public NbtElement getNbtTag(LootContext context) {
		return context.getWorld().getServer().getDataCommandStorage().get(this.source);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
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

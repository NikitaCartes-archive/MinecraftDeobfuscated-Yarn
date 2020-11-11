/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import org.jetbrains.annotations.Nullable;

public class StorageLootNbtProvider
implements LootNbtProvider {
    private final Identifier source;

    private StorageLootNbtProvider(Identifier source) {
        this.source = source;
    }

    @Override
    public LootNbtProviderType getType() {
        return LootNbtProviderTypes.STORAGE;
    }

    @Override
    @Nullable
    public Tag method_32440(LootContext lootContext) {
        return lootContext.getWorld().getServer().getDataCommandStorage().get(this.source);
    }

    @Override
    public Set<LootContextParameter<?>> method_32441() {
        return ImmutableSet.of();
    }

    public static class Serializer
    implements JsonSerializer<StorageLootNbtProvider> {
        @Override
        public void toJson(JsonObject jsonObject, StorageLootNbtProvider storageLootNbtProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("source", storageLootNbtProvider.source.toString());
        }

        @Override
        public StorageLootNbtProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            String string = JsonHelper.getString(jsonObject, "source");
            return new StorageLootNbtProvider(new Identifier(string));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLoader;
import org.slf4j.Logger;

public class WorldgenProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataOutput field_40665;

    public WorldgenProvider(DataOutput dataOutput) {
        this.field_40665 = dataOutput;
    }

    @Override
    public void run(DataWriter writer) {
        DynamicRegistryManager.Immutable dynamicRegistryManager = BuiltinRegistries.createBuiltinRegistryManager();
        RegistryOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
        RegistryLoader.DYNAMIC_REGISTRIES.forEach(info -> this.writeRegistryEntries(writer, dynamicRegistryManager, (DynamicOps<JsonElement>)dynamicOps, (RegistryLoader.Entry)info));
    }

    private <T> void writeRegistryEntries(DataWriter writer, DynamicRegistryManager registryManager, DynamicOps<JsonElement> ops, RegistryLoader.Entry<T> registry) {
        RegistryKey<Registry<T>> registryKey = registry.key();
        Registry<T> registry2 = registryManager.get(registryKey);
        DataOutput.PathResolver pathResolver = this.field_40665.getResolver(DataOutput.OutputType.DATA_PACK, registryKey.getValue().getPath());
        for (Map.Entry<RegistryKey<T>, T> entry : registry2.getEntrySet()) {
            WorldgenProvider.writeToPath(pathResolver.resolveJson(entry.getKey().getValue()), writer, ops, registry.elementCodec(), entry.getValue());
        }
    }

    private static <E> void writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
        try {
            Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", (Object)path, error));
            if (optional.isPresent()) {
                DataProvider.writeToPath(cache, optional.get(), path);
            }
        } catch (IOException iOException) {
            LOGGER.error("Couldn't save element {}", (Object)path, (Object)iOException);
        }
    }

    @Override
    public String getName() {
        return "Worldgen";
    }
}


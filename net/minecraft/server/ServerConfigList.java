/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class ServerConfigList<K, V extends ServerConfigEntry<K>> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    private final Map<String, V> map = Maps.newHashMap();

    public ServerConfigList(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public void add(V entry) {
        this.map.put(this.toString(((ServerConfigEntry)entry).getKey()), entry);
        try {
            this.save();
        } catch (IOException iOException) {
            LOGGER.warn("Could not save the list after adding a user.", (Throwable)iOException);
        }
    }

    @Nullable
    public V get(K key) {
        this.removeInvalidEntries();
        return (V)((ServerConfigEntry)this.map.get(this.toString(key)));
    }

    public void remove(K key) {
        this.map.remove(this.toString(key));
        try {
            this.save();
        } catch (IOException iOException) {
            LOGGER.warn("Could not save the list after removing a user.", (Throwable)iOException);
        }
    }

    public void remove(ServerConfigEntry<K> entry) {
        this.remove(entry.getKey());
    }

    public String[] getNames() {
        return this.map.keySet().toArray(new String[0]);
    }

    public boolean isEmpty() {
        return this.map.size() < 1;
    }

    protected String toString(K profile) {
        return profile.toString();
    }

    protected boolean contains(K object) {
        return this.map.containsKey(this.toString(object));
    }

    private void removeInvalidEntries() {
        ArrayList<Object> list = Lists.newArrayList();
        for (ServerConfigEntry serverConfigEntry : this.map.values()) {
            if (!serverConfigEntry.isInvalid()) continue;
            list.add(serverConfigEntry.getKey());
        }
        for (Object object : list) {
            this.map.remove(this.toString(object));
        }
    }

    protected abstract ServerConfigEntry<K> fromJson(JsonObject var1);

    public Collection<V> values() {
        return this.map.values();
    }

    public void save() throws IOException {
        JsonArray jsonArray = new JsonArray();
        this.map.values().stream().map(entry -> Util.make(new JsonObject(), entry::fromJson)).forEach(jsonArray::add);
        try (BufferedWriter bufferedWriter = Files.newWriter(this.file, StandardCharsets.UTF_8);){
            GSON.toJson((JsonElement)jsonArray, (Appendable)bufferedWriter);
        }
    }

    public void load() throws IOException {
        if (!this.file.exists()) {
            return;
        }
        try (BufferedReader bufferedReader = Files.newReader(this.file, StandardCharsets.UTF_8);){
            JsonArray jsonArray = GSON.fromJson((Reader)bufferedReader, JsonArray.class);
            this.map.clear();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
                ServerConfigEntry<K> serverConfigEntry = this.fromJson(jsonObject);
                if (serverConfigEntry.getKey() == null) continue;
                this.map.put(this.toString(serverConfigEntry.getKey()), serverConfigEntry);
            }
        }
    }
}


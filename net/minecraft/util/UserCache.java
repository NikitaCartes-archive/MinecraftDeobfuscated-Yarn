/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class UserCache {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_29788 = 1000;
    private static final int field_29789 = 1;
    private static boolean useRemote;
    private final Map<String, Entry> byName = Maps.newConcurrentMap();
    private final Map<UUID, Entry> byUuid = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = new GsonBuilder().create();
    private final File cacheFile;
    private final AtomicLong field_25724 = new AtomicLong();

    public UserCache(GameProfileRepository profileRepository, File cacheFile) {
        this.profileRepository = profileRepository;
        this.cacheFile = cacheFile;
        Lists.reverse(this.load()).forEach(this::method_30164);
    }

    private void method_30164(Entry entry) {
        UUID uUID;
        GameProfile gameProfile = entry.getProfile();
        entry.method_30171(this.method_30169());
        String string = gameProfile.getName();
        if (string != null) {
            this.byName.put(string.toLowerCase(Locale.ROOT), entry);
        }
        if ((uUID = gameProfile.getId()) != null) {
            this.byUuid.put(uUID, entry);
        }
    }

    @Nullable
    private static GameProfile findProfileByName(GameProfileRepository repository, String name) {
        final AtomicReference atomicReference = new AtomicReference();
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                atomicReference.set(profile);
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                atomicReference.set(null);
            }
        };
        repository.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, profileLookupCallback);
        GameProfile gameProfile = (GameProfile)atomicReference.get();
        if (!UserCache.shouldUseRemote() && gameProfile == null) {
            UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, name));
            gameProfile = new GameProfile(uUID, name);
        }
        return gameProfile;
    }

    public static void setUseRemote(boolean value) {
        useRemote = value;
    }

    private static boolean shouldUseRemote() {
        return useRemote;
    }

    public void add(GameProfile gameProfile) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(2, 1);
        Date date = calendar.getTime();
        Entry entry = new Entry(gameProfile, date);
        this.method_30164(entry);
        this.save();
    }

    private long method_30169() {
        return this.field_25724.incrementAndGet();
    }

    @Nullable
    public GameProfile findByName(String name) {
        GameProfile gameProfile;
        String string = name.toLowerCase(Locale.ROOT);
        Entry entry = this.byName.get(string);
        boolean bl = false;
        if (entry != null && new Date().getTime() >= entry.expirationDate.getTime()) {
            this.byUuid.remove(entry.getProfile().getId());
            this.byName.remove(entry.getProfile().getName().toLowerCase(Locale.ROOT));
            bl = true;
            entry = null;
        }
        if (entry != null) {
            entry.method_30171(this.method_30169());
            gameProfile = entry.getProfile();
        } else {
            gameProfile = UserCache.findProfileByName(this.profileRepository, string);
            if (gameProfile != null) {
                this.add(gameProfile);
                bl = false;
            }
        }
        if (bl) {
            this.save();
        }
        return gameProfile;
    }

    @Nullable
    public GameProfile getByUuid(UUID uuid) {
        Entry entry = this.byUuid.get(uuid);
        if (entry == null) {
            return null;
        }
        entry.method_30171(this.method_30169());
        return entry.getProfile();
    }

    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Entry> load() {
        ArrayList<Entry> list = Lists.newArrayList();
        try (BufferedReader reader2 = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);){
            JsonArray jsonArray = this.gson.fromJson((Reader)reader2, JsonArray.class);
            if (jsonArray == null) {
                ArrayList<Entry> arrayList = list;
                return arrayList;
            }
            DateFormat dateFormat = UserCache.getDateFormat();
            jsonArray.forEach(jsonElement -> {
                Entry entry = UserCache.method_30167(jsonElement, dateFormat);
                if (entry != null) {
                    list.add(entry);
                }
            });
            return list;
        } catch (FileNotFoundException reader2) {
            return list;
        } catch (JsonParseException | IOException exception) {
            LOGGER.warn("Failed to load profile cache {}", (Object)this.cacheFile, (Object)exception);
        }
        return list;
    }

    public void save() {
        JsonArray jsonArray = new JsonArray();
        DateFormat dateFormat = UserCache.getDateFormat();
        this.getLastAccessedEntries(1000).forEach(entry -> jsonArray.add(UserCache.method_30165(entry, dateFormat)));
        String string = this.gson.toJson(jsonArray);
        try (BufferedWriter writer = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);){
            writer.write(string);
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<Entry> getLastAccessedEntries(int i) {
        return ImmutableList.copyOf(this.byUuid.values()).stream().sorted(Comparator.comparing(Entry::method_30172).reversed()).limit(i);
    }

    private static JsonElement method_30165(Entry entry, DateFormat dateFormat) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", entry.getProfile().getName());
        UUID uUID = entry.getProfile().getId();
        jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
        jsonObject.addProperty("expiresOn", dateFormat.format(entry.getExpirationDate()));
        return jsonObject;
    }

    @Nullable
    private static Entry method_30167(JsonElement jsonElement, DateFormat dateFormat) {
        if (jsonElement.isJsonObject()) {
            UUID uUID;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement jsonElement2 = jsonObject.get("name");
            JsonElement jsonElement3 = jsonObject.get("uuid");
            JsonElement jsonElement4 = jsonObject.get("expiresOn");
            if (jsonElement2 == null || jsonElement3 == null) {
                return null;
            }
            String string = jsonElement3.getAsString();
            String string2 = jsonElement2.getAsString();
            Date date = null;
            if (jsonElement4 != null) {
                try {
                    date = dateFormat.parse(jsonElement4.getAsString());
                } catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if (string2 == null || string == null || date == null) {
                return null;
            }
            try {
                uUID = UUID.fromString(string);
            } catch (Throwable throwable) {
                return null;
            }
            return new Entry(new GameProfile(uUID, string2), date);
        }
        return null;
    }

    static class Entry {
        private final GameProfile profile;
        private final Date expirationDate;
        private volatile long field_25726;

        private Entry(GameProfile profile, Date expirationDate) {
            this.profile = profile;
            this.expirationDate = expirationDate;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void method_30171(long l) {
            this.field_25726 = l;
        }

        public long method_30172() {
            return this.field_25726;
        }
    }
}


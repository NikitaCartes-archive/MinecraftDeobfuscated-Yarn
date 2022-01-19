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
import com.mojang.logging.LogUtils;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class UserCache {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_SAVED_ENTRIES = 1000;
    private static final int field_29789 = 1;
    private static boolean useRemote;
    private final Map<String, Entry> byName = Maps.newConcurrentMap();
    private final Map<UUID, Entry> byUuid = Maps.newConcurrentMap();
    private final Map<String, CompletableFuture<Optional<GameProfile>>> pendingRequests = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = new GsonBuilder().create();
    private final File cacheFile;
    private final AtomicLong accessCount = new AtomicLong();
    @Nullable
    private Executor executor;

    public UserCache(GameProfileRepository profileRepository, File cacheFile) {
        this.profileRepository = profileRepository;
        this.cacheFile = cacheFile;
        Lists.reverse(this.load()).forEach(this::add);
    }

    private void add(Entry entry) {
        UUID uUID;
        GameProfile gameProfile = entry.getProfile();
        entry.setLastAccessed(this.incrementAndGetAccessCount());
        String string = gameProfile.getName();
        if (string != null) {
            this.byName.put(string.toLowerCase(Locale.ROOT), entry);
        }
        if ((uUID = gameProfile.getId()) != null) {
            this.byUuid.put(uUID, entry);
        }
    }

    private static Optional<GameProfile> findProfileByName(GameProfileRepository repository, String name) {
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
            return Optional.of(new GameProfile(uUID, name));
        }
        return Optional.ofNullable(gameProfile);
    }

    public static void setUseRemote(boolean value) {
        useRemote = value;
    }

    private static boolean shouldUseRemote() {
        return useRemote;
    }

    public void add(GameProfile profile) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(2, 1);
        Date date = calendar.getTime();
        Entry entry = new Entry(profile, date);
        this.add(entry);
        this.save();
    }

    private long incrementAndGetAccessCount() {
        return this.accessCount.incrementAndGet();
    }

    public Optional<GameProfile> findByName(String name) {
        Optional<GameProfile> optional;
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
            entry.setLastAccessed(this.incrementAndGetAccessCount());
            optional = Optional.of(entry.getProfile());
        } else {
            optional = UserCache.findProfileByName(this.profileRepository, string);
            if (optional.isPresent()) {
                this.add(optional.get());
                bl = false;
            }
        }
        if (bl) {
            this.save();
        }
        return optional;
    }

    public void findByNameAsync(String username, Consumer<Optional<GameProfile>> consumer) {
        if (this.executor == null) {
            throw new IllegalStateException("No executor");
        }
        CompletableFuture<Optional<GameProfile>> completableFuture = this.pendingRequests.get(username);
        if (completableFuture != null) {
            this.pendingRequests.put(username, (CompletableFuture<Optional<GameProfile>>)completableFuture.whenCompleteAsync((profile, throwable) -> consumer.accept((Optional<GameProfile>)profile), this.executor));
        } else {
            this.pendingRequests.put(username, (CompletableFuture<Optional<GameProfile>>)((CompletableFuture)CompletableFuture.supplyAsync(() -> this.findByName(username), Util.getMainWorkerExecutor()).whenCompleteAsync((profile, throwable) -> this.pendingRequests.remove(username), this.executor)).whenCompleteAsync((profile, throwable) -> consumer.accept((Optional<GameProfile>)profile), this.executor));
        }
    }

    public Optional<GameProfile> getByUuid(UUID uuid) {
        Entry entry = this.byUuid.get(uuid);
        if (entry == null) {
            return Optional.empty();
        }
        entry.setLastAccessed(this.incrementAndGetAccessCount());
        return Optional.of(entry.getProfile());
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void clearExecutor() {
        this.executor = null;
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
        try (BufferedReader reader2222 = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);){
            JsonArray jsonArray = this.gson.fromJson((Reader)reader2222, JsonArray.class);
            if (jsonArray == null) {
                ArrayList<Entry> arrayList = list;
                return arrayList;
            }
            DateFormat dateFormat = UserCache.getDateFormat();
            jsonArray.forEach(json -> UserCache.entryFromJson(json, dateFormat).ifPresent(list::add));
            return list;
        } catch (FileNotFoundException reader2222) {
            return list;
        } catch (JsonParseException | IOException exception) {
            LOGGER.warn("Failed to load profile cache {}", (Object)this.cacheFile, (Object)exception);
        }
        return list;
    }

    public void save() {
        JsonArray jsonArray = new JsonArray();
        DateFormat dateFormat = UserCache.getDateFormat();
        this.getLastAccessedEntries(1000).forEach(entry -> jsonArray.add(UserCache.entryToJson(entry, dateFormat)));
        String string = this.gson.toJson(jsonArray);
        try (BufferedWriter writer = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);){
            writer.write(string);
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<Entry> getLastAccessedEntries(int limit) {
        return ImmutableList.copyOf(this.byUuid.values()).stream().sorted(Comparator.comparing(Entry::getLastAccessed).reversed()).limit(limit);
    }

    private static JsonElement entryToJson(Entry entry, DateFormat dateFormat) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", entry.getProfile().getName());
        UUID uUID = entry.getProfile().getId();
        jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
        jsonObject.addProperty("expiresOn", dateFormat.format(entry.getExpirationDate()));
        return jsonObject;
    }

    private static Optional<Entry> entryFromJson(JsonElement json, DateFormat dateFormat) {
        if (json.isJsonObject()) {
            UUID uUID;
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement jsonElement = jsonObject.get("name");
            JsonElement jsonElement2 = jsonObject.get("uuid");
            JsonElement jsonElement3 = jsonObject.get("expiresOn");
            if (jsonElement == null || jsonElement2 == null) {
                return Optional.empty();
            }
            String string = jsonElement2.getAsString();
            String string2 = jsonElement.getAsString();
            Date date = null;
            if (jsonElement3 != null) {
                try {
                    date = dateFormat.parse(jsonElement3.getAsString());
                } catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if (string2 == null || string == null || date == null) {
                return Optional.empty();
            }
            try {
                uUID = UUID.fromString(string);
            } catch (Throwable throwable) {
                return Optional.empty();
            }
            return Optional.of(new Entry(new GameProfile(uUID, string2), date));
        }
        return Optional.empty();
    }

    static class Entry {
        private final GameProfile profile;
        final Date expirationDate;
        private volatile long lastAccessed;

        Entry(GameProfile profile, Date expirationDate) {
            this.profile = profile;
            this.expirationDate = expirationDate;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void setLastAccessed(long lastAccessed) {
            this.lastAccessed = lastAccessed;
        }

        public long getLastAccessed() {
            return this.lastAccessed;
        }
    }
}


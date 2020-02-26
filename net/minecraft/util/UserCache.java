/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class UserCache {
    public static final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static boolean useRemote;
    private final Map<String, Entry> byName = Maps.newHashMap();
    private final Map<UUID, Entry> byUuid = Maps.newHashMap();
    private final Deque<GameProfile> byAccessTime = Lists.newLinkedList();
    private final GameProfileRepository profileRepository;
    protected final Gson gson;
    private final File cacheFile;
    private static final TypeToken<List<Entry>> ENTRY_LIST_TYPE;

    public UserCache(GameProfileRepository profileRepository, File file) {
        this.profileRepository = profileRepository;
        this.cacheFile = file;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Entry.class, new JsonConverter());
        this.gson = gsonBuilder.create();
        this.load();
    }

    private static GameProfile findProfileByName(GameProfileRepository repository, String name) {
        final GameProfile[] gameProfiles = new GameProfile[1];
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                gameProfiles[0] = profile;
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                gameProfiles[0] = null;
            }
        };
        repository.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, profileLookupCallback);
        if (!UserCache.shouldUseRemote() && gameProfiles[0] == null) {
            UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, name));
            GameProfile gameProfile = new GameProfile(uUID, name);
            profileLookupCallback.onProfileLookupSucceeded(gameProfile);
        }
        return gameProfiles[0];
    }

    public static void setUseRemote(boolean value) {
        useRemote = value;
    }

    private static boolean shouldUseRemote() {
        return useRemote;
    }

    public void add(GameProfile gameProfile) {
        this.add(gameProfile, null);
    }

    private void add(GameProfile profile, Date date) {
        UUID uUID = profile.getId();
        if (date == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(2, 1);
            date = calendar.getTime();
        }
        Entry entry = new Entry(profile, date);
        if (this.byUuid.containsKey(uUID)) {
            Entry entry2 = this.byUuid.get(uUID);
            this.byName.remove(entry2.getProfile().getName().toLowerCase(Locale.ROOT));
            this.byAccessTime.remove(profile);
        }
        this.byName.put(profile.getName().toLowerCase(Locale.ROOT), entry);
        this.byUuid.put(uUID, entry);
        this.byAccessTime.addFirst(profile);
        this.save();
    }

    @Nullable
    public GameProfile findByName(String string) {
        String string2 = string.toLowerCase(Locale.ROOT);
        Entry entry = this.byName.get(string2);
        if (entry != null && new Date().getTime() >= entry.expirationDate.getTime()) {
            this.byUuid.remove(entry.getProfile().getId());
            this.byName.remove(entry.getProfile().getName().toLowerCase(Locale.ROOT));
            this.byAccessTime.remove(entry.getProfile());
            entry = null;
        }
        if (entry != null) {
            GameProfile gameProfile = entry.getProfile();
            this.byAccessTime.remove(gameProfile);
            this.byAccessTime.addFirst(gameProfile);
        } else {
            GameProfile gameProfile = UserCache.findProfileByName(this.profileRepository, string2);
            if (gameProfile != null) {
                this.add(gameProfile);
                entry = this.byName.get(string2);
            }
        }
        this.save();
        return entry == null ? null : entry.getProfile();
    }

    @Nullable
    public GameProfile getByUuid(UUID uUID) {
        Entry entry = this.byUuid.get(uUID);
        return entry == null ? null : entry.getProfile();
    }

    private Entry getEntry(UUID uUID) {
        Entry entry = this.byUuid.get(uUID);
        if (entry != null) {
            GameProfile gameProfile = entry.getProfile();
            this.byAccessTime.remove(gameProfile);
            this.byAccessTime.addFirst(gameProfile);
        }
        return entry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        BufferedReader bufferedReader;
        block5: {
            bufferedReader = null;
            try {
                bufferedReader = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);
                List<Entry> list = JsonHelper.deserialize(this.gson, (Reader)bufferedReader, ENTRY_LIST_TYPE);
                this.byName.clear();
                this.byUuid.clear();
                this.byAccessTime.clear();
                if (list == null) break block5;
                for (Entry entry : Lists.reverse(list)) {
                    if (entry == null) continue;
                    this.add(entry.getProfile(), entry.getExpirationDate());
                }
            } catch (FileNotFoundException fileNotFoundException) {
                IOUtils.closeQuietly(bufferedReader);
            } catch (JsonParseException jsonParseException) {
                IOUtils.closeQuietly(bufferedReader);
            } catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedReader);
                throw throwable;
            }
        }
        IOUtils.closeQuietly(bufferedReader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save() {
        String string = this.gson.toJson(this.getLastAccessedEntries(1000));
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);
            bufferedWriter.write(string);
        } catch (FileNotFoundException fileNotFoundException) {
            IOUtils.closeQuietly(bufferedWriter);
            return;
        } catch (IOException iOException) {
            IOUtils.closeQuietly(bufferedWriter);
            return;
        } catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedWriter);
            throw throwable;
        }
        IOUtils.closeQuietly(bufferedWriter);
    }

    private List<Entry> getLastAccessedEntries(int i) {
        ArrayList<Entry> list = Lists.newArrayList();
        ArrayList<GameProfile> list2 = Lists.newArrayList(Iterators.limit(this.byAccessTime.iterator(), i));
        for (GameProfile gameProfile : list2) {
            Entry entry = this.getEntry(gameProfile.getId());
            if (entry == null) continue;
            list.add(entry);
        }
        return list;
    }

    static {
        ENTRY_LIST_TYPE = new TypeToken<List<Entry>>(){};
    }

    class Entry {
        private final GameProfile profile;
        private final Date expirationDate;

        private Entry(GameProfile gameProfile, Date date) {
            this.profile = gameProfile;
            this.expirationDate = date;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }

    class JsonConverter
    implements JsonDeserializer<Entry>,
    JsonSerializer<Entry> {
        private JsonConverter() {
        }

        @Override
        public JsonElement serialize(Entry entry, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", entry.getProfile().getName());
            UUID uUID = entry.getProfile().getId();
            jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
            jsonObject.addProperty("expiresOn", EXPIRATION_DATE_FORMAT.format(entry.getExpirationDate()));
            return jsonObject;
        }

        @Override
        public Entry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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
                        date = EXPIRATION_DATE_FORMAT.parse(jsonElement4.getAsString());
                    } catch (ParseException parseException) {
                        date = null;
                    }
                }
                if (string2 == null || string == null) {
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

        @Override
        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((Entry)entry, unused, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}


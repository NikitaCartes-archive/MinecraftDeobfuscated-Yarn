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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserCache {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_29788 = 1000;
	private static final int field_29789 = 1;
	private static boolean useRemote;
	private final Map<String, UserCache.Entry> byName = Maps.<String, UserCache.Entry>newConcurrentMap();
	private final Map<UUID, UserCache.Entry> byUuid = Maps.<UUID, UserCache.Entry>newConcurrentMap();
	private final GameProfileRepository profileRepository;
	private final Gson gson = new GsonBuilder().create();
	private final File cacheFile;
	private final AtomicLong field_25724 = new AtomicLong();

	public UserCache(GameProfileRepository profileRepository, File cacheFile) {
		this.profileRepository = profileRepository;
		this.cacheFile = cacheFile;
		Lists.reverse(this.load()).forEach(this::method_30164);
	}

	private void method_30164(UserCache.Entry entry) {
		GameProfile gameProfile = entry.getProfile();
		entry.method_30171(this.method_30169());
		String string = gameProfile.getName();
		if (string != null) {
			this.byName.put(string.toLowerCase(Locale.ROOT), entry);
		}

		UUID uUID = gameProfile.getId();
		if (uUID != null) {
			this.byUuid.put(uUID, entry);
		}
	}

	@Nullable
	private static GameProfile findProfileByName(GameProfileRepository repository, String name) {
		final AtomicReference<GameProfile> atomicReference = new AtomicReference();
		ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
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
		if (!shouldUseRemote() && gameProfile == null) {
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
		UserCache.Entry entry = new UserCache.Entry(gameProfile, date);
		this.method_30164(entry);
		this.save();
	}

	private long method_30169() {
		return this.field_25724.incrementAndGet();
	}

	@Nullable
	public GameProfile findByName(String name) {
		String string = name.toLowerCase(Locale.ROOT);
		UserCache.Entry entry = (UserCache.Entry)this.byName.get(string);
		boolean bl = false;
		if (entry != null && new Date().getTime() >= entry.expirationDate.getTime()) {
			this.byUuid.remove(entry.getProfile().getId());
			this.byName.remove(entry.getProfile().getName().toLowerCase(Locale.ROOT));
			bl = true;
			entry = null;
		}

		GameProfile gameProfile;
		if (entry != null) {
			entry.method_30171(this.method_30169());
			gameProfile = entry.getProfile();
		} else {
			gameProfile = findProfileByName(this.profileRepository, string);
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
		UserCache.Entry entry = (UserCache.Entry)this.byUuid.get(uuid);
		if (entry == null) {
			return null;
		} else {
			entry.method_30171(this.method_30169());
			return entry.getProfile();
		}
	}

	private static DateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	}

	public List<UserCache.Entry> load() {
		List<UserCache.Entry> list = Lists.<UserCache.Entry>newArrayList();

		try {
			Reader reader = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);
			Throwable var3 = null;

			Object dateFormat;
			try {
				JsonArray jsonArray = this.gson.fromJson(reader, JsonArray.class);
				if (jsonArray != null) {
					DateFormat dateFormatx = getDateFormat();
					jsonArray.forEach(jsonElement -> {
						UserCache.Entry entry = method_30167(jsonElement, dateFormat);
						if (entry != null) {
							list.add(entry);
						}
					});
					return list;
				}

				dateFormat = list;
			} catch (Throwable var17) {
				var3 = var17;
				throw var17;
			} finally {
				if (reader != null) {
					if (var3 != null) {
						try {
							reader.close();
						} catch (Throwable var16) {
							var3.addSuppressed(var16);
						}
					} else {
						reader.close();
					}
				}
			}

			return (List<UserCache.Entry>)dateFormat;
		} catch (FileNotFoundException var19) {
		} catch (JsonParseException | IOException var20) {
			LOGGER.warn("Failed to load profile cache {}", this.cacheFile, var20);
		}

		return list;
	}

	public void save() {
		JsonArray jsonArray = new JsonArray();
		DateFormat dateFormat = getDateFormat();
		this.getLastAccessedEntries(1000).forEach(entry -> jsonArray.add(method_30165(entry, dateFormat)));
		String string = this.gson.toJson((JsonElement)jsonArray);

		try {
			Writer writer = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);
			Throwable var5 = null;

			try {
				writer.write(string);
			} catch (Throwable var15) {
				var5 = var15;
				throw var15;
			} finally {
				if (writer != null) {
					if (var5 != null) {
						try {
							writer.close();
						} catch (Throwable var14) {
							var5.addSuppressed(var14);
						}
					} else {
						writer.close();
					}
				}
			}
		} catch (IOException var17) {
		}
	}

	private Stream<UserCache.Entry> getLastAccessedEntries(int i) {
		return ImmutableList.copyOf(this.byUuid.values()).stream().sorted(Comparator.comparing(UserCache.Entry::method_30172).reversed()).limit((long)i);
	}

	private static JsonElement method_30165(UserCache.Entry entry, DateFormat dateFormat) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", entry.getProfile().getName());
		UUID uUID = entry.getProfile().getId();
		jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
		jsonObject.addProperty("expiresOn", dateFormat.format(entry.getExpirationDate()));
		return jsonObject;
	}

	@Nullable
	private static UserCache.Entry method_30167(JsonElement jsonElement, DateFormat dateFormat) {
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			JsonElement jsonElement2 = jsonObject.get("name");
			JsonElement jsonElement3 = jsonObject.get("uuid");
			JsonElement jsonElement4 = jsonObject.get("expiresOn");
			if (jsonElement2 != null && jsonElement3 != null) {
				String string = jsonElement3.getAsString();
				String string2 = jsonElement2.getAsString();
				Date date = null;
				if (jsonElement4 != null) {
					try {
						date = dateFormat.parse(jsonElement4.getAsString());
					} catch (ParseException var12) {
					}
				}

				if (string2 != null && string != null && date != null) {
					UUID uUID;
					try {
						uUID = UUID.fromString(string);
					} catch (Throwable var11) {
						return null;
					}

					return new UserCache.Entry(new GameProfile(uUID, string2), date);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
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

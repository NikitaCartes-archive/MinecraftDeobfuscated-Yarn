package net.minecraft;

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
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;

public class class_3312 {
	public static final SimpleDateFormat field_14317 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private static boolean field_14313;
	private final Map<String, class_3312.class_3313> field_14312 = Maps.<String, class_3312.class_3313>newHashMap();
	private final Map<UUID, class_3312.class_3313> field_14310 = Maps.<UUID, class_3312.class_3313>newHashMap();
	private final Deque<GameProfile> field_14311 = Lists.<GameProfile>newLinkedList();
	private final GameProfileRepository field_14315;
	protected final Gson field_14318;
	private final File field_14314;
	private static final ParameterizedType field_14316 = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{class_3312.class_3313.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};

	public class_3312(GameProfileRepository gameProfileRepository, File file) {
		this.field_14315 = gameProfileRepository;
		this.field_14314 = file;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(class_3312.class_3313.class, new class_3312.class_3314());
		this.field_14318 = gsonBuilder.create();
		this.method_14517();
	}

	private static GameProfile method_14509(GameProfileRepository gameProfileRepository, String string) {
		final GameProfile[] gameProfiles = new GameProfile[1];
		ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
			@Override
			public void onProfileLookupSucceeded(GameProfile gameProfile) {
				gameProfiles[0] = gameProfile;
			}

			@Override
			public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
				gameProfiles[0] = null;
			}
		};
		gameProfileRepository.findProfilesByNames(new String[]{string}, Agent.MINECRAFT, profileLookupCallback);
		if (!method_14514() && gameProfiles[0] == null) {
			UUID uUID = class_1657.method_7271(new GameProfile(null, string));
			GameProfile gameProfile = new GameProfile(uUID, string);
			profileLookupCallback.onProfileLookupSucceeded(gameProfile);
		}

		return gameProfiles[0];
	}

	public static void method_14510(boolean bl) {
		field_14313 = bl;
	}

	private static boolean method_14514() {
		return field_14313;
	}

	public void method_14508(GameProfile gameProfile) {
		this.method_14511(gameProfile, null);
	}

	private void method_14511(GameProfile gameProfile, Date date) {
		UUID uUID = gameProfile.getId();
		if (date == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(2, 1);
			date = calendar.getTime();
		}

		class_3312.class_3313 lv = new class_3312.class_3313(gameProfile, date);
		if (this.field_14310.containsKey(uUID)) {
			class_3312.class_3313 lv2 = (class_3312.class_3313)this.field_14310.get(uUID);
			this.field_14312.remove(lv2.method_14519().getName().toLowerCase(Locale.ROOT));
			this.field_14311.remove(gameProfile);
		}

		this.field_14312.put(gameProfile.getName().toLowerCase(Locale.ROOT), lv);
		this.field_14310.put(uUID, lv);
		this.field_14311.addFirst(gameProfile);
		this.method_14518();
	}

	@Nullable
	public GameProfile method_14515(String string) {
		String string2 = string.toLowerCase(Locale.ROOT);
		class_3312.class_3313 lv = (class_3312.class_3313)this.field_14312.get(string2);
		if (lv != null && new Date().getTime() >= lv.field_14319.getTime()) {
			this.field_14310.remove(lv.method_14519().getId());
			this.field_14312.remove(lv.method_14519().getName().toLowerCase(Locale.ROOT));
			this.field_14311.remove(lv.method_14519());
			lv = null;
		}

		if (lv != null) {
			GameProfile gameProfile = lv.method_14519();
			this.field_14311.remove(gameProfile);
			this.field_14311.addFirst(gameProfile);
		} else {
			GameProfile gameProfile = method_14509(this.field_14315, string2);
			if (gameProfile != null) {
				this.method_14508(gameProfile);
				lv = (class_3312.class_3313)this.field_14312.get(string2);
			}
		}

		this.method_14518();
		return lv == null ? null : lv.method_14519();
	}

	@Nullable
	public GameProfile method_14512(UUID uUID) {
		class_3312.class_3313 lv = (class_3312.class_3313)this.field_14310.get(uUID);
		return lv == null ? null : lv.method_14519();
	}

	private class_3312.class_3313 method_14513(UUID uUID) {
		class_3312.class_3313 lv = (class_3312.class_3313)this.field_14310.get(uUID);
		if (lv != null) {
			GameProfile gameProfile = lv.method_14519();
			this.field_14311.remove(gameProfile);
			this.field_14311.addFirst(gameProfile);
		}

		return lv;
	}

	public void method_14517() {
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = Files.newReader(this.field_14314, StandardCharsets.UTF_8);
			List<class_3312.class_3313> list = class_3518.method_15297(this.field_14318, bufferedReader, field_14316);
			this.field_14312.clear();
			this.field_14310.clear();
			this.field_14311.clear();
			if (list != null) {
				for (class_3312.class_3313 lv : Lists.reverse(list)) {
					if (lv != null) {
						this.method_14511(lv.method_14519(), lv.method_14520());
					}
				}
			}
		} catch (FileNotFoundException var9) {
		} catch (JsonParseException var10) {
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}
	}

	public void method_14518() {
		String string = this.field_14318.toJson(this.method_14516(1000));
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = Files.newWriter(this.field_14314, StandardCharsets.UTF_8);
			bufferedWriter.write(string);
			return;
		} catch (FileNotFoundException var8) {
			return;
		} catch (IOException var9) {
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}

	private List<class_3312.class_3313> method_14516(int i) {
		List<class_3312.class_3313> list = Lists.<class_3312.class_3313>newArrayList();

		for (GameProfile gameProfile : Lists.newArrayList(Iterators.limit(this.field_14311.iterator(), i))) {
			class_3312.class_3313 lv = this.method_14513(gameProfile.getId());
			if (lv != null) {
				list.add(lv);
			}
		}

		return list;
	}

	class class_3313 {
		private final GameProfile field_14321;
		private final Date field_14319;

		private class_3313(GameProfile gameProfile, Date date) {
			this.field_14321 = gameProfile;
			this.field_14319 = date;
		}

		public GameProfile method_14519() {
			return this.field_14321;
		}

		public Date method_14520() {
			return this.field_14319;
		}
	}

	class class_3314 implements JsonDeserializer<class_3312.class_3313>, JsonSerializer<class_3312.class_3313> {
		private class_3314() {
		}

		public JsonElement method_14522(class_3312.class_3313 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", arg.method_14519().getName());
			UUID uUID = arg.method_14519().getId();
			jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
			jsonObject.addProperty("expiresOn", class_3312.field_14317.format(arg.method_14520()));
			return jsonObject;
		}

		public class_3312.class_3313 method_14523(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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
							date = class_3312.field_14317.parse(jsonElement4.getAsString());
						} catch (ParseException var14) {
							date = null;
						}
					}

					if (string2 != null && string != null) {
						UUID uUID;
						try {
							uUID = UUID.fromString(string);
						} catch (Throwable var13) {
							return null;
						}

						return class_3312.this.new class_3313(new GameProfile(uUID, string2), date);
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
	}
}

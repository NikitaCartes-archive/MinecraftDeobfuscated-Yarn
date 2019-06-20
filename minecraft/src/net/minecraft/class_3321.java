package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3321 {
	private static final Logger field_14326 = LogManager.getLogger();
	public static final File field_14324 = new File("banned-ips.txt");
	public static final File field_14328 = new File("banned-players.txt");
	public static final File field_14327 = new File("ops.txt");
	public static final File field_14325 = new File("white-list.txt");

	static List<String> method_14543(File file, Map<String, String[]> map) throws IOException {
		List<String> list = Files.readLines(file, StandardCharsets.UTF_8);

		for (String string : list) {
			string = string.trim();
			if (!string.startsWith("#") && string.length() >= 1) {
				String[] strings = string.split("\\|");
				map.put(strings[0].toLowerCase(Locale.ROOT), strings);
			}
		}

		return list;
	}

	private static void method_14538(MinecraftServer minecraftServer, Collection<String> collection, ProfileLookupCallback profileLookupCallback) {
		String[] strings = (String[])collection.stream().filter(stringx -> !class_3544.method_15438(stringx)).toArray(String[]::new);
		if (minecraftServer.method_3828()) {
			minecraftServer.method_3719().findProfilesByNames(strings, Agent.MINECRAFT, profileLookupCallback);
		} else {
			for (String string : strings) {
				UUID uUID = class_1657.method_7271(new GameProfile(null, string));
				GameProfile gameProfile = new GameProfile(uUID, string);
				profileLookupCallback.onProfileLookupSucceeded(gameProfile);
			}
		}
	}

	public static boolean method_14547(MinecraftServer minecraftServer) {
		final class_3335 lv = new class_3335(class_3324.field_14355);
		if (field_14328.exists() && field_14328.isFile()) {
			if (lv.method_14643().exists()) {
				try {
					lv.method_14630();
				} catch (FileNotFoundException var6) {
					field_14326.warn("Could not load existing file {}", lv.method_14643().getName(), var6);
				}
			}

			try {
				final Map<String, String[]> map = Maps.<String, String[]>newHashMap();
				method_14543(field_14328, map);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.method_3793().method_14508(gameProfile);
						String[] strings = (String[])map.get(gameProfile.getName().toLowerCase(Locale.ROOT));
						if (strings == null) {
							class_3321.field_14326.warn("Could not convert user banlist entry for {}", gameProfile.getName());
							throw new class_3321.class_3322("Profile not in the conversionlist");
						} else {
							Date date = strings.length > 1 ? class_3321.method_14535(strings[1], null) : null;
							String string = strings.length > 2 ? strings[2] : null;
							Date date2 = strings.length > 3 ? class_3321.method_14535(strings[3], null) : null;
							String string2 = strings.length > 4 ? strings[4] : null;
							lv.method_14633(new class_3336(gameProfile, date, string, date2, string2));
						}
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.field_14326.warn("Could not lookup user banlist entry for {}", gameProfile.getName(), exception);
						if (!(exception instanceof ProfileNotFoundException)) {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}
				};
				method_14538(minecraftServer, map.keySet(), profileLookupCallback);
				lv.method_14629();
				method_14549(field_14328);
				return true;
			} catch (IOException var4) {
				field_14326.warn("Could not read old user banlist to convert it!", (Throwable)var4);
				return false;
			} catch (class_3321.class_3322 var5) {
				field_14326.error("Conversion failed, please try again later", (Throwable)var5);
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean method_14545(MinecraftServer minecraftServer) {
		class_3317 lv = new class_3317(class_3324.field_14364);
		if (field_14324.exists() && field_14324.isFile()) {
			if (lv.method_14643().exists()) {
				try {
					lv.method_14630();
				} catch (FileNotFoundException var11) {
					field_14326.warn("Could not load existing file {}", lv.method_14643().getName(), var11);
				}
			}

			try {
				Map<String, String[]> map = Maps.<String, String[]>newHashMap();
				method_14543(field_14324, map);

				for (String string : map.keySet()) {
					String[] strings = (String[])map.get(string);
					Date date = strings.length > 1 ? method_14535(strings[1], null) : null;
					String string2 = strings.length > 2 ? strings[2] : null;
					Date date2 = strings.length > 3 ? method_14535(strings[3], null) : null;
					String string3 = strings.length > 4 ? strings[4] : null;
					lv.method_14633(new class_3320(string, date, string2, date2, string3));
				}

				lv.method_14629();
				method_14549(field_14324);
				return true;
			} catch (IOException var10) {
				field_14326.warn("Could not parse old ip banlist to convert it!", (Throwable)var10);
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean method_14539(MinecraftServer minecraftServer) {
		final class_3326 lv = new class_3326(class_3324.field_14348);
		if (field_14327.exists() && field_14327.isFile()) {
			if (lv.method_14643().exists()) {
				try {
					lv.method_14630();
				} catch (FileNotFoundException var6) {
					field_14326.warn("Could not load existing file {}", lv.method_14643().getName(), var6);
				}
			}

			try {
				List<String> list = Files.readLines(field_14327, StandardCharsets.UTF_8);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.method_3793().method_14508(gameProfile);
						lv.method_14633(new class_3327(gameProfile, minecraftServer.method_3798(), false));
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.field_14326.warn("Could not lookup oplist entry for {}", gameProfile.getName(), exception);
						if (!(exception instanceof ProfileNotFoundException)) {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}
				};
				method_14538(minecraftServer, list, profileLookupCallback);
				lv.method_14629();
				method_14549(field_14327);
				return true;
			} catch (IOException var4) {
				field_14326.warn("Could not read old oplist to convert it!", (Throwable)var4);
				return false;
			} catch (class_3321.class_3322 var5) {
				field_14326.error("Conversion failed, please try again later", (Throwable)var5);
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean method_14533(MinecraftServer minecraftServer) {
		final class_3337 lv = new class_3337(class_3324.field_14343);
		if (field_14325.exists() && field_14325.isFile()) {
			if (lv.method_14643().exists()) {
				try {
					lv.method_14630();
				} catch (FileNotFoundException var6) {
					field_14326.warn("Could not load existing file {}", lv.method_14643().getName(), var6);
				}
			}

			try {
				List<String> list = Files.readLines(field_14325, StandardCharsets.UTF_8);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.method_3793().method_14508(gameProfile);
						lv.method_14633(new class_3340(gameProfile));
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.field_14326.warn("Could not lookup user whitelist entry for {}", gameProfile.getName(), exception);
						if (!(exception instanceof ProfileNotFoundException)) {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}
				};
				method_14538(minecraftServer, list, profileLookupCallback);
				lv.method_14629();
				method_14549(field_14325);
				return true;
			} catch (IOException var4) {
				field_14326.warn("Could not read old whitelist to convert it!", (Throwable)var4);
				return false;
			} catch (class_3321.class_3322 var5) {
				field_14326.error("Conversion failed, please try again later", (Throwable)var5);
				return false;
			}
		} else {
			return true;
		}
	}

	public static String method_14546(MinecraftServer minecraftServer, String string) {
		if (!class_3544.method_15438(string) && string.length() <= 16) {
			GameProfile gameProfile = minecraftServer.method_3793().method_14515(string);
			if (gameProfile != null && gameProfile.getId() != null) {
				return gameProfile.getId().toString();
			} else if (!minecraftServer.method_3724() && minecraftServer.method_3828()) {
				final List<GameProfile> list = Lists.<GameProfile>newArrayList();
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.method_3793().method_14508(gameProfile);
						list.add(gameProfile);
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.field_14326.warn("Could not lookup user whitelist entry for {}", gameProfile.getName(), exception);
					}
				};
				method_14538(minecraftServer, Lists.<String>newArrayList(string), profileLookupCallback);
				return !list.isEmpty() && ((GameProfile)list.get(0)).getId() != null ? ((GameProfile)list.get(0)).getId().toString() : "";
			} else {
				return class_1657.method_7271(new GameProfile(null, string)).toString();
			}
		} else {
			return string;
		}
	}

	public static boolean method_14550(class_3176 arg) {
		final File file = method_14536(arg);
		final File file2 = new File(file.getParentFile(), "playerdata");
		final File file3 = new File(file.getParentFile(), "unknownplayers");
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			List<String> list = Lists.<String>newArrayList();

			for (File file4 : files) {
				String string = file4.getName();
				if (string.toLowerCase(Locale.ROOT).endsWith(".dat")) {
					String string2 = string.substring(0, string.length() - ".dat".length());
					if (!string2.isEmpty()) {
						list.add(string2);
					}
				}
			}

			try {
				final String[] strings = (String[])list.toArray(new String[list.size()]);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						arg.method_3793().method_14508(gameProfile);
						UUID uUID = gameProfile.getId();
						if (uUID == null) {
							throw new class_3321.class_3322("Missing UUID for user profile " + gameProfile.getName());
						} else {
							this.method_14553(file2, this.method_14554(gameProfile), uUID.toString());
						}
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.field_14326.warn("Could not lookup user uuid for {}", gameProfile.getName(), exception);
						if (exception instanceof ProfileNotFoundException) {
							String string = this.method_14554(gameProfile);
							this.method_14553(file3, string, string);
						} else {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}

					private void method_14553(File file, String string, String string2) {
						File file2 = new File(file, string + ".dat");
						File file3 = new File(file, string2 + ".dat");
						class_3321.method_14534(file);
						if (!file2.renameTo(file3)) {
							throw new class_3321.class_3322("Could not convert file for " + string);
						}
					}

					private String method_14554(GameProfile gameProfile) {
						String string = null;

						for (String string2 : strings) {
							if (string2 != null && string2.equalsIgnoreCase(gameProfile.getName())) {
								string = string2;
								break;
							}
						}

						if (string == null) {
							throw new class_3321.class_3322("Could not find the filename for " + gameProfile.getName() + " anymore");
						} else {
							return string;
						}
					}
				};
				method_14538(arg, Lists.<String>newArrayList(strings), profileLookupCallback);
				return true;
			} catch (class_3321.class_3322 var12) {
				field_14326.error("Conversion failed, please try again later", (Throwable)var12);
				return false;
			}
		} else {
			return true;
		}
	}

	private static void method_14534(File file) {
		if (file.exists()) {
			if (!file.isDirectory()) {
				throw new class_3321.class_3322("Can't create directory " + file.getName() + " in world save directory.");
			}
		} else if (!file.mkdirs()) {
			throw new class_3321.class_3322("Can't create directory " + file.getName() + " in world save directory.");
		}
	}

	public static boolean method_14540(MinecraftServer minecraftServer) {
		boolean bl = method_14541();
		return bl && method_14542(minecraftServer);
	}

	private static boolean method_14541() {
		boolean bl = false;
		if (field_14328.exists() && field_14328.isFile()) {
			bl = true;
		}

		boolean bl2 = false;
		if (field_14324.exists() && field_14324.isFile()) {
			bl2 = true;
		}

		boolean bl3 = false;
		if (field_14327.exists() && field_14327.isFile()) {
			bl3 = true;
		}

		boolean bl4 = false;
		if (field_14325.exists() && field_14325.isFile()) {
			bl4 = true;
		}

		if (!bl && !bl2 && !bl3 && !bl4) {
			return true;
		} else {
			field_14326.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
			field_14326.warn("** please remove the following files and restart the server:");
			if (bl) {
				field_14326.warn("* {}", field_14328.getName());
			}

			if (bl2) {
				field_14326.warn("* {}", field_14324.getName());
			}

			if (bl3) {
				field_14326.warn("* {}", field_14327.getName());
			}

			if (bl4) {
				field_14326.warn("* {}", field_14325.getName());
			}

			return false;
		}
	}

	private static boolean method_14542(MinecraftServer minecraftServer) {
		File file = method_14536(minecraftServer);
		if (!file.exists() || !file.isDirectory() || file.list().length <= 0 && file.delete()) {
			return true;
		} else {
			field_14326.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
			field_14326.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
			field_14326.warn("** please restart the server and if the problem persists, remove the directory '{}'", file.getPath());
			return false;
		}
	}

	private static File method_14536(MinecraftServer minecraftServer) {
		String string = minecraftServer.method_3865();
		File file = new File(string);
		return new File(file, "players");
	}

	private static void method_14549(File file) {
		File file2 = new File(file.getName() + ".converted");
		file.renameTo(file2);
	}

	private static Date method_14535(String string, Date date) {
		Date date2;
		try {
			date2 = class_3309.field_14308.parse(string);
		} catch (ParseException var4) {
			date2 = date;
		}

		return date2;
	}

	static class class_3322 extends RuntimeException {
		private class_3322(String string, Throwable throwable) {
			super(string, throwable);
		}

		private class_3322(String string) {
			super(string);
		}
	}
}

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.config.BanEntry;
import net.minecraft.server.config.BannedIpEntry;
import net.minecraft.server.config.BannedIpsList;
import net.minecraft.server.config.BannedPlayerEntry;
import net.minecraft.server.config.BannedProfilesList;
import net.minecraft.server.config.OperatorEntry;
import net.minecraft.server.config.OpsList;
import net.minecraft.server.config.WhitelistEntry;
import net.minecraft.server.config.WhitelistList;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.ChatUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3321 {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File BANNED_IPS_FILE = new File("banned-ips.txt");
	public static final File BANNED_PLAYERS_FILE = new File("banned-players.txt");
	public static final File OPS_FILE = new File("ops.txt");
	public static final File WHITE_LIST_FILE = new File("white-list.txt");

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
		String[] strings = (String[])collection.stream().filter(stringx -> !ChatUtil.isEmpty(stringx)).toArray(String[]::new);
		if (minecraftServer.isOnlineMode()) {
			minecraftServer.getGameProfileRepo().findProfilesByNames(strings, Agent.MINECRAFT, profileLookupCallback);
		} else {
			for (String string : strings) {
				UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, string));
				GameProfile gameProfile = new GameProfile(uUID, string);
				profileLookupCallback.onProfileLookupSucceeded(gameProfile);
			}
		}
	}

	public static boolean method_14547(MinecraftServer minecraftServer) {
		final BannedProfilesList bannedProfilesList = new BannedProfilesList(PlayerManager.BANNED_PLAYERS_FILE);
		if (BANNED_PLAYERS_FILE.exists() && BANNED_PLAYERS_FILE.isFile()) {
			if (bannedProfilesList.getFile().exists()) {
				try {
					bannedProfilesList.load();
				} catch (FileNotFoundException var6) {
					LOGGER.warn("Could not load existing file {}", bannedProfilesList.getFile().getName(), var6);
				}
			}

			try {
				final Map<String, String[]> map = Maps.<String, String[]>newHashMap();
				method_14543(BANNED_PLAYERS_FILE, map);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.getUserCache().add(gameProfile);
						String[] strings = (String[])map.get(gameProfile.getName().toLowerCase(Locale.ROOT));
						if (strings == null) {
							class_3321.LOGGER.warn("Could not convert user banlist entry for {}", gameProfile.getName());
							throw new class_3321.class_3322("Profile not in the conversionlist");
						} else {
							Date date = strings.length > 1 ? class_3321.method_14535(strings[1], null) : null;
							String string = strings.length > 2 ? strings[2] : null;
							Date date2 = strings.length > 3 ? class_3321.method_14535(strings[3], null) : null;
							String string2 = strings.length > 4 ? strings[4] : null;
							bannedProfilesList.add(new BannedPlayerEntry(gameProfile, date, string, date2, string2));
						}
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.LOGGER.warn("Could not lookup user banlist entry for {}", gameProfile.getName(), exception);
						if (!(exception instanceof ProfileNotFoundException)) {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}
				};
				method_14538(minecraftServer, map.keySet(), profileLookupCallback);
				bannedProfilesList.save();
				method_14549(BANNED_PLAYERS_FILE);
				return true;
			} catch (IOException var4) {
				LOGGER.warn("Could not read old user banlist to convert it!", (Throwable)var4);
				return false;
			} catch (class_3321.class_3322 var5) {
				LOGGER.error("Conversion failed, please try again later", (Throwable)var5);
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean method_14545(MinecraftServer minecraftServer) {
		BannedIpsList bannedIpsList = new BannedIpsList(PlayerManager.BANNED_IPS_FILE);
		if (BANNED_IPS_FILE.exists() && BANNED_IPS_FILE.isFile()) {
			if (bannedIpsList.getFile().exists()) {
				try {
					bannedIpsList.load();
				} catch (FileNotFoundException var11) {
					LOGGER.warn("Could not load existing file {}", bannedIpsList.getFile().getName(), var11);
				}
			}

			try {
				Map<String, String[]> map = Maps.<String, String[]>newHashMap();
				method_14543(BANNED_IPS_FILE, map);

				for (String string : map.keySet()) {
					String[] strings = (String[])map.get(string);
					Date date = strings.length > 1 ? method_14535(strings[1], null) : null;
					String string2 = strings.length > 2 ? strings[2] : null;
					Date date2 = strings.length > 3 ? method_14535(strings[3], null) : null;
					String string3 = strings.length > 4 ? strings[4] : null;
					bannedIpsList.add(new BannedIpEntry(string, date, string2, date2, string3));
				}

				bannedIpsList.save();
				method_14549(BANNED_IPS_FILE);
				return true;
			} catch (IOException var10) {
				LOGGER.warn("Could not parse old ip banlist to convert it!", (Throwable)var10);
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean method_14539(MinecraftServer minecraftServer) {
		final OpsList opsList = new OpsList(PlayerManager.OPS_FILE);
		if (OPS_FILE.exists() && OPS_FILE.isFile()) {
			if (opsList.getFile().exists()) {
				try {
					opsList.load();
				} catch (FileNotFoundException var6) {
					LOGGER.warn("Could not load existing file {}", opsList.getFile().getName(), var6);
				}
			}

			try {
				List<String> list = Files.readLines(OPS_FILE, StandardCharsets.UTF_8);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.getUserCache().add(gameProfile);
						opsList.add(new OperatorEntry(gameProfile, minecraftServer.getOpPermissionLevel(), false));
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.LOGGER.warn("Could not lookup oplist entry for {}", gameProfile.getName(), exception);
						if (!(exception instanceof ProfileNotFoundException)) {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}
				};
				method_14538(minecraftServer, list, profileLookupCallback);
				opsList.save();
				method_14549(OPS_FILE);
				return true;
			} catch (IOException var4) {
				LOGGER.warn("Could not read old oplist to convert it!", (Throwable)var4);
				return false;
			} catch (class_3321.class_3322 var5) {
				LOGGER.error("Conversion failed, please try again later", (Throwable)var5);
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean method_14533(MinecraftServer minecraftServer) {
		final WhitelistList whitelistList = new WhitelistList(PlayerManager.WHITELIST_FILE);
		if (WHITE_LIST_FILE.exists() && WHITE_LIST_FILE.isFile()) {
			if (whitelistList.getFile().exists()) {
				try {
					whitelistList.load();
				} catch (FileNotFoundException var6) {
					LOGGER.warn("Could not load existing file {}", whitelistList.getFile().getName(), var6);
				}
			}

			try {
				List<String> list = Files.readLines(WHITE_LIST_FILE, StandardCharsets.UTF_8);
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.getUserCache().add(gameProfile);
						whitelistList.add(new WhitelistEntry(gameProfile));
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.LOGGER.warn("Could not lookup user whitelist entry for {}", gameProfile.getName(), exception);
						if (!(exception instanceof ProfileNotFoundException)) {
							throw new class_3321.class_3322("Could not request user " + gameProfile.getName() + " from backend systems", exception);
						}
					}
				};
				method_14538(minecraftServer, list, profileLookupCallback);
				whitelistList.save();
				method_14549(WHITE_LIST_FILE);
				return true;
			} catch (IOException var4) {
				LOGGER.warn("Could not read old whitelist to convert it!", (Throwable)var4);
				return false;
			} catch (class_3321.class_3322 var5) {
				LOGGER.error("Conversion failed, please try again later", (Throwable)var5);
				return false;
			}
		} else {
			return true;
		}
	}

	public static String method_14546(MinecraftServer minecraftServer, String string) {
		if (!ChatUtil.isEmpty(string) && string.length() <= 16) {
			GameProfile gameProfile = minecraftServer.getUserCache().findByName(string);
			if (gameProfile != null && gameProfile.getId() != null) {
				return gameProfile.getId().toString();
			} else if (!minecraftServer.isSinglePlayer() && minecraftServer.isOnlineMode()) {
				final List<GameProfile> list = Lists.<GameProfile>newArrayList();
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					@Override
					public void onProfileLookupSucceeded(GameProfile gameProfile) {
						minecraftServer.getUserCache().add(gameProfile);
						list.add(gameProfile);
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.LOGGER.warn("Could not lookup user whitelist entry for {}", gameProfile.getName(), exception);
					}
				};
				method_14538(minecraftServer, Lists.<String>newArrayList(string), profileLookupCallback);
				return !list.isEmpty() && ((GameProfile)list.get(0)).getId() != null ? ((GameProfile)list.get(0)).getId().toString() : "";
			} else {
				return PlayerEntity.getUuidFromProfile(new GameProfile(null, string)).toString();
			}
		} else {
			return string;
		}
	}

	public static boolean method_14550(MinecraftDedicatedServer minecraftDedicatedServer) {
		final File file = method_14536(minecraftDedicatedServer);
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
						minecraftDedicatedServer.getUserCache().add(gameProfile);
						UUID uUID = gameProfile.getId();
						if (uUID == null) {
							throw new class_3321.class_3322("Missing UUID for user profile " + gameProfile.getName());
						} else {
							this.method_14553(file2, this.method_14554(gameProfile), uUID.toString());
						}
					}

					@Override
					public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
						class_3321.LOGGER.warn("Could not lookup user uuid for {}", gameProfile.getName(), exception);
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
				method_14538(minecraftDedicatedServer, Lists.<String>newArrayList(strings), profileLookupCallback);
				return true;
			} catch (class_3321.class_3322 var12) {
				LOGGER.error("Conversion failed, please try again later", (Throwable)var12);
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
		if (BANNED_PLAYERS_FILE.exists() && BANNED_PLAYERS_FILE.isFile()) {
			bl = true;
		}

		boolean bl2 = false;
		if (BANNED_IPS_FILE.exists() && BANNED_IPS_FILE.isFile()) {
			bl2 = true;
		}

		boolean bl3 = false;
		if (OPS_FILE.exists() && OPS_FILE.isFile()) {
			bl3 = true;
		}

		boolean bl4 = false;
		if (WHITE_LIST_FILE.exists() && WHITE_LIST_FILE.isFile()) {
			bl4 = true;
		}

		if (!bl && !bl2 && !bl3 && !bl4) {
			return true;
		} else {
			LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
			LOGGER.warn("** please remove the following files and restart the server:");
			if (bl) {
				LOGGER.warn("* {}", BANNED_PLAYERS_FILE.getName());
			}

			if (bl2) {
				LOGGER.warn("* {}", BANNED_IPS_FILE.getName());
			}

			if (bl3) {
				LOGGER.warn("* {}", OPS_FILE.getName());
			}

			if (bl4) {
				LOGGER.warn("* {}", WHITE_LIST_FILE.getName());
			}

			return false;
		}
	}

	private static boolean method_14542(MinecraftServer minecraftServer) {
		File file = method_14536(minecraftServer);
		if (!file.exists() || !file.isDirectory() || file.list().length <= 0 && file.delete()) {
			return true;
		} else {
			LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
			LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
			LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", file.getPath());
			return false;
		}
	}

	private static File method_14536(MinecraftServer minecraftServer) {
		String string = minecraftServer.getLevelName();
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
			date2 = BanEntry.DATE_FORMAT.parse(string);
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

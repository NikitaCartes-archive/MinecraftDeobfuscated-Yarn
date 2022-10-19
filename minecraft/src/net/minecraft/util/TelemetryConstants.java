package net.minecraft.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TelemetryConstants {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
	public static final String WORLD_LOADED = "WorldLoaded";
	public static final String SERVER_MODDED = "serverModded";
	public static final String USER_ID = "UserId";
	public static final String CLIENT_ID = "ClientId";
	public static final String DEVICE_SESSION_ID = "deviceSessionId";
	public static final String WORLD_SESSION_ID = "WorldSessionId";
	public static final String EVENT_TIMESTAMP_UTC = "eventTimestampUtc";
	public static final String BUILD_DISPLAY_NAME = "build_display_name";
	public static final String CLIENT_MODDED = "clientModded";
	public static final String SERVER_TYPE = "server_type";
	public static final String BUILD_PLAT = "BuildPlat";
	public static final String PLAT = "Plat";
	public static final String JAVA_VERSION = "javaVersion";
	public static final String PLAYER_GAME_MODE = "PlayerGameMode";
	public static final int SURVIVAL = 0;
	public static final int CREATIVE = 1;
	public static final int ADVENTURE = 2;
	public static final int SPECTATOR = 6;
	public static final int HARDCORE = 99;
	public static final String REALM = "realm";
	public static final String LOCAL = "local";
	public static final String SERVER = "server";
}

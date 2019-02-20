package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Session;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class Realms {
	public static boolean isTouchScreen() {
		return MinecraftClient.getInstance().options.touchscreen;
	}

	public static Proxy getProxy() {
		return MinecraftClient.getInstance().getNetworkProxy();
	}

	public static String sessionId() {
		Session session = MinecraftClient.getInstance().getSession();
		return session == null ? null : session.getSessionId();
	}

	public static String userName() {
		Session session = MinecraftClient.getInstance().getSession();
		return session == null ? null : session.getUsername();
	}

	public static long currentTimeMillis() {
		return SystemUtil.getMeasuringTimeMs();
	}

	public static String getSessionId() {
		return MinecraftClient.getInstance().getSession().getSessionId();
	}

	public static String getUUID() {
		return MinecraftClient.getInstance().getSession().getUuid();
	}

	public static String getName() {
		return MinecraftClient.getInstance().getSession().getUsername();
	}

	public static String uuidToName(String string) {
		return MinecraftClient.getInstance().getSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), null), false).getName();
	}

	public static <V> CompletableFuture<V> execute(Supplier<V> supplier) {
		return MinecraftClient.getInstance().executeFuture(supplier);
	}

	public static void execute(Runnable runnable) {
		MinecraftClient.getInstance().executeFuture(runnable);
	}

	public static void setScreen(RealmsScreen realmsScreen) {
		execute((Supplier)(() -> {
			setScreenDirect(realmsScreen);
			return null;
		}));
	}

	public static void setScreenDirect(RealmsScreen realmsScreen) {
		MinecraftClient.getInstance().openScreen(realmsScreen.getProxy());
	}

	public static String getGameDirectoryPath() {
		return MinecraftClient.getInstance().runDirectory.getAbsolutePath();
	}

	public static int survivalId() {
		return GameMode.field_9215.getId();
	}

	public static int creativeId() {
		return GameMode.field_9220.getId();
	}

	public static int adventureId() {
		return GameMode.field_9216.getId();
	}

	public static int spectatorId() {
		return GameMode.field_9219.getId();
	}

	public static void setConnectedToRealms(boolean bl) {
		MinecraftClient.getInstance().setConnectedToRealms(bl);
	}

	public static CompletableFuture<?> downloadResourcePack(String string, String string2) {
		return MinecraftClient.getInstance().getResourcePackDownloader().download(string, string2);
	}

	public static void clearResourcePack() {
		MinecraftClient.getInstance().getResourcePackDownloader().clear();
	}

	public static boolean getRealmsNotificationsEnabled() {
		return MinecraftClient.getInstance().options.realmsNotifications;
	}

	public static boolean inTitleScreen() {
		return MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().currentScreen instanceof MainMenuScreen;
	}

	public static void deletePlayerTag(File file) {
		if (file.exists()) {
			try {
				CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
				CompoundTag compoundTag2 = compoundTag.getCompound("Data");
				compoundTag2.remove("Player");
				NbtIo.writeCompressed(compoundTag, new FileOutputStream(file));
			} catch (Exception var3) {
				var3.printStackTrace();
			}
		}
	}
}

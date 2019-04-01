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
import net.minecraft.class_156;
import net.minecraft.class_1934;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_442;

@Environment(EnvType.CLIENT)
public class Realms {
	public static boolean isTouchScreen() {
		return class_310.method_1551().field_1690.field_1854;
	}

	public static Proxy getProxy() {
		return class_310.method_1551().method_1487();
	}

	public static String sessionId() {
		class_320 lv = class_310.method_1551().method_1548();
		return lv == null ? null : lv.method_1675();
	}

	public static String userName() {
		class_320 lv = class_310.method_1551().method_1548();
		return lv == null ? null : lv.method_1676();
	}

	public static long currentTimeMillis() {
		return class_156.method_658();
	}

	public static String getSessionId() {
		return class_310.method_1551().method_1548().method_1675();
	}

	public static String getUUID() {
		return class_310.method_1551().method_1548().method_1673();
	}

	public static String getName() {
		return class_310.method_1551().method_1548().method_1676();
	}

	public static String uuidToName(String string) {
		return class_310.method_1551().method_1495().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), null), false).getName();
	}

	public static <V> CompletableFuture<V> execute(Supplier<V> supplier) {
		return class_310.method_1551().method_5385(supplier);
	}

	public static void execute(Runnable runnable) {
		class_310.method_1551().execute(runnable);
	}

	public static void setScreen(RealmsScreen realmsScreen) {
		execute((Supplier)(() -> {
			setScreenDirect(realmsScreen);
			return null;
		}));
	}

	public static void setScreenDirect(RealmsScreen realmsScreen) {
		class_310.method_1551().method_1507(realmsScreen.getProxy());
	}

	public static String getGameDirectoryPath() {
		return class_310.method_1551().field_1697.getAbsolutePath();
	}

	public static int survivalId() {
		return class_1934.field_9215.method_8379();
	}

	public static int creativeId() {
		return class_1934.field_9220.method_8379();
	}

	public static int adventureId() {
		return class_1934.field_9216.method_8379();
	}

	public static int spectatorId() {
		return class_1934.field_9219.method_8379();
	}

	public static void setConnectedToRealms(boolean bl) {
		class_310.method_1551().method_1537(bl);
	}

	public static CompletableFuture<?> downloadResourcePack(String string, String string2) {
		return class_310.method_1551().method_1516().method_4640(string, string2);
	}

	public static void clearResourcePack() {
		class_310.method_1551().method_1516().method_4642();
	}

	public static boolean getRealmsNotificationsEnabled() {
		return class_310.method_1551().field_1690.field_1830;
	}

	public static boolean inTitleScreen() {
		return class_310.method_1551().field_1755 != null && class_310.method_1551().field_1755 instanceof class_442;
	}

	public static void deletePlayerTag(File file) {
		if (file.exists()) {
			try {
				class_2487 lv = class_2507.method_10629(new FileInputStream(file));
				class_2487 lv2 = lv.method_10562("Data");
				lv2.method_10551("Player");
				class_2507.method_10634(lv, new FileOutputStream(file));
			} catch (Exception var3) {
				var3.printStackTrace();
			}
		}
	}
}

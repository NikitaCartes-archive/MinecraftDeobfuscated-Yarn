package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.command.TranslatableBuiltInExceptions;

public class SharedConstants {
	@Deprecated
	public static final boolean IS_DEVELOPMENT_VERSION = true;
	@Deprecated
	public static final int WORLD_VERSION = 2726;
	@Deprecated
	public static final String VERSION_NAME = "1.17.1-pre2";
	@Deprecated
	public static final String RELEASE_TARGET = "1.17.1";
	@Deprecated
	public static final int RELEASE_TARGET_PROTOCOL_VERSION = 756;
	@Deprecated
	public static final int field_29736 = 37;
	public static final int SNBT_TOO_OLD_THRESHOLD = 2678;
	private static final int field_29708 = 30;
	@Deprecated
	public static final int field_29738 = 7;
	@Deprecated
	public static final int field_29739 = 7;
	public static final String DATA_VERSION_KEY = "DataVersion";
	public static final boolean field_33711 = false;
	public static final boolean field_33712 = false;
	public static final boolean field_29741 = false;
	public static final boolean field_29742 = false;
	public static final boolean field_33556 = false;
	public static final boolean field_29743 = false;
	public static final boolean field_29744 = false;
	public static final boolean field_29745 = false;
	public static final boolean field_33851 = false;
	public static final boolean field_29746 = false;
	public static final boolean field_29747 = false;
	public static final boolean field_33557 = false;
	public static final boolean field_29748 = false;
	public static final boolean field_33753 = false;
	public static final boolean field_29749 = false;
	public static final boolean field_29750 = false;
	public static final boolean field_29751 = false;
	public static final boolean field_29752 = false;
	public static final boolean field_29753 = false;
	public static final boolean field_29754 = false;
	public static final boolean field_29755 = false;
	public static final boolean field_29756 = false;
	public static final boolean field_29676 = false;
	public static final boolean field_29677 = false;
	public static final boolean field_29678 = false;
	public static final boolean field_29679 = false;
	public static final boolean field_29680 = false;
	public static final boolean field_29681 = false;
	public static final boolean field_29682 = false;
	public static final boolean field_29683 = false;
	public static final boolean field_29684 = false;
	public static final boolean field_29685 = false;
	public static final boolean field_29686 = false;
	public static final boolean field_29687 = false;
	public static final boolean field_29688 = false;
	public static final boolean field_29689 = false;
	public static final boolean field_29690 = false;
	public static final boolean field_29691 = false;
	public static final boolean field_29692 = false;
	public static final boolean field_29693 = false;
	public static final boolean field_29694 = false;
	public static final boolean field_29695 = false;
	public static final boolean field_29696 = false;
	public static final boolean field_29697 = false;
	public static final boolean field_29698 = false;
	public static final boolean field_29699 = false;
	public static final boolean field_29700 = false;
	public static final boolean field_33554 = false;
	public static final boolean field_29701 = false;
	public static final boolean field_29710 = false;
	public static final boolean field_29711 = false;
	public static final boolean field_29712 = false;
	public static final boolean field_29713 = false;
	public static final boolean field_29714 = false;
	public static final boolean field_29715 = false;
	public static final boolean field_29716 = false;
	public static final boolean field_29717 = false;
	public static final boolean field_29718 = false;
	public static final boolean field_33555 = false;
	public static final boolean field_33640 = false;
	public static final int DEFAULT_PORT = 25565;
	public static final boolean field_29720 = false;
	public static final boolean field_29721 = false;
	public static final int field_29722 = 0;
	public static final int field_29723 = 0;
	public static final Level RESOURCE_LEAK_DETECTOR_DISABLED = Level.DISABLED;
	public static final boolean field_29724 = false;
	public static final boolean field_29725 = false;
	public static final boolean field_29726 = false;
	public static final boolean field_29727 = false;
	public static final float field_29728 = 0.15F;
	public static final long field_22251 = Duration.ofMillis(300L).toNanos();
	/**
	 * Specifies whether Minecraft should use choice type registrations from the game's schema when entity types or block entity types are created.
	 */
	public static boolean useChoiceTypeRegistrations = true;
	public static boolean isDevelopment;
	public static final int CHUNK_WIDTH = 16;
	public static final int DEFAULT_WORLD_HEIGHT = 256;
	public static final int COMMAND_MAX_LENGTH = 32500;
	public static final char[] INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
	public static final int TICKS_PER_SECOND = 20;
	public static final int TICKS_PER_MINUTE = 1200;
	public static final int TICKS_PER_IN_GAME_DAY = 24000;
	public static final float field_29705 = 1365.3334F;
	public static final float field_29706 = 0.87890625F;
	public static final float field_29707 = 17.578125F;
	@Nullable
	private static GameVersion gameVersion;

	public static boolean isValidChar(char chr) {
		return chr != 167 && chr >= ' ' && chr != 127;
	}

	public static String stripInvalidChars(String s) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : s.toCharArray()) {
			if (isValidChar(c)) {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	public static void setGameVersion(GameVersion version) {
		if (gameVersion == null) {
			gameVersion = version;
		} else if (version != gameVersion) {
			throw new IllegalStateException("Cannot override the current game version!");
		}
	}

	public static void createGameVersion() {
		if (gameVersion == null) {
			gameVersion = MinecraftVersion.create();
		}
	}

	public static GameVersion getGameVersion() {
		if (gameVersion == null) {
			throw new IllegalStateException("Game version not set");
		} else {
			return gameVersion;
		}
	}

	public static int getProtocolVersion() {
		return 1073741861;
	}

	static {
		ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
		CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
		CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableBuiltInExceptions();
	}
}

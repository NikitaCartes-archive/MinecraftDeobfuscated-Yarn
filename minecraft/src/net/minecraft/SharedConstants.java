package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.command.TranslatableBuiltInExceptions;
import net.minecraft.datafixer.DataFixerPhase;
import net.minecraft.util.math.ChunkPos;

public class SharedConstants {
	@Deprecated
	public static final boolean IS_DEVELOPMENT_VERSION = true;
	@Deprecated
	public static final int WORLD_VERSION = 3089;
	@Deprecated
	public static final String CURRENT_SERIES = "main";
	@Deprecated
	public static final String VERSION_NAME = "22w15a";
	@Deprecated
	public static final String RELEASE_TARGET = "1.19";
	@Deprecated
	public static final int RELEASE_TARGET_PROTOCOL_VERSION = 759;
	@Deprecated
	public static final int field_29736 = 79;
	public static final int SNBT_TOO_OLD_THRESHOLD = 3075;
	private static final int field_29708 = 30;
	public static final boolean field_36325 = true;
	@Deprecated
	public static final int RESOURCE_PACK_VERSION = 9;
	@Deprecated
	public static final int DATA_PACK_VERSION = 10;
	public static final String DATA_VERSION_KEY = "DataVersion";
	public static final boolean field_33712 = false;
	public static final boolean field_29743 = false;
	public static final boolean field_29744 = false;
	public static final boolean field_29745 = false;
	public static final boolean field_33851 = false;
	public static final boolean field_29747 = false;
	public static final boolean field_35006 = false;
	public static final boolean field_35563 = false;
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
	public static final boolean field_37273 = false;
	public static final boolean field_34368 = false;
	public static final boolean field_29701 = false;
	public static final boolean field_29710 = false;
	public static final boolean field_34369 = false;
	public static final boolean field_34370 = false;
	public static boolean DEBUG_BIOME_SOURCE = false;
	public static boolean DEBUG_NOISE = false;
	public static final boolean field_29711 = false;
	public static final boolean field_29712 = false;
	public static final boolean field_29713 = false;
	public static final boolean field_29715 = false;
	public static final boolean field_29716 = false;
	public static final boolean field_29717 = false;
	public static final boolean field_29718 = false;
	public static final boolean field_33555 = false;
	public static final boolean field_35438 = false;
	public static final boolean field_35439 = false;
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
	public static final boolean field_35652 = false;
	public static final long field_22251 = Duration.ofMillis(300L).toNanos();
	/**
	 * Specifies whether Minecraft should use choice type registrations from the game's schema when entity types or block entity types are created.
	 */
	public static boolean useChoiceTypeRegistrations = true;
	public static boolean isDevelopment;
	public static DataFixerPhase dataFixerPhase = DataFixerPhase.UNINITIALIZED_UNOPTIMIZED;
	public static final int CHUNK_WIDTH = 16;
	public static final int DEFAULT_WORLD_HEIGHT = 256;
	public static final int COMMAND_MAX_LENGTH = 32500;
	public static final int field_38052 = 1000000;
	public static final char[] INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
	public static final int TICKS_PER_SECOND = 20;
	public static final int TICKS_PER_MINUTE = 1200;
	public static final int TICKS_PER_IN_GAME_DAY = 24000;
	public static final float field_29705 = 1365.3334F;
	public static final float field_29706 = 0.87890625F;
	public static final float field_29707 = 17.578125F;
	@Nullable
	private static GameVersion gameVersion;

	/**
	 * {@return true if the character is not {@linkplain
	 * net.minecraft.util.Formatting#FORMATTING_CODE_PREFIX the formatting code
	 * prefix} (&bsol;u00a7), C0 control code (&bsol;u0000 to &bsol;u001f) or
	 * delete (&bsol;u007f)}
	 * 
	 * @apiNote This method is used to determine if the server should
	 * accept a chat message sent from client.
	 * 
	 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onChatMessage
	 */
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

	public static void setGameVersion(GameVersion gameVersion) {
		if (SharedConstants.gameVersion == null) {
			SharedConstants.gameVersion = gameVersion;
		} else if (gameVersion != SharedConstants.gameVersion) {
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
		return 1073741903;
	}

	public static boolean method_37896(ChunkPos chunkPos) {
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		return !DEBUG_BIOME_SOURCE ? false : i > 8192 || i < 0 || j > 1024 || j < 0;
	}

	public static void method_43250() {
		dataFixerPhase = switch (dataFixerPhase) {
			case INITIALIZED_UNOPTIMIZED -> throw new IllegalStateException("Tried to enable datafixer optimization after unoptimized initialization");
			case INITIALIZED_OPTIMIZED -> DataFixerPhase.INITIALIZED_OPTIMIZED;
			default -> DataFixerPhase.UNINITIALIZED_OPTIMIZED;
		};
	}

	static {
		ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
		CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
		CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableBuiltInExceptions();
	}
}

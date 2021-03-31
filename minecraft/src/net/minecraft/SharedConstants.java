package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.time.Duration;
import net.minecraft.command.TranslatableBuiltInExceptions;

public class SharedConstants {
	@Deprecated
	public static final boolean field_29709 = true;
	@Deprecated
	public static final int field_29732 = 2705;
	@Deprecated
	public static final String field_29733 = "21w13a";
	@Deprecated
	public static final String field_29734 = "1.17";
	@Deprecated
	public static final int field_29735 = 755;
	@Deprecated
	public static final int field_29736 = 20;
	public static final int field_29737 = 2678;
	private static final int field_29708 = 30;
	@Deprecated
	public static final int field_29738 = 7;
	@Deprecated
	public static final int field_29739 = 7;
	public static final String field_29740 = "DataVersion";
	public static final boolean field_29741 = true;
	public static final boolean field_29742 = true;
	public static final boolean field_29743 = false;
	public static final boolean field_29744 = false;
	public static final boolean field_29745 = false;
	public static final boolean field_29746 = false;
	public static final boolean field_29747 = false;
	public static final boolean field_29748 = false;
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
	public static final int field_29719 = 25565;
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
	public static final int field_29729 = 16;
	public static final int field_29730 = 256;
	public static final int field_29731 = 32500;
	public static final char[] INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
	public static final int field_29702 = 20;
	public static final int field_29703 = 1200;
	public static final int field_29704 = 24000;
	public static final float field_29705 = 1365.3334F;
	public static final float field_29706 = 0.87890625F;
	public static final float field_29707 = 17.578125F;
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

	public static void method_34872(GameVersion gameVersion) {
		if (SharedConstants.gameVersion == null) {
			SharedConstants.gameVersion = gameVersion;
		} else if (gameVersion != SharedConstants.gameVersion) {
			throw new IllegalStateException("Cannot override the current game version!");
		}
	}

	public static GameVersion getGameVersion() {
		if (gameVersion == null) {
			gameVersion = MinecraftVersion.create();
		}

		return gameVersion;
	}

	public static int getProtocolVersion() {
		return 1073741844;
	}

	static {
		ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
		CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
		CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableBuiltInExceptions();
	}
}

package net.minecraft;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;

public class class_3806 extends class_3808<class_3806> {
	public final boolean field_16813 = this.method_16740("online-mode", true);
	public final boolean field_16839 = this.method_16740("prevent-proxy-connections", false);
	public final String field_16829 = this.method_16732("server-ip", "");
	public final boolean field_16836 = this.method_16740("spawn-animals", true);
	public final boolean field_16809 = this.method_16740("spawn-npcs", true);
	public final boolean field_16833 = this.method_16740("pvp", true);
	public final boolean field_16807 = this.method_16740("allow-flight", false);
	public final String field_16801 = this.method_16732("resource-pack", "");
	public final String field_16825 = this.method_16732("motd", "A Minecraft Server");
	public final boolean field_16827 = this.method_16740("force-gamemode", false);
	public final boolean field_16805 = this.method_16740("enforce-whitelist", false);
	public final boolean field_16826 = this.method_16740("generate-structures", true);
	public final Difficulty field_16840 = this.method_16741(
		"difficulty", method_16722(Difficulty::byId, Difficulty::method_16691), Difficulty::getTranslationKey, Difficulty.EASY
	);
	public final GameMode field_16841 = this.method_16741("gamemode", method_16722(GameMode::byId, GameMode::byName), GameMode::getName, GameMode.field_9215);
	public final String field_16820 = this.method_16732("level-name", "world");
	public final String field_16843 = this.method_16732("level-seed", "");
	public final LevelGeneratorType field_16803 = this.method_16741(
		"level-type", LevelGeneratorType::getTypeFromName, LevelGeneratorType::getName, LevelGeneratorType.DEFAULT
	);
	public final String field_16822 = this.method_16732("generator-settings", "");
	public final int field_16837 = this.method_16726("server-port", 25565);
	public final int field_16810 = this.method_16720("max-build-height", integer -> MathHelper.clamp((integer + 8) / 16 * 16, 64, 256), 256);
	public final Boolean field_16830 = this.method_16736("announce-player-achievements");
	public final boolean field_16819 = this.method_16740("enable-query", false);
	public final int field_16831 = this.method_16726("query.port", 25565);
	public final boolean field_16818 = this.method_16740("enable-rcon", false);
	public final int field_16828 = this.method_16726("rcon.port", 25575);
	public final String field_16823 = this.method_16732("rcon.password", "");
	public final String field_16834 = this.method_16738("resource-pack-hash");
	public final String field_16821 = this.method_16732("resource-pack-sha1", "");
	public final boolean field_16838 = this.method_16740("hardcore", false);
	public final boolean field_16811 = this.method_16740("allow-nether", true);
	public final boolean field_16835 = this.method_16740("spawn-monsters", true);
	public final boolean field_16808;
	public final boolean field_16832;
	public final boolean field_16806;
	public final int field_16816;
	public final int field_16845;
	public final long field_16815;
	public final int field_16844;
	public final int field_16814;
	public final int field_16842;
	public final boolean field_16824;
	public final boolean field_16802;
	public final int field_16812;
	public final class_3808<class_3806>.class_3809<Integer> field_16817;
	public final class_3808<class_3806>.class_3809<Boolean> field_16804;

	public class_3806(Properties properties) {
		super(properties);
		if (this.method_16740("snooper-enabled", true)) {
		}

		this.field_16808 = false;
		this.field_16832 = this.method_16740("use-native-transport", true);
		this.field_16806 = this.method_16740("enable-command-block", false);
		this.field_16816 = this.method_16726("spawn-protection", 16);
		this.field_16845 = this.method_16726("op-permission-level", 4);
		this.field_16815 = this.method_16725("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
		this.field_16844 = this.method_16726("view-distance", 10);
		this.field_16814 = this.method_16726("max-players", 20);
		this.field_16842 = this.method_16726("network-compression-threshold", 256);
		this.field_16824 = this.method_16740("broadcast-rcon-to-ops", true);
		this.field_16802 = this.method_16740("broadcast-console-to-ops", true);
		this.field_16812 = this.method_16720("max-world-size", integer -> MathHelper.clamp(integer, 1, 29999984), 29999984);
		this.field_16817 = this.method_16743("player-idle-timeout", 0);
		this.field_16804 = this.method_16744("white-list", false);
	}

	public static class_3806 method_16714(Path path) {
		return new class_3806(method_16727(path));
	}

	protected class_3806 method_16713(Properties properties) {
		return new class_3806(properties);
	}
}

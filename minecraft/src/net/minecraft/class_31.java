package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

public class class_31 {
	private String field_162;
	private int field_183;
	private boolean field_194;
	public static final class_1267 field_175 = class_1267.field_5802;
	private long field_153;
	private class_1942 field_163 = class_1942.field_9265;
	private class_2487 field_166 = new class_2487();
	@Nullable
	private String field_152;
	private int field_151;
	private int field_167;
	private int field_182;
	private long field_189;
	private long field_198;
	private long field_165;
	private long field_181;
	@Nullable
	private final DataFixer field_184;
	private final int field_196;
	private boolean field_172;
	private class_2487 field_170;
	private int field_186;
	private String field_169;
	private int field_158;
	private int field_176;
	private boolean field_190;
	private int field_192;
	private boolean field_168;
	private int field_173;
	private class_1934 field_179;
	private boolean field_195;
	private boolean field_159;
	private boolean field_177;
	private boolean field_185;
	private class_1267 field_174;
	private boolean field_157;
	private double field_164;
	private double field_180;
	private double field_188 = 6.0E7;
	private long field_199;
	private double field_160;
	private double field_178 = 5.0;
	private double field_187 = 0.2;
	private int field_197 = 5;
	private int field_161 = 15;
	private final Set<String> field_155 = Sets.<String>newHashSet();
	private final Set<String> field_171 = Sets.<String>newLinkedHashSet();
	private final Map<class_2874, class_2487> field_193 = Maps.<class_2874, class_2487>newIdentityHashMap();
	private class_2487 field_156;
	private int field_17736;
	private int field_17737;
	private UUID field_17738;
	private final class_1928 field_154 = new class_1928();
	private final class_236<MinecraftServer> field_191 = new class_236<>(class_233.field_1306);

	protected class_31() {
		this.field_184 = null;
		this.field_196 = class_155.method_16673().getWorldVersion();
		this.method_175(new class_2487());
	}

	public class_31(class_2487 arg, DataFixer dataFixer, int i, @Nullable class_2487 arg2) {
		this.field_184 = dataFixer;
		if (arg.method_10573("Version", 10)) {
			class_2487 lv = arg.method_10562("Version");
			this.field_162 = lv.method_10558("Name");
			this.field_183 = lv.method_10550("Id");
			this.field_194 = lv.method_10577("Snapshot");
		}

		this.field_153 = arg.method_10537("RandomSeed");
		if (arg.method_10573("generatorName", 8)) {
			String string = arg.method_10558("generatorName");
			this.field_163 = class_1942.method_8639(string);
			if (this.field_163 == null) {
				this.field_163 = class_1942.field_9265;
			} else if (this.field_163 == class_1942.field_9278) {
				this.field_152 = arg.method_10558("generatorOptions");
			} else if (this.field_163.method_8643()) {
				int j = 0;
				if (arg.method_10573("generatorVersion", 99)) {
					j = arg.method_10550("generatorVersion");
				}

				this.field_163 = this.field_163.method_8632(j);
			}

			this.method_175(arg.method_10562("generatorOptions"));
		}

		this.field_179 = class_1934.method_8384(arg.method_10550("GameType"));
		if (arg.method_10573("legacy_custom_options", 8)) {
			this.field_152 = arg.method_10558("legacy_custom_options");
		}

		if (arg.method_10573("MapFeatures", 99)) {
			this.field_195 = arg.method_10577("MapFeatures");
		} else {
			this.field_195 = true;
		}

		this.field_151 = arg.method_10550("SpawnX");
		this.field_167 = arg.method_10550("SpawnY");
		this.field_182 = arg.method_10550("SpawnZ");
		this.field_189 = arg.method_10537("Time");
		if (arg.method_10573("DayTime", 99)) {
			this.field_198 = arg.method_10537("DayTime");
		} else {
			this.field_198 = this.field_189;
		}

		this.field_165 = arg.method_10537("LastPlayed");
		this.field_181 = arg.method_10537("SizeOnDisk");
		this.field_169 = arg.method_10558("LevelName");
		this.field_158 = arg.method_10550("version");
		this.field_176 = arg.method_10550("clearWeatherTime");
		this.field_192 = arg.method_10550("rainTime");
		this.field_190 = arg.method_10577("raining");
		this.field_173 = arg.method_10550("thunderTime");
		this.field_168 = arg.method_10577("thundering");
		this.field_159 = arg.method_10577("hardcore");
		if (arg.method_10573("initialized", 99)) {
			this.field_185 = arg.method_10577("initialized");
		} else {
			this.field_185 = true;
		}

		if (arg.method_10573("allowCommands", 99)) {
			this.field_177 = arg.method_10577("allowCommands");
		} else {
			this.field_177 = this.field_179 == class_1934.field_9220;
		}

		this.field_196 = i;
		if (arg2 != null) {
			this.field_170 = arg2;
		}

		if (arg.method_10573("GameRules", 10)) {
			this.field_154.method_8357(arg.method_10562("GameRules"));
		}

		if (arg.method_10573("Difficulty", 99)) {
			this.field_174 = class_1267.method_5462(arg.method_10571("Difficulty"));
		}

		if (arg.method_10573("DifficultyLocked", 1)) {
			this.field_157 = arg.method_10577("DifficultyLocked");
		}

		if (arg.method_10573("BorderCenterX", 99)) {
			this.field_164 = arg.method_10574("BorderCenterX");
		}

		if (arg.method_10573("BorderCenterZ", 99)) {
			this.field_180 = arg.method_10574("BorderCenterZ");
		}

		if (arg.method_10573("BorderSize", 99)) {
			this.field_188 = arg.method_10574("BorderSize");
		}

		if (arg.method_10573("BorderSizeLerpTime", 99)) {
			this.field_199 = arg.method_10537("BorderSizeLerpTime");
		}

		if (arg.method_10573("BorderSizeLerpTarget", 99)) {
			this.field_160 = arg.method_10574("BorderSizeLerpTarget");
		}

		if (arg.method_10573("BorderSafeZone", 99)) {
			this.field_178 = arg.method_10574("BorderSafeZone");
		}

		if (arg.method_10573("BorderDamagePerBlock", 99)) {
			this.field_187 = arg.method_10574("BorderDamagePerBlock");
		}

		if (arg.method_10573("BorderWarningBlocks", 99)) {
			this.field_197 = arg.method_10550("BorderWarningBlocks");
		}

		if (arg.method_10573("BorderWarningTime", 99)) {
			this.field_161 = arg.method_10550("BorderWarningTime");
		}

		if (arg.method_10573("DimensionData", 10)) {
			class_2487 lv = arg.method_10562("DimensionData");

			for (String string2 : lv.method_10541()) {
				this.field_193.put(class_2874.method_12490(Integer.parseInt(string2)), lv.method_10562(string2));
			}
		}

		if (arg.method_10573("DataPacks", 10)) {
			class_2487 lv = arg.method_10562("DataPacks");
			class_2499 lv2 = lv.method_10554("Disabled", 8);

			for (int k = 0; k < lv2.size(); k++) {
				this.field_155.add(lv2.method_10608(k));
			}

			class_2499 lv3 = lv.method_10554("Enabled", 8);

			for (int l = 0; l < lv3.size(); l++) {
				this.field_171.add(lv3.method_10608(l));
			}
		}

		if (arg.method_10573("CustomBossEvents", 10)) {
			this.field_156 = arg.method_10562("CustomBossEvents");
		}

		if (arg.method_10573("ScheduledEvents", 9)) {
			this.field_191.method_979(arg.method_10554("ScheduledEvents", 10));
		}

		if (arg.method_10573("WanderingTraderSpawnDelay", 99)) {
			this.field_17736 = arg.method_10550("WanderingTraderSpawnDelay");
		}

		if (arg.method_10573("WanderingTraderSpawnChance", 99)) {
			this.field_17737 = arg.method_10550("WanderingTraderSpawnChance");
		}

		if (arg.method_10573("WanderingTraderId", 8)) {
			this.field_17738 = UUID.fromString(arg.method_10558("WanderingTraderId"));
		}
	}

	public class_31(class_1940 arg, String string) {
		this.field_184 = null;
		this.field_196 = class_155.method_16673().getWorldVersion();
		this.method_140(arg);
		this.field_169 = string;
		this.field_174 = field_175;
		this.field_185 = false;
	}

	public void method_140(class_1940 arg) {
		this.field_153 = arg.method_8577();
		this.field_179 = arg.method_8574();
		this.field_195 = arg.method_8583();
		this.field_159 = arg.method_8582();
		this.field_163 = arg.method_8576();
		this.method_175((class_2487)Dynamic.convert(JsonOps.INSTANCE, class_2509.field_11560, arg.method_8584()));
		this.field_177 = arg.method_8573();
	}

	public class_2487 method_163(@Nullable class_2487 arg) {
		this.method_185();
		if (arg == null) {
			arg = this.field_170;
		}

		class_2487 lv = new class_2487();
		this.method_158(lv, arg);
		return lv;
	}

	private void method_158(class_2487 arg, class_2487 arg2) {
		class_2487 lv = new class_2487();
		lv.method_10582("Name", class_155.method_16673().getName());
		lv.method_10569("Id", class_155.method_16673().getWorldVersion());
		lv.method_10556("Snapshot", !class_155.method_16673().isStable());
		arg.method_10566("Version", lv);
		arg.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
		arg.method_10544("RandomSeed", this.field_153);
		arg.method_10582("generatorName", this.field_163.method_8638());
		arg.method_10569("generatorVersion", this.field_163.method_8636());
		if (!this.field_166.isEmpty()) {
			arg.method_10566("generatorOptions", this.field_166);
		}

		if (this.field_152 != null) {
			arg.method_10582("legacy_custom_options", this.field_152);
		}

		arg.method_10569("GameType", this.field_179.method_8379());
		arg.method_10556("MapFeatures", this.field_195);
		arg.method_10569("SpawnX", this.field_151);
		arg.method_10569("SpawnY", this.field_167);
		arg.method_10569("SpawnZ", this.field_182);
		arg.method_10544("Time", this.field_189);
		arg.method_10544("DayTime", this.field_198);
		arg.method_10544("SizeOnDisk", this.field_181);
		arg.method_10544("LastPlayed", class_156.method_659());
		arg.method_10582("LevelName", this.field_169);
		arg.method_10569("version", this.field_158);
		arg.method_10569("clearWeatherTime", this.field_176);
		arg.method_10569("rainTime", this.field_192);
		arg.method_10556("raining", this.field_190);
		arg.method_10569("thunderTime", this.field_173);
		arg.method_10556("thundering", this.field_168);
		arg.method_10556("hardcore", this.field_159);
		arg.method_10556("allowCommands", this.field_177);
		arg.method_10556("initialized", this.field_185);
		arg.method_10549("BorderCenterX", this.field_164);
		arg.method_10549("BorderCenterZ", this.field_180);
		arg.method_10549("BorderSize", this.field_188);
		arg.method_10544("BorderSizeLerpTime", this.field_199);
		arg.method_10549("BorderSafeZone", this.field_178);
		arg.method_10549("BorderDamagePerBlock", this.field_187);
		arg.method_10549("BorderSizeLerpTarget", this.field_160);
		arg.method_10549("BorderWarningBlocks", (double)this.field_197);
		arg.method_10549("BorderWarningTime", (double)this.field_161);
		if (this.field_174 != null) {
			arg.method_10567("Difficulty", (byte)this.field_174.method_5461());
		}

		arg.method_10556("DifficultyLocked", this.field_157);
		arg.method_10566("GameRules", this.field_154.method_8358());
		class_2487 lv2 = new class_2487();

		for (Entry<class_2874, class_2487> entry : this.field_193.entrySet()) {
			lv2.method_10566(String.valueOf(((class_2874)entry.getKey()).method_12484()), (class_2520)entry.getValue());
		}

		arg.method_10566("DimensionData", lv2);
		if (arg2 != null) {
			arg.method_10566("Player", arg2);
		}

		class_2487 lv3 = new class_2487();
		class_2499 lv4 = new class_2499();

		for (String string : this.field_171) {
			lv4.add(new class_2519(string));
		}

		lv3.method_10566("Enabled", lv4);
		class_2499 lv5 = new class_2499();

		for (String string2 : this.field_155) {
			lv5.add(new class_2519(string2));
		}

		lv3.method_10566("Disabled", lv5);
		arg.method_10566("DataPacks", lv3);
		if (this.field_156 != null) {
			arg.method_10566("CustomBossEvents", this.field_156);
		}

		arg.method_10566("ScheduledEvents", this.field_191.method_982());
		arg.method_10569("WanderingTraderSpawnDelay", this.field_17736);
		arg.method_10569("WanderingTraderSpawnChance", this.field_17737);
		if (this.field_17738 != null) {
			arg.method_10582("WanderingTraderId", this.field_17738.toString());
		}
	}

	public long method_184() {
		return this.field_153;
	}

	public int method_215() {
		return this.field_151;
	}

	public int method_144() {
		return this.field_167;
	}

	public int method_166() {
		return this.field_182;
	}

	public long method_188() {
		return this.field_189;
	}

	public long method_217() {
		return this.field_198;
	}

	private void method_185() {
		if (!this.field_172 && this.field_170 != null) {
			if (this.field_196 < class_155.method_16673().getWorldVersion()) {
				if (this.field_184 == null) {
					throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
				}

				this.field_170 = class_2512.method_10688(this.field_184, DataFixTypes.PLAYER, this.field_170, this.field_196);
			}

			this.field_186 = this.field_170.method_10550("Dimension");
			this.field_172 = true;
		}
	}

	public class_2487 method_226() {
		this.method_185();
		return this.field_170;
	}

	@Environment(EnvType.CLIENT)
	public int method_218() {
		this.method_185();
		return this.field_186;
	}

	@Environment(EnvType.CLIENT)
	public void method_212(int i) {
		this.field_151 = i;
	}

	@Environment(EnvType.CLIENT)
	public void method_213(int i) {
		this.field_167 = i;
	}

	@Environment(EnvType.CLIENT)
	public void method_154(int i) {
		this.field_182 = i;
	}

	public void method_177(long l) {
		this.field_189 = l;
	}

	public void method_165(long l) {
		this.field_198 = l;
	}

	public void method_187(class_2338 arg) {
		this.field_151 = arg.method_10263();
		this.field_167 = arg.method_10264();
		this.field_182 = arg.method_10260();
	}

	public String method_150() {
		return this.field_169;
	}

	public void method_182(String string) {
		this.field_169 = string;
	}

	public int method_168() {
		return this.field_158;
	}

	public void method_142(int i) {
		this.field_158 = i;
	}

	@Environment(EnvType.CLIENT)
	public long method_191() {
		return this.field_165;
	}

	public int method_155() {
		return this.field_176;
	}

	public void method_167(int i) {
		this.field_176 = i;
	}

	public boolean method_203() {
		return this.field_168;
	}

	public void method_147(boolean bl) {
		this.field_168 = bl;
	}

	public int method_145() {
		return this.field_173;
	}

	public void method_173(int i) {
		this.field_173 = i;
	}

	public boolean method_156() {
		return this.field_190;
	}

	public void method_157(boolean bl) {
		this.field_190 = bl;
	}

	public int method_190() {
		return this.field_192;
	}

	public void method_164(int i) {
		this.field_192 = i;
	}

	public class_1934 method_210() {
		return this.field_179;
	}

	public boolean method_220() {
		return this.field_195;
	}

	public void method_196(boolean bl) {
		this.field_195 = bl;
	}

	public void method_193(class_1934 arg) {
		this.field_179 = arg;
	}

	public boolean method_152() {
		return this.field_159;
	}

	public void method_198(boolean bl) {
		this.field_159 = bl;
	}

	public class_1942 method_153() {
		return this.field_163;
	}

	public void method_225(class_1942 arg) {
		this.field_163 = arg;
	}

	public class_2487 method_169() {
		return this.field_166;
	}

	public void method_175(class_2487 arg) {
		this.field_166 = arg;
	}

	public boolean method_194() {
		return this.field_177;
	}

	public void method_211(boolean bl) {
		this.field_177 = bl;
	}

	public boolean method_222() {
		return this.field_185;
	}

	public void method_223(boolean bl) {
		this.field_185 = bl;
	}

	public class_1928 method_146() {
		return this.field_154;
	}

	public double method_204() {
		return this.field_164;
	}

	public double method_139() {
		return this.field_180;
	}

	public double method_206() {
		return this.field_188;
	}

	public void method_162(double d) {
		this.field_188 = d;
	}

	public long method_183() {
		return this.field_199;
	}

	public void method_195(long l) {
		this.field_199 = l;
	}

	public double method_159() {
		return this.field_160;
	}

	public void method_174(double d) {
		this.field_160 = d;
	}

	public void method_189(double d) {
		this.field_180 = d;
	}

	public void method_200(double d) {
		this.field_164 = d;
	}

	public double method_178() {
		return this.field_178;
	}

	public void method_216(double d) {
		this.field_178 = d;
	}

	public double method_202() {
		return this.field_187;
	}

	public void method_229(double d) {
		this.field_187 = d;
	}

	public int method_227() {
		return this.field_197;
	}

	public int method_161() {
		return this.field_161;
	}

	public void method_201(int i) {
		this.field_197 = i;
	}

	public void method_192(int i) {
		this.field_161 = i;
	}

	public class_1267 method_207() {
		return this.field_174;
	}

	public void method_208(class_1267 arg) {
		this.field_174 = arg;
	}

	public boolean method_197() {
		return this.field_157;
	}

	public void method_186(boolean bl) {
		this.field_157 = bl;
	}

	public class_236<MinecraftServer> method_143() {
		return this.field_191;
	}

	public void method_151(class_129 arg) {
		arg.method_577("Level seed", () -> String.valueOf(this.method_184()));
		arg.method_577(
			"Level generator",
			() -> String.format(
					"ID %02d - %s, ver %d. Features enabled: %b", this.field_163.method_8637(), this.field_163.method_8635(), this.field_163.method_8636(), this.field_195
				)
		);
		arg.method_577("Level generator options", () -> this.field_166.toString());
		arg.method_577("Level spawn location", () -> class_129.method_581(this.field_151, this.field_167, this.field_182));
		arg.method_577("Level time", () -> String.format("%d game time, %d day time", this.field_189, this.field_198));
		arg.method_577("Level dimension", () -> String.valueOf(this.field_186));
		arg.method_577("Level storage version", () -> {
			String string = "Unknown?";

			try {
				switch (this.field_158) {
					case 19132:
						string = "McRegion";
						break;
					case 19133:
						string = "Anvil";
				}
			} catch (Throwable var3) {
			}

			return String.format("0x%05X - %s", this.field_158, string);
		});
		arg.method_577(
			"Level weather", () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.field_192, this.field_190, this.field_173, this.field_168)
		);
		arg.method_577(
			"Level game mode",
			() -> String.format(
					"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.field_179.method_8381(), this.field_179.method_8379(), this.field_159, this.field_177
				)
		);
	}

	public class_2487 method_170(class_2874 arg) {
		class_2487 lv = (class_2487)this.field_193.get(arg);
		return lv == null ? new class_2487() : lv;
	}

	public void method_160(class_2874 arg, class_2487 arg2) {
		this.field_193.put(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	public int method_180() {
		return this.field_183;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_171() {
		return this.field_194;
	}

	@Environment(EnvType.CLIENT)
	public String method_219() {
		return this.field_162;
	}

	public Set<String> method_209() {
		return this.field_155;
	}

	public Set<String> method_179() {
		return this.field_171;
	}

	@Nullable
	public class_2487 method_228() {
		return this.field_156;
	}

	public void method_221(@Nullable class_2487 arg) {
		this.field_156 = arg;
	}

	public int method_18038() {
		return this.field_17736;
	}

	public void method_18041(int i) {
		this.field_17736 = i;
	}

	public int method_18039() {
		return this.field_17737;
	}

	public void method_18042(int i) {
		this.field_17737 = i;
	}

	public void method_18040(UUID uUID) {
		this.field_17738 = uUID;
	}
}

package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.StringHelper;

@Environment(EnvType.CLIENT)
public class RealmsWorldOptions extends ValueObject {
	public final boolean pvp;
	public final boolean spawnMonsters;
	public final int spawnProtection;
	public final boolean commandBlocks;
	public final boolean forceGameMode;
	public final int difficulty;
	public final int gameMode;
	private final String slotName;
	public final String version;
	public final RealmsServer.Compatibility compatibility;
	public long templateId;
	@Nullable
	public String templateImage;
	public boolean empty;
	private static final boolean field_32100 = false;
	private static final boolean field_32101 = true;
	private static final boolean field_32103 = true;
	private static final int field_32105 = 0;
	private static final boolean field_32106 = false;
	private static final int DEFAULT_DIFFICULTY = 2;
	private static final int field_32108 = 0;
	private static final String DEFAULT_SLOT_NAME = "";
	private static final String field_46845 = "";
	private static final RealmsServer.Compatibility DEFAULT_COMPATIBILITY = RealmsServer.Compatibility.UNVERIFIABLE;
	private static final long DEFAULT_WORLD_TEMPLATE_ID = -1L;
	private static final String DEFAULT_WORLD_TEMPLATE_IMAGE = null;

	public RealmsWorldOptions(
		boolean pvp,
		boolean spawnAnimals,
		int i,
		boolean bl,
		int spawnProtection,
		int j,
		boolean bl2,
		String string,
		String string2,
		RealmsServer.Compatibility compatibility
	) {
		this.pvp = pvp;
		this.spawnMonsters = spawnAnimals;
		this.spawnProtection = i;
		this.commandBlocks = bl;
		this.difficulty = spawnProtection;
		this.gameMode = j;
		this.forceGameMode = bl2;
		this.slotName = string;
		this.version = string2;
		this.compatibility = compatibility;
	}

	public static RealmsWorldOptions getDefaults() {
		return new RealmsWorldOptions(true, true, 0, false, 2, 0, false, "", "", DEFAULT_COMPATIBILITY);
	}

	public static RealmsWorldOptions getEmptyDefaults() {
		RealmsWorldOptions realmsWorldOptions = getDefaults();
		realmsWorldOptions.setEmpty(true);
		return realmsWorldOptions;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public static RealmsWorldOptions parse(JsonObject json) {
		RealmsWorldOptions realmsWorldOptions = new RealmsWorldOptions(
			JsonUtils.getBooleanOr("pvp", json, true),
			JsonUtils.getBooleanOr("spawnMonsters", json, true),
			JsonUtils.getIntOr("spawnProtection", json, 0),
			JsonUtils.getBooleanOr("commandBlocks", json, false),
			JsonUtils.getIntOr("difficulty", json, 2),
			JsonUtils.getIntOr("gameMode", json, 0),
			JsonUtils.getBooleanOr("forceGameMode", json, false),
			JsonUtils.getStringOr("slotName", json, ""),
			JsonUtils.getStringOr("version", json, ""),
			RealmsServer.getCompatibility(JsonUtils.getStringOr("compatibility", json, RealmsServer.Compatibility.UNVERIFIABLE.name()))
		);
		realmsWorldOptions.templateId = JsonUtils.getLongOr("worldTemplateId", json, -1L);
		realmsWorldOptions.templateImage = JsonUtils.getNullableStringOr("worldTemplateImage", json, DEFAULT_WORLD_TEMPLATE_IMAGE);
		return realmsWorldOptions;
	}

	public String getSlotName(int index) {
		if (StringHelper.isBlank(this.slotName)) {
			return this.empty ? I18n.translate("mco.configure.world.slot.empty") : this.getDefaultSlotName(index);
		} else {
			return this.slotName;
		}
	}

	public String getDefaultSlotName(int index) {
		return I18n.translate("mco.configure.world.slot", index);
	}

	public String toJson() {
		JsonObject jsonObject = new JsonObject();
		if (!this.pvp) {
			jsonObject.addProperty("pvp", this.pvp);
		}

		if (!this.spawnMonsters) {
			jsonObject.addProperty("spawnMonsters", this.spawnMonsters);
		}

		if (this.spawnProtection != 0) {
			jsonObject.addProperty("spawnProtection", this.spawnProtection);
		}

		if (this.commandBlocks) {
			jsonObject.addProperty("commandBlocks", this.commandBlocks);
		}

		if (this.difficulty != 2) {
			jsonObject.addProperty("difficulty", this.difficulty);
		}

		if (this.gameMode != 0) {
			jsonObject.addProperty("gameMode", this.gameMode);
		}

		if (this.forceGameMode) {
			jsonObject.addProperty("forceGameMode", this.forceGameMode);
		}

		if (!Objects.equals(this.slotName, "")) {
			jsonObject.addProperty("slotName", this.slotName);
		}

		if (!Objects.equals(this.version, "")) {
			jsonObject.addProperty("version", this.version);
		}

		if (this.compatibility != DEFAULT_COMPATIBILITY) {
			jsonObject.addProperty("compatibility", this.compatibility.name());
		}

		return jsonObject.toString();
	}

	public RealmsWorldOptions clone() {
		return new RealmsWorldOptions(
			this.pvp,
			this.spawnMonsters,
			this.spawnProtection,
			this.commandBlocks,
			this.difficulty,
			this.gameMode,
			this.forceGameMode,
			this.slotName,
			this.version,
			this.compatibility
		);
	}
}

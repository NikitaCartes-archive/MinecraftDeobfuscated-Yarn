package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class RealmsWorldOptions extends ValueObject {
	public final boolean pvp;
	public final boolean spawnAnimals;
	public final boolean spawnMonsters;
	public final boolean spawnNPCs;
	public final int spawnProtection;
	public final boolean commandBlocks;
	public final boolean forceGameMode;
	public final int difficulty;
	public final int gameMode;
	@Nullable
	private final String slotName;
	public long templateId;
	@Nullable
	public String templateImage;
	public boolean empty;
	private static final String DEFAULT_WORLD_TEMPLATE_IMAGE = null;

	public RealmsWorldOptions(boolean bl, boolean bl2, boolean bl3, boolean bl4, int i, boolean bl5, int j, int k, boolean bl6, @Nullable String slotName) {
		this.pvp = bl;
		this.spawnAnimals = bl2;
		this.spawnMonsters = bl3;
		this.spawnNPCs = bl4;
		this.spawnProtection = i;
		this.commandBlocks = bl5;
		this.difficulty = j;
		this.gameMode = k;
		this.forceGameMode = bl6;
		this.slotName = slotName;
	}

	public static RealmsWorldOptions getDefaults() {
		return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
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
			JsonUtils.getBooleanOr("spawnAnimals", json, true),
			JsonUtils.getBooleanOr("spawnMonsters", json, true),
			JsonUtils.getBooleanOr("spawnNPCs", json, true),
			JsonUtils.getIntOr("spawnProtection", json, 0),
			JsonUtils.getBooleanOr("commandBlocks", json, false),
			JsonUtils.getIntOr("difficulty", json, 2),
			JsonUtils.getIntOr("gameMode", json, 0),
			JsonUtils.getBooleanOr("forceGameMode", json, false),
			JsonUtils.getStringOr("slotName", json, "")
		);
		realmsWorldOptions.templateId = JsonUtils.getLongOr("worldTemplateId", json, -1L);
		realmsWorldOptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", json, DEFAULT_WORLD_TEMPLATE_IMAGE);
		return realmsWorldOptions;
	}

	public String getSlotName(int index) {
		if (this.slotName != null && !this.slotName.isEmpty()) {
			return this.slotName;
		} else {
			return this.empty ? I18n.translate("mco.configure.world.slot.empty") : this.getDefaultSlotName(index);
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

		if (!this.spawnAnimals) {
			jsonObject.addProperty("spawnAnimals", this.spawnAnimals);
		}

		if (!this.spawnMonsters) {
			jsonObject.addProperty("spawnMonsters", this.spawnMonsters);
		}

		if (!this.spawnNPCs) {
			jsonObject.addProperty("spawnNPCs", this.spawnNPCs);
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

		return jsonObject.toString();
	}

	public RealmsWorldOptions clone() {
		return new RealmsWorldOptions(
			this.pvp,
			this.spawnAnimals,
			this.spawnMonsters,
			this.spawnNPCs,
			this.spawnProtection,
			this.commandBlocks,
			this.difficulty,
			this.gameMode,
			this.forceGameMode,
			this.slotName
		);
	}
}

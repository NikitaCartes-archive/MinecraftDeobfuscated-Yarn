package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class LevelGeneratorType {
	public static final LevelGeneratorType[] TYPES = new LevelGeneratorType[16];
	public static final LevelGeneratorType DEFAULT = new LevelGeneratorType(0, "default", 1).setVersioned();
	public static final LevelGeneratorType FLAT = new LevelGeneratorType(1, "flat").setCustomizable(true);
	public static final LevelGeneratorType LARGE_BIOMES = new LevelGeneratorType(2, "largeBiomes");
	public static final LevelGeneratorType AMPLIFIED = new LevelGeneratorType(3, "amplified").setHasInfo();
	public static final LevelGeneratorType CUSTOMIZED = new LevelGeneratorType(4, "customized", "normal", 0).setCustomizable(true).setVisible(false);
	public static final LevelGeneratorType BUFFET = new LevelGeneratorType(5, "buffet").setCustomizable(true);
	public static final LevelGeneratorType DEBUG_ALL_BLOCK_STATES = new LevelGeneratorType(6, "debug_all_block_states");
	public static final LevelGeneratorType DEFAULT_1_1 = new LevelGeneratorType(8, "default_1_1", 0).setVisible(false);
	private final int id;
	private final String name;
	private final String storedName;
	private final int version;
	private boolean visible;
	private boolean versioned;
	private boolean info;
	private boolean customizable;

	private LevelGeneratorType(int id, String name) {
		this(id, name, name, 0);
	}

	private LevelGeneratorType(int id, String name, int version) {
		this(id, name, name, version);
	}

	private LevelGeneratorType(int id, String name, String storedName, int version) {
		this.name = name;
		this.storedName = storedName;
		this.version = version;
		this.visible = true;
		this.id = id;
		TYPES[id] = this;
	}

	public String getName() {
		return this.name;
	}

	public String getStoredName() {
		return this.storedName;
	}

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return "generator." + this.name;
	}

	@Environment(EnvType.CLIENT)
	public String getInfoTranslationKey() {
		return this.getTranslationKey() + ".info";
	}

	public int getVersion() {
		return this.version;
	}

	public LevelGeneratorType getTypeForVersion(int version) {
		return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCustomizable() {
		return this.customizable;
	}

	public LevelGeneratorType setCustomizable(boolean customizable) {
		this.customizable = customizable;
		return this;
	}

	private LevelGeneratorType setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean isVisible() {
		return this.visible;
	}

	private LevelGeneratorType setVersioned() {
		this.versioned = true;
		return this;
	}

	public boolean isVersioned() {
		return this.versioned;
	}

	@Nullable
	public static LevelGeneratorType getTypeFromName(String name) {
		for (LevelGeneratorType levelGeneratorType : TYPES) {
			if (levelGeneratorType != null && levelGeneratorType.name.equalsIgnoreCase(name)) {
				return levelGeneratorType;
			}
		}

		return null;
	}

	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasInfo() {
		return this.info;
	}

	private LevelGeneratorType setHasInfo() {
		this.info = true;
		return this;
	}
}

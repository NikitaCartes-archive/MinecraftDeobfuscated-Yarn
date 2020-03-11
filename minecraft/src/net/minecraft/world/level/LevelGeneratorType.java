package net.minecraft.world.level;

import com.mojang.datafixers.Dynamic;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Lazy;

public class LevelGeneratorType {
	public static final LevelGeneratorType[] TYPES = new LevelGeneratorType[16];
	private static final Dynamic<?> EMPTY_COMPOUND_NBT_DYNAMIC = new Dynamic<>(NbtOps.INSTANCE, new CompoundTag());
	public static final LevelGeneratorType DEFAULT = new LevelGeneratorType(0, "default", 1, LevelGeneratorOptions::createDefault).setVersioned();
	public static final LevelGeneratorType FLAT = new LevelGeneratorType(1, "flat", LevelGeneratorOptions::createFlat).setCustomizable(true);
	public static final LevelGeneratorType LARGE_BIOMES = new LevelGeneratorType(2, "largeBiomes", LevelGeneratorOptions::createDefault);
	public static final LevelGeneratorType AMPLIFIED = new LevelGeneratorType(3, "amplified", LevelGeneratorOptions::createDefault).setHasInfo();
	public static final LevelGeneratorType CUSTOMIZED = new LevelGeneratorType(4, "customized", "normal", 0, LevelGeneratorOptions::createDefault)
		.setCustomizable(true)
		.setVisible(false);
	public static final LevelGeneratorType BUFFET = new LevelGeneratorType(5, "buffet", LevelGeneratorOptions::createBuffet).setCustomizable(true);
	public static final LevelGeneratorType DEBUG_ALL_BLOCK_STATES = new LevelGeneratorType(6, "debug_all_block_states", LevelGeneratorOptions::createDebug);
	public static final LevelGeneratorType DEFAULT_1_1 = new LevelGeneratorType(8, "default_1_1", 0, LevelGeneratorOptions::createDefault).setVisible(false);
	private final int id;
	private final String name;
	private final String storedName;
	private final int version;
	private final Function<Dynamic<?>, LevelGeneratorOptions> optionsLoader;
	private final Lazy<LevelGeneratorOptions> defaultOptions;
	private boolean visible;
	private boolean versioned;
	private boolean info;
	private boolean customizable;

	private LevelGeneratorType(int index, String name, BiFunction<LevelGeneratorType, Dynamic<?>, LevelGeneratorOptions> optionsFactory) {
		this(index, name, name, 0, optionsFactory);
	}

	private LevelGeneratorType(int index, String name, int version, BiFunction<LevelGeneratorType, Dynamic<?>, LevelGeneratorOptions> optionsFactory) {
		this(index, name, name, version, optionsFactory);
	}

	private LevelGeneratorType(
		int index, String name, String storedName, int version, BiFunction<LevelGeneratorType, Dynamic<?>, LevelGeneratorOptions> optionsFactory
	) {
		this.name = name;
		this.storedName = storedName;
		this.version = version;
		this.optionsLoader = dynamic -> (LevelGeneratorOptions)optionsFactory.apply(this, dynamic);
		this.defaultOptions = new Lazy<>(() -> (LevelGeneratorOptions)optionsFactory.apply(this, EMPTY_COMPOUND_NBT_DYNAMIC));
		this.visible = true;
		this.id = index;
		TYPES[index] = this;
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

	public LevelGeneratorOptions loadOptions(Dynamic<?> dynamic) {
		return (LevelGeneratorOptions)this.optionsLoader.apply(dynamic);
	}

	public LevelGeneratorOptions getDefaultOptions() {
		return this.defaultOptions.get();
	}
}

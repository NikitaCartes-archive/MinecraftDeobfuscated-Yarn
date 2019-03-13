package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;

@Environment(EnvType.CLIENT)
public class RealmsAnvilLevelStorageSource {
	private final LevelStorage levelStorageSource;

	public RealmsAnvilLevelStorageSource(LevelStorage levelStorage) {
		this.levelStorageSource = levelStorage;
	}

	public String getName() {
		return this.levelStorageSource.getName();
	}

	public boolean levelExists(String string) {
		return this.levelStorageSource.levelExists(string);
	}

	public boolean convertLevel(String string, ProgressListener progressListener) {
		return this.levelStorageSource.method_17927(string, progressListener);
	}

	public boolean requiresConversion(String string) {
		return this.levelStorageSource.requiresConversion(string);
	}

	public boolean isNewLevelIdAcceptable(String string) {
		return this.levelStorageSource.isLevelNameValid(string);
	}

	public boolean deleteLevel(String string) {
		return this.levelStorageSource.deleteLevel(string);
	}

	public void renameLevel(String string, String string2) {
		this.levelStorageSource.renameLevel(string, string2);
	}

	public List<RealmsLevelSummary> getLevelList() throws LevelStorageException {
		List<RealmsLevelSummary> list = Lists.<RealmsLevelSummary>newArrayList();

		for (LevelSummary levelSummary : this.levelStorageSource.getLevelList()) {
			list.add(new RealmsLevelSummary(levelSummary));
		}

		return list;
	}
}

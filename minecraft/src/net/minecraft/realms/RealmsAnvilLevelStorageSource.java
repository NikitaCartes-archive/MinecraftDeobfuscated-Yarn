package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_32;
import net.minecraft.class_33;
import net.minecraft.class_34;
import net.minecraft.class_3536;

@Environment(EnvType.CLIENT)
public class RealmsAnvilLevelStorageSource {
	private final class_32 levelStorageSource;

	public RealmsAnvilLevelStorageSource(class_32 arg) {
		this.levelStorageSource = arg;
	}

	public String getName() {
		return this.levelStorageSource.method_232();
	}

	public boolean levelExists(String string) {
		return this.levelStorageSource.method_230(string);
	}

	public boolean convertLevel(String string, class_3536 arg) {
		return this.levelStorageSource.method_234(string, arg);
	}

	public boolean requiresConversion(String string) {
		return this.levelStorageSource.method_231(string);
	}

	public boolean isNewLevelIdAcceptable(String string) {
		return this.levelStorageSource.method_240(string);
	}

	public boolean deleteLevel(String string) {
		return this.levelStorageSource.method_233(string);
	}

	public boolean isConvertible(String string) {
		return this.levelStorageSource.method_244(string);
	}

	public void renameLevel(String string, String string2) {
		this.levelStorageSource.method_241(string, string2);
	}

	public void clearAll() {
		this.levelStorageSource.method_245();
	}

	public List<RealmsLevelSummary> getLevelList() throws class_33 {
		List<RealmsLevelSummary> list = Lists.<RealmsLevelSummary>newArrayList();

		for (class_34 lv : this.levelStorageSource.method_235()) {
			list.add(new RealmsLevelSummary(lv));
		}

		return list;
	}
}

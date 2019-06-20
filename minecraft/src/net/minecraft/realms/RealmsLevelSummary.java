package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_34;

@Environment(EnvType.CLIENT)
public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
	private final class_34 levelSummary;

	public RealmsLevelSummary(class_34 arg) {
		this.levelSummary = arg;
	}

	public int getGameMode() {
		return this.levelSummary.method_247().method_8379();
	}

	public String getLevelId() {
		return this.levelSummary.method_248();
	}

	public boolean hasCheats() {
		return this.levelSummary.method_259();
	}

	public boolean isHardcore() {
		return this.levelSummary.method_257();
	}

	public boolean isRequiresConversion() {
		return this.levelSummary.method_255();
	}

	public String getLevelName() {
		return this.levelSummary.method_252();
	}

	public long getLastPlayed() {
		return this.levelSummary.method_249();
	}

	public int compareTo(class_34 arg) {
		return this.levelSummary.method_251(arg);
	}

	public long getSizeOnDisk() {
		return this.levelSummary.method_250();
	}

	public int compareTo(RealmsLevelSummary realmsLevelSummary) {
		if (this.levelSummary.method_249() < realmsLevelSummary.getLastPlayed()) {
			return 1;
		} else {
			return this.levelSummary.method_249() > realmsLevelSummary.getLastPlayed() ? -1 : this.levelSummary.method_248().compareTo(realmsLevelSummary.getLevelId());
		}
	}
}

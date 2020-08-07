package net.minecraft.util;

public class WorldSavePath {
	public static final WorldSavePath field_24180 = new WorldSavePath("advancements");
	public static final WorldSavePath field_24181 = new WorldSavePath("stats");
	public static final WorldSavePath field_24182 = new WorldSavePath("playerdata");
	public static final WorldSavePath field_24183 = new WorldSavePath("players");
	public static final WorldSavePath field_24184 = new WorldSavePath("level.dat");
	public static final WorldSavePath field_24185 = new WorldSavePath("generated");
	public static final WorldSavePath field_24186 = new WorldSavePath("datapacks");
	public static final WorldSavePath field_24187 = new WorldSavePath("resources.zip");
	public static final WorldSavePath ROOT = new WorldSavePath(".");
	private final String relativePath;

	private WorldSavePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getRelativePath() {
		return this.relativePath;
	}

	public String toString() {
		return "/" + this.relativePath;
	}
}

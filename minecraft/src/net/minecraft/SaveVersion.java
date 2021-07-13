package net.minecraft;

/**
 * The version components of Minecraft that is used for identification in
 * save games.
 */
public class SaveVersion {
	private final int id;
	private final String series;
	/**
	 * The default series of a version, {@value}, if a series is not specified.
	 */
	public static String MAIN_SERIES = "main";

	public SaveVersion(int id) {
		this(id, MAIN_SERIES);
	}

	public SaveVersion(int id, String series) {
		this.id = id;
		this.series = series;
	}

	public boolean isNotMainSeries() {
		return !this.series.equals(MAIN_SERIES);
	}

	/**
	 * {@return the series of this version}
	 * 
	 * <p>This is stored in the {@code Series} field within {@code level.dat}.
	 */
	public String getSeries() {
		return this.series;
	}

	/**
	 * {@return the integer data version of this save version}
	 */
	public int getId() {
		return this.id;
	}

	public boolean hasSameSeries(SaveVersion other) {
		return this.getSeries().equals(other.getSeries());
	}

	/**
	 * {@return whether this save version can be loaded by the {@code other} version}
	 */
	public boolean isAvailableTo(SaveVersion other) {
		return !this.getSeries().equals(other.getSeries()) ? false : this.hasOldWorldHeight() == other.hasOldWorldHeight();
	}

	/**
	 * This method always returns {@code false}, but its usage appears to
	 * indicate that it returns if this save version has the old 0 to 255 world
	 * height limit.
	 */
	public boolean hasOldWorldHeight() {
		return false;
	}
}

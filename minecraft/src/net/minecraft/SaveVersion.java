package net.minecraft;

/**
 * The version components of Minecraft that is used for identification in
 * save games.
 */
public class SaveVersion {
	private final int id;
	private final String series;
	/**
	 * The default series of a version, {@code main}, if a series is not specified.
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
	 * 
	 * <p>Known values include {@code main} for the main series and
	 * {@code ccpreview} for the caves and cliffs experimental snapshots.
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
		return this.getSeries().equals(other.getSeries()) && this.getId() == other.getId();
	}

	/**
	 * {@return whether this save version can be loaded by the {@code other} version}
	 */
	public boolean isAvailableTo(SaveVersion other) {
		return !this.hasSameSeries(other) ? false : this.hasIncompatibleWorldHeight() == other.hasIncompatibleWorldHeight();
	}

	/**
	 * {@return if this save version has the new -64 to 319 world height limit}
	 * 
	 * <p>This corresponds to world versions {@code 2693} to {@code 2706}, or
	 * game versions 21w06a to 21w14a.
	 */
	public boolean hasIncompatibleWorldHeight() {
		return this.id > 2692 && this.id <= 2706;
	}
}

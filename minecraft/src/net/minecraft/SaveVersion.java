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
	 * <p>Known values include:<ul>
	 * <li>{@code main} for versions that are not experimental snapshots</li>
	 * <li>{@code ccpreview} for the 1.18 Caves and Cliffs experimental snapshots</li>
	 * <li>{@code deep_dark_preview} for 1.19 Deep Dark experimental snapshots</li>
	 * </ul>
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

	/**
	 * {@return whether this save version can be loaded by the {@code other} version}
	 */
	public boolean isAvailableTo(SaveVersion other) {
		return this.getSeries().equals(other.getSeries());
	}
}

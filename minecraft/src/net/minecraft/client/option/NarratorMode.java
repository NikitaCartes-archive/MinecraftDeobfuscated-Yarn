package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

/**
 * Contains the different narrator modes that control
 * which messages the narrator narrates.
 */
@Environment(EnvType.CLIENT)
public enum NarratorMode {
	/**
	 * The narrator is disabled and narrates nothing.
	 */
	OFF(0, "options.narrator.off"),
	/**
	 * The narrator narrates everything narrated in the {@link #CHAT} and {@link #SYSTEM} modes.
	 */
	ALL(1, "options.narrator.all"),
	/**
	 * The narrator narrates chat messages.
	 */
	CHAT(2, "options.narrator.chat"),
	/**
	 * The narrator narrates system text, including screens.
	 */
	SYSTEM(3, "options.narrator.system");

	private static final IntFunction<NarratorMode> BY_ID = ValueLists.createIdToValueFunction(NarratorMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);
	private final int id;
	private final Text name;

	private NarratorMode(final int id, final String name) {
		this.id = id;
		this.name = Text.translatable(name);
	}

	/**
	 * {@return the unique int ID of this mode}
	 * @see #byId(int)
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * {@return the human-readable name of this mode}
	 */
	public Text getName() {
		return this.name;
	}

	/**
	 * {@return the narrator mode matching the specified ID with wraparound}
	 * @see #getId
	 */
	public static NarratorMode byId(int id) {
		return (NarratorMode)BY_ID.apply(id);
	}

	/**
	 * Checks if this mode narrates chat messages.
	 * 
	 * @return {@code true} if chat messages are narrated, {@code false} otherwise
	 * @see #CHAT
	 */
	public boolean shouldNarrateChat() {
		return this == ALL || this == CHAT;
	}

	/**
	 * Checks if this mode narrates system text.
	 * 
	 * @return {@code true} if system text is narrated, {@code false} otherwise
	 * @see #SYSTEM
	 */
	public boolean shouldNarrateSystem() {
		return this == ALL || this == SYSTEM;
	}
}

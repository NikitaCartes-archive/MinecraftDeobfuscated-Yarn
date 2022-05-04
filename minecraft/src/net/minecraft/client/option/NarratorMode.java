package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum NarratorMode {
	OFF(0, "options.narrator.off"),
	ALL(1, "options.narrator.all"),
	CHAT(2, "options.narrator.chat"),
	SYSTEM(3, "options.narrator.system");

	private static final NarratorMode[] VALUES = (NarratorMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(NarratorMode::getId))
		.toArray(NarratorMode[]::new);
	private final int id;
	private final Text name;

	private NarratorMode(int id, String name) {
		this.id = id;
		this.name = Text.translatable(name);
	}

	public int getId() {
		return this.id;
	}

	public Text getName() {
		return this.name;
	}

	public static NarratorMode byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}

	/**
	 * {@return whether a message from the server should be narrated,
	 * given the message type's narration kind}
	 */
	public boolean shouldNarrate(MessageType.NarrationRule.Kind kind) {
		return switch (this) {
			case OFF -> false;
			case ALL -> true;
			case CHAT -> kind == MessageType.NarrationRule.Kind.CHAT;
			case SYSTEM -> kind == MessageType.NarrationRule.Kind.SYSTEM;
		};
	}
}

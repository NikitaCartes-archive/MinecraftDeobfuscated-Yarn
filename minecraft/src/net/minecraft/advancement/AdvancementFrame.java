package net.minecraft.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public enum AdvancementFrame implements StringIdentifiable {
	TASK("task", Formatting.GREEN),
	CHALLENGE("challenge", Formatting.DARK_PURPLE),
	GOAL("goal", Formatting.GREEN);

	public static final Codec<AdvancementFrame> CODEC = StringIdentifiable.createCodec(AdvancementFrame::values);
	private final String id;
	private final Formatting titleFormat;
	private final Text toastText;

	private AdvancementFrame(final String id, final Formatting titleFormat) {
		this.id = id;
		this.titleFormat = titleFormat;
		this.toastText = Text.translatable("advancements.toast." + id);
	}

	public Formatting getTitleFormat() {
		return this.titleFormat;
	}

	public Text getToastText() {
		return this.toastText;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public MutableText getChatAnnouncementText(AdvancementEntry advancementEntry, ServerPlayerEntity player) {
		return Text.translatable("chat.type.advancement." + this.id, player.getDisplayName(), Advancement.getNameFromIdentity(advancementEntry));
	}
}

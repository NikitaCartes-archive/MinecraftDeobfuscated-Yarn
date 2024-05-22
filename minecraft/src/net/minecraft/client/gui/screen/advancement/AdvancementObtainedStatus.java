package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public enum AdvancementObtainedStatus {
	OBTAINED(
		Identifier.method_60656("advancements/box_obtained"),
		Identifier.method_60656("advancements/task_frame_obtained"),
		Identifier.method_60656("advancements/challenge_frame_obtained"),
		Identifier.method_60656("advancements/goal_frame_obtained")
	),
	UNOBTAINED(
		Identifier.method_60656("advancements/box_unobtained"),
		Identifier.method_60656("advancements/task_frame_unobtained"),
		Identifier.method_60656("advancements/challenge_frame_unobtained"),
		Identifier.method_60656("advancements/goal_frame_unobtained")
	);

	private final Identifier boxTexture;
	private final Identifier taskFrameTexture;
	private final Identifier challengeFrameTexture;
	private final Identifier goalFrameTexture;

	private AdvancementObtainedStatus(
		final Identifier boxTexture, final Identifier taskFrameTexture, final Identifier challengeFrameTexture, final Identifier goalFrameTexture
	) {
		this.boxTexture = boxTexture;
		this.taskFrameTexture = taskFrameTexture;
		this.challengeFrameTexture = challengeFrameTexture;
		this.goalFrameTexture = goalFrameTexture;
	}

	public Identifier getBoxTexture() {
		return this.boxTexture;
	}

	public Identifier getFrameTexture(AdvancementFrame frame) {
		return switch (frame) {
			case TASK -> this.taskFrameTexture;
			case CHALLENGE -> this.challengeFrameTexture;
			case GOAL -> this.goalFrameTexture;
		};
	}
}

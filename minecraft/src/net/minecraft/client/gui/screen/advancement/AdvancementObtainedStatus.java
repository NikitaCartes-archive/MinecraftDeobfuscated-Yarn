package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public enum AdvancementObtainedStatus {
	OBTAINED(
		new Identifier("advancements/box_obtained"),
		new Identifier("advancements/task_frame_obtained"),
		new Identifier("advancements/challenge_frame_obtained"),
		new Identifier("advancements/goal_frame_obtained")
	),
	UNOBTAINED(
		new Identifier("advancements/box_unobtained"),
		new Identifier("advancements/task_frame_unobtained"),
		new Identifier("advancements/challenge_frame_unobtained"),
		new Identifier("advancements/goal_frame_unobtained")
	);

	private final Identifier boxTexture;
	private final Identifier taskFrameTexture;
	private final Identifier challengeFrameTexture;
	private final Identifier goalFrameTexture;

	private AdvancementObtainedStatus(Identifier boxTexture, Identifier taskFrameTexture, Identifier challengeFrameTexture, Identifier goalFrameTexture) {
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

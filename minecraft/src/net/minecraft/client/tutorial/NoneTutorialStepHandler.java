package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NoneTutorialStepHandler implements TutorialStepHandler {
	private final TutorialManager field_5613;

	public NoneTutorialStepHandler(TutorialManager tutorialManager) {
		this.field_5613 = tutorialManager;
	}
}

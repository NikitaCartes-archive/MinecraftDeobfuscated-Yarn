package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NoneTutorialStepHandler implements TutorialStepHandler {
	private final TutorialManager manager;

	public NoneTutorialStepHandler(TutorialManager tutorialManager) {
		this.manager = tutorialManager;
	}
}

package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum TutorialStep {
	MOVEMENT("movement", MovementTutorialStepHandler::new),
	FIND_TREE("find_tree", FindTreeTutorialStepHandler::new),
	PUNCH_TREE("punch_tree", PunchTreeTutorialStepHandler::new),
	OPEN_INVENTORY("open_inventory", OpenInventoryTutorialStepHandler::new),
	CRAFT_PLANKS("craft_planks", CraftPlanksTutorialStepHandler::new),
	NONE("none", NoneTutorialStepHandler::new);

	private final String name;
	private final Function<TutorialManager, ? extends TutorialStepHandler> handlerFactory;

	private <T extends TutorialStepHandler> TutorialStep(String string2, Function<TutorialManager, T> function) {
		this.name = string2;
		this.handlerFactory = function;
	}

	public TutorialStepHandler createHandler(TutorialManager tutorialManager) {
		return (TutorialStepHandler)this.handlerFactory.apply(tutorialManager);
	}

	public String getName() {
		return this.name;
	}

	public static TutorialStep byName(String string) {
		for (TutorialStep tutorialStep : values()) {
			if (tutorialStep.name.equals(string)) {
				return tutorialStep;
			}
		}

		return NONE;
	}
}

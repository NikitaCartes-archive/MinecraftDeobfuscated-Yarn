package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum TutorialStep {
	field_5650("movement", MovementTutorialStepHandler::new),
	field_5648("find_tree", FindTreeTutorialStepHandler::new),
	field_5649("punch_tree", PunchTreeTutorialStepHandler::new),
	field_5652("open_inventory", OpenInventoryTutorialStepHandler::new),
	field_5655("craft_planks", CraftPlanksTutorialStepHandler::new),
	field_5653("none", NoneTutorialStepHandler::new);

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

		return field_5653;
	}
}

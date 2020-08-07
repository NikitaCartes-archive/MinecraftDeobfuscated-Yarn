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

	private <T extends TutorialStepHandler> TutorialStep(String name, Function<TutorialManager, T> factory) {
		this.name = name;
		this.handlerFactory = factory;
	}

	public TutorialStepHandler createHandler(TutorialManager manager) {
		return (TutorialStepHandler)this.handlerFactory.apply(manager);
	}

	public String getName() {
		return this.name;
	}

	public static TutorialStep byName(String name) {
		for (TutorialStep tutorialStep : values()) {
			if (tutorialStep.name.equals(name)) {
				return tutorialStep;
			}
		}

		return field_5653;
	}
}

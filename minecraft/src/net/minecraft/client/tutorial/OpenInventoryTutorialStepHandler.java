package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_372;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler {
	private static final TextComponent TITLE = new TranslatableTextComponent("tutorial.open_inventory.title");
	private static final TextComponent DESCRIPTION = new TranslatableTextComponent(
		"tutorial.open_inventory.description", TutorialManager.getKeybindName("inventory")
	);
	private final TutorialManager manager;
	private class_372 field_5642;
	private int ticks;

	public OpenInventoryTutorialStepHandler(TutorialManager tutorialManager) {
		this.manager = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.manager.getGameMode() != GameMode.field_9215) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks >= 600 && this.field_5642 == null) {
				this.field_5642 = new class_372(class_372.class_373.field_2233, TITLE, DESCRIPTION, false);
				this.manager.getClient().getToastManager().add(this.field_5642);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.field_5642 != null) {
			this.field_5642.method_1993();
			this.field_5642 = null;
		}
	}

	@Override
	public void onInventoryOpened() {
		this.manager.setStep(TutorialStep.CRAFT_PLANKS);
	}
}

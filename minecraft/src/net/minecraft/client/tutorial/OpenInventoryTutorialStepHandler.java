package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler {
	private static final Component TITLE = new TranslatableComponent("tutorial.open_inventory.title");
	private static final Component DESCRIPTION = new TranslatableComponent("tutorial.open_inventory.description", TutorialManager.getKeybindName("inventory"));
	private final TutorialManager manager;
	private TutorialToast field_5642;
	private int ticks;

	public OpenInventoryTutorialStepHandler(TutorialManager tutorialManager) {
		this.manager = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.manager.getGameMode() != GameMode.field_9215) {
			this.manager.setStep(TutorialStep.field_5653);
		} else {
			if (this.ticks >= 600 && this.field_5642 == null) {
				this.field_5642 = new TutorialToast(TutorialToast.Type.field_2233, TITLE, DESCRIPTION, false);
				this.manager.getClient().getToastManager().add(this.field_5642);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.field_5642 != null) {
			this.field_5642.hide();
			this.field_5642 = null;
		}
	}

	@Override
	public void onInventoryOpened() {
		this.manager.setStep(TutorialStep.field_5655);
	}
}

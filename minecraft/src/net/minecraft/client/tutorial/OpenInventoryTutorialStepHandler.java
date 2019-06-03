package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler {
	private static final Text field_5643 = new TranslatableText("tutorial.open_inventory.title");
	private static final Text field_5644 = new TranslatableText("tutorial.open_inventory.description", TutorialManager.method_4913("inventory"));
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
				this.field_5642 = new TutorialToast(TutorialToast.Type.field_2233, field_5643, field_5644, false);
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

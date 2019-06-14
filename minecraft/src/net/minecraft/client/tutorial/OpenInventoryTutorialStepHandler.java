package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler {
	private static final Text TITLE = new TranslatableText("tutorial.open_inventory.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.open_inventory.description", TutorialManager.getKeybindName("inventory"));
	private final TutorialManager field_5640;
	private TutorialToast field_5642;
	private int ticks;

	public OpenInventoryTutorialStepHandler(TutorialManager tutorialManager) {
		this.field_5640 = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.field_5640.getGameMode() != GameMode.field_9215) {
			this.field_5640.setStep(TutorialStep.field_5653);
		} else {
			if (this.ticks >= 600 && this.field_5642 == null) {
				this.field_5642 = new TutorialToast(TutorialToast.Type.field_2233, TITLE, DESCRIPTION, false);
				this.field_5640.getClient().method_1566().add(this.field_5642);
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
		this.field_5640.setStep(TutorialStep.field_5655);
	}
}

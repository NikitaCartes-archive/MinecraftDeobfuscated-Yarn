package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler {
	private static final int field_33034 = 600;
	private static final Text TITLE = new TranslatableText("tutorial.open_inventory.title");
	private static final Text DESCRIPTION = new TranslatableText("tutorial.open_inventory.description", TutorialManager.keyToText("inventory"));
	private final TutorialManager manager;
	private TutorialToast toast;
	private int ticks;

	public OpenInventoryTutorialStepHandler(TutorialManager manager) {
		this.manager = manager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (!this.manager.isInSurvival()) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks >= 600 && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.RECIPE_BOOK, TITLE, DESCRIPTION, false);
				this.manager.getClient().getToastManager().add(this.toast);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.toast != null) {
			this.toast.hide();
			this.toast = null;
		}
	}

	@Override
	public void onInventoryOpened() {
		this.manager.setStep(TutorialStep.CRAFT_PLANKS);
	}
}

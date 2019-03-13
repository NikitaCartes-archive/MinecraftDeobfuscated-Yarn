package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler {
	private static final TextComponent field_5643 = new TranslatableTextComponent("tutorial.open_inventory.title");
	private static final TextComponent field_5644 = new TranslatableTextComponent("tutorial.open_inventory.description", TutorialManager.method_4913("inventory"));
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
			this.field_5640.method_4910(TutorialStep.NONE);
		} else {
			if (this.ticks >= 600 && this.field_5642 == null) {
				this.field_5642 = new TutorialToast(TutorialToast.Type.field_2233, field_5643, field_5644, false);
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
		this.field_5640.method_4910(TutorialStep.CRAFT_PLANKS);
	}
}

package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class MovementTutorialStepHandler implements TutorialStepHandler {
	private static final TextComponent field_5624 = new TranslatableTextComponent(
		"tutorial.move.title",
		TutorialManager.method_4913("forward"),
		TutorialManager.method_4913("left"),
		TutorialManager.method_4913("back"),
		TutorialManager.method_4913("right")
	);
	private static final TextComponent field_5617 = new TranslatableTextComponent("tutorial.move.description", TutorialManager.method_4913("jump"));
	private static final TextComponent field_5621 = new TranslatableTextComponent("tutorial.look.title");
	private static final TextComponent field_5614 = new TranslatableTextComponent("tutorial.look.description");
	private final TutorialManager field_5618;
	private TutorialToast field_5622;
	private TutorialToast field_5623;
	private int ticks;
	private int field_5615;
	private int field_5627;
	private boolean field_5620;
	private boolean field_5619;
	private int field_5626 = -1;
	private int field_5625 = -1;

	public MovementTutorialStepHandler(TutorialManager tutorialManager) {
		this.field_5618 = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.field_5620) {
			this.field_5615++;
			this.field_5620 = false;
		}

		if (this.field_5619) {
			this.field_5627++;
			this.field_5619 = false;
		}

		if (this.field_5626 == -1 && this.field_5615 > 40) {
			if (this.field_5622 != null) {
				this.field_5622.hide();
				this.field_5622 = null;
			}

			this.field_5626 = this.ticks;
		}

		if (this.field_5625 == -1 && this.field_5627 > 40) {
			if (this.field_5623 != null) {
				this.field_5623.hide();
				this.field_5623 = null;
			}

			this.field_5625 = this.ticks;
		}

		if (this.field_5626 != -1 && this.field_5625 != -1) {
			if (this.field_5618.getGameMode() == GameMode.field_9215) {
				this.field_5618.method_4910(TutorialStep.FIND_TREE);
			} else {
				this.field_5618.method_4910(TutorialStep.NONE);
			}
		}

		if (this.field_5622 != null) {
			this.field_5622.method_1992((float)this.field_5615 / 40.0F);
		}

		if (this.field_5623 != null) {
			this.field_5623.method_1992((float)this.field_5627 / 40.0F);
		}

		if (this.ticks >= 100) {
			if (this.field_5626 == -1 && this.field_5622 == null) {
				this.field_5622 = new TutorialToast(TutorialToast.Type.field_2230, field_5624, field_5617, true);
				this.field_5618.getClient().method_1566().add(this.field_5622);
			} else if (this.field_5626 != -1 && this.ticks - this.field_5626 >= 20 && this.field_5625 == -1 && this.field_5623 == null) {
				this.field_5623 = new TutorialToast(TutorialToast.Type.field_2237, field_5621, field_5614, true);
				this.field_5618.getClient().method_1566().add(this.field_5623);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.field_5622 != null) {
			this.field_5622.hide();
			this.field_5622 = null;
		}

		if (this.field_5623 != null) {
			this.field_5623.hide();
			this.field_5623 = null;
		}
	}

	@Override
	public void method_4903(Input input) {
		if (input.pressingForward || input.pressingBack || input.pressingLeft || input.pressingRight || input.jumping) {
			this.field_5620 = true;
		}
	}

	@Override
	public void method_4901(double d, double e) {
		if (Math.abs(d) > 0.01 || Math.abs(e) > 0.01) {
			this.field_5619 = true;
		}
	}
}

package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MovementTutorialStepHandler implements TutorialStepHandler {
	private static final int field_33029 = 40;
	private static final int field_33030 = 40;
	private static final int field_33031 = 100;
	private static final int field_33032 = 20;
	private static final int field_33033 = -1;
	private static final Text MOVE_TITLE = Text.translatable(
		"tutorial.move.title",
		TutorialManager.keyToText("forward"),
		TutorialManager.keyToText("left"),
		TutorialManager.keyToText("back"),
		TutorialManager.keyToText("right")
	);
	private static final Text MOVE_DESCRIPTION = Text.translatable("tutorial.move.description", TutorialManager.keyToText("jump"));
	private static final Text LOOK_TITLE = Text.translatable("tutorial.look.title");
	private static final Text LOOK_DESCRIPTION = Text.translatable("tutorial.look.description");
	private final TutorialManager manager;
	private TutorialToast moveToast;
	private TutorialToast lookAroundToast;
	private int ticks;
	private int movedTicks;
	private int lookedAroundTicks;
	private boolean movedLastTick;
	private boolean lookedAroundLastTick;
	private int moveAroundCompletionTicks = -1;
	private int lookAroundCompletionTicks = -1;

	public MovementTutorialStepHandler(TutorialManager manager) {
		this.manager = manager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.movedLastTick) {
			this.movedTicks++;
			this.movedLastTick = false;
		}

		if (this.lookedAroundLastTick) {
			this.lookedAroundTicks++;
			this.lookedAroundLastTick = false;
		}

		if (this.moveAroundCompletionTicks == -1 && this.movedTicks > 40) {
			if (this.moveToast != null) {
				this.moveToast.hide();
				this.moveToast = null;
			}

			this.moveAroundCompletionTicks = this.ticks;
		}

		if (this.lookAroundCompletionTicks == -1 && this.lookedAroundTicks > 40) {
			if (this.lookAroundToast != null) {
				this.lookAroundToast.hide();
				this.lookAroundToast = null;
			}

			this.lookAroundCompletionTicks = this.ticks;
		}

		if (this.moveAroundCompletionTicks != -1 && this.lookAroundCompletionTicks != -1) {
			if (this.manager.isInSurvival()) {
				this.manager.setStep(TutorialStep.FIND_TREE);
			} else {
				this.manager.setStep(TutorialStep.NONE);
			}
		}

		if (this.moveToast != null) {
			this.moveToast.setProgress((float)this.movedTicks / 40.0F);
		}

		if (this.lookAroundToast != null) {
			this.lookAroundToast.setProgress((float)this.lookedAroundTicks / 40.0F);
		}

		if (this.ticks >= 100) {
			if (this.moveAroundCompletionTicks == -1 && this.moveToast == null) {
				this.moveToast = new TutorialToast(TutorialToast.Type.MOVEMENT_KEYS, MOVE_TITLE, MOVE_DESCRIPTION, true);
				this.manager.getClient().getToastManager().add(this.moveToast);
			} else if (this.moveAroundCompletionTicks != -1
				&& this.ticks - this.moveAroundCompletionTicks >= 20
				&& this.lookAroundCompletionTicks == -1
				&& this.lookAroundToast == null) {
				this.lookAroundToast = new TutorialToast(TutorialToast.Type.MOUSE, LOOK_TITLE, LOOK_DESCRIPTION, true);
				this.manager.getClient().getToastManager().add(this.lookAroundToast);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.moveToast != null) {
			this.moveToast.hide();
			this.moveToast = null;
		}

		if (this.lookAroundToast != null) {
			this.lookAroundToast.hide();
			this.lookAroundToast = null;
		}
	}

	@Override
	public void onMovement(Input input) {
		if (input.pressingForward || input.pressingBack || input.pressingLeft || input.pressingRight || input.jumping) {
			this.movedLastTick = true;
		}
	}

	@Override
	public void onMouseUpdate(double deltaX, double deltaY) {
		if (Math.abs(deltaX) > 0.01 || Math.abs(deltaY) > 0.01) {
			this.lookedAroundLastTick = true;
		}
	}
}

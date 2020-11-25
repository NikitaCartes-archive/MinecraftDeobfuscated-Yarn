package net.minecraft.client.tutorial;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.text.KeybindText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TutorialManager {
	private final MinecraftClient client;
	@Nullable
	private TutorialStepHandler currentHandler;
	private List<TutorialManager.class_5524> field_26893 = Lists.<TutorialManager.class_5524>newArrayList();

	public TutorialManager(MinecraftClient client) {
		this.client = client;
	}

	public void onMovement(Input input) {
		if (this.currentHandler != null) {
			this.currentHandler.onMovement(input);
		}
	}

	public void onUpdateMouse(double deltaX, double deltaY) {
		if (this.currentHandler != null) {
			this.currentHandler.onMouseUpdate(deltaX, deltaY);
		}
	}

	public void tick(@Nullable ClientWorld world, @Nullable HitResult hitResult) {
		if (this.currentHandler != null && hitResult != null && world != null) {
			this.currentHandler.onTarget(world, hitResult);
		}
	}

	public void onBlockAttacked(ClientWorld world, BlockPos pos, BlockState state, float f) {
		if (this.currentHandler != null) {
			this.currentHandler.onBlockAttacked(world, pos, state, f);
		}
	}

	public void onInventoryOpened() {
		if (this.currentHandler != null) {
			this.currentHandler.onInventoryOpened();
		}
	}

	public void onSlotUpdate(ItemStack stack) {
		if (this.currentHandler != null) {
			this.currentHandler.onSlotUpdate(stack);
		}
	}

	public void destroyHandler() {
		if (this.currentHandler != null) {
			this.currentHandler.destroy();
			this.currentHandler = null;
		}
	}

	public void createHandler() {
		if (this.currentHandler != null) {
			this.destroyHandler();
		}

		this.currentHandler = this.client.options.tutorialStep.createHandler(this);
	}

	public void method_31365(TutorialToast tutorialToast, int i) {
		this.field_26893.add(new TutorialManager.class_5524(tutorialToast, i));
		this.client.getToastManager().add(tutorialToast);
	}

	public void method_31364(TutorialToast tutorialToast) {
		this.field_26893.removeIf(arg -> arg.field_26894 == tutorialToast);
		tutorialToast.hide();
	}

	public void tick() {
		this.field_26893.removeIf(object -> ((TutorialManager.class_5524)object).method_31368());
		if (this.currentHandler != null) {
			if (this.client.world != null) {
				this.currentHandler.tick();
			} else {
				this.destroyHandler();
			}
		} else if (this.client.world != null) {
			this.createHandler();
		}
	}

	public void setStep(TutorialStep step) {
		this.client.options.tutorialStep = step;
		this.client.options.write();
		if (this.currentHandler != null) {
			this.currentHandler.destroy();
			this.currentHandler = step.createHandler(this);
		}
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	/**
	 * Returns whether the current game mode of the client is {@linkplain net.minecraft.world.GameMode#SURVIVAL survival}.
	 */
	public boolean isInSurvival() {
		return this.client.interactionManager == null ? false : this.client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL;
	}

	public static Text getKeybindName(String string) {
		return new KeybindText("key." + string).formatted(Formatting.BOLD);
	}

	@Environment(EnvType.CLIENT)
	static final class class_5524 {
		private final TutorialToast field_26894;
		private final int field_26895;
		private int field_26896;

		private class_5524(TutorialToast tutorialToast, int i) {
			this.field_26894 = tutorialToast;
			this.field_26895 = i;
		}

		private boolean method_31368() {
			this.field_26894.setProgress(Math.min((float)(++this.field_26896) / (float)this.field_26895, 1.0F));
			if (this.field_26896 > this.field_26895) {
				this.field_26894.hide();
				return true;
			} else {
				return false;
			}
		}
	}
}

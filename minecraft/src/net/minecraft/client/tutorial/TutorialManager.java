package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
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
	private TutorialStepHandler field_5646;

	public TutorialManager(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void onMovement(Input input) {
		if (this.field_5646 != null) {
			this.field_5646.onMovement(input);
		}
	}

	public void onUpdateMouse(double d, double e) {
		if (this.field_5646 != null) {
			this.field_5646.onMouseUpdate(d, e);
		}
	}

	public void tick(@Nullable ClientWorld clientWorld, @Nullable HitResult hitResult) {
		if (this.field_5646 != null && hitResult != null && clientWorld != null) {
			this.field_5646.onTarget(clientWorld, hitResult);
		}
	}

	public void onBlockAttacked(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		if (this.field_5646 != null) {
			this.field_5646.onBlockAttacked(clientWorld, blockPos, blockState, f);
		}
	}

	public void onInventoryOpened() {
		if (this.field_5646 != null) {
			this.field_5646.onInventoryOpened();
		}
	}

	public void onSlotUpdate(ItemStack itemStack) {
		if (this.field_5646 != null) {
			this.field_5646.onSlotUpdate(itemStack);
		}
	}

	public void destroyHandler() {
		if (this.field_5646 != null) {
			this.field_5646.destroy();
			this.field_5646 = null;
		}
	}

	public void createHandler() {
		if (this.field_5646 != null) {
			this.destroyHandler();
		}

		this.field_5646 = this.client.field_1690.tutorialStep.createHandler(this);
	}

	public void tick() {
		if (this.field_5646 != null) {
			if (this.client.field_1687 != null) {
				this.field_5646.tick();
			} else {
				this.destroyHandler();
			}
		} else if (this.client.field_1687 != null) {
			this.createHandler();
		}
	}

	public void setStep(TutorialStep tutorialStep) {
		this.client.field_1690.tutorialStep = tutorialStep;
		this.client.field_1690.write();
		if (this.field_5646 != null) {
			this.field_5646.destroy();
			this.field_5646 = tutorialStep.createHandler(this);
		}
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	public GameMode getGameMode() {
		return this.client.field_1761 == null ? GameMode.field_9218 : this.client.field_1761.getCurrentGameMode();
	}

	public static Text getKeybindName(String string) {
		return new KeybindText("key." + string).formatted(Formatting.field_1067);
	}
}

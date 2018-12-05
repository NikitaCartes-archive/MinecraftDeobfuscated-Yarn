package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_372;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.HitResult;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class FindTreeTutorialStepHandler implements TutorialStepHandler {
	private static final Set<Block> MATCHING_BLOCKS = Sets.<Block>newHashSet(
		Blocks.field_10431,
		Blocks.field_10037,
		Blocks.field_10511,
		Blocks.field_10306,
		Blocks.field_10533,
		Blocks.field_10010,
		Blocks.field_10126,
		Blocks.field_10155,
		Blocks.field_10307,
		Blocks.field_10303,
		Blocks.field_9999,
		Blocks.field_10178,
		Blocks.field_10503,
		Blocks.field_9988,
		Blocks.field_10539,
		Blocks.field_10335,
		Blocks.field_10098,
		Blocks.field_10035
	);
	private static final TextComponent TITLE = new TranslatableTextComponent("tutorial.find_tree.title");
	private static final TextComponent DESCRIPTION = new TranslatableTextComponent("tutorial.find_tree.description");
	private final TutorialManager manager;
	private class_372 field_5633;
	private int ticks;

	public FindTreeTutorialStepHandler(TutorialManager tutorialManager) {
		this.manager = tutorialManager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.manager.getGameMode() != GameMode.field_9215) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.manager.getClient().player;
				if (clientPlayerEntity != null) {
					for (Block block : MATCHING_BLOCKS) {
						if (clientPlayerEntity.inventory.method_7379(new ItemStack(block))) {
							this.manager.setStep(TutorialStep.CRAFT_PLANKS);
							return;
						}
					}

					if (method_4896(clientPlayerEntity)) {
						this.manager.setStep(TutorialStep.CRAFT_PLANKS);
						return;
					}
				}
			}

			if (this.ticks >= 6000 && this.field_5633 == null) {
				this.field_5633 = new class_372(class_372.class_373.field_2235, TITLE, DESCRIPTION, false);
				this.manager.getClient().getToastManager().add(this.field_5633);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.field_5633 != null) {
			this.field_5633.method_1993();
			this.field_5633 = null;
		}
	}

	@Override
	public void method_4898(ClientWorld clientWorld, HitResult hitResult) {
		if (hitResult.type == HitResult.Type.BLOCK && hitResult.getBlockPos() != null) {
			BlockState blockState = clientWorld.getBlockState(hitResult.getBlockPos());
			if (MATCHING_BLOCKS.contains(blockState.getBlock())) {
				this.manager.setStep(TutorialStep.PUNCH_TREE);
			}
		}
	}

	@Override
	public void onSlotUpdate(ItemStack itemStack) {
		for (Block block : MATCHING_BLOCKS) {
			if (itemStack.getItem() == block.getItem()) {
				this.manager.setStep(TutorialStep.CRAFT_PLANKS);
				return;
			}
		}
	}

	public static boolean method_4896(ClientPlayerEntity clientPlayerEntity) {
		for (Block block : MATCHING_BLOCKS) {
			if (clientPlayerEntity.getStats().method_15025(Stats.field_15427.method_14956(block)) > 0) {
				return true;
			}
		}

		return false;
	}
}

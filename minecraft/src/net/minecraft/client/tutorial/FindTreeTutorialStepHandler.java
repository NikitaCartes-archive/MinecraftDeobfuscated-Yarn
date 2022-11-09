package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

@Environment(EnvType.CLIENT)
public class FindTreeTutorialStepHandler implements TutorialStepHandler {
	private static final int DELAY = 6000;
	private static final Text TITLE = Text.translatable("tutorial.find_tree.title");
	private static final Text DESCRIPTION = Text.translatable("tutorial.find_tree.description");
	private final TutorialManager manager;
	private TutorialToast toast;
	private int ticks;

	public FindTreeTutorialStepHandler(TutorialManager manager) {
		this.manager = manager;
	}

	@Override
	public void tick() {
		this.ticks++;
		if (!this.manager.isInSurvival()) {
			this.manager.setStep(TutorialStep.NONE);
		} else {
			if (this.ticks == 1) {
				ClientPlayerEntity clientPlayerEntity = this.manager.getClient().player;
				if (clientPlayerEntity != null && (hasItem(clientPlayerEntity) || hasBrokenTreeBlocks(clientPlayerEntity))) {
					this.manager.setStep(TutorialStep.CRAFT_PLANKS);
					return;
				}
			}

			if (this.ticks >= 6000 && this.toast == null) {
				this.toast = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
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
	public void onTarget(ClientWorld world, HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockState blockState = world.getBlockState(((BlockHitResult)hitResult).getBlockPos());
			if (blockState.isIn(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
				this.manager.setStep(TutorialStep.PUNCH_TREE);
			}
		}
	}

	@Override
	public void onSlotUpdate(ItemStack stack) {
		if (stack.isIn(ItemTags.COMPLETES_FIND_TREE_TUTORIAL)) {
			this.manager.setStep(TutorialStep.CRAFT_PLANKS);
		}
	}

	private static boolean hasItem(ClientPlayerEntity player) {
		return player.getInventory().containsAny(stack -> stack.isIn(ItemTags.COMPLETES_FIND_TREE_TUTORIAL));
	}

	public static boolean hasBrokenTreeBlocks(ClientPlayerEntity player) {
		for (RegistryEntry<Block> registryEntry : Registries.BLOCK.iterateEntries(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
			Block block = registryEntry.value();
			if (player.getStatHandler().getStat(Stats.MINED.getOrCreateStat(block)) > 0) {
				return true;
			}
		}

		return false;
	}
}

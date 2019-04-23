/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class FindTreeTutorialStepHandler
implements TutorialStepHandler {
    private static final Set<Block> MATCHING_BLOCKS = Sets.newHashSet(Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES);
    private static final Component TITLE = new TranslatableComponent("tutorial.find_tree.title", new Object[0]);
    private static final Component DESCRIPTION = new TranslatableComponent("tutorial.find_tree.description", new Object[0]);
    private final TutorialManager manager;
    private TutorialToast toast;
    private int ticks;

    public FindTreeTutorialStepHandler(TutorialManager tutorialManager) {
        this.manager = tutorialManager;
    }

    @Override
    public void tick() {
        ClientPlayerEntity clientPlayerEntity;
        ++this.ticks;
        if (this.manager.getGameMode() != GameMode.SURVIVAL) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks == 1 && (clientPlayerEntity = this.manager.getClient().player) != null) {
            for (Block block : MATCHING_BLOCKS) {
                if (!clientPlayerEntity.inventory.contains(new ItemStack(block))) continue;
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepHandler.method_4896(clientPlayerEntity)) {
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
            this.manager.getClient().getToastManager().add(this.toast);
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
    public void onTarget(ClientWorld clientWorld, HitResult hitResult) {
        BlockState blockState;
        if (hitResult.getType() == HitResult.Type.BLOCK && MATCHING_BLOCKS.contains((blockState = clientWorld.getBlockState(((BlockHitResult)hitResult).getBlockPos())).getBlock())) {
            this.manager.setStep(TutorialStep.PUNCH_TREE);
        }
    }

    @Override
    public void onSlotUpdate(ItemStack itemStack) {
        for (Block block : MATCHING_BLOCKS) {
            if (itemStack.getItem() != block.asItem()) continue;
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
    }

    public static boolean method_4896(ClientPlayerEntity clientPlayerEntity) {
        for (Block block : MATCHING_BLOCKS) {
            if (clientPlayerEntity.getStats().getStat(Stats.MINED.getOrCreateStat(block)) <= 0) continue;
            return true;
        }
        return false;
    }
}


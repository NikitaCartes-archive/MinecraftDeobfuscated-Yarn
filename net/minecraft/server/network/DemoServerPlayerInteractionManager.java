/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DemoServerPlayerInteractionManager
extends ServerPlayerInteractionManager {
    private boolean field_13890;
    private boolean demoEnded;
    private int field_13888;
    private int field_13887;

    public DemoServerPlayerInteractionManager(ServerWorld serverWorld) {
        super(serverWorld);
    }

    @Override
    public void update() {
        super.update();
        ++this.field_13887;
        long l = this.world.getTime();
        long m = l / 24000L + 1L;
        if (!this.field_13890 && this.field_13887 > 20) {
            this.field_13890 = true;
            this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 0.0f));
        }
        boolean bl = this.demoEnded = l > 120500L;
        if (this.demoEnded) {
            ++this.field_13888;
        }
        if (l % 24000L == 500L) {
            if (m <= 6L) {
                if (m == 6L) {
                    this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 104.0f));
                } else {
                    this.player.sendMessage(new TranslatableText("demo.day." + m, new Object[0]));
                }
            }
        } else if (m == 1L) {
            if (l == 100L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 101.0f));
            } else if (l == 175L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 102.0f));
            } else if (l == 250L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 103.0f));
            }
        } else if (m == 5L && l % 24000L == 22000L) {
            this.player.sendMessage(new TranslatableText("demo.day.warning", new Object[0]));
        }
    }

    private void sendDemoReminder() {
        if (this.field_13888 > 100) {
            this.player.sendMessage(new TranslatableText("demo.reminder", new Object[0]));
            this.field_13888 = 0;
        }
    }

    @Override
    public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return;
        }
        super.processBlockBreakingAction(pos, action, direction, worldHeight);
    }

    @Override
    public ActionResult interactItem(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return ActionResult.PASS;
        }
        return super.interactItem(playerEntity, world, itemStack, hand);
    }

    @Override
    public ActionResult interactBlock(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return ActionResult.PASS;
        }
        return super.interactBlock(player, world, stack, hand, hitResult);
    }
}


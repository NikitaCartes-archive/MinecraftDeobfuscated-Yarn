package net.minecraft.server.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DemoServerPlayerInteractionManager extends ServerPlayerInteractionManager {
	private boolean sentHelp;
	private boolean demoEnded;
	private int reminderTicks;
	private int tick;

	public DemoServerPlayerInteractionManager(ServerWorld serverWorld) {
		super(serverWorld);
	}

	@Override
	public void update() {
		super.update();
		this.tick++;
		long l = this.world.getTime();
		long m = l / 24000L + 1L;
		if (!this.sentHelp && this.tick > 20) {
			this.sentHelp = true;
			this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN, 0.0F));
		}

		this.demoEnded = l > 120500L;
		if (this.demoEnded) {
			this.reminderTicks++;
		}

		if (l % 24000L == 500L) {
			if (m <= 6L) {
				if (m == 6L) {
					this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN, 104.0F));
				} else {
					this.player.sendSystemMessage(new TranslatableText("demo.day." + m), Util.NIL_UUID);
				}
			}
		} else if (m == 1L) {
			if (l == 100L) {
				this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN, 101.0F));
			} else if (l == 175L) {
				this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN, 102.0F));
			} else if (l == 250L) {
				this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN, 103.0F));
			}
		} else if (m == 5L && l % 24000L == 22000L) {
			this.player.sendSystemMessage(new TranslatableText("demo.day.warning"), Util.NIL_UUID);
		}
	}

	private void sendDemoReminder() {
		if (this.reminderTicks > 100) {
			this.player.sendSystemMessage(new TranslatableText("demo.reminder"), Util.NIL_UUID);
			this.reminderTicks = 0;
		}
	}

	@Override
	public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
		if (this.demoEnded) {
			this.sendDemoReminder();
		} else {
			super.processBlockBreakingAction(pos, action, direction, worldHeight);
		}
	}

	@Override
	public ActionResult interactItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand) {
		if (this.demoEnded) {
			this.sendDemoReminder();
			return ActionResult.PASS;
		} else {
			return super.interactItem(player, world, stack, hand);
		}
	}

	@Override
	public ActionResult interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
		if (this.demoEnded) {
			this.sendDemoReminder();
			return ActionResult.PASS;
		} else {
			return super.interactBlock(player, world, stack, hand, hitResult);
		}
	}
}

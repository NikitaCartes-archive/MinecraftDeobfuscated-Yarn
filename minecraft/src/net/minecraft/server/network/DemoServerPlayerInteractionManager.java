package net.minecraft.server.network;

import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DemoServerPlayerInteractionManager extends ServerPlayerInteractionManager {
	private boolean field_13890;
	private boolean field_13889;
	private int field_13888;
	private int field_13887;

	public DemoServerPlayerInteractionManager(ServerWorld serverWorld) {
		super(serverWorld);
	}

	@Override
	public void update() {
		super.update();
		this.field_13887++;
		long l = this.world.getTime();
		long m = l / 24000L + 1L;
		if (!this.field_13890 && this.field_13887 > 20) {
			this.field_13890 = true;
			this.player.field_13987.sendPacket(new GameStateChangeS2CPacket(5, 0.0F));
		}

		this.field_13889 = l > 120500L;
		if (this.field_13889) {
			this.field_13888++;
		}

		if (l % 24000L == 500L) {
			if (m <= 6L) {
				if (m == 6L) {
					this.player.field_13987.sendPacket(new GameStateChangeS2CPacket(5, 104.0F));
				} else {
					this.player.method_9203(new TranslatableTextComponent("demo.day." + m));
				}
			}
		} else if (m == 1L) {
			if (l == 100L) {
				this.player.field_13987.sendPacket(new GameStateChangeS2CPacket(5, 101.0F));
			} else if (l == 175L) {
				this.player.field_13987.sendPacket(new GameStateChangeS2CPacket(5, 102.0F));
			} else if (l == 250L) {
				this.player.field_13987.sendPacket(new GameStateChangeS2CPacket(5, 103.0F));
			}
		} else if (m == 5L && l % 24000L == 22000L) {
			this.player.method_9203(new TranslatableTextComponent("demo.day.warning"));
		}
	}

	private void method_14031() {
		if (this.field_13888 > 100) {
			this.player.method_9203(new TranslatableTextComponent("demo.reminder"));
			this.field_13888 = 0;
		}
	}

	@Override
	public void method_14263(BlockPos blockPos, Direction direction) {
		if (this.field_13889) {
			this.method_14031();
		} else {
			super.method_14263(blockPos, direction);
		}
	}

	@Override
	public void method_14258(BlockPos blockPos) {
		if (!this.field_13889) {
			super.method_14258(blockPos);
		}
	}

	@Override
	public boolean tryBreakBlock(BlockPos blockPos) {
		return this.field_13889 ? false : super.tryBreakBlock(blockPos);
	}

	@Override
	public ActionResult interactItem(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand) {
		if (this.field_13889) {
			this.method_14031();
			return ActionResult.PASS;
		} else {
			return super.interactItem(playerEntity, world, itemStack, hand);
		}
	}

	@Override
	public ActionResult interactBlock(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand, BlockHitResult blockHitResult) {
		if (this.field_13889) {
			this.method_14031();
			return ActionResult.PASS;
		} else {
			return super.interactBlock(playerEntity, world, itemStack, hand, blockHitResult);
		}
	}
}

package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Blocks;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = ChestAnimationProgress.class
	)})
public class EnderChestBlockEntity extends BlockEntity implements ChestAnimationProgress, Tickable {
	public float animationProgress;
	public float lastAnimationProgress;
	public int viewerCount;
	private int ticks;

	public EnderChestBlockEntity() {
		super(BlockEntityType.ENDER_CHEST);
	}

	@Override
	public void tick() {
		if (++this.ticks % 20 * 4 == 0) {
			this.world.addBlockAction(this.pos, Blocks.ENDER_CHEST, 1, this.viewerCount);
		}

		this.lastAnimationProgress = this.animationProgress;
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		float f = 0.1F;
		if (this.viewerCount > 0 && this.animationProgress == 0.0F) {
			double d = (double)i + 0.5;
			double e = (double)k + 0.5;
			this.world
				.playSound(null, d, (double)j + 0.5, e, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (this.viewerCount == 0 && this.animationProgress > 0.0F || this.viewerCount > 0 && this.animationProgress < 1.0F) {
			float g = this.animationProgress;
			if (this.viewerCount > 0) {
				this.animationProgress += 0.1F;
			} else {
				this.animationProgress -= 0.1F;
			}

			if (this.animationProgress > 1.0F) {
				this.animationProgress = 1.0F;
			}

			float h = 0.5F;
			if (this.animationProgress < 0.5F && g >= 0.5F) {
				double e = (double)i + 0.5;
				double l = (double)k + 0.5;
				this.world
					.playSound(null, e, (double)j + 0.5, l, SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (this.animationProgress < 0.0F) {
				this.animationProgress = 0.0F;
			}
		}
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.viewerCount = j;
			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	@Override
	public void markRemoved() {
		this.resetBlock();
		super.markRemoved();
	}

	public void onOpen() {
		this.viewerCount++;
		this.world.addBlockAction(this.pos, Blocks.ENDER_CHEST, 1, this.viewerCount);
	}

	public void onClose() {
		this.viewerCount--;
		this.world.addBlockAction(this.pos, Blocks.ENDER_CHEST, 1, this.viewerCount);
	}

	public boolean canPlayerUse(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastAnimationProgress, this.animationProgress);
	}
}

package net.minecraft.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LeadKnotEntity extends AbstractDecorationEntity {
	public LeadKnotEntity(EntityType<? extends LeadKnotEntity> entityType, World world) {
		super(entityType, world);
	}

	public LeadKnotEntity(World world, BlockPos blockPos) {
		super(EntityType.LEASH_KNOT, world, blockPos);
		this.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
		float f = 0.125F;
		float g = 0.1875F;
		float h = 0.25F;
		this.method_5857(new BoundingBox(this.x - 0.1875, this.y - 0.25 + 0.125, this.z - 0.1875, this.x + 0.1875, this.y + 0.25 + 0.125, this.z + 0.1875));
		this.teleporting = true;
	}

	@Override
	public void setPosition(double d, double e, double f) {
		super.setPosition((double)MathHelper.floor(d) + 0.5, (double)MathHelper.floor(e) + 0.5, (double)MathHelper.floor(f) + 0.5);
	}

	@Override
	protected void method_6895() {
		this.x = (double)this.field_7100.getX() + 0.5;
		this.y = (double)this.field_7100.getY() + 0.5;
		this.z = (double)this.field_7100.getZ() + 0.5;
	}

	@Override
	public void method_6892(Direction direction) {
	}

	@Override
	public int getWidthPixels() {
		return 9;
	}

	@Override
	public int getHeightPixels() {
		return 9;
	}

	@Override
	protected float method_18378(EntityPose entityPose, EntitySize entitySize) {
		return -0.0625F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		return d < 1024.0;
	}

	@Override
	public void copyEntityData(@Nullable Entity entity) {
		this.method_5783(SoundEvents.field_15184, 1.0F, 1.0F);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
	}

	@Override
	public boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		if (this.field_6002.isClient) {
			return true;
		} else {
			boolean bl = false;
			double d = 7.0;
			List<MobEntity> list = this.field_6002
				.method_18467(MobEntity.class, new BoundingBox(this.x - 7.0, this.y - 7.0, this.z - 7.0, this.x + 7.0, this.y + 7.0, this.z + 7.0));

			for (MobEntity mobEntity : list) {
				if (mobEntity.getHoldingEntity() == playerEntity) {
					mobEntity.attachLeash(this, true);
					bl = true;
				}
			}

			if (!bl) {
				this.invalidate();
				if (playerEntity.abilities.creativeMode) {
					for (MobEntity mobEntityx : list) {
						if (mobEntityx.isLeashed() && mobEntityx.getHoldingEntity() == this) {
							mobEntityx.detachLeash(true, false);
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean method_6888() {
		return this.field_6002.method_8320(this.field_7100).getBlock().method_9525(BlockTags.field_16584);
	}

	public static LeadKnotEntity method_6932(World world, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();

		for (LeadKnotEntity leadKnotEntity : world.method_18467(
			LeadKnotEntity.class, new BoundingBox((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
		)) {
			if (leadKnotEntity.method_6896().equals(blockPos)) {
				return leadKnotEntity;
			}
		}

		LeadKnotEntity leadKnotEntity2 = new LeadKnotEntity(world, blockPos);
		world.spawnEntity(leadKnotEntity2);
		leadKnotEntity2.method_6894();
		return leadKnotEntity2;
	}

	@Override
	public void method_6894() {
		this.method_5783(SoundEvents.field_15062, 1.0F, 1.0F);
	}

	@Override
	public Packet<?> method_18002() {
		return new EntitySpawnS2CPacket(this, this.method_5864(), 0, this.method_6896());
	}
}

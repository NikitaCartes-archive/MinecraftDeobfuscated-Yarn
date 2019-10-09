package net.minecraft.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
		this.setBoundingBox(
			new Box(this.getX() - 0.1875, this.getY() - 0.25 + 0.125, this.getZ() - 0.1875, this.getX() + 0.1875, this.getY() + 0.25 + 0.125, this.getZ() + 0.1875)
		);
		this.teleporting = true;
	}

	@Override
	public void setPosition(double d, double e, double f) {
		super.setPosition((double)MathHelper.floor(d) + 0.5, (double)MathHelper.floor(e) + 0.5, (double)MathHelper.floor(f) + 0.5);
	}

	@Override
	protected void method_6895() {
		this.setPos((double)this.blockPos.getX() + 0.5, (double)this.blockPos.getY() + 0.5, (double)this.blockPos.getZ() + 0.5);
	}

	@Override
	public void setFacing(Direction direction) {
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
	protected float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return -0.0625F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		return d < 1024.0;
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		if (this.world.isClient) {
			return true;
		} else {
			boolean bl = false;
			double d = 7.0;
			List<MobEntity> list = this.world
				.getNonSpectatingEntities(
					MobEntity.class, new Box(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0)
				);

			for (MobEntity mobEntity : list) {
				if (mobEntity.getHoldingEntity() == playerEntity) {
					mobEntity.attachLeash(this, true);
					bl = true;
				}
			}

			if (!bl) {
				this.remove();
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
		return this.world.getBlockState(this.blockPos).getBlock().matches(BlockTags.FENCES);
	}

	public static LeadKnotEntity getOrCreate(World world, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();

		for (LeadKnotEntity leadKnotEntity : world.getNonSpectatingEntities(
			LeadKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
		)) {
			if (leadKnotEntity.getDecorationBlockPos().equals(blockPos)) {
				return leadKnotEntity;
			}
		}

		LeadKnotEntity leadKnotEntity2 = new LeadKnotEntity(world, blockPos);
		world.spawnEntity(leadKnotEntity2);
		leadKnotEntity2.onPlace();
		return leadKnotEntity2;
	}

	@Override
	public void onPlace() {
		this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0F, 1.0F);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, this.getType(), 0, this.getDecorationBlockPos());
	}
}

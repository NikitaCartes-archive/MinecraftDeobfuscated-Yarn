package net.minecraft.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class LeashKnotEntity extends BlockAttachedEntity {
	public static final double field_30455 = 0.375;

	public LeashKnotEntity(EntityType<? extends LeashKnotEntity> entityType, World world) {
		super(entityType, world);
	}

	public LeashKnotEntity(World world, BlockPos pos) {
		super(EntityType.LEASH_KNOT, world, pos);
		this.setPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void updateAttachmentPosition() {
		this.setPos((double)this.attachedBlockPos.getX() + 0.5, (double)this.attachedBlockPos.getY() + 0.375, (double)this.attachedBlockPos.getZ() + 0.5);
		double d = (double)this.getType().getWidth() / 2.0;
		double e = (double)this.getType().getHeight();
		this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + e, this.getZ() + d));
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < 1024.0;
	}

	@Override
	public void onBreak(@Nullable Entity breaker) {
		this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0F, 1.0F);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (this.getWorld().isClient) {
			return ActionResult.SUCCESS;
		} else {
			boolean bl = false;
			double d = 7.0;
			List<MobEntity> list = this.getWorld()
				.getNonSpectatingEntities(
					MobEntity.class, new Box(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0)
				);

			for (MobEntity mobEntity : list) {
				if (mobEntity.getHoldingEntity() == player) {
					mobEntity.attachLeash(this, true);
					bl = true;
				}
			}

			boolean bl2 = false;
			if (!bl) {
				this.discard();
				if (player.getAbilities().creativeMode) {
					for (MobEntity mobEntity2 : list) {
						if (mobEntity2.isLeashed() && mobEntity2.getHoldingEntity() == this) {
							mobEntity2.detachLeash(true, false);
							bl2 = true;
						}
					}
				}
			}

			if (bl || bl2) {
				this.emitGameEvent(GameEvent.BLOCK_ATTACH, player);
			}

			return ActionResult.CONSUME;
		}
	}

	@Override
	public boolean canStayAttached() {
		return this.getWorld().getBlockState(this.attachedBlockPos).isIn(BlockTags.FENCES);
	}

	public static LeashKnotEntity getOrCreate(World world, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for (LeashKnotEntity leashKnotEntity : world.getNonSpectatingEntities(
			LeashKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
		)) {
			if (leashKnotEntity.getAttachedBlockPos().equals(pos)) {
				return leashKnotEntity;
			}
		}

		LeashKnotEntity leashKnotEntity2 = new LeashKnotEntity(world, pos);
		world.spawnEntity(leashKnotEntity2);
		return leashKnotEntity2;
	}

	public void method_59944() {
		this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0F, 1.0F);
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, 0, this.getAttachedBlockPos());
	}

	@Override
	public Vec3d getLeashPos(float delta) {
		return this.getLerpedPos(delta).add(0.0, 0.2, 0.0);
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(Items.LEAD);
	}
}

package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MooshroomEntity extends CowEntity {
	public MooshroomEntity(World world) {
		super(EntityType.MOOSHROOM, world);
		this.setSize(0.9F, 1.4F);
		this.spawningGround = Blocks.field_10402;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8428 && this.getBreedingAge() >= 0 && !playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, new ItemStack(Items.field_8208));
			} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.field_8208))) {
				playerEntity.dropItem(new ItemStack(Items.field_8208), false);
			}

			return true;
		} else if (itemStack.getItem() == Items.field_8868 && this.getBreedingAge() >= 0) {
			this.world.method_8406(ParticleTypes.field_11236, this.x, this.y + (double)(this.height / 2.0F), this.z, 0.0, 0.0, 0.0);
			if (!this.world.isRemote) {
				this.invalidate();
				CowEntity cowEntity = new CowEntity(this.world);
				cowEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.field_6283 = this.field_6283;
				if (this.hasCustomName()) {
					cowEntity.setCustomName(this.getCustomName());
				}

				this.world.spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y + (double)this.height, this.z, new ItemStack(Blocks.field_10559)));
				}

				itemStack.applyDamage(1, playerEntity);
				this.playSoundAtEntity(SoundEvents.field_14705, 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	public MooshroomEntity createChild(PassiveEntity passiveEntity) {
		return new MooshroomEntity(this.world);
	}
}

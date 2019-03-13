package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ZombieHorseEntity extends HorseBaseEntity {
	public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(15.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
		this.method_5996(ATTR_JUMP_STRENGTH).setBaseValue(this.method_6774());
	}

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected SoundEvent method_5994() {
		super.method_5994();
		return SoundEvents.field_15154;
	}

	@Override
	protected SoundEvent method_6002() {
		super.method_6002();
		return SoundEvents.field_14543;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		super.method_6011(damageSource);
		return SoundEvents.field_15179;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.ZOMBIE_HORSE.method_5883(this.field_6002);
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.method_5992(playerEntity, hand);
		} else if (!this.isTame()) {
			return false;
		} else if (this.isChild()) {
			return super.method_5992(playerEntity, hand);
		} else if (playerEntity.isSneaking()) {
			this.method_6722(playerEntity);
			return true;
		} else if (this.hasPassengers()) {
			return super.method_5992(playerEntity, hand);
		} else {
			if (!itemStack.isEmpty()) {
				if (!this.isSaddled() && itemStack.getItem() == Items.field_8175) {
					this.method_6722(playerEntity);
					return true;
				}

				if (itemStack.interactWithEntity(playerEntity, this, hand)) {
					return true;
				}
			}

			this.method_6726(playerEntity);
			return true;
		}
	}

	@Override
	protected void method_6764() {
	}
}

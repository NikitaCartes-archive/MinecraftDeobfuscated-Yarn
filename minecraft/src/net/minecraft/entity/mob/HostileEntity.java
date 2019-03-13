package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BaseBowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class HostileEntity extends MobEntityWithAi implements Monster {
	protected HostileEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public SoundCategory method_5634() {
		return SoundCategory.field_15251;
	}

	@Override
	public void updateMovement() {
		this.method_6119();
		this.method_16827();
		super.updateMovement();
	}

	protected void method_16827() {
		float f = this.method_5718();
		if (f > 0.5F) {
			this.despawnCounter += 2;
		}
	}

	@Override
	public void update() {
		super.update();
		if (!this.field_6002.isClient && this.field_6002.getDifficulty() == Difficulty.PEACEFUL) {
			this.invalidate();
		}
	}

	@Override
	protected SoundEvent method_5737() {
		return SoundEvents.field_14630;
	}

	@Override
	protected SoundEvent method_5625() {
		return SoundEvents.field_14836;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14994;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14899;
	}

	@Override
	protected SoundEvent method_6041(int i) {
		return i > 4 ? SoundEvents.field_15157 : SoundEvents.field_14754;
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return 0.5F - viewableWorld.method_8610(blockPos);
	}

	protected boolean checkLightLevelForSpawn() {
		BlockPos blockPos = new BlockPos(this.x, this.method_5829().minY, this.z);
		if (this.field_6002.method_8314(LightType.SKY, blockPos) > this.random.nextInt(32)) {
			return false;
		} else {
			int i = this.field_6002.isThundering() ? this.field_6002.method_8603(blockPos, 10) : this.field_6002.method_8602(blockPos);
			return i <= this.random.nextInt(8);
		}
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return iWorld.getDifficulty() != Difficulty.PEACEFUL && this.checkLightLevelForSpawn() && super.method_5979(iWorld, spawnType);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_6127().register(EntityAttributes.ATTACK_DAMAGE);
	}

	@Override
	protected boolean canDropLootAndXp() {
		return true;
	}

	public boolean method_7076(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public ItemStack method_18808(ItemStack itemStack) {
		if (itemStack.getItem() instanceof BaseBowItem) {
			Predicate<ItemStack> predicate = ((BaseBowItem)itemStack.getItem()).method_19268();
			ItemStack itemStack2 = BaseBowItem.method_18815(this, predicate);
			return itemStack2.isEmpty() ? new ItemStack(Items.field_8107) : itemStack2;
		} else {
			return ItemStack.EMPTY;
		}
	}
}

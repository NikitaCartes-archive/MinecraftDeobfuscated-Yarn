/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExperienceOrbEntity
extends Entity {
    private int orbAge;
    private int health = 5;
    private int amount;
    private int pickingCount = 1;
    private PlayerEntity target;

    public ExperienceOrbEntity(World world, double x, double y, double z, int amount) {
        this((EntityType<? extends ExperienceOrbEntity>)EntityType.EXPERIENCE_ORB, world);
        this.setPosition(x, y, z);
        this.yaw = (float)(this.random.nextDouble() * 360.0);
        this.setVelocity((this.random.nextDouble() * (double)0.2f - (double)0.1f) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * (double)0.2f - (double)0.1f) * 2.0);
        this.amount = amount;
    }

    public ExperienceOrbEntity(EntityType<? extends ExperienceOrbEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void tick() {
        Vec3d vec3d;
        double d;
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        if (this.isSubmergedIn(FluidTags.WATER)) {
            this.applyWaterMovement();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
        }
        if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA)) {
            this.setVelocity((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }
        if (!this.world.isSpaceEmpty(this.getBoundingBox())) {
            this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
        }
        if (this.age % 20 == 1) {
            this.method_31498();
        }
        if (this.target != null && (this.target.isSpectator() || this.target.isDead())) {
            this.target = null;
        }
        if (this.target != null && (d = (vec3d = new Vec3d(this.target.getX() - this.getX(), this.target.getY() + (double)this.target.getStandingEyeHeight() / 2.0 - this.getY(), this.target.getZ() - this.getZ())).lengthSquared()) < 64.0) {
            double e = 1.0 - Math.sqrt(d) / 8.0;
            this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(e * e * 0.1)));
        }
        this.move(MovementType.SELF, this.getVelocity());
        float f = 0.98f;
        if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98f;
        }
        this.setVelocity(this.getVelocity().multiply(f, 0.98, f));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
        }
        ++this.orbAge;
        if (this.orbAge >= 6000) {
            this.discard();
        }
    }

    private void method_31498() {
        if (this.target == null || this.target.squaredDistanceTo(this) > 64.0) {
            this.target = this.world.getClosestPlayer(this, 8.0);
        }
        if (this.world instanceof ServerWorld) {
            List<ExperienceOrbEntity> list = this.world.getEntitiesByType(TypeFilter.instanceOf(ExperienceOrbEntity.class), this.getBoundingBox().expand(0.5), this::isMergeable);
            for (ExperienceOrbEntity experienceOrbEntity : list) {
                this.merge(experienceOrbEntity);
            }
        }
    }

    public static void spawn(ServerWorld world, Vec3d pos, int amount) {
        while (amount > 0) {
            int i = ExperienceOrbEntity.roundToOrbSize(amount);
            amount -= i;
            if (ExperienceOrbEntity.wasMergedIntoExistingOrb(world, pos, i)) continue;
            world.spawnEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), i));
        }
    }

    private static boolean wasMergedIntoExistingOrb(ServerWorld world, Vec3d pos, int amount) {
        Box box = Box.of(pos, 1.0, 1.0, 1.0);
        int i = world.getRandom().nextInt(40);
        List<ExperienceOrbEntity> list = world.getEntitiesByType(TypeFilter.instanceOf(ExperienceOrbEntity.class), box, experienceOrbEntity -> ExperienceOrbEntity.isMergeable(experienceOrbEntity, i, amount));
        if (!list.isEmpty()) {
            ExperienceOrbEntity experienceOrbEntity2 = list.get(0);
            ++experienceOrbEntity2.pickingCount;
            experienceOrbEntity2.orbAge = 0;
            return true;
        }
        return false;
    }

    private boolean isMergeable(ExperienceOrbEntity other) {
        return other != this && ExperienceOrbEntity.isMergeable(other, this.getId(), this.amount);
    }

    private static boolean isMergeable(ExperienceOrbEntity orb, int seed, int amount) {
        return !orb.isRemoved() && (orb.getId() - seed) % 40 == 0 && orb.amount == amount;
    }

    private void merge(ExperienceOrbEntity other) {
        this.pickingCount += other.pickingCount;
        this.orbAge = Math.min(this.orbAge, other.orbAge);
        other.discard();
    }

    private void applyWaterMovement() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)0.99f, Math.min(vec3d.y + (double)5.0E-4f, (double)0.06f), vec3d.z * (double)0.99f);
    }

    @Override
    protected void onSwimmingStart() {
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        this.health = (int)((float)this.health - amount);
        if (this.health <= 0) {
            this.discard();
        }
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        tag.putShort("Health", (short)this.health);
        tag.putShort("Age", (short)this.orbAge);
        tag.putShort("Value", (short)this.amount);
        tag.putInt("Count", this.pickingCount);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        this.health = tag.getShort("Health");
        this.orbAge = tag.getShort("Age");
        this.amount = tag.getShort("Value");
        this.pickingCount = Math.max(tag.getInt("Count"), 1);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.world.isClient) {
            return;
        }
        if (player.experiencePickUpDelay == 0) {
            ItemStack itemStack;
            player.experiencePickUpDelay = 2;
            player.sendPickup(this, 1);
            Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
            if (entry != null && !(itemStack = entry.getValue()).isEmpty() && itemStack.isDamaged()) {
                int i = Math.min(this.getMendingRepairAmount(this.amount), itemStack.getDamage());
                this.amount -= this.getMendingRepairCost(i);
                itemStack.setDamage(itemStack.getDamage() - i);
            }
            if (this.amount > 0) {
                player.addExperience(this.amount);
            }
            --this.pickingCount;
            if (this.pickingCount == 0) {
                this.discard();
            }
        }
    }

    private int getMendingRepairCost(int repairAmount) {
        return repairAmount / 2;
    }

    private int getMendingRepairAmount(int experienceAmount) {
        return experienceAmount * 2;
    }

    public int getExperienceAmount() {
        return this.amount;
    }

    @Environment(value=EnvType.CLIENT)
    public int getOrbSize() {
        if (this.amount >= 2477) {
            return 10;
        }
        if (this.amount >= 1237) {
            return 9;
        }
        if (this.amount >= 617) {
            return 8;
        }
        if (this.amount >= 307) {
            return 7;
        }
        if (this.amount >= 149) {
            return 6;
        }
        if (this.amount >= 73) {
            return 5;
        }
        if (this.amount >= 37) {
            return 4;
        }
        if (this.amount >= 17) {
            return 3;
        }
        if (this.amount >= 7) {
            return 2;
        }
        if (this.amount >= 3) {
            return 1;
        }
        return 0;
    }

    public static int roundToOrbSize(int value) {
        if (value >= 2477) {
            return 2477;
        }
        if (value >= 1237) {
            return 1237;
        }
        if (value >= 617) {
            return 617;
        }
        if (value >= 307) {
            return 307;
        }
        if (value >= 149) {
            return 149;
        }
        if (value >= 73) {
            return 73;
        }
        if (value >= 37) {
            return 37;
        }
        if (value >= 17) {
            return 17;
        }
        if (value >= 7) {
            return 7;
        }
        if (value >= 3) {
            return 3;
        }
        return 1;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new ExperienceOrbSpawnS2CPacket(this);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.AMBIENT;
    }
}


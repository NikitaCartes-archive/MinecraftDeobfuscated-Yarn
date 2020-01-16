/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DamageTracker {
    private final List<DamageRecord> recentDamage = Lists.newArrayList();
    private final LivingEntity entity;
    private int ageOnLastDamage;
    private int ageOnLastAttacked;
    private int ageOnLastUpdate;
    private boolean recentlyAttacked;
    private boolean hasDamage;
    private String fallDeathSuffix;

    public DamageTracker(LivingEntity livingEntity) {
        this.entity = livingEntity;
    }

    public void setFallDeathSuffix() {
        this.clearFallDeathSuffix();
        if (this.entity.isClimbing()) {
            Block block = this.entity.world.getBlockState(new BlockPos(this.entity)).getBlock();
            if (block == Blocks.LADDER) {
                this.fallDeathSuffix = "ladder";
            } else if (block == Blocks.VINE) {
                this.fallDeathSuffix = "vines";
            }
        } else if (this.entity.isTouchingWater()) {
            this.fallDeathSuffix = "water";
        }
    }

    public void onDamage(DamageSource damageSource, float originalHealth, float f) {
        this.update();
        this.setFallDeathSuffix();
        DamageRecord damageRecord = new DamageRecord(damageSource, this.entity.age, originalHealth, f, this.fallDeathSuffix, this.entity.fallDistance);
        this.recentDamage.add(damageRecord);
        this.ageOnLastDamage = this.entity.age;
        this.hasDamage = true;
        if (damageRecord.isAttackerLiving() && !this.recentlyAttacked && this.entity.isAlive()) {
            this.recentlyAttacked = true;
            this.ageOnLastUpdate = this.ageOnLastAttacked = this.entity.age;
            this.entity.enterCombat();
        }
    }

    public Text getDeathMessage() {
        Text text3;
        if (this.recentDamage.isEmpty()) {
            return new TranslatableText("death.attack.generic", this.entity.getDisplayName());
        }
        DamageRecord damageRecord = this.getBiggestFall();
        DamageRecord damageRecord2 = this.recentDamage.get(this.recentDamage.size() - 1);
        Text text = damageRecord2.getAttackerName();
        Entity entity = damageRecord2.getDamageSource().getAttacker();
        if (damageRecord != null && damageRecord2.getDamageSource() == DamageSource.FALL) {
            Text text2 = damageRecord.getAttackerName();
            if (damageRecord.getDamageSource() == DamageSource.FALL || damageRecord.getDamageSource() == DamageSource.OUT_OF_WORLD) {
                text3 = new TranslatableText("death.fell.accident." + this.getFallDeathSuffix(damageRecord), this.entity.getDisplayName());
            } else if (!(text2 == null || text != null && text2.equals(text))) {
                ItemStack itemStack;
                Entity entity2 = damageRecord.getDamageSource().getAttacker();
                ItemStack itemStack2 = itemStack = entity2 instanceof LivingEntity ? ((LivingEntity)entity2).getMainHandStack() : ItemStack.EMPTY;
                text3 = !itemStack.isEmpty() && itemStack.hasCustomName() ? new TranslatableText("death.fell.assist.item", this.entity.getDisplayName(), text2, itemStack.toHoverableText()) : new TranslatableText("death.fell.assist", this.entity.getDisplayName(), text2);
            } else if (text != null) {
                ItemStack itemStack2;
                ItemStack itemStack = itemStack2 = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack() : ItemStack.EMPTY;
                text3 = !itemStack2.isEmpty() && itemStack2.hasCustomName() ? new TranslatableText("death.fell.finish.item", this.entity.getDisplayName(), text, itemStack2.toHoverableText()) : new TranslatableText("death.fell.finish", this.entity.getDisplayName(), text);
            } else {
                text3 = new TranslatableText("death.fell.killer", this.entity.getDisplayName());
            }
        } else {
            text3 = damageRecord2.getDamageSource().getDeathMessage(this.entity);
        }
        return text3;
    }

    @Nullable
    public LivingEntity getBiggestAttacker() {
        LivingEntity livingEntity = null;
        PlayerEntity playerEntity = null;
        float f = 0.0f;
        float g = 0.0f;
        for (DamageRecord damageRecord : this.recentDamage) {
            if (damageRecord.getDamageSource().getAttacker() instanceof PlayerEntity && (playerEntity == null || damageRecord.getDamage() > g)) {
                g = damageRecord.getDamage();
                playerEntity = (PlayerEntity)damageRecord.getDamageSource().getAttacker();
            }
            if (!(damageRecord.getDamageSource().getAttacker() instanceof LivingEntity) || livingEntity != null && !(damageRecord.getDamage() > f)) continue;
            f = damageRecord.getDamage();
            livingEntity = (LivingEntity)damageRecord.getDamageSource().getAttacker();
        }
        if (playerEntity != null && g >= f / 3.0f) {
            return playerEntity;
        }
        return livingEntity;
    }

    @Nullable
    private DamageRecord getBiggestFall() {
        DamageRecord damageRecord = null;
        DamageRecord damageRecord2 = null;
        float f = 0.0f;
        float g = 0.0f;
        for (int i = 0; i < this.recentDamage.size(); ++i) {
            DamageRecord damageRecord4;
            DamageRecord damageRecord3 = this.recentDamage.get(i);
            DamageRecord damageRecord5 = damageRecord4 = i > 0 ? this.recentDamage.get(i - 1) : null;
            if ((damageRecord3.getDamageSource() == DamageSource.FALL || damageRecord3.getDamageSource() == DamageSource.OUT_OF_WORLD) && damageRecord3.getFallDistance() > 0.0f && (damageRecord == null || damageRecord3.getFallDistance() > g)) {
                damageRecord = i > 0 ? damageRecord4 : damageRecord3;
                g = damageRecord3.getFallDistance();
            }
            if (damageRecord3.getFallDeathSuffix() == null || damageRecord2 != null && !(damageRecord3.getDamage() > f)) continue;
            damageRecord2 = damageRecord3;
            f = damageRecord3.getDamage();
        }
        if (g > 5.0f && damageRecord != null) {
            return damageRecord;
        }
        if (f > 5.0f && damageRecord2 != null) {
            return damageRecord2;
        }
        return null;
    }

    private String getFallDeathSuffix(DamageRecord damageRecord) {
        return damageRecord.getFallDeathSuffix() == null ? "generic" : damageRecord.getFallDeathSuffix();
    }

    public int getTimeSinceLastAttack() {
        if (this.recentlyAttacked) {
            return this.entity.age - this.ageOnLastAttacked;
        }
        return this.ageOnLastUpdate - this.ageOnLastAttacked;
    }

    private void clearFallDeathSuffix() {
        this.fallDeathSuffix = null;
    }

    public void update() {
        int i;
        int n = i = this.recentlyAttacked ? 300 : 100;
        if (this.hasDamage && (!this.entity.isAlive() || this.entity.age - this.ageOnLastDamage > i)) {
            boolean bl = this.recentlyAttacked;
            this.hasDamage = false;
            this.recentlyAttacked = false;
            this.ageOnLastUpdate = this.entity.age;
            if (bl) {
                this.entity.endCombat();
            }
            this.recentDamage.clear();
        }
    }

    public LivingEntity getEntity() {
        return this.entity;
    }
}


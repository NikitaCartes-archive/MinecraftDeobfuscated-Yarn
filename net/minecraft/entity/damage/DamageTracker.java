/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DamageTracker {
    public static final int DAMAGE_COOLDOWN = 100;
    public static final int ATTACK_DAMAGE_COOLDOWN = 300;
    private static final Style INTENTIONAL_GAME_DESIGN_ISSUE_LINK_STYLE = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("MCPE-28723")));
    private final List<DamageRecord> recentDamage = Lists.newArrayList();
    private final LivingEntity entity;
    private int ageOnLastDamage;
    private int ageOnLastAttacked;
    private int ageOnLastUpdate;
    private boolean recentlyAttacked;
    private boolean hasDamage;
    @Nullable
    private String fallDeathSuffix;

    public DamageTracker(LivingEntity entity) {
        this.entity = entity;
    }

    public void setFallDeathSuffix() {
        this.clearFallDeathSuffix();
        Optional<BlockPos> optional = this.entity.getClimbingPos();
        if (optional.isPresent()) {
            BlockState blockState = this.entity.world.getBlockState(optional.get());
            this.fallDeathSuffix = blockState.isOf(Blocks.LADDER) || blockState.isIn(BlockTags.TRAPDOORS) ? "ladder" : (blockState.isOf(Blocks.VINE) ? "vines" : (blockState.isOf(Blocks.WEEPING_VINES) || blockState.isOf(Blocks.WEEPING_VINES_PLANT) ? "weeping_vines" : (blockState.isOf(Blocks.TWISTING_VINES) || blockState.isOf(Blocks.TWISTING_VINES_PLANT) ? "twisting_vines" : (blockState.isOf(Blocks.SCAFFOLDING) ? "scaffolding" : "other_climbable"))));
        } else if (this.entity.isTouchingWater()) {
            this.fallDeathSuffix = "water";
        }
    }

    public void onDamage(DamageSource damageSource, float originalHealth, float damage) {
        this.update();
        this.setFallDeathSuffix();
        DamageRecord damageRecord = new DamageRecord(damageSource, this.entity.age, originalHealth, damage, this.fallDeathSuffix, this.entity.fallDistance);
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
            return Text.translatable("death.attack.generic", this.entity.getDisplayName());
        }
        DamageRecord damageRecord = this.getBiggestFall();
        DamageRecord damageRecord2 = this.recentDamage.get(this.recentDamage.size() - 1);
        Text text = damageRecord2.getAttackerName();
        DamageSource damageSource = damageRecord2.getDamageSource();
        Entity entity = damageSource.getAttacker();
        DeathMessageType deathMessageType = damageSource.getType().deathMessageType();
        if (damageRecord != null && deathMessageType == DeathMessageType.FALL_VARIANTS) {
            Text text2 = damageRecord.getAttackerName();
            DamageSource damageSource2 = damageRecord.getDamageSource();
            if (damageSource2.isIn(DamageTypeTags.IS_FALL) || damageSource2.isIn(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)) {
                text3 = Text.translatable("death.fell.accident." + this.getFallDeathSuffix(damageRecord), this.entity.getDisplayName());
            } else if (text2 != null && !text2.equals(text)) {
                ItemStack itemStack;
                Entity entity2 = damageSource2.getAttacker();
                if (entity2 instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity)entity2;
                    v0 = livingEntity.getMainHandStack();
                } else {
                    v0 = itemStack = ItemStack.EMPTY;
                }
                text3 = !itemStack.isEmpty() && itemStack.hasCustomName() ? Text.translatable("death.fell.assist.item", this.entity.getDisplayName(), text2, itemStack.toHoverableText()) : Text.translatable("death.fell.assist", this.entity.getDisplayName(), text2);
            } else if (text != null) {
                ItemStack itemStack2;
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity)entity;
                    v1 = livingEntity2.getMainHandStack();
                } else {
                    v1 = itemStack2 = ItemStack.EMPTY;
                }
                text3 = !itemStack2.isEmpty() && itemStack2.hasCustomName() ? Text.translatable("death.fell.finish.item", this.entity.getDisplayName(), text, itemStack2.toHoverableText()) : Text.translatable("death.fell.finish", this.entity.getDisplayName(), text);
            } else {
                text3 = Text.translatable("death.fell.killer", this.entity.getDisplayName());
            }
        } else {
            if (deathMessageType == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
                String string = "death.attack." + damageSource.getName();
                MutableText text4 = Texts.bracketed(Text.translatable(string + ".link")).fillStyle(INTENTIONAL_GAME_DESIGN_ISSUE_LINK_STYLE);
                return Text.translatable(string + ".message", this.entity.getDisplayName(), text4);
            }
            text3 = damageSource.getDeathMessage(this.entity);
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
            Entity entity = damageRecord.getDamageSource().getAttacker();
            if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity2 = (PlayerEntity)entity;
                if (playerEntity == null || damageRecord.getDamage() > g) {
                    g = damageRecord.getDamage();
                    playerEntity = playerEntity2;
                }
            }
            if (!((entity = damageRecord.getDamageSource().getAttacker()) instanceof LivingEntity)) continue;
            LivingEntity livingEntity2 = (LivingEntity)entity;
            if (livingEntity != null && !(damageRecord.getDamage() > f)) continue;
            f = damageRecord.getDamage();
            livingEntity = livingEntity2;
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
            float h;
            DamageRecord damageRecord3 = this.recentDamage.get(i);
            DamageRecord damageRecord4 = i > 0 ? this.recentDamage.get(i - 1) : null;
            DamageSource damageSource = damageRecord3.getDamageSource();
            boolean bl = damageSource.isIn(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL);
            float f2 = h = bl ? Float.MAX_VALUE : damageRecord3.getFallDistance();
            if ((damageSource.isIn(DamageTypeTags.IS_FALL) || bl) && h > 0.0f && (damageRecord == null || h > g)) {
                damageRecord = i > 0 ? damageRecord4 : damageRecord3;
                g = h;
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

    public boolean hasDamage() {
        this.update();
        return this.hasDamage;
    }

    public boolean wasRecentlyAttacked() {
        this.update();
        return this.recentlyAttacked;
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

    @Nullable
    public DamageRecord getMostRecentDamage() {
        if (this.recentDamage.isEmpty()) {
            return null;
        }
        return this.recentDamage.get(this.recentDamage.size() - 1);
    }

    /**
     * Gets the Entity ID of the biggest attacker
     * @see #getBiggestAttacker() for getting the entity itself
     */
    public int getBiggestAttackerId() {
        LivingEntity livingEntity = this.getBiggestAttacker();
        return livingEntity == null ? -1 : livingEntity.getId();
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StatusEffectInstance
implements Comparable<StatusEffectInstance> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final StatusEffect type;
    int duration;
    private int amplifier;
    private boolean ambient;
    private boolean permanent;
    private boolean showParticles;
    private boolean showIcon;
    /**
     * The effect hidden when upgrading effects. Duration decreases with this
     * effect.
     * 
     * <p>This exists so that long-duration low-amplifier effects reappears
     * after short-duration high-amplifier effects run out.
     */
    @Nullable
    private StatusEffectInstance hiddenEffect;
    private Optional<FactorCalculationData> factorCalculationData;

    public StatusEffectInstance(StatusEffect statusEffect) {
        this(statusEffect, 0, 0);
    }

    public StatusEffectInstance(StatusEffect type, int duration) {
        this(type, duration, 0);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier) {
        this(type, duration, amplifier, false, true);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean visible) {
        this(type, duration, amplifier, ambient, visible, visible);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        this(type, duration, amplifier, ambient, showParticles, showIcon, null, type.getFactorCalculationDataSupplier());
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
        this.showIcon = showIcon;
        this.hiddenEffect = hiddenEffect;
        this.factorCalculationData = factorCalculationData;
    }

    public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
        this.type = statusEffectInstance.type;
        this.factorCalculationData = this.type.getFactorCalculationDataSupplier();
        this.copyFrom(statusEffectInstance);
    }

    public Optional<FactorCalculationData> getFactorCalculationData() {
        return this.factorCalculationData;
    }

    void copyFrom(StatusEffectInstance that) {
        this.duration = that.duration;
        this.amplifier = that.amplifier;
        this.ambient = that.ambient;
        this.showParticles = that.showParticles;
        this.showIcon = that.showIcon;
    }

    public boolean upgrade(StatusEffectInstance that) {
        if (this.type != that.type) {
            LOGGER.warn("This method should only be called for matching effects!");
        }
        int i = this.duration;
        boolean bl = false;
        if (that.amplifier > this.amplifier) {
            if (that.duration < this.duration) {
                StatusEffectInstance statusEffectInstance = this.hiddenEffect;
                this.hiddenEffect = new StatusEffectInstance(this);
                this.hiddenEffect.hiddenEffect = statusEffectInstance;
            }
            this.amplifier = that.amplifier;
            this.duration = that.duration;
            bl = true;
        } else if (that.duration > this.duration) {
            if (that.amplifier == this.amplifier) {
                this.duration = that.duration;
                bl = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new StatusEffectInstance(that);
            } else {
                this.hiddenEffect.upgrade(that);
            }
        }
        if (!that.ambient && this.ambient || bl) {
            this.ambient = that.ambient;
            bl = true;
        }
        if (that.showParticles != this.showParticles) {
            this.showParticles = that.showParticles;
            bl = true;
        }
        if (that.showIcon != this.showIcon) {
            this.showIcon = that.showIcon;
            bl = true;
        }
        if (i != this.duration) {
            this.factorCalculationData.ifPresent(factorCalculationData -> factorCalculationData.effectChangedTimestamp += this.duration - i);
            bl = true;
        }
        return bl;
    }

    public StatusEffect getEffectType() {
        return this.type;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean shouldShowParticles() {
        return this.showParticles;
    }

    public boolean shouldShowIcon() {
        return this.showIcon;
    }

    public boolean update(LivingEntity entity, Runnable overwriteCallback) {
        if (this.duration > 0) {
            if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
                this.applyUpdateEffect(entity);
            }
            this.updateDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.copyFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                overwriteCallback.run();
            }
        }
        this.factorCalculationData.ifPresent(factorCalculationData -> factorCalculationData.update(this));
        return this.duration > 0;
    }

    private int updateDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.updateDuration();
        }
        return --this.duration;
    }

    public void applyUpdateEffect(LivingEntity entity) {
        if (this.duration > 0) {
            this.type.applyUpdateEffect(entity, this.amplifier);
        }
    }

    public String getTranslationKey() {
        return this.type.getTranslationKey();
    }

    public String toString() {
        String string = this.amplifier > 0 ? this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration : this.getTranslationKey() + ", Duration: " + this.duration;
        if (!this.showParticles) {
            string = string + ", Particles: false";
        }
        if (!this.showIcon) {
            string = string + ", Show Icon: false";
        }
        return string;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof StatusEffectInstance) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance)o;
            return this.duration == statusEffectInstance.duration && this.amplifier == statusEffectInstance.amplifier && this.ambient == statusEffectInstance.ambient && this.type.equals(statusEffectInstance.type);
        }
        return false;
    }

    public int hashCode() {
        int i = this.type.hashCode();
        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        i = 31 * i + (this.ambient ? 1 : 0);
        return i;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("Id", StatusEffect.getRawId(this.getEffectType()));
        this.writeTypelessNbt(nbt);
        return nbt;
    }

    private void writeTypelessNbt(NbtCompound nbt) {
        nbt.putByte("Amplifier", (byte)this.getAmplifier());
        nbt.putInt("Duration", this.getDuration());
        nbt.putBoolean("Ambient", this.isAmbient());
        nbt.putBoolean("ShowParticles", this.shouldShowParticles());
        nbt.putBoolean("ShowIcon", this.shouldShowIcon());
        if (this.hiddenEffect != null) {
            NbtCompound nbtCompound = new NbtCompound();
            this.hiddenEffect.writeNbt(nbtCompound);
            nbt.put("HiddenEffect", nbtCompound);
        }
        this.factorCalculationData.ifPresent(factorCalculationData -> FactorCalculationData.CODEC.encodeStart(NbtOps.INSTANCE, (FactorCalculationData)factorCalculationData).resultOrPartial(LOGGER::error).ifPresent(factorCalculationDataNbt -> nbt.put("FactorCalculationData", (NbtElement)factorCalculationDataNbt)));
    }

    @Nullable
    public static StatusEffectInstance fromNbt(NbtCompound nbt) {
        int i = nbt.getInt("Id");
        StatusEffect statusEffect = StatusEffect.byRawId(i);
        if (statusEffect == null) {
            return null;
        }
        return StatusEffectInstance.fromNbt(statusEffect, nbt);
    }

    private static StatusEffectInstance fromNbt(StatusEffect type, NbtCompound nbt) {
        byte i = nbt.getByte("Amplifier");
        int j = nbt.getInt("Duration");
        boolean bl = nbt.getBoolean("Ambient");
        boolean bl2 = true;
        if (nbt.contains("ShowParticles", NbtElement.BYTE_TYPE)) {
            bl2 = nbt.getBoolean("ShowParticles");
        }
        boolean bl3 = bl2;
        if (nbt.contains("ShowIcon", NbtElement.BYTE_TYPE)) {
            bl3 = nbt.getBoolean("ShowIcon");
        }
        StatusEffectInstance statusEffectInstance = null;
        if (nbt.contains("HiddenEffect", NbtElement.COMPOUND_TYPE)) {
            statusEffectInstance = StatusEffectInstance.fromNbt(type, nbt.getCompound("HiddenEffect"));
        }
        Optional<FactorCalculationData> optional = nbt.contains("FactorCalculationData", NbtElement.COMPOUND_TYPE) ? FactorCalculationData.CODEC.parse(new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbt.getCompound("FactorCalculationData"))).resultOrPartial(LOGGER::error) : Optional.empty();
        return new StatusEffectInstance(type, j, Math.max(i, 0), bl, bl2, bl3, statusEffectInstance, optional);
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    @Override
    public int compareTo(StatusEffectInstance statusEffectInstance) {
        int i = 32147;
        if (this.getDuration() > 32147 && statusEffectInstance.getDuration() > 32147 || this.isAmbient() && statusEffectInstance.isAmbient()) {
            return ComparisonChain.start().compare(this.isAmbient(), statusEffectInstance.isAmbient()).compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor()).result();
        }
        return ComparisonChain.start().compare(this.isAmbient(), statusEffectInstance.isAmbient()).compare(this.getDuration(), statusEffectInstance.getDuration()).compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor()).result();
    }

    @Override
    public /* synthetic */ int compareTo(Object that) {
        return this.compareTo((StatusEffectInstance)that);
    }

    public static class FactorCalculationData {
        public static final Codec<FactorCalculationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("padding_duration")).forGetter(data -> data.paddingDuration), ((MapCodec)Codec.FLOAT.fieldOf("factor_start")).orElse(Float.valueOf(0.0f)).forGetter(factorCalculationData -> Float.valueOf(factorCalculationData.field_39111)), ((MapCodec)Codec.FLOAT.fieldOf("factor_target")).orElse(Float.valueOf(1.0f)).forGetter(data -> Float.valueOf(data.factorTarget)), ((MapCodec)Codec.FLOAT.fieldOf("factor_current")).orElse(Float.valueOf(0.0f)).forGetter(data -> Float.valueOf(data.factorCurrent)), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("effect_changed_timestamp")).orElse(0).forGetter(data -> data.effectChangedTimestamp), ((MapCodec)Codec.FLOAT.fieldOf("factor_previous_frame")).orElse(Float.valueOf(0.0f)).forGetter(data -> Float.valueOf(data.factorPreviousFrame)), ((MapCodec)Codec.BOOL.fieldOf("had_effect_last_tick")).orElse(false).forGetter(data -> data.hadEffectLastTick)).apply((Applicative<FactorCalculationData, ?>)instance, FactorCalculationData::new));
        private final int paddingDuration;
        private float field_39111;
        private float factorTarget;
        private float factorCurrent;
        int effectChangedTimestamp;
        private float factorPreviousFrame;
        private boolean hadEffectLastTick;

        public FactorCalculationData(int paddingDuration, float factorTarget, float f, float g, int i, float h, boolean bl) {
            this.paddingDuration = paddingDuration;
            this.field_39111 = factorTarget;
            this.factorTarget = f;
            this.factorCurrent = g;
            this.effectChangedTimestamp = i;
            this.factorPreviousFrame = h;
            this.hadEffectLastTick = bl;
        }

        public FactorCalculationData(int paddingDuration) {
            this(paddingDuration, 0.0f, 1.0f, 0.0f, 0, 0.0f, false);
        }

        public void update(StatusEffectInstance instance) {
            boolean bl;
            this.factorPreviousFrame = this.factorCurrent;
            boolean bl2 = bl = instance.duration > this.paddingDuration;
            if (this.hadEffectLastTick != bl) {
                this.hadEffectLastTick = bl;
                this.effectChangedTimestamp = instance.duration;
                this.field_39111 = this.factorCurrent;
                this.factorTarget = bl ? 1.0f : 0.0f;
            }
            float f = MathHelper.clamp(((float)this.effectChangedTimestamp - (float)instance.duration) / (float)this.paddingDuration, 0.0f, 1.0f);
            this.factorCurrent = MathHelper.lerp(f, this.field_39111, this.factorTarget);
        }

        public float lerp(LivingEntity livingEntity, float f) {
            if (livingEntity.isRemoved()) {
                this.factorPreviousFrame = this.factorCurrent;
            }
            return MathHelper.lerp(f, this.factorPreviousFrame, this.factorCurrent);
        }
    }
}


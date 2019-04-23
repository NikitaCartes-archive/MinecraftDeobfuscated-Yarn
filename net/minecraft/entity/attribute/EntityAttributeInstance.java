/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;

public interface EntityAttributeInstance {
    public EntityAttribute getAttribute();

    public double getBaseValue();

    public void setBaseValue(double var1);

    public Collection<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation var1);

    public Collection<EntityAttributeModifier> getModifiers();

    public boolean hasModifier(EntityAttributeModifier var1);

    @Nullable
    public EntityAttributeModifier getModifier(UUID var1);

    public void addModifier(EntityAttributeModifier var1);

    public void removeModifier(EntityAttributeModifier var1);

    public void removeModifier(UUID var1);

    @Environment(value=EnvType.CLIENT)
    public void clearModifiers();

    public double getValue();
}


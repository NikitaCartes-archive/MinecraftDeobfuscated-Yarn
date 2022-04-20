/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class AttributeCommand {
    private static final DynamicCommandExceptionType ENTITY_FAILED_EXCEPTION = new DynamicCommandExceptionType(name -> Text.method_43469("commands.attribute.failed.entity", name));
    private static final Dynamic2CommandExceptionType NO_ATTRIBUTE_EXCEPTION = new Dynamic2CommandExceptionType((entityName, attributeName) -> Text.method_43469("commands.attribute.failed.no_attribute", entityName, attributeName));
    private static final Dynamic3CommandExceptionType NO_MODIFIER_EXCEPTION = new Dynamic3CommandExceptionType((entityName, attributeName, uuid) -> Text.method_43469("commands.attribute.failed.no_modifier", attributeName, entityName, uuid));
    private static final Dynamic3CommandExceptionType MODIFIER_ALREADY_PRESENT_EXCEPTION = new Dynamic3CommandExceptionType((entityName, attributeName, uuid) -> Text.method_43469("commands.attribute.failed.modifier_already_present", uuid, attributeName, entityName));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("attribute").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("target", EntityArgumentType.entity()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("attribute", RegistryKeyArgumentType.registryKey(Registry.ATTRIBUTE_KEY)).then((ArgumentBuilder<ServerCommandSource, ?>)((LiteralArgumentBuilder)CommandManager.literal("get").executes(context -> AttributeCommand.executeValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), DoubleArgumentType.getDouble(context, "scale")))))).then(((LiteralArgumentBuilder)CommandManager.literal("base").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeBaseValueSet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), DoubleArgumentType.getDouble(context, "value")))))).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(context -> AttributeCommand.executeBaseValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeBaseValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), DoubleArgumentType.getDouble(context, "scale"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("modifier").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("uuid", UuidArgumentType.uuid()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.string()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("value", DoubleArgumentType.doubleArg()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("add").executes(context -> AttributeCommand.executeModifierAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), UuidArgumentType.getUuid(context, "uuid"), StringArgumentType.getString(context, "name"), DoubleArgumentType.getDouble(context, "value"), EntityAttributeModifier.Operation.ADDITION)))).then(CommandManager.literal("multiply").executes(context -> AttributeCommand.executeModifierAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), UuidArgumentType.getUuid(context, "uuid"), StringArgumentType.getString(context, "name"), DoubleArgumentType.getDouble(context, "value"), EntityAttributeModifier.Operation.MULTIPLY_TOTAL)))).then(CommandManager.literal("multiply_base").executes(context -> AttributeCommand.executeModifierAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), UuidArgumentType.getUuid(context, "uuid"), StringArgumentType.getString(context, "name"), DoubleArgumentType.getDouble(context, "value"), EntityAttributeModifier.Operation.MULTIPLY_BASE)))))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(context -> AttributeCommand.executeModifierRemove((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), UuidArgumentType.getUuid(context, "uuid")))))).then(CommandManager.literal("value").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("get").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(context -> AttributeCommand.executeModifierValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), UuidArgumentType.getUuid(context, "uuid"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeModifierValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryKeyArgumentType.getAttribute(context, "attribute"), UuidArgumentType.getUuid(context, "uuid"), DoubleArgumentType.getDouble(context, "scale")))))))))));
    }

    private static EntityAttributeInstance getAttributeInstance(Entity entity, EntityAttribute attribute) throws CommandSyntaxException {
        EntityAttributeInstance entityAttributeInstance = AttributeCommand.getLivingEntity(entity).getAttributes().getCustomInstance(attribute);
        if (entityAttributeInstance == null) {
            throw NO_ATTRIBUTE_EXCEPTION.create(entity.getName(), Text.method_43471(attribute.getTranslationKey()));
        }
        return entityAttributeInstance;
    }

    private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ENTITY_FAILED_EXCEPTION.create(entity.getName());
        }
        return (LivingEntity)entity;
    }

    private static LivingEntity getLivingEntityWithAttribute(Entity entity, EntityAttribute attribute) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntity(entity);
        if (!livingEntity.getAttributes().hasAttribute(attribute)) {
            throw NO_ATTRIBUTE_EXCEPTION.create(entity.getName(), Text.method_43471(attribute.getTranslationKey()));
        }
        return livingEntity;
    }

    private static int executeValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        double d = livingEntity.getAttributeValue(attribute);
        source.sendFeedback(Text.method_43469("commands.attribute.value.get.success", Text.method_43471(attribute.getTranslationKey()), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int executeBaseValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        double d = livingEntity.getAttributeBaseValue(attribute);
        source.sendFeedback(Text.method_43469("commands.attribute.base_value.get.success", Text.method_43471(attribute.getTranslationKey()), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int executeModifierValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        AttributeContainer attributeContainer = livingEntity.getAttributes();
        if (!attributeContainer.hasModifierForAttribute(attribute, uuid)) {
            throw NO_MODIFIER_EXCEPTION.create(target.getName(), Text.method_43471(attribute.getTranslationKey()), uuid);
        }
        double d = attributeContainer.getModifierValue(attribute, uuid);
        source.sendFeedback(Text.method_43469("commands.attribute.modifier.value.get.success", uuid, Text.method_43471(attribute.getTranslationKey()), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int executeBaseValueSet(ServerCommandSource source, Entity target, EntityAttribute attribute, double value) throws CommandSyntaxException {
        AttributeCommand.getAttributeInstance(target, attribute).setBaseValue(value);
        source.sendFeedback(Text.method_43469("commands.attribute.base_value.set.success", Text.method_43471(attribute.getTranslationKey()), target.getName(), value), false);
        return 1;
    }

    private static int executeModifierAdd(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) throws CommandSyntaxException {
        EntityAttributeModifier entityAttributeModifier;
        EntityAttributeInstance entityAttributeInstance = AttributeCommand.getAttributeInstance(target, attribute);
        if (entityAttributeInstance.hasModifier(entityAttributeModifier = new EntityAttributeModifier(uuid, name, value, operation))) {
            throw MODIFIER_ALREADY_PRESENT_EXCEPTION.create(target.getName(), Text.method_43471(attribute.getTranslationKey()), uuid);
        }
        entityAttributeInstance.addPersistentModifier(entityAttributeModifier);
        source.sendFeedback(Text.method_43469("commands.attribute.modifier.add.success", uuid, Text.method_43471(attribute.getTranslationKey()), target.getName()), false);
        return 1;
    }

    private static int executeModifierRemove(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid) throws CommandSyntaxException {
        EntityAttributeInstance entityAttributeInstance = AttributeCommand.getAttributeInstance(target, attribute);
        if (entityAttributeInstance.tryRemoveModifier(uuid)) {
            source.sendFeedback(Text.method_43469("commands.attribute.modifier.remove.success", uuid, Text.method_43471(attribute.getTranslationKey()), target.getName()), false);
            return 1;
        }
        throw NO_MODIFIER_EXCEPTION.create(target.getName(), Text.method_43471(attribute.getTranslationKey()), uuid);
    }
}


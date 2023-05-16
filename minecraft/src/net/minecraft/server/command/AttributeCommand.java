package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class AttributeCommand {
	private static final DynamicCommandExceptionType ENTITY_FAILED_EXCEPTION = new DynamicCommandExceptionType(
		name -> Text.translatable("commands.attribute.failed.entity", name)
	);
	private static final Dynamic2CommandExceptionType NO_ATTRIBUTE_EXCEPTION = new Dynamic2CommandExceptionType(
		(entityName, attributeName) -> Text.translatable("commands.attribute.failed.no_attribute", entityName, attributeName)
	);
	private static final Dynamic3CommandExceptionType NO_MODIFIER_EXCEPTION = new Dynamic3CommandExceptionType(
		(entityName, attributeName, uuid) -> Text.translatable("commands.attribute.failed.no_modifier", attributeName, entityName, uuid)
	);
	private static final Dynamic3CommandExceptionType MODIFIER_ALREADY_PRESENT_EXCEPTION = new Dynamic3CommandExceptionType(
		(entityName, attributeName, uuid) -> Text.translatable("commands.attribute.failed.modifier_already_present", uuid, attributeName, entityName)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("attribute")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.then(
							CommandManager.argument("attribute", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ATTRIBUTE))
								.then(
									CommandManager.literal("get")
										.executes(
											context -> executeValueGet(
													context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryEntryArgumentType.getEntityAttribute(context, "attribute"), 1.0
												)
										)
										.then(
											CommandManager.argument("scale", DoubleArgumentType.doubleArg())
												.executes(
													context -> executeValueGet(
															context.getSource(),
															EntityArgumentType.getEntity(context, "target"),
															RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
															DoubleArgumentType.getDouble(context, "scale")
														)
												)
										)
								)
								.then(
									CommandManager.literal("base")
										.then(
											CommandManager.literal("set")
												.then(
													CommandManager.argument("value", DoubleArgumentType.doubleArg())
														.executes(
															context -> executeBaseValueSet(
																	context.getSource(),
																	EntityArgumentType.getEntity(context, "target"),
																	RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																	DoubleArgumentType.getDouble(context, "value")
																)
														)
												)
										)
										.then(
											CommandManager.literal("get")
												.executes(
													context -> executeBaseValueGet(
															context.getSource(), EntityArgumentType.getEntity(context, "target"), RegistryEntryArgumentType.getEntityAttribute(context, "attribute"), 1.0
														)
												)
												.then(
													CommandManager.argument("scale", DoubleArgumentType.doubleArg())
														.executes(
															context -> executeBaseValueGet(
																	context.getSource(),
																	EntityArgumentType.getEntity(context, "target"),
																	RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																	DoubleArgumentType.getDouble(context, "scale")
																)
														)
												)
										)
								)
								.then(
									CommandManager.literal("modifier")
										.then(
											CommandManager.literal("add")
												.then(
													CommandManager.argument("uuid", UuidArgumentType.uuid())
														.then(
															CommandManager.argument("name", StringArgumentType.string())
																.then(
																	CommandManager.argument("value", DoubleArgumentType.doubleArg())
																		.then(
																			CommandManager.literal("add")
																				.executes(
																					context -> executeModifierAdd(
																							context.getSource(),
																							EntityArgumentType.getEntity(context, "target"),
																							RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																							UuidArgumentType.getUuid(context, "uuid"),
																							StringArgumentType.getString(context, "name"),
																							DoubleArgumentType.getDouble(context, "value"),
																							EntityAttributeModifier.Operation.ADDITION
																						)
																				)
																		)
																		.then(
																			CommandManager.literal("multiply")
																				.executes(
																					context -> executeModifierAdd(
																							context.getSource(),
																							EntityArgumentType.getEntity(context, "target"),
																							RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																							UuidArgumentType.getUuid(context, "uuid"),
																							StringArgumentType.getString(context, "name"),
																							DoubleArgumentType.getDouble(context, "value"),
																							EntityAttributeModifier.Operation.MULTIPLY_TOTAL
																						)
																				)
																		)
																		.then(
																			CommandManager.literal("multiply_base")
																				.executes(
																					context -> executeModifierAdd(
																							context.getSource(),
																							EntityArgumentType.getEntity(context, "target"),
																							RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																							UuidArgumentType.getUuid(context, "uuid"),
																							StringArgumentType.getString(context, "name"),
																							DoubleArgumentType.getDouble(context, "value"),
																							EntityAttributeModifier.Operation.MULTIPLY_BASE
																						)
																				)
																		)
																)
														)
												)
										)
										.then(
											CommandManager.literal("remove")
												.then(
													CommandManager.argument("uuid", UuidArgumentType.uuid())
														.executes(
															context -> executeModifierRemove(
																	context.getSource(),
																	EntityArgumentType.getEntity(context, "target"),
																	RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																	UuidArgumentType.getUuid(context, "uuid")
																)
														)
												)
										)
										.then(
											CommandManager.literal("value")
												.then(
													CommandManager.literal("get")
														.then(
															CommandManager.argument("uuid", UuidArgumentType.uuid())
																.executes(
																	context -> executeModifierValueGet(
																			context.getSource(),
																			EntityArgumentType.getEntity(context, "target"),
																			RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																			UuidArgumentType.getUuid(context, "uuid"),
																			1.0
																		)
																)
																.then(
																	CommandManager.argument("scale", DoubleArgumentType.doubleArg())
																		.executes(
																			context -> executeModifierValueGet(
																					context.getSource(),
																					EntityArgumentType.getEntity(context, "target"),
																					RegistryEntryArgumentType.getEntityAttribute(context, "attribute"),
																					UuidArgumentType.getUuid(context, "uuid"),
																					DoubleArgumentType.getDouble(context, "scale")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static EntityAttributeInstance getAttributeInstance(Entity entity, RegistryEntry<EntityAttribute> attribute) throws CommandSyntaxException {
		EntityAttributeInstance entityAttributeInstance = getLivingEntity(entity).getAttributes().getCustomInstance(attribute);
		if (entityAttributeInstance == null) {
			throw NO_ATTRIBUTE_EXCEPTION.create(entity.getName(), getName(attribute));
		} else {
			return entityAttributeInstance;
		}
	}

	private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity)) {
			throw ENTITY_FAILED_EXCEPTION.create(entity.getName());
		} else {
			return (LivingEntity)entity;
		}
	}

	private static LivingEntity getLivingEntityWithAttribute(Entity entity, RegistryEntry<EntityAttribute> attribute) throws CommandSyntaxException {
		LivingEntity livingEntity = getLivingEntity(entity);
		if (!livingEntity.getAttributes().hasAttribute(attribute)) {
			throw NO_ATTRIBUTE_EXCEPTION.create(entity.getName(), getName(attribute));
		} else {
			return livingEntity;
		}
	}

	private static int executeValueGet(ServerCommandSource source, Entity target, RegistryEntry<EntityAttribute> attribute, double multiplier) throws CommandSyntaxException {
		LivingEntity livingEntity = getLivingEntityWithAttribute(target, attribute);
		double d = livingEntity.getAttributeValue(attribute);
		source.sendFeedback(() -> Text.translatable("commands.attribute.value.get.success", getName(attribute), target.getName(), d), false);
		return (int)(d * multiplier);
	}

	private static int executeBaseValueGet(ServerCommandSource source, Entity target, RegistryEntry<EntityAttribute> attribute, double multiplier) throws CommandSyntaxException {
		LivingEntity livingEntity = getLivingEntityWithAttribute(target, attribute);
		double d = livingEntity.getAttributeBaseValue(attribute);
		source.sendFeedback(() -> Text.translatable("commands.attribute.base_value.get.success", getName(attribute), target.getName(), d), false);
		return (int)(d * multiplier);
	}

	private static int executeModifierValueGet(ServerCommandSource source, Entity target, RegistryEntry<EntityAttribute> attribute, UUID uuid, double multiplier) throws CommandSyntaxException {
		LivingEntity livingEntity = getLivingEntityWithAttribute(target, attribute);
		AttributeContainer attributeContainer = livingEntity.getAttributes();
		if (!attributeContainer.hasModifierForAttribute(attribute, uuid)) {
			throw NO_MODIFIER_EXCEPTION.create(target.getName(), getName(attribute), uuid);
		} else {
			double d = attributeContainer.getModifierValue(attribute, uuid);
			source.sendFeedback(() -> Text.translatable("commands.attribute.modifier.value.get.success", uuid, getName(attribute), target.getName(), d), false);
			return (int)(d * multiplier);
		}
	}

	private static int executeBaseValueSet(ServerCommandSource source, Entity target, RegistryEntry<EntityAttribute> attribute, double value) throws CommandSyntaxException {
		getAttributeInstance(target, attribute).setBaseValue(value);
		source.sendFeedback(() -> Text.translatable("commands.attribute.base_value.set.success", getName(attribute), target.getName(), value), false);
		return 1;
	}

	private static int executeModifierAdd(
		ServerCommandSource source,
		Entity target,
		RegistryEntry<EntityAttribute> attribute,
		UUID uuid,
		String name,
		double value,
		EntityAttributeModifier.Operation operation
	) throws CommandSyntaxException {
		EntityAttributeInstance entityAttributeInstance = getAttributeInstance(target, attribute);
		EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(uuid, name, value, operation);
		if (entityAttributeInstance.hasModifier(entityAttributeModifier)) {
			throw MODIFIER_ALREADY_PRESENT_EXCEPTION.create(target.getName(), getName(attribute), uuid);
		} else {
			entityAttributeInstance.addPersistentModifier(entityAttributeModifier);
			source.sendFeedback(() -> Text.translatable("commands.attribute.modifier.add.success", uuid, getName(attribute), target.getName()), false);
			return 1;
		}
	}

	private static int executeModifierRemove(ServerCommandSource source, Entity target, RegistryEntry<EntityAttribute> attribute, UUID uuid) throws CommandSyntaxException {
		EntityAttributeInstance entityAttributeInstance = getAttributeInstance(target, attribute);
		if (entityAttributeInstance.tryRemoveModifier(uuid)) {
			source.sendFeedback(() -> Text.translatable("commands.attribute.modifier.remove.success", uuid, getName(attribute), target.getName()), false);
			return 1;
		} else {
			throw NO_MODIFIER_EXCEPTION.create(target.getName(), getName(attribute), uuid);
		}
	}

	private static Text getName(RegistryEntry<EntityAttribute> attribute) {
		return Text.translatable(attribute.value().getTranslationKey());
	}
}

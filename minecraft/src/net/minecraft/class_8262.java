package net.minecraft;

import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class class_8262 {
	private static final Logger field_43433 = LogUtils.getLogger();
	private static final SuggestionProvider<ServerCommandSource> field_43434 = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource().getServer().method_51115().method_50569().map(UUID::toString), suggestionsBuilder
		);

	public static void method_50063(CommandDispatcher<ServerCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
		commandDispatcher.register(
			CommandManager.literal("vote")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("pending")
						.then(
							CommandManager.literal("start")
								.executes(commandContext -> method_50071(commandContext.getSource(), Optional.empty()))
								.then(
									CommandManager.argument("rule", RegistryEntryArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.RULES))
										.executes(
											commandContext -> method_50071(
													commandContext.getSource(), Optional.of(RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES))
												)
										)
								)
						)
						.then(CommandManager.literal("repeal").executes(commandContext -> method_50066(commandContext.getSource())))
						.then(
							CommandManager.literal("finish")
								.then(CommandManager.literal("*").executes(commandContext -> method_50075(commandContext.getSource(), true)))
								.then(
									CommandManager.argument("id", UuidArgumentType.uuid())
										.suggests(field_43434)
										.executes(commandContext -> method_50074(commandContext.getSource(), UuidArgumentType.getUuid(commandContext, "id"), true))
								)
						)
						.then(
							CommandManager.literal("discard")
								.then(CommandManager.literal("*").executes(commandContext -> method_50075(commandContext.getSource(), false)))
								.then(
									CommandManager.argument("id", UuidArgumentType.uuid())
										.suggests(field_43434)
										.executes(commandContext -> method_50074(commandContext.getSource(), UuidArgumentType.getUuid(commandContext, "id"), false))
								)
						)
						.then(
							CommandManager.literal("vote")
								.then(
									CommandManager.argument("id", UuidArgumentType.uuid())
										.suggests(field_43434)
										.then(
											CommandManager.argument("option", IntegerArgumentType.integer(0))
												.executes(
													commandContext -> method_50072(
															commandContext.getSource(), UuidArgumentType.getUuid(commandContext, "id"), IntegerArgumentType.getInteger(commandContext, "option"), 1
														)
												)
												.then(
													CommandManager.argument("count", IntegerArgumentType.integer())
														.executes(
															commandContext -> method_50072(
																	commandContext.getSource(),
																	UuidArgumentType.getUuid(commandContext, "id"),
																	IntegerArgumentType.getInteger(commandContext, "option"),
																	IntegerArgumentType.getInteger(commandContext, "count")
																)
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("rule")
						.then(
							CommandManager.argument("rule", RegistryEntryArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.RULES))
								.then(
									CommandManager.literal("approve")
										.executes(
											commandContext -> method_50070(
													commandContext.getSource(),
													RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES),
													class_8290.APPROVE,
													new NbtCompound()
												)
										)
										.then(
											CommandManager.literal("?")
												.executes(
													commandContext -> method_50068(
															commandContext.getSource(), class_8290.APPROVE, RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES)
														)
												)
										)
										.then(
											CommandManager.argument("value", NbtElementArgumentType.nbtElement())
												.executes(
													commandContext -> method_50070(
															commandContext.getSource(),
															RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES),
															class_8290.APPROVE,
															NbtElementArgumentType.getNbtElement(commandContext, "value")
														)
												)
										)
								)
								.then(
									CommandManager.literal("repeal")
										.executes(
											commandContext -> method_50070(
													commandContext.getSource(),
													RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES),
													class_8290.REPEAL,
													new NbtCompound()
												)
										)
										.then(
											CommandManager.literal("?")
												.executes(
													commandContext -> method_50068(
															commandContext.getSource(), class_8290.REPEAL, RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES)
														)
												)
										)
										.then(
											CommandManager.literal("*")
												.executes(
													commandContext -> method_50069(commandContext.getSource(), RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES))
												)
										)
										.then(
											CommandManager.argument("value", NbtElementArgumentType.nbtElement())
												.executes(
													commandContext -> method_50070(
															commandContext.getSource(),
															RegistryEntryArgumentType.getRegistryEntry(commandContext, "rule", RegistryKeys.RULES),
															class_8290.REPEAL,
															NbtElementArgumentType.getNbtElement(commandContext, "value")
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("?")
								.then(CommandManager.literal("approve").executes(commandContext -> method_50067(commandContext.getSource(), class_8290.APPROVE)))
								.then(CommandManager.literal("repeal").executes(commandContext -> method_50067(commandContext.getSource(), class_8290.REPEAL)))
						)
						.then(CommandManager.literal("*").then(CommandManager.literal("repeal").executes(commandContext -> method_50089(commandContext.getSource()))))
				)
				.then(
					CommandManager.literal("dump_all")
						.executes(commandContext -> method_50091(commandContext.getSource(), false))
						.then(CommandManager.literal("short").executes(commandContext -> method_50091(commandContext.getSource(), true)))
						.then(CommandManager.literal("long").executes(commandContext -> method_50091(commandContext.getSource(), false)))
				)
				.then(CommandManager.literal("io").then(CommandManager.literal("flush").executes(commandContext -> {
					commandContext.getSource().getServer().method_51120();
					field_43433.info("ThreadedAnvilChunkStorage: Hey, how are you?");
					commandContext.getSource().sendFeedback(Text.literal("Flushed votes"), true);
					return 1;
				})).then(CommandManager.literal("reload").executes(commandContext -> {
					commandContext.getSource().getServer().method_51121();
					commandContext.getSource().sendFeedback(Text.literal("Reloaded votes"), true);
					return 1;
				})))
		);
	}

	private static Text method_50081(UUID uUID, class_8376 arg) {
		String string = (String)class_8376.field_43995.encodeStart(JsonOps.INSTANCE, arg).get().left().map(JsonElement::toString).orElse("Error! Oh Error!");
		return Text.literal(uUID.toString())
			.styled(style -> style.withUnderline(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(string))));
	}

	private static Integer method_50073(ServerCommandSource serverCommandSource, UUID uUID, MinecraftServer minecraftServer, Optional<class_8376> optional) {
		return (Integer)optional.map(arg -> {
			minecraftServer.method_51112(uUID, arg);
			serverCommandSource.sendFeedback(Text.literal("Started vote for ").append(method_50081(uUID, arg)), true);
			return 1;
		}).orElseGet(() -> {
			serverCommandSource.sendError(Text.literal("Failed to start vote, maybe retry?"));
			return 0;
		});
	}

	private static int method_50071(ServerCommandSource serverCommandSource, Optional<RegistryEntry.Reference<class_8289>> optional) {
		Random random = serverCommandSource.getWorld().random;
		UUID uUID = UUID.randomUUID();
		MinecraftServer minecraftServer = serverCommandSource.getServer();
		Set<class_8289> set = minecraftServer.method_51115().method_50570();
		class_8376.class_8379 lv = class_8376.class_8379.method_50547(random);
		Optional<class_8376> optional2 = (Optional<class_8376>)optional.map(
				reference -> class_8376.method_50529(uUID, minecraftServer, lv, (class_8289)reference.value())
			)
			.orElseGet(() -> class_8376.method_50528(uUID, set, minecraftServer, lv));
		return method_50073(serverCommandSource, uUID, minecraftServer, optional2);
	}

	private static int method_50066(ServerCommandSource serverCommandSource) {
		Random random = serverCommandSource.getWorld().random;
		UUID uUID = UUID.randomUUID();
		MinecraftServer minecraftServer = serverCommandSource.getServer();
		Set<class_8289> set = minecraftServer.method_51115().method_50570();
		class_8376.class_8379 lv = class_8376.class_8379.method_50547(random);
		Optional<class_8376> optional = class_8376.method_50537(uUID, set, minecraftServer, lv);
		return method_50073(serverCommandSource, uUID, minecraftServer, optional);
	}

	private static int method_50075(ServerCommandSource serverCommandSource, boolean bl) {
		List<UUID> list = serverCommandSource.getServer().method_51115().method_50569().toList();
		int i = 0;

		for (UUID uUID : list) {
			i += method_50074(serverCommandSource, uUID, bl);
		}

		return i;
	}

	private static int method_50074(ServerCommandSource serverCommandSource, UUID uUID, boolean bl) {
		class_8370 lv = serverCommandSource.getServer().method_51113(uUID, bl);
		if (lv != null) {
			serverCommandSource.sendFeedback(Text.literal(bl ? "Finished vote for " : "Rejected vote for ").append(method_50081(uUID, lv.vote())), true);
			return 1;
		} else {
			serverCommandSource.sendError(Text.literal("Failed to finish vote ").append(String.valueOf(uUID)));
			return 0;
		}
	}

	private static int method_50072(ServerCommandSource serverCommandSource, UUID uUID, int i, int j) throws CommandSyntaxException {
		Entity entity = serverCommandSource.getEntityOrThrow();
		if (serverCommandSource.getServer().method_51110(new class_8373(uUID, i), entity, j)) {
			serverCommandSource.sendFeedback(Text.translatable("Added %s votes from %s to option %s of vote %s", j, entity.getDisplayName(), i, uUID), true);
			return 1;
		} else {
			serverCommandSource.sendError(Text.literal("Failed to add votes to ").append(String.valueOf(uUID)));
			return 0;
		}
	}

	private static int method_50067(ServerCommandSource serverCommandSource, class_8290 arg) {
		RegistryEntry.Reference<class_8289> reference = class_8293.method_50208(serverCommandSource.getWorld().getRandom());
		return method_50068(serverCommandSource, arg, reference);
	}

	private static int method_50068(ServerCommandSource serverCommandSource, class_8290 arg, RegistryEntry.Reference<class_8289> reference) {
		return (Integer)(switch (arg) {
			case APPROVE -> reference.value().method_50118(serverCommandSource.getServer(), serverCommandSource.getWorld().getRandom(), 1);
			case REPEAL -> Util.getRandomOrEmpty(reference.value().method_50204().toList(), serverCommandSource.getWorld().getRandom()).stream();
		}).findAny().map(arg2 -> method_50062(arg2, arg, serverCommandSource)).orElseGet(() -> {
			serverCommandSource.sendError(Text.literal("Failed to find applicable rule in ").append(reference.registryKey().getValue().toString()));
			return 0;
		});
	}

	private static int method_50070(ServerCommandSource serverCommandSource, RegistryEntry.Reference<class_8289> reference, class_8290 arg, NbtElement nbtElement) {
		return reference.value()
			.method_50120()
			.parse(new Dynamic<>(NbtOps.INSTANCE, nbtElement))
			.get()
			.<Integer>map(arg2 -> method_50062(arg2, arg, serverCommandSource), partialResult -> {
				field_43433.warn("Failed to decode {}/{}: {}", reference.registryKey().getValue(), nbtElement, partialResult.message());
				serverCommandSource.sendError(Text.literal("Failed to decode ").append(reference.registryKey().getValue().toString()));
				return 0;
			});
	}

	private static int method_50062(class_8291 arg, class_8290 arg2, ServerCommandSource serverCommandSource) {
		Text text = arg.method_50130(arg2);
		arg.method_50164(arg2, serverCommandSource.getServer());
		serverCommandSource.sendFeedback(Text.literal("Applied ").append(text), true);
		return 1;
	}

	private static int method_50089(ServerCommandSource serverCommandSource) {
		int i = serverCommandSource.getRegistryManager().get(RegistryKeys.RULES).stream().mapToInt(arg -> arg.method_50203(false)).sum();
		serverCommandSource.sendFeedback(Text.literal("Repealed " + i + " changes from all rules"), true);
		return i;
	}

	private static int method_50069(ServerCommandSource serverCommandSource, RegistryEntry.Reference<class_8289> reference) {
		int i = reference.value().method_50203(false);
		serverCommandSource.sendFeedback(Text.literal("Repealed " + i + " changes for " + reference.registryKey().getValue()), true);
		return i;
	}

	private static void method_50078(PrintStream printStream, class_8291 arg) {
		class_8291.field_43504.encodeStart(JsonOps.INSTANCE, arg).resultOrPartial(field_43433::error).ifPresent(jsonElement -> {
			printStream.println("\t\t" + jsonElement);
			Text text = arg.method_50130(class_8290.APPROVE);
			printStream.println("\t\t\t Approve: " + text.getString());
			Text text2 = arg.method_50130(class_8290.REPEAL);
			printStream.println("\t\t\t Repeal: " + text2.getString());
		});
	}

	private static void method_50084(MinecraftServer minecraftServer, PrintStream printStream, Random random, RegistryEntry.Reference<class_8289> reference) {
		printStream.println(reference.registryKey().getValue());
		List<class_8291> list = reference.value().method_50119().toList();
		if (!list.isEmpty()) {
			printStream.println("\tApproved:");

			for (class_8291 lv : list) {
				method_50078(printStream, lv);
			}
		}

		list = reference.value().method_50118(minecraftServer, random, 5).toList();
		if (!list.isEmpty()) {
			printStream.println("\tExample proposals:");

			for (class_8291 lv : list) {
				method_50078(printStream, lv);
			}
		}
	}

	private static int method_50091(ServerCommandSource serverCommandSource, boolean bl) {
		MinecraftServer minecraftServer = serverCommandSource.getServer();
		Registry<class_8289> registry = serverCommandSource.getRegistryManager().get(RegistryKeys.RULES);
		PrintStream printStream = Bootstrap.SYSOUT;
		Random random = Random.create();
		registry.streamEntries()
			.sorted(Comparator.comparing(reference -> reference.registryKey().getValue()))
			.forEach(
				reference -> {
					if (!bl) {
						method_50084(minecraftServer, printStream, random, reference);
					} else {
						String string = (String)((class_8289)reference.value())
							.method_50118(minecraftServer, random, 3)
							.map(arg -> "\"" + arg.method_50130(class_8290.APPROVE).getString() + "\"")
							.collect(Collectors.joining(", "));
						printStream.println(reference.registryKey().getValue() + ": " + string);
					}
				}
			);
		serverCommandSource.sendFeedback(Text.literal("Whew! That was scary!"), false);
		return 1;
	}
}

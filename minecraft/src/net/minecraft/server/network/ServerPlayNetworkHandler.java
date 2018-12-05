package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_2774;
import net.minecraft.class_2793;
import net.minecraft.class_2795;
import net.minecraft.class_2799;
import net.minecraft.class_2805;
import net.minecraft.class_2809;
import net.minecraft.class_2813;
import net.minecraft.class_2822;
import net.minecraft.class_2827;
import net.minecraft.class_2828;
import net.minecraft.class_2833;
import net.minecraft.class_2836;
import net.minecraft.class_2838;
import net.minecraft.class_2842;
import net.minecraft.class_2848;
import net.minecraft.class_2851;
import net.minecraft.class_2853;
import net.minecraft.class_2855;
import net.minecraft.class_2856;
import net.minecraft.class_2859;
import net.minecraft.class_2863;
import net.minecraft.class_2866;
import net.minecraft.class_2868;
import net.minecraft.class_2870;
import net.minecraft.class_2871;
import net.minecraft.class_2873;
import net.minecraft.class_2875;
import net.minecraft.class_2877;
import net.minecraft.class_2879;
import net.minecraft.class_2884;
import net.minecraft.class_2955;
import net.minecraft.class_2958;
import net.minecraft.class_3753;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.CommandSuggestionsClientPacket;
import net.minecraft.client.network.packet.DisconnectClientPacket;
import net.minecraft.client.network.packet.GuiActionConfirmClientPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.client.network.packet.HeldItemChangeClientPacket;
import net.minecraft.client.network.packet.KeepAliveClientPacket;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.client.network.packet.VehicleMoveClientPacket;
import net.minecraft.client.sortme.ChatMessageType;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.VillagerContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.packet.BookUpdateServerPacket;
import net.minecraft.server.network.packet.ChatMessageServerPacket;
import net.minecraft.server.network.packet.ClientSettingsServerPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.server.network.packet.GuiCloseServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityServerPacket;
import net.minecraft.server.network.packet.PlayerInteractItemServerPacket;
import net.minecraft.server.network.packet.RecipeClickServerPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener, Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection client;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int field_14118;
	private long field_14136;
	private boolean field_14125;
	private long field_14134;
	private int field_14116;
	private int field_14133;
	private final IntHashMap<Short> field_14132 = new IntHashMap<>();
	private double field_14130;
	private double field_14146;
	private double field_14128;
	private double field_14145;
	private double field_14126;
	private double field_14144;
	private Entity field_14147;
	private double field_14143;
	private double field_14124;
	private double field_14142;
	private double field_14122;
	private double field_14141;
	private double field_14120;
	private Vec3d field_14119;
	private int field_14123;
	private int field_14139;
	private boolean field_14131;
	private int field_14138;
	private boolean field_14129;
	private int field_14137;
	private int field_14117;
	private int field_14135;

	public ServerPlayNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.client = clientConnection;
		clientConnection.setPacketListener(this);
		this.player = serverPlayerEntity;
		serverPlayerEntity.networkHandler = this;
	}

	@Override
	public void tick() {
		this.method_14372();
		this.player.method_14226();
		this.player.setPositionAnglesAndUpdate(this.field_14130, this.field_14146, this.field_14128, this.player.yaw, this.player.pitch);
		this.field_14118++;
		this.field_14135 = this.field_14117;
		if (this.field_14131) {
			if (++this.field_14138 > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
				this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.field_14131 = false;
			this.field_14138 = 0;
		}

		this.field_14147 = this.player.getTopmostRiddenEntity();
		if (this.field_14147 != this.player && this.field_14147.method_5642() == this.player) {
			this.field_14143 = this.field_14147.x;
			this.field_14124 = this.field_14147.y;
			this.field_14142 = this.field_14147.z;
			this.field_14122 = this.field_14147.x;
			this.field_14141 = this.field_14147.y;
			this.field_14120 = this.field_14147.z;
			if (this.field_14129 && this.player.getTopmostRiddenEntity().method_5642() == this.player) {
				if (++this.field_14137 > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
					this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.field_14129 = false;
				this.field_14137 = 0;
			}
		} else {
			this.field_14147 = null;
			this.field_14129 = false;
			this.field_14137 = 0;
		}

		this.server.getProfiler().begin("keepAlive");
		long l = SystemUtil.getMeasuringTimeMili();
		if (l - this.field_14136 >= 15000L) {
			if (this.field_14125) {
				this.method_14367(new TranslatableTextComponent("disconnect.timeout"));
			} else {
				this.field_14125 = true;
				this.field_14136 = l;
				this.field_14134 = l;
				this.sendPacket(new KeepAliveClientPacket(this.field_14134));
			}
		}

		this.server.getProfiler().end();
		if (this.field_14116 > 0) {
			this.field_14116--;
		}

		if (this.field_14133 > 0) {
			this.field_14133--;
		}

		if (this.player.method_14219() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& SystemUtil.getMeasuringTimeMili() - this.player.method_14219() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.idling"));
		}
	}

	public void method_14372() {
		this.field_14130 = this.player.x;
		this.field_14146 = this.player.y;
		this.field_14128 = this.player.z;
		this.field_14145 = this.player.x;
		this.field_14126 = this.player.y;
		this.field_14144 = this.player.z;
	}

	public ClientConnection getConnection() {
		return this.client;
	}

	public void method_14367(TextComponent textComponent) {
		this.client.sendPacket(new DisconnectClientPacket(textComponent), future -> this.client.disconnect(textComponent));
		this.client.method_10757();
		this.server.executeFuture(this.client::handleDisconnection).join();
	}

	@Override
	public void method_12067(class_2851 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.method_14218(arg.method_12372(), arg.method_12373(), arg.method_12371(), arg.method_12370());
	}

	private static boolean method_14362(class_2828 arg) {
		return Doubles.isFinite(arg.method_12269(0.0))
				&& Doubles.isFinite(arg.method_12268(0.0))
				&& Doubles.isFinite(arg.method_12274(0.0))
				&& Floats.isFinite(arg.method_12270(0.0F))
				&& Floats.isFinite(arg.method_12271(0.0F))
			? Math.abs(arg.method_12269(0.0)) > 3.0E7 || Math.abs(arg.method_12268(0.0)) > 3.0E7 || Math.abs(arg.method_12274(0.0)) > 3.0E7
			: true;
	}

	private static boolean method_14371(class_2833 arg) {
		return !Doubles.isFinite(arg.method_12279())
			|| !Doubles.isFinite(arg.method_12280())
			|| !Doubles.isFinite(arg.method_12276())
			|| !Floats.isFinite(arg.method_12277())
			|| !Floats.isFinite(arg.method_12281());
	}

	@Override
	public void method_12078(class_2833 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (method_14371(arg)) {
			this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getTopmostRiddenEntity();
			if (entity != this.player && entity.method_5642() == this.player && entity == this.field_14147) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.x;
				double e = entity.y;
				double f = entity.z;
				double g = arg.method_12279();
				double h = arg.method_12280();
				double i = arg.method_12276();
				float j = arg.method_12281();
				float k = arg.method_12277();
				double l = g - this.field_14143;
				double m = h - this.field_14124;
				double n = i - this.field_14142;
				double o = entity.velocityX * entity.velocityX + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ;
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(entity.getName().getString()))) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
					this.client.sendPacket(new VehicleMoveClientPacket(entity));
					return;
				}

				boolean bl = serverWorld.method_8587(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.field_14122;
				m = h - this.field_14141 - 1.0E-6;
				n = i - this.field_14120;
				entity.move(MovementType.PLAYER, l, m, n);
				l = g - entity.x;
				m = h - entity.y;
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.z;
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn("{} moved wrongly!", entity.getName().getString());
				}

				entity.setPositionAnglesAndUpdate(g, h, i, j, k);
				boolean bl3 = serverWorld.method_8587(entity, entity.getBoundingBox().contract(0.0625));
				if (bl && (bl2 || !bl3)) {
					entity.setPositionAnglesAndUpdate(d, e, f, j, k);
					this.client.sendPacket(new VehicleMoveClientPacket(entity));
					return;
				}

				this.server.getConfigurationManager().method_14575(this.player);
				this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
				this.field_14129 = m >= -0.03125
					&& !this.server.isFlightEnabled()
					&& !serverWorld.isAreaNotEmpty(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
				this.field_14122 = entity.x;
				this.field_14141 = entity.y;
				this.field_14120 = entity.z;
			}
		}
	}

	@Override
	public void method_12050(class_2793 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (arg.method_12086() == this.field_14123) {
			this.player.setPositionAnglesAndUpdate(this.field_14119.x, this.field_14119.y, this.field_14119.z, this.player.yaw, this.player.pitch);
			this.field_14145 = this.field_14119.x;
			this.field_14126 = this.field_14119.y;
			this.field_14144 = this.field_14119.z;
			if (this.player.method_14208()) {
				this.player.method_14240();
			}

			this.field_14119 = null;
		}
	}

	@Override
	public void method_12047(class_2853 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (arg.method_12402() == class_2853.class_2854.field_13011) {
			Recipe recipe = this.server.getRecipeManager().get(arg.method_12406());
			if (recipe != null) {
				this.player.getRecipeBook().method_14886(recipe);
			}
		} else if (arg.method_12402() == class_2853.class_2854.field_13010) {
			this.player.getRecipeBook().setGuiOpen(arg.isGuiOpen());
			this.player.getRecipeBook().setFilteringCraftable(arg.isFilteringCraftable());
			this.player.getRecipeBook().setFurnaceGuiOpen(arg.isFurnaceGuiOpen());
			this.player.getRecipeBook().setFurnaceFilteringCraftable(arg.isFurnaceFilteringCraftable());
		}
	}

	@Override
	public void method_12058(class_2859 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (arg.method_12415() == class_2859.class_2860.field_13024) {
			Identifier identifier = arg.method_12416();
			SimpleAdvancement simpleAdvancement = this.server.getAdvancementManager().get(identifier);
			if (simpleAdvancement != null) {
				this.player.getAdvancementManager().method_12888(simpleAdvancement);
			}
		}
	}

	@Override
	public void method_12059(class_2805 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(arg.method_12148());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.client.sendPacket(new CommandSuggestionsClientPacket(arg.method_12149(), suggestions)));
	}

	@Override
	public void method_12077(class_2870 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notEnabled"));
		} else if (!this.player.method_7338()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = arg.getBlockPos();
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = arg.getCommand();
			boolean bl = arg.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				Direction direction = this.player.world.getBlockState(blockPos).get(CommandBlock.field_10791);
				switch (arg.method_12468()) {
					case CHAIN: {
						BlockState blockState = Blocks.field_10395.getDefaultState();
						this.player
							.world
							.setBlockState(blockPos, blockState.with(CommandBlock.field_10791, direction).with(CommandBlock.field_10793, Boolean.valueOf(arg.method_12471())), 2);
						break;
					}
					case REPEATING: {
						BlockState blockState = Blocks.field_10263.getDefaultState();
						this.player
							.world
							.setBlockState(blockPos, blockState.with(CommandBlock.field_10791, direction).with(CommandBlock.field_10793, Boolean.valueOf(arg.method_12471())), 2);
						break;
					}
					case NORMAL:
					default: {
						BlockState blockState = Blocks.field_10525.getDefaultState();
						this.player
							.world
							.setBlockState(blockPos, blockState.with(CommandBlock.field_10791, direction).with(CommandBlock.field_10793, Boolean.valueOf(arg.method_12471())), 2);
					}
				}

				blockEntity.validate();
				this.player.world.setBlockEntity(blockPos, blockEntity);
				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.shouldTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.setAuto(arg.method_12474());
				commandBlockExecutor.method_8295();
				if (!ChatUtil.isEmpty(string)) {
					this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void method_12049(class_2871 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notEnabled"));
		} else if (!this.player.method_7338()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = arg.method_12476(this.player.world);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(arg.method_12475());
				commandBlockExecutor.shouldTrackOutput(arg.method_12478());
				if (!arg.method_12478()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.method_8295();
				this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.setCommand.success", arg.method_12475()));
			}
		}
	}

	@Override
	public void method_12084(class_2838 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.inventory.swapSlotWithHotbar(arg.getSlot());
		this.player
			.networkHandler
			.sendPacket(new GuiSlotUpdateClientPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot)));
		this.player.networkHandler.sendPacket(new GuiSlotUpdateClientPacket(-2, arg.getSlot(), this.player.inventory.getInvStack(arg.getSlot())));
		this.player.networkHandler.sendPacket(new HeldItemChangeClientPacket(this.player.inventory.selectedSlot));
	}

	@Override
	public void method_12060(class_2855 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.container instanceof AnvilContainer) {
			AnvilContainer anvilContainer = (AnvilContainer)this.player.container;
			String string = SharedConstants.stripInvalidChars(arg.method_12407());
			if (string.length() <= 35) {
				anvilContainer.setNewItemName(string);
			}
		}
	}

	@Override
	public void method_12057(class_2866 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.container instanceof BeaconContainer) {
			BeaconContainer beaconContainer = (BeaconContainer)this.player.container;
			Slot slot = beaconContainer.getSlot(0);
			if (slot.hasStack()) {
				slot.takeStack(1);
				Inventory inventory = beaconContainer.getInventory();
				inventory.setInvProperty(1, arg.method_12436());
				inventory.setInvProperty(2, arg.method_12435());
				inventory.markDirty();
			}
		}
	}

	@Override
	public void method_12051(class_2875 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.method_7338()) {
			BlockPos blockPos = arg.method_12499();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				structureBlockBlockEntity.setMode(arg.getMode());
				structureBlockBlockEntity.setStructureName(arg.getStructureName());
				structureBlockBlockEntity.setOffset(arg.getOffset());
				structureBlockBlockEntity.setSize(arg.getSize());
				structureBlockBlockEntity.setMirror(arg.getMirror());
				structureBlockBlockEntity.setRotation(arg.getRotation());
				structureBlockBlockEntity.setMetadata(arg.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(arg.getIgnoreEntities());
				structureBlockBlockEntity.setShowAir(arg.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(arg.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(arg.getIntegrity());
				structureBlockBlockEntity.setSeed(arg.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getStructureName();
					if (arg.method_12500() == StructureBlockBlockEntity.class_2634.field_12110) {
						if (structureBlockBlockEntity.method_11365()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.save_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.save_failure", string), false);
						}
					} else if (arg.method_12500() == StructureBlockBlockEntity.class_2634.field_12109) {
						if (!structureBlockBlockEntity.method_11372()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.method_11376()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_prepare", string), false);
						}
					} else if (arg.method_12500() == StructureBlockBlockEntity.class_2634.field_12106) {
						if (structureBlockBlockEntity.method_11383()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.size_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.addChatMessage(new TranslatableTextComponent("structure_block.invalid_structure_name", arg.getStructureName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_16383(class_3753 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.method_7338()) {
			BlockPos blockPos = arg.method_16396();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof JigsawBlockEntity) {
				JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
				jigsawBlockEntity.method_16379(arg.method_16395());
				jigsawBlockEntity.method_16378(arg.method_16394());
				jigsawBlockEntity.method_16377(arg.method_16393());
				jigsawBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void method_12080(class_2863 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		int i = arg.method_12431();
		Container container = this.player.container;
		if (container instanceof VillagerContainer) {
			((VillagerContainer)container).setRecipeIndex(i);
		}
	}

	@Override
	public void onBookUpdate(BookUpdateServerPacket bookUpdateServerPacket) {
		ItemStack itemStack = bookUpdateServerPacket.stack();
		if (!itemStack.isEmpty()) {
			if (WritableBookItem.method_8047(itemStack.getTag())) {
				ItemStack itemStack2 = this.player.getStackInHand(bookUpdateServerPacket.hand());
				if (itemStack.getItem() == Items.field_8674 && itemStack2.getItem() == Items.field_8674) {
					if (bookUpdateServerPacket.method_12238()) {
						ItemStack itemStack3 = new ItemStack(Items.field_8360);
						CompoundTag compoundTag = itemStack2.getTag();
						if (compoundTag != null) {
							itemStack3.setTag(compoundTag.copy());
						}

						itemStack3.setChildTag("author", new StringTag(this.player.getName().getString()));
						itemStack3.setChildTag("title", new StringTag(itemStack.getTag().getString("title")));
						ListTag listTag = itemStack.getTag().getList("pages", 8);

						for (int i = 0; i < listTag.size(); i++) {
							String string = listTag.getString(i);
							TextComponent textComponent = new StringTextComponent(string);
							string = TextComponent.Serializer.toJsonString(textComponent);
							listTag.set(i, (Tag)(new StringTag(string)));
						}

						itemStack3.setChildTag("pages", listTag);
						this.player.setStackInHand(bookUpdateServerPacket.hand(), itemStack3);
					} else {
						itemStack2.setChildTag("pages", itemStack.getTag().getList("pages", 8));
					}
				}
			}
		}
	}

	@Override
	public void method_12074(class_2822 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(arg.method_12244());
			if (entity != null) {
				CompoundTag compoundTag = entity.toTag(new CompoundTag());
				this.player.networkHandler.sendPacket(new class_2774(arg.method_12245(), compoundTag));
			}
		}
	}

	@Override
	public void method_12072(class_2795 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().getBlockEntity(arg.method_12094());
			CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
			this.player.networkHandler.sendPacket(new class_2774(arg.method_12096(), compoundTag));
		}
	}

	@Override
	public void method_12063(class_2828 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (method_14362(arg)) {
			this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
			if (!this.player.field_13989) {
				if (this.field_14118 == 0) {
					this.method_14372();
				}

				if (this.field_14119 != null) {
					if (this.field_14118 - this.field_14139 > 20) {
						this.field_14139 = this.field_14118;
						this.method_14363(this.field_14119.x, this.field_14119.y, this.field_14119.z, this.player.yaw, this.player.pitch);
					}
				} else {
					this.field_14139 = this.field_14118;
					if (this.player.hasVehicle()) {
						this.player
							.setPositionAnglesAndUpdate(this.player.x, this.player.y, this.player.z, arg.method_12271(this.player.yaw), arg.method_12270(this.player.pitch));
						this.server.getConfigurationManager().method_14575(this.player);
					} else {
						double d = this.player.x;
						double e = this.player.y;
						double f = this.player.z;
						double g = this.player.y;
						double h = arg.method_12269(this.player.x);
						double i = arg.method_12268(this.player.y);
						double j = arg.method_12274(this.player.z);
						float k = arg.method_12271(this.player.yaw);
						float l = arg.method_12270(this.player.pitch);
						double m = h - this.field_14130;
						double n = i - this.field_14146;
						double o = j - this.field_14128;
						double p = this.player.velocityX * this.player.velocityX + this.player.velocityY * this.player.velocityY + this.player.velocityZ * this.player.velocityZ;
						double q = m * m + n * n + o * o;
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.method_14363(this.player.x, this.player.y, this.player.z, arg.method_12271(this.player.yaw), arg.method_12270(this.player.pitch));
							}
						} else {
							this.field_14117++;
							int r = this.field_14117 - this.field_14135;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
								r = 1;
							}

							if (!this.player.method_14208()
								&& (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.isFallFlying())) {
								float s = this.player.isFallFlying() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(this.player.getGameProfile().getName()))) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), m, n, o);
									this.method_14363(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
									return;
								}
							}

							boolean bl = serverWorld.method_8587(this.player, this.player.getBoundingBox().contract(0.0625));
							m = h - this.field_14145;
							n = i - this.field_14126;
							o = j - this.field_14144;
							if (this.player.onGround && !arg.method_12273() && n > 0.0) {
								this.player.method_6043();
							}

							this.player.move(MovementType.PLAYER, m, n, o);
							this.player.onGround = arg.method_12273();
							m = h - this.player.x;
							n = i - this.player.y;
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = j - this.player.z;
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.player.method_14208()
								&& q > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.interactionManager.isCreative()
								&& this.player.interactionManager.getGameMode() != GameMode.field_9219) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
							}

							this.player.setPositionAnglesAndUpdate(h, i, j, k, l);
							this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
							if (!this.player.noClip && !this.player.isSleeping()) {
								boolean bl3 = serverWorld.method_8587(this.player, this.player.getBoundingBox().contract(0.0625));
								if (bl && (bl2 || !bl3)) {
									this.method_14363(d, e, f, k, l);
									return;
								}
							}

							this.field_14131 = n >= -0.03125;
							this.field_14131 = this.field_14131 & (!this.server.isFlightEnabled() && !this.player.abilities.allowFlying);
							this.field_14131 = this.field_14131
								& (
									!this.player.hasPotionEffect(StatusEffects.field_5902)
										&& !this.player.isFallFlying()
										&& !serverWorld.isAreaNotEmpty(this.player.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0))
								);
							this.player.onGround = arg.method_12273();
							this.server.getConfigurationManager().method_14575(this.player);
							this.player.method_14207(this.player.y - g, arg.method_12273());
							this.field_14145 = this.player.x;
							this.field_14126 = this.player.y;
							this.field_14144 = this.player.z;
						}
					}
				}
			}
		}
	}

	public void method_14363(double d, double e, double f, float g, float h) {
		this.method_14360(d, e, f, g, h, Collections.emptySet());
	}

	public void method_14360(double d, double e, double f, float g, float h, Set<PlayerPositionLookClientPacket.Flag> set) {
		double i = set.contains(PlayerPositionLookClientPacket.Flag.X) ? this.player.x : 0.0;
		double j = set.contains(PlayerPositionLookClientPacket.Flag.Y) ? this.player.y : 0.0;
		double k = set.contains(PlayerPositionLookClientPacket.Flag.Z) ? this.player.z : 0.0;
		float l = set.contains(PlayerPositionLookClientPacket.Flag.Y_ROT) ? this.player.yaw : 0.0F;
		float m = set.contains(PlayerPositionLookClientPacket.Flag.X_ROT) ? this.player.pitch : 0.0F;
		this.field_14119 = new Vec3d(d, e, f);
		if (++this.field_14123 == Integer.MAX_VALUE) {
			this.field_14123 = 0;
		}

		this.field_14139 = this.field_14118;
		this.player.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.player.networkHandler.sendPacket(new PlayerPositionLookClientPacket(d - i, e - j, f - k, g - l, h - m, set, this.field_14123));
	}

	@Override
	public void onPlayerAction(PlayerActionServerPacket playerActionServerPacket) {
		NetworkThreadUtils.forceMainThread(playerActionServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = playerActionServerPacket.getPos();
		this.player.method_14234();
		switch (playerActionServerPacket.getAction()) {
			case field_12969:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.OFF);
					this.player.setStackInHand(Hand.OFF, this.player.getStackInHand(Hand.MAIN));
					this.player.setStackInHand(Hand.MAIN, itemStack);
				}

				return;
			case field_12975:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case field_12970:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case field_12974:
				this.player.method_6075();
				return;
			case field_12968:
			case field_12971:
			case field_12973:
				double d = this.player.x - ((double)blockPos.getX() + 0.5);
				double e = this.player.y - ((double)blockPos.getY() + 0.5) + 1.5;
				double f = this.player.z - ((double)blockPos.getZ() + 0.5);
				double g = d * d + e * e + f * f;
				if (g > 36.0) {
					return;
				} else if (blockPos.getY() >= this.server.getWorldHeight()) {
					return;
				} else {
					if (playerActionServerPacket.getAction() == PlayerActionServerPacket.class_2847.field_12968) {
						if (!this.server.isSpawnProtected(serverWorld, blockPos, this.player) && serverWorld.getWorldBorder().contains(blockPos)) {
							this.player.interactionManager.method_14263(blockPos, playerActionServerPacket.method_12360());
						} else {
							this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos));
						}
					} else {
						if (playerActionServerPacket.getAction() == PlayerActionServerPacket.class_2847.field_12973) {
							this.player.interactionManager.method_14258(blockPos);
						} else if (playerActionServerPacket.getAction() == PlayerActionServerPacket.class_2847.field_12971) {
							this.player.interactionManager.method_14269();
						}

						if (!serverWorld.getBlockState(blockPos).isAir()) {
							this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos));
						}
					}

					return;
				}
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockServerPacket playerInteractBlockServerPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractBlockServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = playerInteractBlockServerPacket.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockPos blockPos = playerInteractBlockServerPacket.getPos();
		Direction direction = playerInteractBlockServerPacket.getFacing();
		this.player.method_14234();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.field_14119 == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& !this.server.isSpawnProtected(serverWorld, blockPos, this.player)
				&& serverWorld.getWorldBorder().contains(blockPos)) {
				this.player
					.interactionManager
					.interactBlock(
						this.player,
						serverWorld,
						itemStack,
						hand,
						blockPos,
						direction,
						playerInteractBlockServerPacket.getHitX(),
						playerInteractBlockServerPacket.getHitY(),
						playerInteractBlockServerPacket.getHitZ()
					);
			}
		} else {
			TextComponent textComponent = new TranslatableTextComponent("build.tooHigh", this.server.getWorldHeight()).applyFormat(TextFormat.RED);
			this.player.networkHandler.sendPacket(new ChatMessageClientPacket(textComponent, ChatMessageType.field_11733));
		}

		this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos.method_10093(direction)));
	}

	@Override
	public void onPlayerInteractItem(PlayerInteractItemServerPacket playerInteractItemServerPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractItemServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = playerInteractItemServerPacket.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.method_14234();
		if (!itemStack.isEmpty()) {
			this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
		}
	}

	@Override
	public void method_12073(class_2884 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			Entity entity = null;

			for (ServerWorld serverWorld : this.server.getWorlds()) {
				entity = arg.method_12541(serverWorld);
				if (entity != null) {
					break;
				}
			}

			if (entity != null) {
				this.player.method_14251((ServerWorld)entity.world, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
			}
		}
	}

	@Override
	public void method_12081(class_2856 arg) {
	}

	@Override
	public void method_12064(class_2836 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		Entity entity = this.player.getRiddenEntity();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).method_7538(arg.method_12284(), arg.method_12285());
		}
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), textComponent.getString());
		this.server.method_3856();
		this.server
			.getConfigurationManager()
			.sendToAll(new TranslatableTextComponent("multiplayer.player.left", this.player.getDisplayName()).applyFormat(TextFormat.YELLOW));
		this.player.method_14231();
		this.server.getConfigurationManager().method_14611(this.player);
		if (this.server.isSinglePlayer() && this.player.getName().getString().equals(this.server.getUserName())) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (packet instanceof ChatMessageClientPacket) {
			ChatMessageClientPacket chatMessageClientPacket = (ChatMessageClientPacket)packet;
			PlayerEntity.ChatVisibility chatVisibility = this.player.getClientChatVisibility();
			if (chatVisibility == PlayerEntity.ChatVisibility.HIDDEN && chatMessageClientPacket.getLocation() != ChatMessageType.field_11733) {
				return;
			}

			if (chatVisibility == PlayerEntity.ChatVisibility.COMMANDS && !chatMessageClientPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.client.sendPacket(packet, genericFutureListener);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Sending packet");
			CrashReportElement crashReportElement = crashReport.addElement("Packet being sent");
			crashReportElement.add("Packet class", (ICrashCallable<String>)(() -> packet.getClass().getCanonicalName()));
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void method_12056(class_2868 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (arg.method_12442() >= 0 && arg.method_12442() < PlayerInventory.getHotbarSize()) {
			this.player.inventory.selectedSlot = arg.method_12442();
			this.player.method_14234();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
		}
	}

	@Override
	public void onChatMessage(ChatMessageServerPacket chatMessageServerPacket) {
		NetworkThreadUtils.forceMainThread(chatMessageServerPacket, this, this.player.getServerWorld());
		if (this.player.getClientChatVisibility() == PlayerEntity.ChatVisibility.HIDDEN) {
			this.sendPacket(new ChatMessageClientPacket(new TranslatableTextComponent("chat.cannotSend").applyFormat(TextFormat.RED)));
		} else {
			this.player.method_14234();
			String string = chatMessageServerPacket.method_12114();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.illegal_characters"));
					return;
				}
			}

			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				TextComponent textComponent = new TranslatableTextComponent("chat.type.text", this.player.getDisplayName(), string);
				this.server.getConfigurationManager().broadcastChatMessage(textComponent, false);
			}

			this.field_14116 += 20;
			if (this.field_14116 > 200 && !this.server.getConfigurationManager().isOperator(this.player.getGameProfile())) {
				this.method_14367(new TranslatableTextComponent("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String string) {
		this.server.getCommandManager().execute(this.player.getCommandSource(), string);
	}

	@Override
	public void method_12052(class_2879 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.method_14234();
		this.player.swingHand(arg.method_12512());
	}

	@Override
	public void method_12045(class_2848 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.method_14234();
		switch (arg.method_12365()) {
			case field_12979:
				this.player.setSneaking(true);
				break;
			case field_12984:
				this.player.setSneaking(false);
				break;
			case field_12981:
				this.player.setSprinting(true);
				break;
			case field_12985:
				this.player.setSprinting(false);
				break;
			case field_12986:
				if (this.player.isSleeping()) {
					this.player.method_7358(false, true, true);
					this.field_14119 = new Vec3d(this.player.x, this.player.y, this.player.z);
				}
				break;
			case field_12987:
				if (this.player.getRiddenEntity() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getRiddenEntity();
					int i = arg.method_12366();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case field_12980:
				if (this.player.getRiddenEntity() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getRiddenEntity();
					jumpingMount.stopJumping();
				}
				break;
			case field_12988:
				if (this.player.getRiddenEntity() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getRiddenEntity()).method_6722(this.player);
				}
				break;
			case field_12982:
				if (!this.player.onGround && this.player.velocityY < 0.0 && !this.player.isFallFlying() && !this.player.isInsideWater()) {
					ItemStack itemStack = this.player.getEquippedStack(EquipmentSlot.CHEST);
					if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack)) {
						this.player.method_14243();
					}
				} else {
					this.player.method_14229();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityServerPacket playerInteractEntityServerPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractEntityServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Entity entity = playerInteractEntityServerPacket.getEntity(serverWorld);
		this.player.method_14234();
		if (entity != null) {
			boolean bl = this.player.canSee(entity);
			double d = 36.0;
			if (!bl) {
				d = 9.0;
			}

			if (this.player.squaredDistanceTo(entity) < d) {
				if (playerInteractEntityServerPacket.getType() == PlayerInteractEntityServerPacket.InteractionType.field_12876) {
					Hand hand = playerInteractEntityServerPacket.getHand();
					this.player.interact(entity, hand);
				} else if (playerInteractEntityServerPacket.getType() == PlayerInteractEntityServerPacket.InteractionType.field_12873) {
					Hand hand = playerInteractEntityServerPacket.getHand();
					entity.interactAt(this.player, playerInteractEntityServerPacket.getHitPosition(), hand);
				} else if (playerInteractEntityServerPacket.getType() == PlayerInteractEntityServerPacket.InteractionType.field_12875) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ProjectileEntity || entity == this.player) {
						this.method_14367(new TranslatableTextComponent("multiplayer.disconnect.invalid_entity_attacked"));
						this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
						return;
					}

					this.player.attack(entity);
				}
			}
		}
	}

	@Override
	public void method_12068(class_2799 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.method_14234();
		class_2799.class_2800 lv = arg.method_12119();
		switch (lv) {
			case field_12774:
				if (this.player.field_13989) {
					this.player.field_13989 = false;
					this.player = this.server.getConfigurationManager().method_14556(this.player, DimensionType.field_13072, true);
					CriterionCriterions.CHANGED_DIMENSION.handle(this.player, DimensionType.field_13078, DimensionType.field_13072);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getConfigurationManager().method_14556(this.player, DimensionType.field_13072, false);
					if (this.server.isHardcore()) {
						this.player.setGameMode(GameMode.field_9219);
						this.player.getServerWorld().getGameRules().put("spectatorsGenerateChunks", "false", this.server);
					}
				}
				break;
			case field_12775:
				this.player.method_14248().method_14910(this.player);
		}
	}

	@Override
	public void onGuiClose(GuiCloseServerPacket guiCloseServerPacket) {
		NetworkThreadUtils.forceMainThread(guiCloseServerPacket, this, this.player.getServerWorld());
		this.player.closeContainer();
	}

	@Override
	public void method_12076(class_2813 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.method_14234();
		if (this.player.container.syncId == arg.method_12194() && this.player.container.method_7622(this.player)) {
			if (this.player.isSpectator()) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.create();

				for (int i = 0; i < this.player.container.slotList.size(); i++) {
					defaultedList.add(((Slot)this.player.container.slotList.get(i)).getStack());
				}

				this.player.onContainerRegistered(this.player.container, defaultedList);
			} else {
				ItemStack itemStack = this.player.container.onSlotClick(arg.method_12192(), arg.method_12193(), arg.method_12195(), this.player);
				if (ItemStack.areEqual(arg.method_12190(), itemStack)) {
					this.player.networkHandler.sendPacket(new GuiActionConfirmClientPacket(arg.method_12194(), arg.method_12189(), true));
					this.player.field_13991 = true;
					this.player.container.sendContentUpdates();
					this.player.method_14241();
					this.player.field_13991 = false;
				} else {
					this.field_14132.put(this.player.container.syncId, arg.method_12189());
					this.player.networkHandler.sendPacket(new GuiActionConfirmClientPacket(arg.method_12194(), arg.method_12189(), false));
					this.player.container.method_7590(this.player, false);
					DefaultedList<ItemStack> defaultedList2 = DefaultedList.create();

					for (int j = 0; j < this.player.container.slotList.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.container.slotList.get(j)).getStack();
						defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
					}

					this.player.onContainerRegistered(this.player.container, defaultedList2);
				}
			}
		}
	}

	@Override
	public void method_12061(RecipeClickServerPacket recipeClickServerPacket) {
		NetworkThreadUtils.forceMainThread(recipeClickServerPacket, this, this.player.getServerWorld());
		this.player.method_14234();
		if (!this.player.isSpectator() && this.player.container.syncId == recipeClickServerPacket.getSyncId() && this.player.container.method_7622(this.player)) {
			Recipe recipe = this.server.getRecipeManager().get(recipeClickServerPacket.getRecipe());
			if (this.player.container instanceof FurnaceContainer) {
				new class_2958().method_12826(this.player, recipe, recipeClickServerPacket.method_12319());
			} else {
				new class_2955().method_12826(this.player, recipe, recipeClickServerPacket.method_12319());
			}
		}
	}

	@Override
	public void method_12055(ButtonClickServerPacket buttonClickServerPacket) {
		NetworkThreadUtils.forceMainThread(buttonClickServerPacket, this, this.player.getServerWorld());
		this.player.method_14234();
		if (this.player.container.syncId == buttonClickServerPacket.getSyncId() && this.player.container.method_7622(this.player) && !this.player.isSpectator()) {
			this.player.container.onButtonClick(this.player, buttonClickServerPacket.method_12186());
			this.player.container.sendContentUpdates();
		}
	}

	@Override
	public void method_12070(class_2873 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = arg.method_12481() < 0;
			ItemStack itemStack = arg.method_12479();
			CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
			if (!itemStack.isEmpty() && compoundTag != null && compoundTag.containsKey("x") && compoundTag.containsKey("y") && compoundTag.containsKey("z")) {
				BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
				BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
					compoundTag2.remove("x");
					compoundTag2.remove("y");
					compoundTag2.remove("z");
					itemStack.setChildTag("BlockEntityTag", compoundTag2);
				}
			}

			boolean bl2 = arg.method_12481() >= 1 && arg.method_12481() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getAmount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				if (itemStack.isEmpty()) {
					this.player.containerPlayer.setStackInSlot(arg.method_12481(), ItemStack.EMPTY);
				} else {
					this.player.containerPlayer.setStackInSlot(arg.method_12481(), itemStack);
				}

				this.player.containerPlayer.method_7590(this.player, true);
			} else if (bl && bl3 && this.field_14133 < 200) {
				this.field_14133 += 20;
				ItemEntity itemEntity = this.player.dropItem(itemStack, true);
				if (itemEntity != null) {
					itemEntity.method_6980();
				}
			}
		}
	}

	@Override
	public void method_12079(class_2809 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		Short short_ = this.field_14132.get(this.player.container.syncId);
		if (short_ != null
			&& arg.method_12176() == short_
			&& this.player.container.syncId == arg.method_12178()
			&& !this.player.container.method_7622(this.player)
			&& !this.player.isSpectator()) {
			this.player.container.method_7590(this.player, true);
		}
	}

	@Override
	public void method_12071(class_2877 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.method_14234();
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = arg.method_12510();
		if (serverWorld.isBlockLoaded(blockPos)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = arg.method_12508();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.method_11299(i, new StringTextComponent(TextFormat.stripFormatting(strings[i])));
			}

			signBlockEntity.markDirty();
			serverWorld.updateListeners(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void method_12082(class_2827 arg) {
		if (this.field_14125 && arg.method_12267() == this.field_14134) {
			int i = (int)(SystemUtil.getMeasuringTimeMili() - this.field_14136);
			this.player.field_13967 = (this.player.field_13967 * 3 + i) / 4;
			this.field_14125 = false;
		} else if (!this.player.getName().getString().equals(this.server.getUserName())) {
			this.method_14367(new TranslatableTextComponent("disconnect.timeout"));
		}
	}

	@Override
	public void method_12083(class_2842 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.abilities.flying = arg.method_12346() && this.player.abilities.allowFlying;
	}

	@Override
	public void onClientSettings(ClientSettingsServerPacket clientSettingsServerPacket) {
		NetworkThreadUtils.forceMainThread(clientSettingsServerPacket, this, this.player.getServerWorld());
		this.player.setClientSettings(clientSettingsServerPacket);
	}

	@Override
	public void onCustomPayload(CustomPayloadServerPacket customPayloadServerPacket) {
	}
}

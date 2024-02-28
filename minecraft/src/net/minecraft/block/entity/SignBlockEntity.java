package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class SignBlockEntity extends BlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int MAX_TEXT_WIDTH = 90;
	private static final int TEXT_LINE_HEIGHT = 10;
	@Nullable
	private UUID editor;
	private SignText frontText = this.createText();
	private SignText backText = this.createText();
	private boolean waxed;

	public SignBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityType.SIGN, pos, state);
	}

	public SignBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	protected SignText createText() {
		return new SignText();
	}

	public boolean isPlayerFacingFront(PlayerEntity player) {
		if (this.getCachedState().getBlock() instanceof AbstractSignBlock abstractSignBlock) {
			Vec3d vec3d = abstractSignBlock.getCenter(this.getCachedState());
			double d = player.getX() - ((double)this.getPos().getX() + vec3d.x);
			double e = player.getZ() - ((double)this.getPos().getZ() + vec3d.z);
			float f = abstractSignBlock.getRotationDegrees(this.getCachedState());
			float g = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
			return MathHelper.angleBetween(f, g) <= 90.0F;
		} else {
			return false;
		}
	}

	public SignText getText(boolean front) {
		return front ? this.frontText : this.backText;
	}

	public SignText getFrontText() {
		return this.frontText;
	}

	public SignText getBackText() {
		return this.backText;
	}

	public int getTextLineHeight() {
		return 10;
	}

	public int getMaxTextWidth() {
		return 90;
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		DynamicOps<NbtElement> dynamicOps = registryLookup.getOps(NbtOps.INSTANCE);
		SignText.CODEC.encodeStart(dynamicOps, this.frontText).resultOrPartial(LOGGER::error).ifPresent(frontText -> nbt.put("front_text", frontText));
		SignText.CODEC.encodeStart(dynamicOps, this.backText).resultOrPartial(LOGGER::error).ifPresent(backText -> nbt.put("back_text", backText));
		nbt.putBoolean("is_waxed", this.waxed);
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		DynamicOps<NbtElement> dynamicOps = registryLookup.getOps(NbtOps.INSTANCE);
		if (nbt.contains("front_text")) {
			SignText.CODEC
				.parse(dynamicOps, nbt.getCompound("front_text"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(signText -> this.frontText = this.parseLines(signText));
		}

		if (nbt.contains("back_text")) {
			SignText.CODEC
				.parse(dynamicOps, nbt.getCompound("back_text"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(signText -> this.backText = this.parseLines(signText));
		}

		this.waxed = nbt.getBoolean("is_waxed");
	}

	private SignText parseLines(SignText signText) {
		for (int i = 0; i < 4; i++) {
			Text text = this.parseLine(signText.getMessage(i, false));
			Text text2 = this.parseLine(signText.getMessage(i, true));
			signText = signText.withMessage(i, text, text2);
		}

		return signText;
	}

	private Text parseLine(Text text) {
		if (this.world instanceof ServerWorld serverWorld) {
			try {
				return Texts.parse(createCommandSource(null, serverWorld, this.pos), text, null, 0);
			} catch (CommandSyntaxException var4) {
			}
		}

		return text;
	}

	public void tryChangeText(PlayerEntity player, boolean front, List<FilteredMessage> messages) {
		if (!this.isWaxed() && player.getUuid().equals(this.getEditor()) && this.world != null) {
			this.changeText(text -> this.getTextWithMessages(player, messages, text), front);
			this.setEditor(null);
			this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
		} else {
			LOGGER.warn("Player {} just tried to change non-editable sign", player.getName().getString());
		}
	}

	public boolean changeText(UnaryOperator<SignText> textChanger, boolean front) {
		SignText signText = this.getText(front);
		return this.setText((SignText)textChanger.apply(signText), front);
	}

	private SignText getTextWithMessages(PlayerEntity player, List<FilteredMessage> messages, SignText text) {
		for (int i = 0; i < messages.size(); i++) {
			FilteredMessage filteredMessage = (FilteredMessage)messages.get(i);
			Style style = text.getMessage(i, player.shouldFilterText()).getStyle();
			if (player.shouldFilterText()) {
				text = text.withMessage(i, Text.literal(filteredMessage.getString()).setStyle(style));
			} else {
				text = text.withMessage(i, Text.literal(filteredMessage.raw()).setStyle(style), Text.literal(filteredMessage.getString()).setStyle(style));
			}
		}

		return text;
	}

	public boolean setText(SignText text, boolean front) {
		return front ? this.setFrontText(text) : this.setBackText(text);
	}

	private boolean setBackText(SignText backText) {
		if (backText != this.backText) {
			this.backText = backText;
			this.updateListeners();
			return true;
		} else {
			return false;
		}
	}

	private boolean setFrontText(SignText frontText) {
		if (frontText != this.frontText) {
			this.frontText = frontText;
			this.updateListeners();
			return true;
		} else {
			return false;
		}
	}

	public boolean canRunCommandClickEvent(boolean front, PlayerEntity player) {
		return this.isWaxed() && this.getText(front).hasRunCommandClickEvent(player);
	}

	public boolean runCommandClickEvent(PlayerEntity player, World world, BlockPos pos, boolean front) {
		boolean bl = false;

		for (Text text : this.getText(front).getMessages(player.shouldFilterText())) {
			Style style = text.getStyle();
			ClickEvent clickEvent = style.getClickEvent();
			if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				player.getServer().getCommandManager().executeWithPrefix(createCommandSource(player, world, pos), clickEvent.getValue());
				bl = true;
			}
		}

		return bl;
	}

	private static ServerCommandSource createCommandSource(@Nullable PlayerEntity player, World world, BlockPos pos) {
		String string = player == null ? "Sign" : player.getName().getString();
		Text text = (Text)(player == null ? Text.literal("Sign") : player.getDisplayName());
		return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(pos), Vec2f.ZERO, (ServerWorld)world, 2, string, text, world.getServer(), player);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createNbt(registryLookup);
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public void setEditor(@Nullable UUID editor) {
		this.editor = editor;
	}

	@Nullable
	public UUID getEditor() {
		return this.editor;
	}

	private void updateListeners() {
		this.markDirty();
		this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
	}

	public boolean isWaxed() {
		return this.waxed;
	}

	public boolean setWaxed(boolean waxed) {
		if (this.waxed != waxed) {
			this.waxed = waxed;
			this.updateListeners();
			return true;
		} else {
			return false;
		}
	}

	public boolean isPlayerTooFarToEdit(UUID uuid) {
		PlayerEntity playerEntity = this.world.getPlayerByUuid(uuid);
		return playerEntity == null
			|| playerEntity.squaredDistanceTo((double)this.getPos().getX(), (double)this.getPos().getY(), (double)this.getPos().getZ()) > 64.0;
	}

	public static void tick(World world, BlockPos pos, BlockState state, SignBlockEntity blockEntity) {
		UUID uUID = blockEntity.getEditor();
		if (uUID != null) {
			blockEntity.tryClearInvalidEditor(blockEntity, world, uUID);
		}
	}

	private void tryClearInvalidEditor(SignBlockEntity blockEntity, World world, UUID uuid) {
		if (blockEntity.isPlayerTooFarToEdit(uuid)) {
			blockEntity.setEditor(null);
		}
	}

	public SoundEvent getInteractionFailSound() {
		return SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL;
	}
}

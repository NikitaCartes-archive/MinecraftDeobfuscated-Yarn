package net.minecraft.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class AdvancementDisplay {
	private final Text title;
	private final Text description;
	private final ItemStack icon;
	private final Identifier background;
	private final AdvancementFrame frame;
	private final boolean showToast;
	private final boolean announceToChat;
	private final boolean hidden;
	private float xPos;
	private float yPos;

	public AdvancementDisplay(
		ItemStack itemStack, Text text, Text text2, @Nullable Identifier identifier, AdvancementFrame advancementFrame, boolean bl, boolean bl2, boolean bl3
	) {
		this.title = text;
		this.description = text2;
		this.icon = itemStack;
		this.background = identifier;
		this.frame = advancementFrame;
		this.showToast = bl;
		this.announceToChat = bl2;
		this.hidden = bl3;
	}

	public void setPosition(float f, float g) {
		this.xPos = f;
		this.yPos = g;
	}

	public Text getTitle() {
		return this.title;
	}

	public Text getDescription() {
		return this.description;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getIcon() {
		return this.icon;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier getBackground() {
		return this.background;
	}

	public AdvancementFrame getFrame() {
		return this.frame;
	}

	@Environment(EnvType.CLIENT)
	public float getX() {
		return this.xPos;
	}

	@Environment(EnvType.CLIENT)
	public float getY() {
		return this.yPos;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldShowToast() {
		return this.showToast;
	}

	public boolean shouldAnnounceToChat() {
		return this.announceToChat;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public static AdvancementDisplay fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Text text = JsonHelper.deserialize(jsonObject, "title", jsonDeserializationContext, Text.class);
		Text text2 = JsonHelper.deserialize(jsonObject, "description", jsonDeserializationContext, Text.class);
		if (text != null && text2 != null) {
			ItemStack itemStack = iconFromJson(JsonHelper.getObject(jsonObject, "icon"));
			Identifier identifier = jsonObject.has("background") ? new Identifier(JsonHelper.getString(jsonObject, "background")) : null;
			AdvancementFrame advancementFrame = jsonObject.has("frame")
				? AdvancementFrame.forName(JsonHelper.getString(jsonObject, "frame"))
				: AdvancementFrame.field_1254;
			boolean bl = JsonHelper.getBoolean(jsonObject, "show_toast", true);
			boolean bl2 = JsonHelper.getBoolean(jsonObject, "announce_to_chat", true);
			boolean bl3 = JsonHelper.getBoolean(jsonObject, "hidden", false);
			return new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, bl2, bl3);
		} else {
			throw new JsonSyntaxException("Both title and description must be set");
		}
	}

	private static ItemStack iconFromJson(JsonObject jsonObject) {
		if (!jsonObject.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = JsonHelper.getItem(jsonObject, "item");
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack itemStack = new ItemStack(item);
				if (jsonObject.has("nbt")) {
					try {
						CompoundTag compoundTag = StringNbtReader.parse(JsonHelper.asString(jsonObject.get("nbt"), "nbt"));
						itemStack.setTag(compoundTag);
					} catch (CommandSyntaxException var4) {
						throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
					}
				}

				return itemStack;
			}
		}
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.method_10805(this.title);
		packetByteBuf.method_10805(this.description);
		packetByteBuf.writeItemStack(this.icon);
		packetByteBuf.writeEnumConstant(this.frame);
		int i = 0;
		if (this.background != null) {
			i |= 1;
		}

		if (this.showToast) {
			i |= 2;
		}

		if (this.hidden) {
			i |= 4;
		}

		packetByteBuf.writeInt(i);
		if (this.background != null) {
			packetByteBuf.writeIdentifier(this.background);
		}

		packetByteBuf.writeFloat(this.xPos);
		packetByteBuf.writeFloat(this.yPos);
	}

	public static AdvancementDisplay fromPacket(PacketByteBuf packetByteBuf) {
		Text text = packetByteBuf.method_10808();
		Text text2 = packetByteBuf.method_10808();
		ItemStack itemStack = packetByteBuf.readItemStack();
		AdvancementFrame advancementFrame = packetByteBuf.readEnumConstant(AdvancementFrame.class);
		int i = packetByteBuf.readInt();
		Identifier identifier = (i & 1) != 0 ? packetByteBuf.readIdentifier() : null;
		boolean bl = (i & 2) != 0;
		boolean bl2 = (i & 4) != 0;
		AdvancementDisplay advancementDisplay = new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, false, bl2);
		advancementDisplay.setPosition(packetByteBuf.readFloat(), packetByteBuf.readFloat());
		return advancementDisplay;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("icon", this.iconToJson());
		jsonObject.add("title", Text.Serializer.toJsonTree(this.title));
		jsonObject.add("description", Text.Serializer.toJsonTree(this.description));
		jsonObject.addProperty("frame", this.frame.getId());
		jsonObject.addProperty("show_toast", this.showToast);
		jsonObject.addProperty("announce_to_chat", this.announceToChat);
		jsonObject.addProperty("hidden", this.hidden);
		if (this.background != null) {
			jsonObject.addProperty("background", this.background.toString());
		}

		return jsonObject;
	}

	private JsonObject iconToJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", Registry.ITEM.getId(this.icon.getItem()).toString());
		if (this.icon.hasTag()) {
			jsonObject.addProperty("nbt", this.icon.getTag().toString());
		}

		return jsonObject;
	}
}

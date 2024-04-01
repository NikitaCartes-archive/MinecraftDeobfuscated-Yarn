package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_9606 extends Screen {
	private static final Text field_51096 = Text.literal("potato");
	private int field_51097;
	private final int field_51098 = 20;
	private int field_51099;

	public class_9606() {
		super(field_51096);
	}

	@Override
	protected void init() {
		if (this.client.player != null) {
			this.client.player.playSound(SoundEvents.ENTITY_PLAYER_SPROUT_RESPAWN_ONE);
		}
	}

	@Override
	public void tick() {
		this.field_51099++;
		if (this.field_51099 >= 20) {
			this.close();
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.fill(0, 0, this.width, this.height, -16777216);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		this.method_59335();
	}

	private void method_59335() {
		this.client.setScreen(null);
		if (this.client.player != null) {
			this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.SPROUT_RESPAWN));
		}
	}
}

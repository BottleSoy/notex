package name.soy.notex.client.mixin;

import com.google.gson.Gson;
import name.soy.notex.client.NoteInfo;
import name.soy.notex.client.NoteScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class NoteXUIMixixn {

	@Shadow private MinecraftClient client;

	@Inject(method = "onCustomPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getData()Lnet/minecraft/network/PacketByteBuf;", shift = At.Shift.AFTER))
	public void payloadForNoteX(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (packet.getChannel().equals(new Identifier("soy", "notex"))) {
			PacketByteBuf data = packet.getData();
			String json = data.readString();
			NoteInfo info = new Gson().fromJson(json, NoteInfo.class);
			NoteScreen screen = new NoteScreen(info);
			this.client.openScreen(screen);
		}
	}
}

package name.soy.notex.mixin;

import name.soy.notex.*;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class PlayerNetworkMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "onClientSettings", at = @At("RETURN"))
	public void settings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
		NXVisiualize.PLAYER_LANG.put(player.getUuid(), ((ClientSettingsC2SPacketAccessor) packet).getLanguage());
	}

	@Inject(method = "onUpdateSelectedSlot",
			at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V"))
	public void scroll(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
		if (NXVisiualize.PLAYER_SCROLLS.contains(player.getUuid())) {
			int from = player.inventory.selectedSlot;
			int to = packet.getSelectedSlot();
			int offset;
			offset = to - from;
			if (offset < -4) offset += 9;
			if (offset > 4) offset -= 9;
			BlockPos playerSee = NXVisiualize.getPlayerSeeNote(player.getServerWorld(), player);
			if (playerSee != null) {
				NoteBlockEntity nbe = (NoteBlockEntity) player.getServerWorld().getBlockEntity(playerSee);
				assert nbe != null;
				CustomIns cins = Notex.insNames.get(nbe.getIns());
				if (cins != null) {
					nbe.setNote(nbe.getNote() + offset);
					while (nbe.getNote() < cins.min)
						nbe.setNote(nbe.getNote() + cins.max - cins.min + 1);
					while (nbe.getNote() > cins.max)
						nbe.setNote(nbe.getNote() - cins.max - cins.min - 1);
					nbe.play();
				} else {
					player.sendMessage(new LiteralText("音符盒乐器未注册!"), true);
				}
			}
		}
	}
}

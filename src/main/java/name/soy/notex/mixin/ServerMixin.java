package name.soy.notex.mixin;

import name.soy.notex.Notex;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class ServerMixin {
	@Inject(method = "save", at = @At("RETURN"))
	public void save(boolean suppressLogs, boolean bl, boolean bl2, CallbackInfoReturnable<Boolean> cir) {
		Notex.save((MinecraftServer) (Object) this);
	}
}

package name.soy.notex.mixin;

import name.soy.notex.NXVisiualize;
import name.soy.notex.Notex;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerMixin {
	@Inject(method = "save", at = @At("RETURN"))
	public void save(boolean suppressLogs, boolean bl, boolean bl2, CallbackInfoReturnable<Boolean> cir) {
		Notex.save((MinecraftServer) (Object) this);
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	public void shutdown(CallbackInfo ci) {
		NXVisiualize.SEE_ENTITY.forEach((pos, list) -> list.forEach(ArmorStandEntity::kill));
		NXVisiualize.OLD_LOOKED.clear();
		NXVisiualize.NEW_LOOKED.clear();
		NXVisiualize.PLAYER_SCROLLS.clear();
	}

	@Inject(method = "tickWorlds", at = @At("RETURN"))
	public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		NXVisiualize.OLD_LOOKED.forEach(pos -> {
			NXVisiualize.SEE_ENTITY.remove(pos).forEach(ArmorStandEntity::kill);
		});
		NXVisiualize.OLD_LOOKED.clear();
		NXVisiualize.OLD_LOOKED = NXVisiualize.NEW_LOOKED;
		NXVisiualize.NEW_LOOKED = new HashSet<>();
	}
}

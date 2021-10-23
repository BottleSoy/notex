package name.soy.notex.mixin;

import com.mojang.authlib.GameProfile;
import name.soy.notex.*;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity {

	@Shadow
	public abstract ServerWorld getServerWorld();

	@Shadow
	public abstract void playerTick();

	public PlayerMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void playerHasStick(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		if (this.getOffHandStack().getItem() == Items.DEBUG_STICK|| Notex.playersee.contains(uuid)) {
			BlockPos playerSee = NXVisiualize.getPlayerSeeNote(getServerWorld(), (ServerPlayerEntity) (Object) this);
			if (playerSee != null) {
				NoteBlockEntity be = (NoteBlockEntity) world.getBlockEntity(playerSee);
				assert be != null;
				if (!NXVisiualize.OLD_LOOKED.contains(playerSee)) {
					Vec3d pos1 = Vec3d.ofCenter(playerSee).add(0, 0.4, 0);
					Vec3d pos2 = Vec3d.ofCenter(playerSee).add(0, 0.65, 0);
					ArmorStandEntity ase1 = new ArmorStandEntity(world, pos1.x, pos1.y, pos1.z);
					ArmorStandEntity ase2 = new ArmorStandEntity(world, pos2.x, pos2.y, pos2.z);

					ase1.addScoreboardTag("notex-show");
					ase2.addScoreboardTag("notex-show");

					ase1.addScoreboardTag("notex-show-ins");
					ase2.addScoreboardTag("notex-show-note");

					ase1.setNoGravity(true);
					ase2.setNoGravity(true);

					ase1.setInvisible(true);
					ase2.setInvisible(true);

					ase1.setCustomNameVisible(true);
					ase2.setCustomNameVisible(true);

					//setMarker
					ase1.getDataTracker().set(ArmorStandEntity.ARMOR_STAND_FLAGS, (byte) (ase1.getDataTracker().get(ArmorStandEntity.ARMOR_STAND_FLAGS) | 16));
					ase2.getDataTracker().set(ArmorStandEntity.ARMOR_STAND_FLAGS, (byte) (ase2.getDataTracker().get(ArmorStandEntity.ARMOR_STAND_FLAGS) | 16));

					world.spawnEntity(ase1);
					world.spawnEntity(ase2);
					NXVisiualize.SEE_ENTITY.put(playerSee, Arrays.asList(ase1, ase2));
				} else {
					NXVisiualize.OLD_LOOKED.remove(playerSee);
				}
				NXVisiualize.SEE_ENTITY.get(playerSee).get(0).setCustomName(new LiteralText(Lang.get(player, "ins") + ":" + be.getIns()));
				CustomIns customIns = Notex.insNames.get(be.getIns());
				LiteralText note;
				if (customIns != null && customIns.basepitch > 0) {
					note = new LiteralText(Lang.get(player, "pitch") + ":" + be.getNote() + "(" + NoteMap.ALL_NOTENAMES.get(be.getNote() + customIns.basepitch) + ")");
				} else {
					note = new LiteralText(Lang.get(player, "pitch") + ":" + be.getNote());
				}
				if (be.locked) {
					note.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)));
				} else {
					note.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)));
				}
				NXVisiualize.SEE_ENTITY.get(playerSee).get(1).setCustomName(note);
				NXVisiualize.NEW_LOOKED.add(playerSee);
			}
		}
	}

}

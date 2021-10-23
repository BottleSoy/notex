package name.soy.notex.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.codecs.SimpleMapCodec;
import name.soy.notex.CustomIns;
import name.soy.notex.Lang;
import name.soy.notex.NoteBlockEntity;
import name.soy.notex.Notex;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block {
	@Shadow
	@Final
	public static IntProperty NOTE;

	public NoteBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "onSyncedBlockEvent", at = @At(value = "HEAD"), cancellable = true)
	public void onSync(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient) {
//			System.out.println("NoteBlock onSyncedBlockEvent");
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockEntity) {
				NoteBlockEntity note = (NoteBlockEntity) blockEntity;
				note.play();
			}
			cir.setReturnValue(true);
		}
		//客户端也屏蔽
		cir.setReturnValue(true);
	}

	@Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;cycle(Lnet/minecraft/state/property/Property;)Ljava/lang/Object;"))
	public <T extends Comparable<T>, S> S cycle(BlockState blockState, Property<T> property) {
		return (S) blockState;
	}

	@Inject(method = "onUse", at = @At("RETURN"))
	public void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockEntity) {
				NoteBlockEntity note = (NoteBlockEntity) blockEntity;
				if (Notex.insNames.containsKey(note.getIns())) {
					CustomIns custom = Notex.insNames.get(note.getIns());
					note.setNote(note.getNote() + 1);
					if (note.getNote() > custom.max || note.getNote() < custom.min) {
						note.setNote(custom.min);
					}
//					state = state.with(NOTE, note.note);

				} else {
					if (player != null) {
						player.sendMessage(new LiteralText(Lang.get((ServerPlayerEntity) player, "ins.notregistered")), true);
					}
				}
			}
		}
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At(value = "RETURN"))
	public void update(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (direction == Direction.DOWN && !world.isClient()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockEntity) {
				CachedBlockPosition cbp = new CachedBlockPosition(world, pos.down(), false);
//				System.out.println(cbp.getBlockState());
				CustomIns ins = Notex.insMap.getIf(cbp);
//				System.out.println(ins);
				((NoteBlockEntity) blockEntity).setIns(ins.name);
			}
		}
	}

//	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
//	public void playSound(World world, PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {}
//
//	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
//	public void noParticle(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {}

	@Redirect(method = "playNote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	public BlockState ignoreUp(World world, BlockPos pos) {
//		System.out.println("play note");
		return world.getGameRules().getBoolean(Notex.noteIgnoreAirsRule) ?
				new BlockState(Blocks.AIR, null, null) :
				world.getBlockState(pos);
	}
}

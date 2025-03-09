package com.rooxchicken.agnilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.data.PlayerModification;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class ModifyPlayerVelocity 
{
    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info)
    {
        if(!AgniLibClient.hasInitialized)
            return;

        ((PlayerEntity)(Object)this).addVelocity(PlayerModification.velocity);
    }

}

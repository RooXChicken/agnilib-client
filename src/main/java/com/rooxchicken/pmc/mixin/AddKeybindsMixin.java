package com.rooxchicken.pmc.mixin;

import com.rooxchicken.pmc.PMC;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

@Mixin(GameOptions.class)
public interface AddKeybindsMixin
{
    // @Accessor("allKeys")
    // public KeyBinding[] getAllKeys();

    // @Accessor("allKeys")
    // @Mutable
    // public void setAllKeys(KeyBinding[] _keys);
}

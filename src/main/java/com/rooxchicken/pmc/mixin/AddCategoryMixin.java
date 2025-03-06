package com.rooxchicken.pmc.mixin;

import java.util.List;
import java.util.Map;

import com.rooxchicken.pmc.PMC;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Mixin(KeyBinding.class)
public interface AddCategoryMixin
{
    @Accessor("CATEGORY_ORDER_MAP")
    public static Map<String, Integer> getCategories()
    {
        throw new AssertionError();
    }
}

package com.rooxchicken.pmc.networking.handlers;

import org.apache.commons.lang3.ArrayUtils;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.event.KeybindCallback;
import com.rooxchicken.pmc.mixin.AddCategoryMixin;
import com.rooxchicken.pmc.mixin.AddKeybindsMixin;
import com.rooxchicken.pmc.networking.PMCDataHandler;
import com.rooxchicken.pmc.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class RegisterKeybindHandler extends PMCDataHandler
{
    private static int addedCategoryIndex = 0;

    public RegisterKeybindHandler(PMCClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        String _category = pmc.readString(_buf);
        String _translation = pmc.readString(_buf);

        AddKeybindsMixin _keybinds = ((AddKeybindsMixin)MinecraftClient.getInstance().options);

        for(KeyBinding _keybind : _keybinds.getAllKeys())
            if(_keybind.getTranslationKey().equals(_translation) && _keybind.getCategory().equals(_category))
                return;

        if(!AddCategoryMixin.getCategories().containsKey(_category))
            AddCategoryMixin.getCategories().put(_category, (addedCategoryIndex++) + 7);

        KeyBinding _key = new KeyBinding(_translation, InputUtil.UNKNOWN_KEY.getCode(), _category);
        _keybinds.setAllKeys((KeyBinding[])ArrayUtils.add(_keybinds.getAllKeys(), _key));

        KeybindCallback.registeredBindings.add(_key);
    }
}

package com.rooxchicken.agnilib.networking.handlers;

import org.apache.commons.lang3.ArrayUtils;

import com.rooxchicken.agnilib.AgniLib;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.event.KeybindCallback;
import com.rooxchicken.agnilib.mixin.AddCategoryMixin;
import com.rooxchicken.agnilib.mixin.AddKeybindsMixin;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class RegisterKeybindHandler extends AgniLibDataHandler
{
    private static int addedCategoryIndex = 0;

    public RegisterKeybindHandler(AgniLibClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        String _category = agnilib.readString(_buf);
        String _translation = agnilib.readString(_buf);

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

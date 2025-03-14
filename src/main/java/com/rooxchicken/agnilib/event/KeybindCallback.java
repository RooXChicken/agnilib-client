package com.rooxchicken.agnilib.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.rooxchicken.agnilib.AgniLib;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.mixin.AddCategoryMixin;
import com.rooxchicken.agnilib.mixin.AddKeybindsMixin;
import com.rooxchicken.agnilib.networking.handlers.RegisterKeybindHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.End;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeybindCallback implements End
{
    public static final short keybindID = 1;
    public static final short createKeybindID = 7;

    public static HashSet<KeyBinding> registeredBindings;
    public static HashSet<String> registeredCategories;
    private HashMap<KeyBinding, Boolean> keyState;

    public KeybindCallback()
    {
        keyState = new HashMap<KeyBinding, Boolean>();
        registeredBindings = new HashSet<KeyBinding>();
        registeredCategories = new HashSet<String>();
    }

    @Override
    public void onEnd(WorldRenderContext context)
    {
        MinecraftClient _client = MinecraftClient.getInstance();

        ByteBuf _buf = Unpooled.buffer();
        _buf.writeShort(keybindID);

        boolean _empty = true;
        for(KeyBinding _bind : _client.options.allKeys)
        {
            if(_bind == null || _bind.isUnbound())
                continue;

            if(!keyState.containsKey(_bind))
                keyState.put(_bind, false);

            boolean _prev = keyState.get(_bind);
            if(_bind.isPressed() == _prev)
                continue;

            byte _mask = 0;

            if(_bind.isPressed() && !_prev)
                _mask += 1; // is just pressed
            else if(!_bind.isPressed() && _prev)
                _mask += 2; // is just released

            AgniLibClient.writeString(_bind.getCategory(), _buf);
            AgniLibClient.writeString(_bind.getTranslationKey(), _buf);
            _buf.writeByte(_mask);

            keyState.put(_bind, _bind.isPressed());
            _empty = false;
        }

        if(!_empty)
            AgniLibClient.sendData(_buf.array());
    }

    public void unregisterAllCustom()
    {
        AddKeybindsMixin _keybinds = ((AddKeybindsMixin)MinecraftClient.getInstance().options);
        ArrayList<KeyBinding> _notCreated = new ArrayList<KeyBinding>();
        _notCreated.addAll(List.of(_keybinds.getAllKeys()));

        for(int i = 0; i < _notCreated.size(); i++)
        {
            if(registeredBindings.contains(_notCreated.get(i)))
                _notCreated.remove(i--);
        }

        _keybinds.setAllKeys(_notCreated.toArray(new KeyBinding[] {}));
        registeredBindings.clear();

        Set<Entry<String, Integer>> _set = AddCategoryMixin.getCategories().entrySet();
        for(Entry<String, Integer> _cat : _set)
        {
            if(registeredCategories.contains(_cat.getKey()))
                AddCategoryMixin.getCategories().remove(_cat.getKey());
        }

        RegisterKeybindHandler.addedCategoryIndex = 0;
    }

    public static HashMap<String, Object> saveSettings()
    {
        HashMap<String, Object> _keybinds = new HashMap<String, Object>();
        
        for(KeyBinding _key : registeredBindings)
            _keybinds.put(_key.getCategory() + "~" + _key.getTranslationKey(), InputUtil.fromTranslationKey(_key.getBoundKeyTranslationKey()).getCode());

        return _keybinds;
    }
}

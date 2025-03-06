package com.rooxchicken.pmc.event;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.mixin.AddCategoryMixin;
import com.rooxchicken.pmc.mixin.AddKeybindsMixin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.End;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class KeybindCallback implements End
{
    public static final short keybindID = 1;
    public static final short createKeybindID = 7;

    public static HashSet<KeyBinding> registeredBindings;
    private HashMap<KeyBinding, Boolean> keyState;

    public KeybindCallback()
    {
        keyState = new HashMap<KeyBinding, Boolean>();
        registeredBindings = new HashSet<KeyBinding>();
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

            PMCClient.writeString(_bind.getCategory(), _buf);
            PMCClient.writeString(_bind.getTranslationKey(), _buf);
            _buf.writeByte(_mask);

            keyState.put(_bind, _bind.isPressed());
            _empty = false;
        }

        if(!_empty)
            PMCClient.sendData(_buf.array());
    }

    public void unregisterAllCustom()
    {
        AddKeybindsMixin _keybinds = ((AddKeybindsMixin)MinecraftClient.getInstance().options);
        ArrayList<KeyBinding> _notCreated = new ArrayList<KeyBinding>();

        for(KeyBinding _bind : _keybinds.getAllKeys())
        {
            if(!KeybindCallback.registeredBindings.contains(_bind))
                _notCreated.add(_bind);
        }

        _keybinds.setAllKeys(_notCreated.toArray(new KeyBinding[] {}));

        ArrayList<String> _toRemove = new ArrayList<String>();
        for(Entry<String, Integer> _category : AddCategoryMixin.getCategories().entrySet())
        {
            if(_category.getValue() > 7)
                _toRemove.add(_category.getKey());
        }

        for(String _category : _toRemove)
            AddCategoryMixin.getCategories().remove(_category);
    }
}

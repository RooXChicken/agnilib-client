package com.rooxchicken.pmc.event;

import java.util.HashMap;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.PMCClient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.End;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.encoding.VarInts;

public class KeybindCallback implements End
{
    private static final short keybindID = 1;

    private HashMap<KeyBinding, Boolean> keyState;

    public KeybindCallback()
    {
        keyState = new HashMap<KeyBinding, Boolean>();
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

            PMCClient.writeString(_bind.getTranslationKey(), _buf);
            _buf.writeByte(_mask);

            keyState.put(_bind, _bind.isPressed());
            _empty = false;
        }

        if(!_empty)
            PMCClient.sendData(_buf.array());
    }   
}

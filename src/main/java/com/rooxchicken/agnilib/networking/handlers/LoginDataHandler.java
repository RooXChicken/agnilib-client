package com.rooxchicken.agnilib.networking.handlers;

import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.data.AgniLibSettings;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class LoginDataHandler extends AgniLibDataHandler
{
    public static final short loginID = 0;
    
    public LoginDataHandler(AgniLibClient _client)
    {
        super(_client);
    }
    
    @Override
    public void handleData(ByteBuf _buf)
    {
        int _version = _buf.readInt();
        agnilib.keybindCallback.unregisterAllCustom();

        if(_version > AgniLibClient.AgniLib_VERSION)
            MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.of("§4Your mod version is out of date! Expect bugs! C: " + AgniLibClient.AgniLib_VERSION + " S: " + _version));
        else if(_version < AgniLibClient.AgniLib_VERSION)
            MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.of("§4Your mod version is too new! Expect bugs! C: " + AgniLibClient.AgniLib_VERSION + " S: " + _version));
    }
}

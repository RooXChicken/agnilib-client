package com.rooxchicken.agnilib.networking.handlers;

import com.rooxchicken.agnilib.AgniLib;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.data.PlayerModification;
import com.rooxchicken.agnilib.networking.AgniLibDataHandler;
import com.rooxchicken.agnilib.objects.Component;
import com.rooxchicken.agnilib.objects.Text;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class PlayerModificationHandler extends AgniLibDataHandler
{
    public static final short playerModification = 8;
    
    public static final short playerSetVelocity = 0;
    public static final short playerGetVelocity = 1;

    public static final short playerGetTarget = 3;

    public PlayerModificationHandler(AgniLibClient _client)
    {
        super(_client);
    }

    @Override
    public void handleData(ByteBuf _buf)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        short _state = _buf.readShort();

        switch(_state)
        {
            case playerSetVelocity:
                PlayerModification.velocity = new Vec3d(_buf.readDouble(), _buf.readDouble(), _buf.readDouble());
            break;
        }
    }
}

package com.rooxchicken.pmc.objects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import com.rooxchicken.pmc.PMC;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class Image extends Component
{
    public static final short imageID = 3;
    public static final short finishID = 4;
    public static final short MAX_SEND_SIZE = 13240;

    // public ByteBuffer data;
    public byte[] data;
    
    public Identifier identifier;
    public NativeImageBackedTexture image = null;
    public NativeImage nativeImage = null;

    public Image(String _id)
    {
        super(_id);
    }

    public void importImage()
    {
        identifier = Identifier.of(PMC.MOD_ID, "textures/" + id);
        try
        {
            InputStream _stream = new ByteArrayInputStream(data);
            nativeImage = NativeImage.read(_stream);
            _stream.close();
            image = new NativeImageBackedTexture(nativeImage);
            
            MinecraftClient _client = MinecraftClient.getInstance();
            _client.getTextureManager().registerTexture(identifier, image);

        }
        catch(Exception e)
        {
            PMC.LOGGER.error("Failed to read byte array for texture: " + id, e);
        }
    }

    @Override
    public void onDestroy()
    {
        MinecraftClient _client = MinecraftClient.getInstance();
        _client.getTextureManager().destroyTexture(identifier);
        
        image.close();
        image = null;
    }
}

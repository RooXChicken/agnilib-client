package com.rooxchicken.pmc.objects;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.data.ImagePair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class Image extends Component
{
    public static HashMap<String, ImagePair> loadedTextures = new HashMap<String, ImagePair>();

    public static final short imageID = 4;

    public static final short preloadID = 5;
    public static final short finishID = 6;

    public String name;

    public float r = 0;
    public float g = 0;
    public float b = 0;
    public float a = 0;
    public boolean blend = false;

    public Image(String _id)
    {
        super(_id);
    }

    public static void loadImage(String _id, byte[] _data)
    {
        try
        {
            InputStream _stream = new ByteArrayInputStream(_data);
            NativeImage _nativeImage = NativeImage.read(_stream);
            _stream.close();

            NativeImageBackedTexture _backedImage = new NativeImageBackedTexture(_nativeImage);
            
            MinecraftClient _client = MinecraftClient.getInstance();
            _client.getTextureManager().registerTexture(Identifier.of(PMC.MOD_ID, "textures/" + _id), _backedImage);

            loadedTextures.put(_id, new ImagePair(_nativeImage, _backedImage));
        }
        catch(Exception e)
        {
            PMC.LOGGER.error("Failed to read byte array for texture: " + _id, e);
        }
    }

    public static void destroyImage(String _id)
    {
        MinecraftClient _client = MinecraftClient.getInstance();
        _client.getTextureManager().destroyTexture(Identifier.of(PMC.MOD_ID, "textures/" + _id));
    }
}

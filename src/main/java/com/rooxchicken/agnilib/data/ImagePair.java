package com.rooxchicken.agnilib.data;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class ImagePair
{
    public NativeImage nativeImage;
    public NativeImageBackedTexture backedImage;

    public ImagePair(NativeImage _nativeImage, NativeImageBackedTexture _backedImage)
    {
        nativeImage = _nativeImage;
        backedImage = _backedImage;
    }
}
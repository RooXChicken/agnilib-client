package com.rooxchicken.agnilib.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rooxchicken.agnilib.AgniLib;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.objects.Component;
import com.rooxchicken.agnilib.objects.Image;
import com.rooxchicken.agnilib.objects.Text;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DrawGUICallback implements HudRenderCallback
{
    private MatrixStack matrixStack;

    @Override
    public void onHudRender(DrawContext _drawContext, RenderTickCounter _tickDelta)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        for(Component _component : AgniLibClient.components)
        {
            if(_component instanceof Text _text)
            {
                startTransformation(_drawContext, _text.scaleX, _text.scaleY, _text.posX, _text.posY);
                _drawContext.drawText(textRenderer, net.minecraft.text.Text.of(_text.text), 0, 0, _text.color, true);
                stopTransformation(_drawContext);
            }
            else if(_component instanceof Image _image)
            {
                if(Image.loadedTextures.get(_image.name) == null)
                    continue;
                
                double _scaleX = 1.0;
                double _scaleY = 1.0;
                if(_image.positionType == true)
                {
                    _scaleX *= client.getWindow().getWidth() / client.getWindow().getScaleFactor();
                    _scaleY *= client.getWindow().getHeight() / client.getWindow().getScaleFactor();
                }

                startTransformation(_drawContext, _image.scaleX, _image.scaleY, _image.posX * _scaleX, _image.posY * _scaleY);

                RenderSystem.setShaderColor(_image.r, _image.g, _image.b, _image.a);
                if(_image.blend)
                    RenderSystem.enableBlend();
                    
                int _width = Image.loadedTextures.get(_image.name).nativeImage.getWidth();
                int _height = Image.loadedTextures.get(_image.name).nativeImage.getHeight();
                _drawContext.drawTexture(Identifier.of(AgniLib.MOD_ID, "textures/" + _image.name), 0, 0, 0, 0, _width, _height, _width, _height);
                stopTransformation(_drawContext);
            }
            
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    private void startTransformation(DrawContext _drawContext, double _scaleX, double _scaleY, double _posX, double _posY)
    {
        matrixStack = _drawContext.getMatrices();

		matrixStack.push();
		matrixStack.scale((float)_scaleX, (float)_scaleY, 0);
        matrixStack.translate((float)_posX/_scaleX, (float)_posY/_scaleY, 0);
    }

    private void stopTransformation(DrawContext _drawContext)
    {
        matrixStack.pop();
    }
}

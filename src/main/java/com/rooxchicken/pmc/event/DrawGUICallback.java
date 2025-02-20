package com.rooxchicken.pmc.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Image;
import com.rooxchicken.pmc.objects.Text;

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

        for(Component _component : PMCClient.components)
        {
            if(_component instanceof Text _text)
            {
                startTransformation(_drawContext, _text.scaleX, _text.scaleY, _text.posX, _text.posY);
                _drawContext.drawText(textRenderer, net.minecraft.text.Text.of(_text.text), 0, 0, _text.color, true);
                stopTransformation(_drawContext);
            }
            else if(_component instanceof Image _image)
            {
                if(_image.nativeImage == null)
                    continue;
                    
                startTransformation(_drawContext, _image.scaleX, _image.scaleY, _image.posX, _image.posY);
                _drawContext.drawTexture(_image.identifier, 0, 0, 0, 0, _image.nativeImage.getWidth(), _image.nativeImage.getHeight(), _image.nativeImage.getWidth(), _image.nativeImage.getHeight());
                stopTransformation(_drawContext);
            }
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

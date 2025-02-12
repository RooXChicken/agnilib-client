package com.rooxchicken.pmc.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.PMCClient;
import com.rooxchicken.pmc.data.Text;

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

        for(Text _text : PMCClient.text)
        {
            startTransformation(_drawContext, _text.component.scaleX, _text.component.scaleY, _text.component.posX, _text.component.posY);
            _drawContext.drawText(textRenderer, net.minecraft.text.Text.of(_text.text), 0, 0, _text.color, true);
            stopTransformation(_drawContext);
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

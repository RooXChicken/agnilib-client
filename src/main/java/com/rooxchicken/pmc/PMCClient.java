package com.rooxchicken.pmc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.rooxchicken.pmc.event.DrawGUICallback;
import com.rooxchicken.pmc.networking.PMCPacket;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Image;
import com.rooxchicken.pmc.objects.Text;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler.Payload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.packet.CustomPayload.Id;
import net.minecraft.util.Identifier;

public class PMCClient implements ClientModInitializer
{
	public static ArrayList<Component> components = new ArrayList<Component>();

	@Override
	public void onInitializeClient()
	{
		HudRenderCallback.EVENT.register(new DrawGUICallback());

		ClientPlayConnectionEvents.DISCONNECT.register
		((handler, client) ->
		{
			components.clear();
		});
		
		ClientPlayNetworking.registerGlobalReceiver
		(PMCPacket.PACKET_ID, (_payload, _context) ->
		{
			PacketByteBuf _buf = new PacketByteBuf(Unpooled.copiedBuffer(_payload.buf()));
			short _status = _buf.readShort();

			switch(_status)
			{
				case Component.componentID:
					Component _component = getComponent(_buf, Component.class);

					_component.posX = _buf.readDouble();
					_component.posY = _buf.readDouble();
					_component.scaleX = _buf.readDouble();
					_component.scaleY = _buf.readDouble();
				break;

				case Component.removeID:
					Component _toRemove = getComponent(_buf, Component.class);
					components.remove(_toRemove);
				break;

				case Text.textID:
					Text _text = (Text)getComponent(_buf, Text.class);

					_text.text = readString(_buf);
					_text.color = _buf.readInt();
				break;

				case Image.imageID:
					Image _image = (Image)getComponent(_buf, Image.class);

					int _imgSize = Integer.parseInt(readString(_buf));

					int _start = Integer.parseInt(readString(_buf));
					int _size = Integer.parseInt(readString(_buf));

					if(_start == 0)
						_image.data = new byte[_imgSize];

					for(int i = _start; i < _start+_size; i++)
						_image.data[i] = _buf.readByte();

				break;

				case Image.finishID:
					Image _finishedImage = (Image)getComponent(_buf, Image.class);
					_finishedImage.importImage();
				break;
			}
		});
	}

	private int getComponentID(String _id)
	{
		for(int i = 0; i < components.size(); i++)
			if(components.get(i).id.equals(_id))
				return i;

		return -1;
	}

	private Component getComponent(PacketByteBuf _buf, Class<?> _clazz)
	{
		String _id = readString(_buf);
		int _cID = getComponentID(_id);
		
		if(_cID == -1)
		{
			_cID = components.size();
			try
			{
				Constructor<?> _constructor = _clazz.getConstructor(String.class);
				Object _component = _constructor.newInstance(new Object[] { _id });
				
				components.add((Component)_component);
			}
			catch(Exception e)
			{
				PMC.LOGGER.error("Class does not have this constructor! ", e);
			}
		}

		return components.get(_cID);
	}

	private String readString(PacketByteBuf _buf)
	{
		return _buf.readCharSequence(_buf.readShort(), Charset.defaultCharset()).toString();
	}
}
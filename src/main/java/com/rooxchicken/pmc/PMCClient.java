package com.rooxchicken.pmc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.rooxchicken.pmc.event.DrawGUICallback;
import com.rooxchicken.pmc.event.KeybindCallback;
import com.rooxchicken.pmc.networking.PMCPacket;
import com.rooxchicken.pmc.objects.Component;
import com.rooxchicken.pmc.objects.Image;
import com.rooxchicken.pmc.objects.Text;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler.Payload;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.packet.CustomPayload.Id;
import net.minecraft.util.Identifier;

public class PMCClient implements ClientModInitializer
{
	private static final short PMC_VERSION = 1;

	private static final short loginID = 0;

	public static ArrayList<Component> components = new ArrayList<Component>();
	private static HashMap<String, byte[]> workingTetxures = new HashMap<String, byte[]>();

	private boolean hasInitialized = false;

	@Override
	public void onInitializeClient()
	{
		HudRenderCallback.EVENT.register(new DrawGUICallback());

		ClientPlayConnectionEvents.DISCONNECT.register
		((handler, client) ->
		{
			components.clear();
			Image.loadedTextures.clear();
			hasInitialized = false;
		});
		
		PayloadTypeRegistry.playC2S().register(PMCPacket.PACKET_ID, PMCPacket.PACKET_CODEC);

		ClientPlayNetworking.registerGlobalReceiver
		(PMCPacket.PACKET_ID, (_payload, _context) ->
		{
			ByteBuf _buf = Unpooled.copiedBuffer(_payload.buf());
			short _status = _buf.readShort();

			switch(_status)
			{
				case loginID:
					int _version = _buf.readInt();

					if(_version > PMC_VERSION)
						MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.of("§4Your mod version is out of date! Expect bugs! C: " + PMC_VERSION + " S: " + _version));
					else if(_version < PMC_VERSION)
						MinecraftClient.getInstance().player.sendMessage(net.minecraft.text.Text.of("§4Your mod version is too new! Expect bugs! C: " + PMC_VERSION + " S: " + _version));
				break;

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

				case Image.preloadID:
					String _imageID = readString(_buf);
					int _imgSize = Integer.parseInt(readString(_buf));

					int _start = Integer.parseInt(readString(_buf));
					int _size = Integer.parseInt(readString(_buf));
					
					if(_start == 0)
						workingTetxures.put(_imageID, new byte[_imgSize]);

					byte[] _imageData = workingTetxures.get(_imageID);
					
					for(int i = _start; i < _start+_size; i++)
						_imageData[i] = _buf.readByte();

				break;

				case Image.finishID:
					String _finishedImageID = readString(_buf);
					Image.loadImage(_finishedImageID, workingTetxures.get(_finishedImageID));

					workingTetxures.remove(_finishedImageID);

				break;

				case Image.imageID:
					Image _finishedImage = (Image)getComponent(_buf, Image.class);

					_finishedImage.name = readString(_buf);

					_finishedImage.r = _buf.readFloat();
					_finishedImage.g = _buf.readFloat();
					_finishedImage.b = _buf.readFloat();
					_finishedImage.a = _buf.readFloat();
					_finishedImage.blend = _buf.readBoolean();
				break;
			}
		});

		ClientTickEvents.END_WORLD_TICK.register
		((client) ->
		{
			if(!hasInitialized)
			{
				hasInitialized = true;
				ByteBuf _buf = Unpooled.buffer();
				_buf.writeShort(loginID);

				sendData(_buf.array());
			}
		});

		WorldRenderEvents.END.register(new KeybindCallback());
	}

	private int getComponentID(String _id)
	{
		for(int i = 0; i < components.size(); i++)
			if(components.get(i).id.equals(_id))
				return i;

		return -1;
	}

	private Component getComponent(ByteBuf _buf, Class<?> _clazz)
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

	private String readString(ByteBuf _buf)
	{
		return _buf.readCharSequence(_buf.readShort(), Charset.defaultCharset()).toString();
	}

	public static void writeString(String _string, ByteBuf _buf)
    {
        _buf.writeShort(_string.trim().length());
        _buf.writeCharSequence(_string, Charset.defaultCharset());
    }

	public static void sendData(byte[] _data)
	{
        ByteBuf _buf = Unpooled.buffer(0);
        VarInts.write(_buf, _data.length);

        byte[] _lengthData = new byte[_buf.readableBytes()];
        for(int i = 0; i < _lengthData.length; i++)
            _lengthData[i] = _buf.readByte();

        ByteArrayDataOutput _out = ByteStreams.newDataOutput();
        _out.write(_lengthData);
        _out.write(_data);
            
        ClientPlayNetworking.send(new PMCPacket(_out.toByteArray()));
	}
}
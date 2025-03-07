package com.rooxchicken.agnilib.networking;

import com.rooxchicken.agnilib.AgniLib;
import com.rooxchicken.agnilib.objects.Text;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Style.Codecs;
import net.minecraft.util.Identifier;

public record AgniLibPacket(byte[] buf) implements CustomPayload
{
    public static final short MAX_SEND_SIZE = 13240;

    public static final CustomPayload.Id<AgniLibPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(AgniLib.MOD_ID, "channel"));
    public static final PacketCodec<RegistryByteBuf, AgniLibPacket> PACKET_CODEC = PacketCodecs.BYTE_ARRAY.xmap(AgniLibPacket::new, AgniLibPacket::buf).cast();
    
    @Override
    public Id<? extends CustomPayload> getId() { return PACKET_ID; }
}
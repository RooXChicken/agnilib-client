package com.rooxchicken.pmc.networking;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.objects.Text;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Style.Codecs;
import net.minecraft.util.Identifier;

public record PMCPacket(byte[] buf) implements CustomPayload
{
    public static final CustomPayload.Id<PMCPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(PMC.MOD_ID, "channel"));
    public static final PacketCodec<RegistryByteBuf, PMCPacket> PACKET_CODEC = PacketCodecs.BYTE_ARRAY.xmap(PMCPacket::new, PMCPacket::buf).cast();
    
    @Override
    public Id<? extends CustomPayload> getId() { return PACKET_ID; }
}
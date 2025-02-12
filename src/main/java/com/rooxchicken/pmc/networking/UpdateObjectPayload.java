package com.rooxchicken.pmc.networking;

import com.rooxchicken.pmc.PMC;
import com.rooxchicken.pmc.data.Text;
import com.rooxchicken.pmc.networking.text.TextUAC;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record UpdateObjectPayload(String id, double posX, double posY, double scaleX, double scaleY) implements CustomPayload
{
    public static final CustomPayload.Id<UpdateObjectPayload> PACKET_ID = new CustomPayload.Id<>(Identifier.of(PMC.MOD_ID, "object_u"));
    public static final PacketCodec<RegistryByteBuf, UpdateObjectPayload> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, UpdateObjectPayload::id,
        PacketCodecs.DOUBLE, UpdateObjectPayload::posX,
        PacketCodecs.DOUBLE, UpdateObjectPayload::posY,
        PacketCodecs.DOUBLE, UpdateObjectPayload::scaleX,
        PacketCodecs.DOUBLE, UpdateObjectPayload::scaleY,
        UpdateObjectPayload::new
    );
    
    @Override
    public Id<? extends CustomPayload> getId() { return PACKET_ID; }
}
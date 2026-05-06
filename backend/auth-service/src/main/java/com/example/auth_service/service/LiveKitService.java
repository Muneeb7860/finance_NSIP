package com.example.auth_service.service;

import io.livekit.server.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LiveKitService {

    @Value("${livekit.api.key}")
    private String apiKey;

    @Value("${livekit.api.secret}")
    private String apiSecret;

    public String createToken(String roomName, String participantName) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setName(participantName);
        token.setIdentity(participantName);
        token.setMetadata("{\"role\":\"user\"}");

        token.addGrants(new io.livekit.server.RoomJoin(true));
        token.addGrants(new io.livekit.server.RoomName(roomName));
        token.addGrants(new io.livekit.server.CanPublish(true));
        token.addGrants(new io.livekit.server.CanSubscribe(true));

        return token.toJwt();
    }
}

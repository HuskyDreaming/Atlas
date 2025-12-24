package com.huskydreaming.claims.model.claims;

import com.huskydreaming.claims.enumeration.ClaimType;
import com.huskydreaming.claims.model.flags.ClaimFlag;

import java.util.Objects;
import java.util.UUID;

public sealed interface ResolvedClaim permits ResolvedClaim.Plot, ResolvedClaim.Area, ResolvedClaim.Chunk {

    ClaimType type();

    UUID worldId();

    UUID ownerId();

    boolean can(UUID playerId, ClaimFlag claimFlag);

    record Plot(PlotClaim claim) implements ResolvedClaim {

        public Plot {
            Objects.requireNonNull(claim, "claim");
        }

        @Override
        public ClaimType type() {
            return ClaimType.PLOT;
        }

        @Override
        public UUID worldId() {
            return claim.worldId();
        }

        @Override
        public UUID ownerId() {
            return claim.ownerId();
        }

        @Override
        public boolean can(UUID playerId, ClaimFlag flag) {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(flag, "flag");
            return claim.can(playerId, flag);
        }

        public PlotClaim unwrap() {
            return claim;
        }
    }

    record Area(AreaClaim claim) implements ResolvedClaim {

        public Area {
            Objects.requireNonNull(claim, "claim");
        }

        @Override
        public ClaimType type() {
            return ClaimType.AREA;
        }

        @Override
        public UUID worldId() {
            return claim.worldId();
        }

        @Override
        public UUID ownerId() {
            return claim.ownerId();
        }

        @Override
        public boolean can(UUID playerId, ClaimFlag flag) {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(flag, "flag");
            return claim.can(playerId, flag);
        }

        public AreaClaim unwrap() {
            return claim;
        }
    }

    record Chunk(ChunkClaim claim) implements ResolvedClaim {

        public Chunk {
            Objects.requireNonNull(claim, "claim");
        }

        @Override
        public ClaimType type() {
            return ClaimType.CHUNK;
        }

        @Override
        public UUID worldId() {
            return claim.worldId();
        }

        @Override
        public UUID ownerId() {
            return claim.ownerId();
        }

        @Override
        public boolean can(UUID playerId, ClaimFlag flag) {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(flag, "flag");
            return claim.can(playerId, flag);
        }

        public ChunkClaim unwrap() {
            return claim;
        }
    }

    static ResolvedClaim of(PlotClaim claim) {
        return new Plot(claim);
    }

    static ResolvedClaim of(AreaClaim claim) {
        return new Area(claim);
    }

    static ResolvedClaim of(ChunkClaim claim) {
        return new Chunk(claim);
    }
}
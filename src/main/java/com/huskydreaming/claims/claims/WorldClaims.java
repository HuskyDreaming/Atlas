package com.huskydreaming.claims.claims;

import com.huskydreaming.claims.model.claims.AreaClaim;
import com.huskydreaming.claims.model.claims.ChunkClaim;
import com.huskydreaming.claims.model.claims.PlotClaim;
import com.huskydreaming.claims.model.claims.ResolvedClaim;
import com.huskydreaming.claims.model.flags.ClaimFlag;
import com.huskydreaming.claims.model.positions.BlockPosition;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class WorldClaims {

    private final UUID worldId;

    private final AreaClaims areaClaims;
    private final ChunkClaims chunkClaims;
    private final PlotClaims plotClaims;

    public WorldClaims(UUID worldId) {
        this.worldId = Objects.requireNonNull(worldId, "worldId");
        this.areaClaims = new AreaClaims(worldId);
        this.chunkClaims = new ChunkClaims(worldId);
        this.plotClaims = new PlotClaims(worldId);
    }

    public UUID worldId() {
        return worldId;
    }

    public Optional<ResolvedClaim> getClaimAt(BlockPosition position) {
        Objects.requireNonNull(position, "position");

        PlotClaim plot = plotClaims.getAt(position);
        if (plot != null) {
            return Optional.of(ResolvedClaim.of(plot));
        }

        AreaClaim area = areaClaims.getAt(position);
        if (area != null) {
            return Optional.of(ResolvedClaim.of(area));
        }

        ChunkClaim chunkClaim = chunkClaims.getAt(position);
        if (chunkClaim != null) {
            return Optional.of(ResolvedClaim.of(chunkClaim));
        }

        return Optional.empty();
    }

    public boolean canPerformAction(
            UUID playerId,
            BlockPosition position,
            ClaimFlag flag
    ) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(flag, "flag");

        return getClaimAt(position)
                .map(c -> c.can(playerId, flag))
                .orElse(true);
    }

    public AreaClaims area() {
        return areaClaims;
    }

    public ChunkClaims chunk() {
        return chunkClaims;
    }

    public PlotClaims plot() {
        return plotClaims;
    }

    public void clear() {
        plotClaims.clear();
        areaClaims.clear();
        chunkClaims.clear();
    }
}
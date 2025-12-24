package com.huskydreaming.claims.model.claims;

import com.huskydreaming.claims.helpers.SpatialGrid;
import com.huskydreaming.claims.model.helpers.AreaClaimHelper;
import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.ChunkPosition;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AreaClaimTest {

    @Test
    void touchedChunksSpansMultipleChunksIncludingNegative() {
        AreaClaim claim = AreaClaimHelper.areaClaim(
                0, 0, 0,
                63, 10, 63
        );

        Set<ChunkPosition> touched = AreaClaimHelper.toSet(
                SpatialGrid.touchedChunks(claim.bounds())
        );

        assertEquals(4, touched.size());
        assertTrue(touched.contains(new ChunkPosition(0, 0)));
        assertTrue(touched.contains(new ChunkPosition(1, 0)));
        assertTrue(touched.contains(new ChunkPosition(0, 1)));
        assertTrue(touched.contains(new ChunkPosition(1, 1)));
    }

    @Test
    void touchedChunksHandlesNegativeCoordinatesCorrectly() {
        AreaClaim claim = AreaClaimHelper.areaClaim(
                -1, 0, -1,
                0, 10, 0
        );

        Set<ChunkPosition> touched = AreaClaimHelper.toSet(
                SpatialGrid.touchedChunks(claim.bounds())
        );

        assertEquals(4, touched.size());
        assertTrue(touched.contains(new ChunkPosition(-1, -1)));
        assertTrue(touched.contains(new ChunkPosition(0, -1)));
        assertTrue(touched.contains(new ChunkPosition(-1, 0)));
        assertTrue(touched.contains(new ChunkPosition(0, 0)));
    }

    @Test
    void touchedChunksInclusiveMaxBoundaryCase() {
        AreaClaim claim = AreaClaimHelper.areaClaim(
                0, 0, 0,
                32, 10, 32
        );

        Set<ChunkPosition> touched = AreaClaimHelper.toSet(
                SpatialGrid.touchedChunks(claim.bounds())
        );

        assertEquals(4, touched.size());
    }

    @Test
    void touchedCellsCoversAllCellsWithinBounds() {
        AreaClaim claim = AreaClaimHelper.areaClaim(
                0, 0, 0,
                15, 10, 15
        );

        Iterable<Long> cells = SpatialGrid.touchedCells(claim.bounds());
        Set<Long> cellSet = AreaClaimHelper.toLongSet(cells);

        // 0–15 with CELL_SIZE=8 → cells (0,0), (1,0), (0,1), (1,1)
        assertEquals(4, cellSet.size());

        assertTrue(cellSet.contains(SpatialGrid.cellKey(0, 0)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(1, 0)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(0, 1)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(1, 1)));
    }

    @Test
    void touchedCellsHandlesNegativeCoordinates() {
        AreaClaim claim = AreaClaimHelper.areaClaim(
                -9, 0, -9,
                0, 10, 0
        );

        Iterable<Long> cells = SpatialGrid.touchedCells(claim.bounds());
        Set<Long> cellSet = AreaClaimHelper.toLongSet(cells);

        assertEquals(9, cellSet.size());

        assertTrue(cellSet.contains(SpatialGrid.cellKey(-2, -2)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(-1, -2)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(0, -2)));

        assertTrue(cellSet.contains(SpatialGrid.cellKey(-2, -1)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(-1, -1)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(0, -1)));

        assertTrue(cellSet.contains(SpatialGrid.cellKey(-2, 0)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(-1, 0)));
        assertTrue(cellSet.contains(SpatialGrid.cellKey(0, 0)));
    }

    @Test
    void everyBlockInsideBoundsMapsToTouchedCell() {
        AreaClaim claim = AreaClaimHelper.areaClaim(
                -20, 5, -20,
                20, 15, 20
        );

        Iterable<Long> cells = SpatialGrid.touchedCells(claim.bounds());
        Set<Long> cellSet = AreaClaimHelper.toLongSet(cells);

        for (int x = -20; x <= 20; x += 3) {
            for (int z = -20; z <= 20; z += 3) {
                BlockPosition p = new BlockPosition(x, 10, z);
                assertTrue(claim.contains(p));

                long cellKey = SpatialGrid.cellKeyFromBlock(x, z);
                assertTrue(
                        cellSet.contains(cellKey),
                        "Missing cell for block " + p
                );
            }
        }
    }

    @Test
    void intersectsFalseIfDifferentWorld() {
        AreaClaim a = AreaClaimHelper.areaClaim(UUID.randomUUID(), 0, 0, 0, 10, 10, 10);
        AreaClaim b = AreaClaimHelper.areaClaim(UUID.randomUUID(), 0, 0, 0, 10, 10, 10);

        assertFalse(a.intersects(b));
        assertFalse(b.intersects(a));
    }

    @Test
    void touchedCellsAlwaysCoverAllBlocksInsideBounds() {
        Random rnd = new Random(42);

        for (int i = 0; i < 500; i++) {
            int minX = rnd.nextInt(400) - 200;
            int minZ = rnd.nextInt(400) - 200;

            int maxX = minX + rnd.nextInt(200) + 1;
            int maxZ = minZ + rnd.nextInt(200) + 1;

            AreaClaim claim = AreaClaimHelper.areaClaim(
                    minX, 0, minZ,
                    maxX, 20, maxZ
            );

            Set<Long> touched = AreaClaimHelper.toLongSet(
                    SpatialGrid.touchedCells(claim.bounds())
            );

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPosition block = new BlockPosition(x, 5, z);

                    assertTrue(
                            touched.contains(
                                    SpatialGrid.cellKeyFromBlock(block)
                            ),
                            "Missing cell for block " + block
                    );
                }
            }
        }
    }
}
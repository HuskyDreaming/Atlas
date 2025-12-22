package com.huskydreaming.claims.model.position;

public record BoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

    public static BoundingBox of(BlockPosition a, BlockPosition b) {
        var minX = Math.min(a.x(), b.x());
        var minY = Math.min(a.y(), b.y());
        var minZ = Math.min(a.z(), b.z());

        var maxX = Math.max(a.x(), b.x());
        var maxY = Math.max(a.y(), b.y());
        var maxZ = Math.max(a.z(), b.z());

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean contains(BlockPosition pos) {
        return pos.x() >= minX && pos.x() <= maxX
                && pos.y() >= minY && pos.y() <= maxY
                && pos.z() >= minZ && pos.z() <= maxZ;
    }

    public boolean intersects(BoundingBox other) {
        return this.minX <= other.maxX && this.maxX >= other.minX
                && this.minY <= other.maxY && this.maxY >= other.minY
                && this.minZ <= other.maxZ && this.maxZ >= other.minZ;
    }
}
package com.huskydreaming.claims.model.positions;

public record BoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

    public BoundingBox {
        if (minX > maxX) throw new IllegalArgumentException("minX > maxX");
        if (minY > maxY) throw new IllegalArgumentException("minY > maxY");
        if (minZ > maxZ) throw new IllegalArgumentException("minZ > maxZ");
    }

    public static BoundingBox of(BlockPosition a, BlockPosition b) {
        int minX = Math.min(a.x(), b.x());
        int minY = Math.min(a.y(), b.y());
        int minZ = Math.min(a.z(), b.z());

        int maxX = Math.max(a.x(), b.x());
        int maxY = Math.max(a.y(), b.y());
        int maxZ = Math.max(a.z(), b.z());

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean contains(BlockPosition position) {
        return position.x() >= minX && position.x() <= maxX
                && position.y() >= minY && position.y() <= maxY
                && position.z() >= minZ && position.z() <= maxZ;
    }

    public boolean intersects(BoundingBox other) {
        return this.minX <= other.maxX && this.maxX >= other.minX
                && this.minY <= other.maxY && this.maxY >= other.minY
                && this.minZ <= other.maxZ && this.maxZ >= other.minZ;
    }
}
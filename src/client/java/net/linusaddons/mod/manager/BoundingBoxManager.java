package net.linusaddons.mod.manager;

import lombok.Getter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


public class BoundingBoxManager {

    @Getter
    private final Vec3 center;
    private final Vec3 axisX;
    private final Vec3 axisZ;
    private final double halfLength;
    private final double halfWidth;
    @Getter
    private final double minY;
    @Getter
    private final double maxY;
    @Getter
    private final boolean rotated;

    public BoundingBoxManager(Vec3 corner1, Vec3 corner2) {
        this.minY = Math.min(corner1.y, corner2.y);
        this.maxY = Math.max(corner1.y, corner2.y);
        this.center = corner1.add(corner2).scale(0.5);
        this.halfLength = Math.abs(corner2.x - corner1.x) / 2.0;
        this.halfWidth  = Math.abs(corner2.z - corner1.z) / 2.0;
        this.axisX = new Vec3(1, 0, 0);
        this.axisZ = new Vec3(0, 0, 1);
        this.rotated = false;
    }

    public Vec3[] getCorners() {
        Vec3 lx = axisX.scale(halfLength);
        Vec3 lz = axisZ.scale(halfWidth);

        double b0x = center.x - lx.x - lz.x, b0z = center.z - lx.z - lz.z;
        double b1x = center.x + lx.x - lz.x, b1z = center.z + lx.z - lz.z;
        double b2x = center.x + lx.x + lz.x, b2z = center.z + lx.z + lz.z;
        double b3x = center.x - lx.x + lz.x, b3z = center.z - lx.z + lz.z;

        return new Vec3[] {
                new Vec3(b0x, minY, b0z),  // bottom 0
                new Vec3(b1x, minY, b1z),  // bottom 1
                new Vec3(b2x, minY, b2z),  // bottom 2
                new Vec3(b3x, minY, b3z),  // bottom 3
                new Vec3(b0x, maxY, b0z),  // top 4
                new Vec3(b1x, maxY, b1z),  // top 5
                new Vec3(b2x, maxY, b2z),  // top 6
                new Vec3(b3x, maxY, b3z),  // top 7
        };
    }

    public BoundingBoxManager(Vec3 corner1, Vec3 corner2, double minY, double maxY) {
        this.minY = minY;
        this.maxY = maxY;
        this.center = new Vec3((corner1.x + corner2.x) / 2.0, (minY + maxY) / 2.0, (corner1.z + corner2.z) / 2.0);
        this.halfLength = Math.abs(corner2.x - corner1.x) / 2.0;
        this.halfWidth  = Math.abs(corner2.z - corner1.z) / 2.0;
        this.axisX = new Vec3(1, 0, 0);
        this.axisZ = new Vec3(0, 0, 1);
        this.rotated = false;
    }

    public BoundingBoxManager(Vec3 bottomRight, Vec3 topRight, Vec3 bottomLeft, Vec3 topLeft) {
        this(bottomRight, topRight, bottomLeft, topLeft,
                Math.min(Math.min(bottomRight.y, topRight.y), Math.min(bottomLeft.y, topLeft.y)) - 1,
                Math.max(Math.max(bottomRight.y, topRight.y), Math.max(bottomLeft.y, topLeft.y)) + 1);
    }

    public BoundingBoxManager(Vec3 bottomRight, Vec3 topRight, Vec3 bottomLeft, Vec3 topLeft, double minY, double maxY) {
        this.center = bottomRight.add(topRight).add(bottomLeft).add(topLeft).scale(0.25);
        this.minY = minY;
        this.maxY = maxY;

        Vec3 edgeLength = topRight.subtract(bottomRight);
        Vec3 edgeWidth  = bottomLeft.subtract(bottomRight);

        this.axisX = edgeLength.normalize();
        this.axisZ = edgeWidth.normalize();

        this.halfLength = edgeLength.length() / 2.0;
        this.halfWidth  = edgeWidth.length() / 2.0;
        this.rotated = true;
    }

    public boolean contains(Vec3 point) {
        return toAABB().contains(point);
    }

    public boolean containsPoint(Vec3 point) {
        if (point.y < minY || point.y > maxY) return false;
        Vec3 local = point.subtract(center);
        double projX = local.dot(axisX);
        double projZ = local.dot(axisZ);
        return Math.abs(projX) <= halfLength && Math.abs(projZ) <= halfWidth;
    }

    public AABB toAABB() {
        if (!rotated) {
            return new AABB(
                    center.x - halfLength, minY, center.z - halfWidth,
                    center.x + halfLength, maxY, center.z + halfWidth
            );
        }
        double halfDiag = Math.sqrt(halfLength * halfLength + halfWidth * halfWidth);
        return new AABB(
                center.x - halfDiag, minY, center.z - halfDiag,
                center.x + halfDiag, maxY, center.z + halfDiag
        );
    }

    public double getLength()   { return halfLength * 2; }
    public double getWidth()    { return halfWidth * 2; }
}
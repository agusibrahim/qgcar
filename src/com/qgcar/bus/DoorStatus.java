package com.qgcar.bus;

/**
 * Snapshot of door / hatch / lock states reported by
 * {@code onDoorStatusChanged}. Field order matches the on-wire parcel.
 */
public final class DoorStatus {
    public final DoorState bonnet;
    public final DoorState frontLeft;
    public final DoorState frontLeftLock;
    public final DoorState frontRight;
    public final DoorState frontRightLock;
    public final DoorState trunk;
    public final DoorState rearLeft;
    public final DoorState rearLeftLock;
    public final DoorState rearRight;
    public final DoorState rearRightLock;

    public DoorStatus(DoorState bonnet, DoorState fL, DoorState fLLock,
                      DoorState fR, DoorState fRLock,
                      DoorState trunk,
                      DoorState rL, DoorState rLLock,
                      DoorState rR, DoorState rRLock) {
        this.bonnet = bonnet;
        this.frontLeft = fL; this.frontLeftLock = fLLock;
        this.frontRight = fR; this.frontRightLock = fRLock;
        this.trunk = trunk;
        this.rearLeft = rL; this.rearLeftLock = rLLock;
        this.rearRight = rR; this.rearRightLock = rRLock;
    }

    public boolean anyDoorOpen() {
        return frontLeft == DoorState.OPEN || frontRight == DoorState.OPEN
            || rearLeft == DoorState.OPEN  || rearRight == DoorState.OPEN
            || bonnet == DoorState.OPEN || trunk == DoorState.OPEN;
    }

    public boolean allDoorsLocked() {
        // For locks, "OPEN" in vendor parlance means "unlocked" — that's how
        // the head-unit reports it, even though it's confusing. We surface
        // it as-is and let callers map to their own semantics if needed.
        return frontLeftLock == DoorState.CLOSED
            && frontRightLock == DoorState.CLOSED
            && rearLeftLock == DoorState.CLOSED
            && rearRightLock == DoorState.CLOSED;
    }

    @Override public String toString() {
        return "DoorStatus{bonnet=" + bonnet
            + " fL=" + frontLeft + "/lk=" + frontLeftLock
            + " fR=" + frontRight + "/lk=" + frontRightLock
            + " trunk=" + trunk
            + " rL=" + rearLeft + "/lk=" + rearLeftLock
            + " rR=" + rearRight + "/lk=" + rearRightLock + "}";
    }
}

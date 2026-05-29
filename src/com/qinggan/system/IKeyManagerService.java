package com.qinggan.system;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.qinggan.system.IKeyManagerCallback;

/* loaded from: classes2.dex */
public interface IKeyManagerService extends IInterface {
    void inputKeyEvent(int i, int i2) throws RemoteException;

    int registerInterceptListener(IBinder iBinder, IKeyManagerCallback iKeyManagerCallback, String str, int i, int i2) throws RemoteException;

    int registerKeyManagerListener(IBinder iBinder, IKeyManagerCallback iKeyManagerCallback, String str, int i, int i2) throws RemoteException;

    void setRestriction(boolean z, int[] iArr) throws RemoteException;

    int unregisterInterceptListener(IKeyManagerCallback iKeyManagerCallback, String str) throws RemoteException;

    int unregisterKeyManagerListener(IKeyManagerCallback iKeyManagerCallback, String str) throws RemoteException;

    public static abstract class Stub extends Binder implements IKeyManagerService {
        private static final String DESCRIPTOR = "com.qinggan.system.IKeyManagerService";
        static final int TRANSACTION_inputKeyEvent = 6;
        static final int TRANSACTION_registerInterceptListener = 3;
        static final int TRANSACTION_registerKeyManagerListener = 1;
        static final int TRANSACTION_setRestriction = 5;
        static final int TRANSACTION_unregisterInterceptListener = 4;
        static final int TRANSACTION_unregisterKeyManagerListener = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyManagerService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IKeyManagerService)) {
                return (IKeyManagerService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRegisterKeyManagerListener = registerKeyManagerListener(parcel.readStrongBinder(), IKeyManagerCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(iRegisterKeyManagerListener);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iUnregisterKeyManagerListener = unregisterKeyManagerListener(IKeyManagerCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iUnregisterKeyManagerListener);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRegisterInterceptListener = registerInterceptListener(parcel.readStrongBinder(), IKeyManagerCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(iRegisterInterceptListener);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iUnregisterInterceptListener = unregisterInterceptListener(IKeyManagerCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iUnregisterInterceptListener);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setRestriction(parcel.readInt() != 0, parcel.createIntArray());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    inputKeyEvent(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IKeyManagerService {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.qinggan.system.IKeyManagerService
            public int registerKeyManagerListener(IBinder iBinder, IKeyManagerCallback iKeyManagerCallback, String str, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeStrongBinder(iKeyManagerCallback != null ? iKeyManagerCallback.asBinder() : null);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.qinggan.system.IKeyManagerService
            public int unregisterKeyManagerListener(IKeyManagerCallback iKeyManagerCallback, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iKeyManagerCallback != null ? iKeyManagerCallback.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.qinggan.system.IKeyManagerService
            public int registerInterceptListener(IBinder iBinder, IKeyManagerCallback iKeyManagerCallback, String str, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeStrongBinder(iKeyManagerCallback != null ? iKeyManagerCallback.asBinder() : null);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.qinggan.system.IKeyManagerService
            public int unregisterInterceptListener(IKeyManagerCallback iKeyManagerCallback, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iKeyManagerCallback != null ? iKeyManagerCallback.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.qinggan.system.IKeyManagerService
            public void setRestriction(boolean z, int[] iArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeIntArray(iArr);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.qinggan.system.IKeyManagerService
            public void inputKeyEvent(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}

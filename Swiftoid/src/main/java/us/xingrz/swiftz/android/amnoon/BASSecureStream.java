package us.xingrz.swiftz.android.amnoon;

import android.support.annotation.NonNull;

import us.xingrz.common.SecureStream;

import java.io.InputStream;
import java.io.OutputStream;

public class BASSecureStream extends SecureStream {

    public BASSecureStream(InputStream rawInputStream, OutputStream rawOutputStream) {
        super(rawInputStream, rawOutputStream);
    }

    @Override
    protected void encrypt(@NonNull byte[] buffer, int offset, int count) {
        for (int i = offset; i < offset + count; i++) {
            buffer[i] = (byte) (0xff
                    & (buffer[i] & BIT_1) >> 6
                    | (buffer[i] & BIT_2) >> 4
                    | (buffer[i] & BIT_3) >> 2
                    | (buffer[i] & BIT_4) << 2
                    | (buffer[i] & BIT_5) << 2
                    | (buffer[i] & BIT_6) << 2
                    | (buffer[i] & BIT_7) >> 1
                    | (buffer[i] & BIT_8) << 7
            );
        }
    }

    @Override
    protected void decrypt(@NonNull byte[] buffer, int byteOffset, int byteCount) {
        for (int i = byteOffset; i < byteOffset + byteCount; i++) {
            buffer[i] = (byte) (0xff
                    & (buffer[i] & BIT_1) >> 7
                    | (buffer[i] & BIT_2) >> 2
                    | (buffer[i] & BIT_3) >> 2
                    | (buffer[i] & BIT_4) >> 2
                    | (buffer[i] & BIT_5) << 2
                    | (buffer[i] & BIT_6) << 4
                    | (buffer[i] & BIT_7) << 6
                    | (buffer[i] & BIT_8) << 1
            );
        }
    }

}

package com.DFM.StormFront.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Mick on 2/3/2016.
 */
public class SerializationUtil {
    public static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] buffer = baos.toByteArray();
        oos.close();
        baos.close();
        return buffer;
    }

    public static Object deserialize(byte[] buffer) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        ois.close();
        bais.close();
        return obj;
    }
}

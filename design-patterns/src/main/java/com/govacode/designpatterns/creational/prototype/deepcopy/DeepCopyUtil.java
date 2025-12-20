package com.govacode.designpatterns.creational.prototype.deepcopy;

import java.io.*;

/**
 * 深拷贝工具类（通过JDK序列化与反序列化实现）
 *
 * @author gova
 */
public class DeepCopyUtil {

    public static <T extends Serializable> T copy(final T original) throws IOException, ClassNotFoundException {
        if (original == null) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(original);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                return (T) ois.readObject();
            }
        }
    }
}

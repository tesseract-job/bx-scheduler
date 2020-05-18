package org.bx.scheduler.common.serializer;

public interface ISerializer {
    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}

package org.bx.scheduler.common.serializer;

import com.alibaba.fastjson.JSON;

public class JSONSerializer implements ISerializer {
    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return JSON.parse(bytes);
    }
}

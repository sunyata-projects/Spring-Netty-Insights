package org.sunyata.quark.client.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leo on 17/5/10.
 */
public enum MessageInfoType {
    CreateBusinessComponent(0),
    RunBySerialNo(1);


    public int getValue() {
        return value;
    }

    private int value = 0;

    MessageInfoType(int value) {
        this.value = value;
    }

    // Mapping difficulty to difficulty id
    private static final Map<Integer, MessageInfoType> _map = new HashMap<Integer, MessageInfoType>();

    static {
        for (MessageInfoType difficulty : MessageInfoType.values())
            _map.put(difficulty.value, difficulty);
    }

    /**
     * Get difficulty from value
     *
     * @param value Value
     * @return Difficulty
     */
    public static MessageInfoType from(int value) {
        return _map.get(value);
    }
}

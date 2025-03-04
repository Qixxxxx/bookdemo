package enums;


import java.util.HashMap;
import java.util.Map;

public enum AppointStateEnum {

    INNER_ERROR(-2, "系统异常"),
    REPEAT_APPOINT(-1, "重复预约"),
    NO_NUMBER(0, "库存不足"),
    SUCCESS(1, "预约成功"),
    ;


    AppointStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static AppointStateEnum fromState(int state) {
        return map.get(state);
    }

    public int getState() {
        return this.state;
    }

    public String getStateInfo() {
        return this.stateInfo;
    }

    private final int state;
    private final String stateInfo;
    private static final Map<Integer, AppointStateEnum> map = new HashMap<>();


    static {
        try {
            for (AppointStateEnum item : values()) {
                map.put(item.state, item);
            }
        } catch (Exception e) {

        }
    }

}


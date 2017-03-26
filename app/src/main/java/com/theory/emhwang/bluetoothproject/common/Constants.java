package com.theory.emhwang.bluetoothproject.common;

/**
 * Created by hwangem on 2017-03-21.
 */

public class Constants {

    /**
     * 블루투스 활성화 상태 ON/OFF
     */
    public static final int REQUEST_ENABLE_BT = 100;

    /**
     * 연결한 기기로부터 값을 받기 위해
     */
    public static final int REQUEST_CONNECT_DEVICE = 200;

    /**
     * 새로운 기기 검색 결과
     */
    public static final String NEW_DEVICE = "newDevice";

    /**
     * 검색 성공한 기기 정보(이름)
     */
    public static final String NEW_DEVICE_INFO_NAME = "newDeviceName";

    /**
     * 검색 성공한 기기 정보(Mac 어드레스)
     */
    public static final String NEW_DEVICE_INFO_ADDRESS = "newDeviceAddress";

    /**
     * 결과 상수
     */
    public final static class RESULT_CODE {

        //성공
        public static final String RESULT_TRUE = "true";

        // 실패
        public static final String RESULT_FALSE = "false";

    }
}

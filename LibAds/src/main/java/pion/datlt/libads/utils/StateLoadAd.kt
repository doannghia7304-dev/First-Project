package pion.datlt.libads.utils

/**
 * các trạng thái của quảng cáo.
 */
enum class StateLoadAd {

    /**
     * quảng cáo đang được tải.
     */
    LOADING,

    /**
     * quảng cáo được tải thành công, chỉ chờ show lên.
     */
    SUCCESS,

    /**
     * quảng cáo được tải thất bại.
     */
    LOAD_FAILED,

    /**
     * quảng cáo được show thất bại.
     */
    SHOW_FAILED,

    /**
     * quảng cáo đã được show lên.
     */
    HAS_BEEN_OPENED,

    /**
     * trạng thái được trả về nếu không tìm thấy ID của quảng cáo.
     */
    NONE,

    /**
     * quảng cáo chưa được tải cũng chưa được init.
     */
    NULL
}
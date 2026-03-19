package pion.datlt.libads.callback

/**
 * callback khi gọi hàm load trước quảng cáo.
 */
interface PreloadCallback {

    /**
     * gọi khi quảng cáo được load thành công.
     */
    fun onLoadDone()

    /**
     * gọi khi quảng cáo load failed.
     *
     * @param error: lỗi khiến quảng cáo bị load failed.
     */
    fun onLoadFail(error: String) {}
}
/**
 *
 * 日    期：12-2-22
 */

package com.carit.platform.response;

/**
 * <pre>
 * 通用的响应对象
 * </pre>
 * 
 * @author 陈雄华
 * @version 1.0
 */
public class CommonRopResponse {

    private boolean successful = false;

    public static final CommonRopResponse SUCCESSFUL_RESPONSE = new CommonRopResponse(true);

    public static final CommonRopResponse FAILURE_RESPONSE = new CommonRopResponse(false);

    public CommonRopResponse() {
    }

    private CommonRopResponse(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}

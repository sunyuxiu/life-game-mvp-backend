package com.lifegame.mvp.common;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> CASHIER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> MERCHANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_TYPE = new ThreadLocal<>();

    public static void setUserId(Long userId) { USER_ID.set(userId); }
    public static Long getUserId() { return USER_ID.get(); }

    public static void setCashierId(Long cashierId) { CASHIER_ID.set(cashierId); }
    public static Long getCashierId() { return CASHIER_ID.get(); }

    public static void setMerchantId(Long merchantId) { MERCHANT_ID.set(merchantId); }
    public static Long getMerchantId() { return MERCHANT_ID.get(); }

    public static void setTokenType(String type) { TOKEN_TYPE.set(type); }
    public static String getTokenType() { return TOKEN_TYPE.get(); }

    public static void clear() {
        USER_ID.remove();
        CASHIER_ID.remove();
        MERCHANT_ID.remove();
        TOKEN_TYPE.remove();
    }
}

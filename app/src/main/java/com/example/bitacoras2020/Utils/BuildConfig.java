package com.example.bitacoras2020.Utils;


@SuppressWarnings("FieldCanBeLocal")
public final class BuildConfig {

    public static final String APPLICATION_ID = "";
    private static Branches targetBranch = Branches.GUADALAJARA;
    private static boolean isForSplunkMintMonitoring = true;//!Build.MODEL.equalsIgnoreCase("F600");
    private static boolean autoPrintingEnable = false;
    private static boolean isAutoLoginEnable = false;
    private static boolean printingCopiesEnabled = false;
    private static boolean connectivityTestWithPingEnabled = true;
    private static boolean walletSynchronizationEnabled = true;
    /*
	 this flag is intended to be used as a check for debug-only functions.
	 set to false, when not debugging anymore
	 */
    public static boolean DEBUG = true;

    @SuppressWarnings("SpellCheckingInspection")
    public static enum Branches{GUADALAJARA}

    private BuildConfig(){}

    /**
     * retrieves target branch of application
     *
     * @return
     *      enum data type
     */
    public static Branches getTargetBranch(){
        return targetBranch;
    }

    /**
     * retrieves isForSplunkMintMonitoring flag, telling if it's enabled
     *
     * @return
     *      boolean data type
     */
    public static boolean isIsForSplunkMintMonitoring() {
        return isForSplunkMintMonitoring;
    }

    /**
     * retrieves autoPrintingEnable flag, telling if it's enabled
     *
     * @return
     *      boolean data type
     */
    public static boolean isAutoPrintingEnable() {
        return autoPrintingEnable;
    }

    /**
     * retrieves isAutoLoginEnable flag, telling if it's enabled
     *
     * @return
     *      boolean data type
     */
    public static boolean isAutoLoginEnable() {
        return isAutoLoginEnable;
    }

    /**
     * retrieves isPrintingCopiesEnabled flag, telling if it's enabled
     *
     * @return
     *      boolean data type
     */
    public static boolean isPrintingCopiesEnabled(){
        return printingCopiesEnabled;
    }

    /**
     * retrieves connectivityTestWithPingEnabled flag, telling if it's enabled
     *
     * @return
     *      boolean data type
     */
    public static boolean isConnectivityTestWithPingEnabled(){
        return connectivityTestWithPingEnabled;
    }

    public static boolean isWalletSynchronizationEnabled(){
        return walletSynchronizationEnabled;
    }

    public static void setWalletSynchronizationEnabled(boolean flag){
        walletSynchronizationEnabled = flag;
    }

    public static void setIsForSplunkMintMonitoring(boolean flag){
        isForSplunkMintMonitoring = flag;
    }
}

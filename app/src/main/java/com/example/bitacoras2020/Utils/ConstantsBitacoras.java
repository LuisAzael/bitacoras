package com.example.bitacoras2020.Utils;

import java.util.Date;

public class ConstantsBitacoras extends BaseActivity{

    /***Vieja instancia de servidor***/
    //public static final String baseURL = "http://bitacoras.latinofuneral.com/public";

    /**Nueva Instancia de servidor*/
    public static final String baseURL = "http://3.208.145.41/public";
                                        //http://3.208.145.41/ebita-gdl/public/ws/loginApp


    //** CATALOGOS DE DESCARGA NORMAL **//
    public static final String WS_DRIVERS_URL= getBaseURL() +"/drivers";
    public static final String WS_PLACES_URL= getBaseURL() +"/places";
    public static final String WS_NOTIFICATIONS_URL= getBaseURL() +"/getNotifications";
    public static final String WS_VEHICLES_URL= getBaseURL() +"/vehicles";
    public static final String WS_BITACORA_DETAILS= getBaseURL() +"/getCatalogoBitacoraDetalles";
    public static final String WS_BINNACLE_URL= getBaseURL() +"/getCatalogoBitacoras";
    public static final String WS_DOWNLOAD_BITACORAS_COMPLETAS= getBaseURL() +"/getCatalogoBitacorasPreconfiguradas";
    public static final String WS_DOWNLOAD_BITACORA_INDIVIDUAL= getBaseURL() +"/getSingleBitacora";
    public static final String WS_DOWNLOAD_ARTICULOS_CATALOGO= getBaseURL() +"/getCatalogoArticulos";


    public static final String WS_CONFIGURATION= getBaseURL() +"/getConfig";
    public static final String WS_CHECK_STATUS_BINNACLE= getBaseURL() +"/getAuthorizationToEndBitacora";
    public static final String WS_LOGIN= getBaseURL() +"/loginApp";
    public static final String WS_CHECKINCHECKOUT_NEW= getBaseURL() +"/doCheckinAndCheckout";

    /***********  OLD UPDATE VERSION *************/
    //public static final String URL_CHECK_VERSION= "http://bitacoras.latinofuneral.com/public/update/version.txt";
    //public static final String URL_DOWNLOAD_APK=  "http://bitacoras.latinofuneral.com/public/update/";

    /***** NEW PROCESSS UPDATE ***/
    public static final String URL_CHECK_VERSION= "http://3.208.145.41/public/update/version.txt";
    public static final String URL_DOWNLOAD_APK=  "http://3.208.145.41/public/update/";





    //** GUARDAR MOVIEMIENTOS REGISTRADOS **//
    public static final String WS_URL= getBaseURL() +"/storeBitacora";
    public static final String WS_ASSETS_URL= getBaseURL() +"/inventorys";
    public static final String WS_LOCATION_URL= getBaseURL() +"/storePosition";
    public static final String URL_SAVE_MOVEMENTS= getBaseURL() +"/saveMovements";
    public static final String WS_CHECKIN_AND_CHECKOUT= getBaseURL() +"/storeCheckiAndCheckout";
    public static final String WS_SAVE_INFORMATION_ADICIONAL_POR_BITACORA = getBaseURL() +"/storeBitacoraDetails";
    public static final String WS_SAVE_ATAURNAS = getBaseURL() +"/storeUrnaAndAtaud";
    public static final String WS_SAVE_ARTICLES_INVENTORY = getBaseURL() +"/aaaaaaaaaaaaaaaaaaaa";
    public static final String WS_SAVE_COMENTARIOS = getBaseURL() +"/storeComments";
    public static final String WS_GET_COMENTARIOS = getBaseURL() +"/getComments";


    private static String getBaseURL()
    {
        return baseURL + "/ws";
    }


}

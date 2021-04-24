package com.example.bitacoras2020.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.bitacoras2020.Adapters.AdapterEquiposInstalacion;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.Preferences;
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseAssistant
{
    /*public static void insertarMovimientos(String idchofer, String idayudante, String androidid, String appversion, String androidmodel, String imei, String fecha)
    {
        com.messoft.bitacoras.database.Movimientos movimientos = new com.messoft.bitacoras.database.Movimientos(idchofer, idayudante, androidid, appversion, androidmodel, imei, fecha);
        movimientos.save();
    }*/

    public static final String TAG="DATABASE_ASSITANT";

    public static void resetDatabase( Context context ) {
        SugarContext.terminate();
        SchemaGenerator schemaGenerator = new SchemaGenerator( context );
        schemaGenerator.deleteTables(new SugarDb( context ).getDB());
        SugarContext.init( context );
        schemaGenerator.createDatabase(new SugarDb( context ).getDB());
    }







    // ** INSERCION DE DATOS **//
    public static void insertarBitacoras(String idserver, String bitacorass, String nombre,
                                         String domicilio, String telefono, String chofer,
                                         String ayudante, String carro, String origen,
                                         String destino, String destinoDomicilio, String latitud, String longitud,
                                         String ataud, String panteon, String crematorio, String inicioVelacion, String inicioCortejo, String templo){
        Bitacoras bitacoras = new Bitacoras(idserver, bitacorass, nombre, domicilio, telefono, chofer,
                ayudante, carro, origen, destino, "0", destinoDomicilio, latitud, longitud, ataud, panteon, crematorio, inicioVelacion, inicioCortejo, templo);
        bitacoras.save();
    }


    public static void insertarCodigosDeBarras(String bitacora, String fecha, String hora, String codigoBarras){
        Codigos codigos = new Codigos(bitacora, fecha, hora, codigoBarras);
        codigos.save();
    }

    public static void insertarComentarios(String bitacora, String comentario, String usuario, String fecha, String sync, String pormi){
        Comentarios comentarios = new Comentarios(bitacora, comentario, usuario, fecha, sync, pormi);
        comentarios.save();
    }

    public static void insertarInformacionAdicional(String horaRecoleccion, String ropaEntregada, String lugarDeVelacion,
                                                    String tipoDeServicio, String noEquipoVelacionInstalacion, String observacionesInstalacion,
                                                    String noEquipoVelacionCortejo, String observacionesCortejo, String jsonAdicionalInfo, String bitacora, String encapsulado,
                                                    String observacionesRecoleccion, String observacionesTraslado, String procedimiento,
                                                    String laboratorio, String idLaboratorio){


        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Adicional adicionalinfo = new Adicional(
                horaRecoleccion,
                ropaEntregada,
                lugarDeVelacion,
                tipoDeServicio,
                noEquipoVelacionInstalacion,
                observacionesInstalacion,
                noEquipoVelacionCortejo,
                observacionesCortejo,
                jsonAdicionalInfo,
                bitacora,
                "" + dateFormat.format(new Date()),
                "0",
                encapsulado,
                observacionesRecoleccion,
                observacionesTraslado,
                procedimiento,
                laboratorio,
                idLaboratorio
        );
        adicionalinfo.save();
    }


    public static void insertarDocumentoExtra(String bitacora, String documento)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Documentos documentos = new Documentos(
                "" + bitacora,
                "" + documento,
                "" + dateFormat.format(new Date()),
                "0"
        );
        documentos.save();
    }

    public static void insertarDocumentoExtraFromWebService(String bitacora, String documento)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Documentos documentos = new Documentos(
                "" + bitacora,
                "" + documento,
                "" + dateFormat.format(new Date()),
                "1"
        );
        documentos.save();
    }


    public static void insertarArticulosEscaneados(String nombre, String serie, String bitacora) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Articuloscan articuloscan = new Articuloscan(
                "" + nombre,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "0",
                bitacora
        );
        articuloscan.save();
    }

    public static void insertarArticuloCancelado(String codigo, String descripcion, String serie, String fecha, String proveedor, String bitacora) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cancelados cancelados = new Cancelados(
                "" + codigo,
                "" + descripcion,
                "" + serie,
                "" + fecha,
                "" + proveedor,
                "" + bitacora,
                "0",
                "" + dateFormat.format(new Date())
        );
        cancelados.save();
    }

    public static void insertarEquipoInstalacion(String bitacora, String serie, String nombre, String isBunker)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
        Equipoinstalacion documentos = new Equipoinstalacion(
                "" + bitacora,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "0",
                "" + nombre,
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1],
                isBunker
        );
        documentos.save();
    }


    public static void insertarEquipoInstalacionFromWebService(String bitacora, String serie, String nombre, String isBunker)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

        Equipoinstalacion documentos = new Equipoinstalacion(
                "" + bitacora,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "1",
                "" + nombre,
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1],
                isBunker
        );
        documentos.save();
    }


    public static void insertarEquipoCortejo(String bitacora, String serie, String nombre, String isBunker)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
        Equipocortejo documentos = new Equipocortejo(
                "" + bitacora,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "0",
                "" + nombre,
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1],
                ""+ isBunker
        );
        documentos.save();
    }


    public static void insertarEquipoTraslado(String bitacora, String serie, String nombre, String tipo, String isBunker)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
        EquipoTraslado documentos = new EquipoTraslado(
                "" + bitacora,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "0",
                "" + nombre,
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1],
                "" + tipo,
                "" +  isBunker
        );
        documentos.save();
    }

    public static void insertarEquipoRecoleccion(String bitacora, String serie, String nombre, String tipo, String isBunker)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
        EquipoRecoleccion documentos = new EquipoRecoleccion(
                "" + bitacora,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "0",
                "" + nombre,
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1],
                "" + tipo,
                ""+ isBunker
        );
        documentos.save();
    }

    public static void insertarEquipoCortejoFromWebservice(String bitacora, String serie, String nombre, String isBunker)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
        Equipocortejo documentos = new Equipocortejo(
                "" + bitacora,
                "" + serie,
                "" + dateFormat.format(new Date()),
                "1",
                "" + nombre,
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1],
                "" + isBunker
        );
        documentos.save();
    }

    public static void insertarNotificacion(String titulo, String body, String action, String bitacora, String fecha){
        Notificaciones notificaciones = new Notificaciones(titulo, body, action, bitacora, fecha);
        notificaciones.save();
    }

    public static void insertarCarrosas(String nombre, String status, String id){
        Carros carros = new Carros(nombre, status, id);
        carros.save();
    }

    public static void insertarLugares(String nombre, String status, String latitud, String longitud, String perimetro, String id){
        Lugares lugares = new Lugares(nombre, status, latitud, longitud, perimetro, id);
        lugares.save();
    }

    public static void insertarLoginZone(String start_session_blocked, String start_session_place, String start_session_lat, String start_session_lng,
                                         String end_session_place, String end_session_lat, String end_session_lng, String end_session_blocked){
        LoginZone loginZone = new LoginZone(start_session_blocked, start_session_place, start_session_lat, start_session_lng, end_session_place, end_session_lat, end_session_lng, end_session_blocked);
        loginZone.save();
    }

    public static void insertarChoferes(String nombre, String status, String id){
        Choferes choferes = new Choferes(nombre, status, id);
        choferes.save();
    }


    public static void insertarMovimientos(String idchofer, String idayudante, String androidid, String appversion, String androidmodel, String imei, String fecha, String movimiento)
    {
        Movimientos movimientos = new Movimientos(idchofer, idayudante, androidid, appversion, androidmodel, imei, fecha, "0", movimiento, getUserNameFromSesiones());
        movimientos.save();
    }


    public static void insertarEventos(String bitacora, String chofer, String ayudante, String carro,
                                       String lugar, String fecha, String hora, String dispositivo,
                                       String latitud, String longitud, String estatus, String tipo,
                                       String nombre, String domicilio, String telefonos, String destino, String automatico, String movimiento)
    {
        Eventos eventos = new Eventos(bitacora, chofer, ayudante, carro, lugar, fecha, hora, dispositivo,
                latitud, longitud, estatus,tipo, nombre, domicilio, telefonos, destino, automatico, movimiento, getUserNameFromSesiones(), didTheDriverCheckIn(chofer, ayudante));
        eventos.save();
        Activos.executeQuery("UPDATE ACTIVOS set lugar = '" + lugar + "' where bitacora ='" + bitacora + "'");
        updateBitacoraParaNoVolverAUtilizar(bitacora);
    }

    public static void insertarUbicaciones(String latitud, String longitud, String fecha, String hora){
        Ubicaciones ubicaciones = new Ubicaciones(latitud, longitud, fecha, hora);
        ubicaciones.save();
    }

    public static void insertarInventario(String codigo, String descripcion, String serie, String fecha, String proveedor, String bitacora, String borrado){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

        Inventario inventario = new Inventario(codigo, descripcion, serie, fecha,
                proveedor, "0", bitacora, borrado,
                dateFormat.format(new Date()),
                "" + arregloCoordenadas[0],
                "" + arregloCoordenadas[1]);
        inventario.save();
    }

    public static void insertarToken(String token){
        Usuario.deleteAll(Usuario.class);
        Usuario usuario = new Usuario(token);
        usuario.save();
    }

    public static void insertarActivos(String bitacora, String chofer, String ayudante, String carro, String lugar,
                                       String fecha, String hora, String dispositivo, String latitud,
                                       String longitud, String estatus, String tipo, String nombre,
                                       String domicilio, String telefonos, String codigobarras, String destino, String activo, String destinodomicilio,
                                       String destinolatitud, String destinolongitud, String movimiento){
        Activos activos = new Activos( bitacora, chofer, ayudante, carro, lugar, fecha, hora, dispositivo, latitud, longitud, estatus, tipo, nombre,
                domicilio, telefonos, codigobarras, destino, activo, destinodomicilio, destinolatitud, destinolongitud, movimiento);
        activos.save();
    }

    public static void insertarSesiones(String codigo, String contrasena, String fecha, String latitud, String longitud, String estatus,
                                        String hora, String sync, String isBunker, String isProveedor, String geofence){
        Sesiones sesiones = new Sesiones(codigo, contrasena, fecha, latitud, longitud, estatus, hora, sync, isBunker, isProveedor, geofence);
        sesiones.save();
    }

    public static void insertarAsistencia(String codigo, String contrasena, String fecha, String latitud, String longitud, String estatus,
                                        String hora, String sync, String isBunker, String isProveedor, String geofence, String horaSeleccionada, String horaEntrada, String horaSalida){
        Asistencia asistencia= new Asistencia(codigo, contrasena, fecha, latitud, longitud, estatus, hora, sync, isBunker, isProveedor, geofence, horaSeleccionada, horaEntrada, horaSalida);
        asistencia.save();
    }


    public static void insertarLaboratorios(String nombre, String estatus, String idLaboratorio){

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Laboratorios laboratorios = new Laboratorios(nombre, estatus, dateFormat.format(new Date()), idLaboratorio);
        laboratorios.save();
    }




















    //** CONSULTAS DE DATOS **//
    public static ArrayList<String> getBitacoraName(String parametro)
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        try {
            String query2 = "SELECT * FROM BITACORAS where bitacoras like '%" + parametro + "%' limit 10";
            List<Bitacoras> bitacoras = Bitacoras.findWithQuery(Bitacoras.class, query2);
            if (bitacoras.size() > 0) {
                for (int i = 0; i < bitacoras.size(); i++) {
                    try {
                        BITACORA.add(bitacoras.get(i).getBitacoras());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
            }
        }catch (Throwable e){
            Log.e(TAG, "getBitacoraName: Error en buscar bitacora para hacer nueva: " + e.getMessage() );
        }
        return BITACORA;
    }

    public static ArrayList<String> getChoferes(String parametro)
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        String query2="SELECT * FROM CHOFERES where nombre like '%" + parametro + "%' limit 10";
        List<Choferes> bitacoras = Choferes.findWithQuery(Choferes.class, query2);
        if(bitacoras.size()>0)
        {
            for (int i = 0; i < bitacoras.size(); i++) {
                try {
                    BITACORA.add(bitacoras.get(i).getNombre());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return BITACORA;
    }

    public static ArrayList<String> getCarrosas(String parametro)
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        String query2="SELECT * FROM CARROS where nombre like '%" + parametro + "%' limit 10";
        List<Carros> bitacoras = Carros.findWithQuery(Carros.class, query2);
        if(bitacoras.size()>0)
        {
            for (int i = 0; i < bitacoras.size(); i++) {
                try {
                    BITACORA.add(bitacoras.get(i).getNombre());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return BITACORA;
    }

    public static ArrayList<String> getLugares()
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        String query2="SELECT * FROM LUGARES WHERE status = 'Activo'";
        List<Lugares> bitacoras = Lugares.findWithQuery(Lugares.class, query2);
        if(bitacoras.size()>0)
        {
            for (int i = 0; i < bitacoras.size(); i++) {
                try {
                    BITACORA.add(bitacoras.get(i).getNombre());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return BITACORA;
    }

    public static ArrayList<String> getLaboratorios()
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        String query2="SELECT * FROM LABORATORIOS WHERE estatus = 'Activo'";
        List<Laboratorios> bitacoras = Laboratorios.findWithQuery(Laboratorios.class, query2);
        if(bitacoras.size()>0)
        {
            BITACORA.add("Selecciona una opción...");
            for (int i = 0; i < bitacoras.size(); i++) {
                try {
                    BITACORA.add(bitacoras.get(i).getNombre());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return BITACORA;
    }


    public static ArrayList<String> pedirLugaresDeOrigenYDestino()
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        String query2="SELECT * FROM LUGARES WHERE status = 'Activo'";
        List<Lugares> bitacoras = Lugares.findWithQuery(Lugares.class, query2);
        if(bitacoras.size()>0)
        {
            BITACORA.add("**** FALTA DATO AQUÍ ****");
            for (int i = 0; i < bitacoras.size(); i++) {
                try {
                    BITACORA.add(bitacoras.get(i).getNombre());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return BITACORA;
    }


    public static ArrayList<String> getAllChoferes()
    {
        ArrayList<String> BITACORA = new ArrayList<String>();
        String query2="SELECT * FROM CHOFERES";
        List<Choferes> bitacoras = Choferes.findWithQuery(Choferes.class, query2);
        if(bitacoras.size()>0)
        {
            BITACORA.add("Selecciona tu nombre...");
            for (int i = 0; i < bitacoras.size(); i++) {
                try {
                    BITACORA.add(bitacoras.get(i).getNombre());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return BITACORA;
    }

    public static String getNameBitacora(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '" + bitacora + "'");
        if (lista.size() > 0)
            return lista.get(0).getNombre();
        return "";
    }


    public static boolean iniciarSesionSoloEnBunker() {
        List<LoginZone> lista = LoginZone.findWithQuery(LoginZone.class, "SELECT * FROM LOGIN_ZONE");
        return lista.get(0).getStart_session_blocked().equals("1");
    }

    public static boolean cerrarSesionEnUnLugarEspecifico(){
        List<LoginZone> lista = LoginZone.findWithQuery(LoginZone.class, "SELECT * FROM LOGIN_ZONE");
        return lista.get(0).getEnd_session_blocked().equals("1");
    }

    public static String[] getCoordinatesToDoGeofenceAndCloseSesion(){
        String [] sessionData = null;
        List<LoginZone> lista = LoginZone.findWithQuery(LoginZone.class, "SELECT * FROM LOGIN_ZONE");
        if(lista.size() > 0){
            sessionData = new String[3];
            sessionData[0]=lista.get(0).getEnd_session_lat();
            sessionData[1]=lista.get(0).getEnd_session_lng();
            sessionData[2]=lista.get(0).getEnd_session_place();
        }
        return sessionData;
    }


    public static String getLastestTimeSesion() {
        List<Sesiones> lista = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getFecha() + "  a las  " + lista.get(0).getHora() : "";
    }


    public static String getLastHoraAsistencia() {
        List<Asistencia> lista = Asistencia.findWithQuery(Asistencia.class, "SELECT * FROM ASISTENCIA ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getHoraSeleccionada(): "";
    }

    public static String getLastHoraEntrada() {
        List<Asistencia> lista = Asistencia.findWithQuery(Asistencia.class, "SELECT * FROM ASISTENCIA ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getHoraEntrada(): "";
    }

    public static String getLastHoraSalida() {
        List<Asistencia> lista = Asistencia.findWithQuery(Asistencia.class, "SELECT * FROM ASISTENCIA ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getHoraSalida(): "";
    }

    public static String getLastTipoAsistencia() {
        List<Asistencia> lista = Asistencia.findWithQuery(Asistencia.class, "SELECT * FROM ASISTENCIA ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getEstatus(): "";
    }

    public static String getUserNameFromSesiones() {
        List<Sesiones> lista = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getUsuario() : "";
    }

    public  static String[] getLastedDataFromSessions() {
        String [] sessionData = null;
        List<Sesiones> lista = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES ORDER BY id DESC LIMIT 1");
        if(lista.size() > 0){
            sessionData = new String[6];
            sessionData[0]=lista.get(0).getUsuario();
            sessionData[1]=lista.get(0).getContrasena();
            sessionData[2]=lista.get(0).getFecha();
            sessionData[3]=lista.get(0).getLatitud();
            sessionData[4]=lista.get(0).getLongitud();
            sessionData[5]=lista.get(0).getEstatus();
        }
        return sessionData;
    }



    public  static String[] getOrigenAndDestinoFromBitacora(String bitacora) {
        String [] sessionData = null;
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '" + bitacora + "'");
        if(lista.size() > 0){
            sessionData = new String[4];
            sessionData[0]=lista.get(0).getDestinodomicilio();
            //sessionData[0]="Costco Wholesale";
            sessionData[1]=lista.get(0).getDestinolatitud().equals("") ? "0" : lista.get(0).getDestinolatitud();
            //sessionData[1]= "20.6726574";
            sessionData[2]=lista.get(0).getDestinolongitud().equals("") ? "0" : lista.get(0).getDestinolongitud();
            //sessionData[2]="-103.4099441";
            sessionData[3]=lista.get(0).getDestino();
        }
        return sessionData;
    }



    public static String getDomicilioBitacora(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '" + bitacora + "'");
        if (lista.size() > 0)
            return lista.get(0).getDomicilio();
        return "";
    }

    public static String getTelefonoBitacora(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '" + bitacora + "'");
        if (lista.size() > 0)
            return lista.get(0).getTelefono();
        return "";
    }

    public static boolean laBitacoraEstaActiva(String bitacora) {
        List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM ACTIVOS where bitacora='"+ bitacora +"' and  activo = '1'");
        return lista.size()>0;
    }

    public static String getBitacoraActiva(String bitacora) {
        List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM ACTIVOS where bitacora='"+ bitacora +"' and activo = '1'");
        return lista.size() > 0 ? lista.get(0).getBitacora() : "NOT FOUND" ;
    }

    public static String getCodigoDeBarrasPorBitacora(String bitacora) {
        String codigoConcatenado="";
        List<Codigos> lista = Codigos.findWithQuery(Codigos.class, "SELECT * FROM CODIGOS where bitacora='"+ bitacora +"'");
        if(lista.size()>0) {
            for(int i = 0; i <= lista.size()-1; i++){
                codigoConcatenado =  codigoConcatenado + lista.get(i).getCodigobarras() + "\n";
            }
        }
        return codigoConcatenado;
    }

    public static String[] getDatosDeBitacoraActiva(String bitacora) {
        String [] datosBitacora = new String[13];
        List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM ACTIVOS WHERE bitacora='"+ bitacora +"' and activo = '1'");
        if (lista.size() > 0)
        {
            datosBitacora[0]=lista.get(0).getBitacora();
            datosBitacora[1]=lista.get(0).getChofer();
            datosBitacora[2]=lista.get(0).getAyudante();
            datosBitacora[3]=lista.get(0).getCarro();
            datosBitacora[4]=lista.get(0).getDispositivo();
            datosBitacora[5]=lista.get(0).getEstatus();
            datosBitacora[6]=lista.get(0).getNombre();
            datosBitacora[7]=lista.get(0).getDomicilio();
            datosBitacora[8]=lista.get(0).getTelefonos();
            datosBitacora[9]=lista.get(0).getCodigobarras();
            datosBitacora[10]=lista.get(0).getDestino();
            datosBitacora[11]=lista.get(0).getLugar();
            datosBitacora[12]=lista.get(0).getMovimiento();
        }
        return datosBitacora;
    }

    public static String[] getDataBitacora(String bitacora)
    {
        String [] datosBitacora = null;
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"'");
        if (lista.size() > 0) {
            datosBitacora = new String[17];
            datosBitacora[0]=lista.get(0).getIdserver();
            datosBitacora[1]=lista.get(0).getBitacoras();
            datosBitacora[2]=lista.get(0).getNombre();
            datosBitacora[3]=lista.get(0).getChofer();
            datosBitacora[4]=lista.get(0).getAyudante();
            datosBitacora[5]=lista.get(0).getCarro();
            datosBitacora[6]=lista.get(0).getOrigen();
            datosBitacora[7]=lista.get(0).getDestino();
            datosBitacora[8]=lista.get(0).getDomicilio();
            datosBitacora[9]=lista.get(0).getTelefono();
            datosBitacora[10]=lista.get(0).getStatus();
            datosBitacora[11]=lista.get(0).getDestinodomicilio();
            datosBitacora[12]=lista.get(0).getDestinolatitud();
            datosBitacora[13]=lista.get(0).getDestinolongitud();
            datosBitacora[14]=lista.get(0).getAtaud();
            datosBitacora[15]=lista.get(0).getPanteon();
            datosBitacora[16]=lista.get(0).getCrematorio();

        }
        return datosBitacora;
    }



    public static boolean isThereLastedDataFromSessions(String usuario, String contrasena)
    {
        List<Sesiones> lista = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES WHERE usuario ='" + usuario +"' and contrasena ='"+ contrasena + "' ORDER BY id DESC LIMIT 1");
        return lista.size()>0;
    }

    public static boolean isThereDataFromSessions()
    {
        List<Sesiones> lista = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES");
        return lista.size()>0;
    }

    public static boolean isThereDataChoferes()
    {
        List<Choferes> lista = Choferes.findWithQuery(Choferes.class, "SELECT * FROM CHOFERES");
        return lista.size()>0;
    }


    public static String[] getUltimoEvento(String bitacora) {
        String[] evento = new String[3];
        String query ="SELECT * FROM EVENTOS WHERE bitacora ='" + bitacora + "' ORDER BY id DESC LIMIT 1";
        List<Eventos> lista = Eventos.findWithQuery(Eventos.class, query);
        if(lista.size()>0){
            evento[0]=lista.get(0).getLugar();
            evento[1]=lista.get(0).getDestino();
            evento[2]=lista.get(0).getTipo();
        }
        return evento;
    }

    public static String getLastDestinoPerBitacora(String bitacora) {
        String query ="SELECT * FROM EVENTOS WHERE bitacora ='" + bitacora + "' ORDER BY id DESC LIMIT 1";
        List<Eventos> lista = Eventos.findWithQuery(Eventos.class, query);
        return lista.size()>0 ? lista.get(0).getDestino() : "";
    }

    public static String getLastLlegadaPerBitacora(String bitacora) {
        String query ="SELECT * FROM EVENTOS WHERE bitacora ='" + bitacora + "' ORDER BY id DESC LIMIT 1";
        List<Eventos> lista = Eventos.findWithQuery(Eventos.class, query);
        return lista.size()>0 ? lista.get(0).getLugar() : "";
    }



    public static String[] getLastedEvent(String bitacora) {
        String[] evento = new String[13];
        String query ="SELECT * FROM EVENTOS WHERE bitacora = '" + bitacora + "' ORDER BY id DESC LIMIT 1";
        List<Eventos> lista = Eventos.findWithQuery(Eventos.class, query);
        if(lista.size()>0){
            evento[0]=lista.get(0).getBitacora();
            evento[1]=lista.get(0).getChofer();
            evento[2]=lista.get(0).getAyudante();
            evento[3]=lista.get(0).getCarro();
            evento[4]=lista.get(0).getDomicilio();
            evento[5]=lista.get(0).getLugar();
            evento[6]=lista.get(0).getDestino();
            evento[7]=lista.get(0).getTipo();
            evento[8]=lista.get(0).getDispositivo();
            evento[9]=lista.get(0).getEstatus();
            evento[10]=lista.get(0).getNombre();
            evento[11]=lista.get(0).getTelefonos();
            evento[12]=lista.get(0).getMovimiento();


        }
        return evento;
    }


    public static String getidFromNombreChofer(String nombreChofer) //Choferes y ayudantes
    {
        String query ="SELECT * FROM CHOFERES WHERE nombre ='" + nombreChofer + "' ORDER BY id DESC LIMIT 1";
        List<Choferes> lista = Choferes.findWithQuery(Choferes.class, query);
        return lista.size() > 0 ? lista.get(0).getIdbase() : "0" ;
    }

    public static String getidFromNombreLugar(String nombreLugar) // Lugares
    {
        String query ="SELECT * FROM LUGARES WHERE nombre ='" + nombreLugar + "' ORDER BY id DESC LIMIT 1";
        List<Lugares> lista = Lugares.findWithQuery(Lugares.class, query);
        return lista.size() > 0 ? lista.get(0).getIdbase() : "0" ;
    }

    public static String getLastesRegisterForArticleScann()
    {
        String query ="SELECT * FROM ARTICULOSCAN ORDER BY id DESC LIMIT 1";
        List<Articuloscan> lista = Articuloscan.findWithQuery(Articuloscan.class, query);
        return String.valueOf(lista.get(0).getId());
    }

    public static String getidFromNombreCarro(String nombreCarro) // Carros
    {
        String query ="SELECT * FROM CARROS WHERE nombre ='" + nombreCarro + "' ORDER BY id DESC LIMIT 1";
        List<Carros> lista = Carros.findWithQuery(Carros.class, query);
        return lista.size() > 0 ? lista.get(0).getIdbase() : "0" ;
    }


    public static String getDomicilioDestinoFromBitacora(String bitacora)
    {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"'");
        return lista.size() > 0 ? lista.get(0).getDestinodomicilio() : "-" ;
    }

    public static String getLatitudDestino(String bitacora)
    {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"'");
        return lista.size() > 0 ? lista.get(0).getDestinolatitud() : "0" ;
    }

    public static String getLongitudDestino(String bitacora)
    {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"'");
        return lista.size() > 0 ? lista.get(0).getDestinolongitud() : "0" ;
    }

    public static String getTimeLastEvent(String bitacora)
    {
        List<Eventos> lista = Eventos.findWithQuery(Eventos.class, "SELECT * FROM EVENTOS WHERE bitacora = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getFecha() : "0000-00-00" ;
    }

    public static String getCodigoDeBarrasBitacora(String bitacora)
    {
        List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM ACTIVOS WHERE bitacora = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getCodigobarras() : "0" ;
    }


    public static String getOrigenBitacora(String bitacora)
    {
        List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM EVENTOS WHERE bitacora = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getLugar() : "-" ;
    }
    public static String getDestinoBitacora(String bitacora)
    {
        List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM EVENTOS WHERE bitacora = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getDestino() : "-" ;
    }









    //** UPDATE BITACORAS **//
    public static void deleteBitacorasActivas() {
        Activos.deleteAll(Activos.class);
    }

    public static void updateBitacorasToCero(String bitacora){
        Activos.executeQuery("UPDATE ACTIVOS set activo = '0' where bitacora='" + bitacora + "'");
    }

    public static void updateBitacoraParaNoVolverAUtilizar(String bitacora){
        Bitacoras.executeQuery("UPDATE BITACORAS set status = '1' where bitacoras = '" + bitacora + "'");
    }


    public static void deleteEventos() {
        Eventos.deleteAll(Eventos.class);
    }

    public static void deleteBitacoras() {
        Bitacoras.deleteAll(Bitacoras.class);
    }


    public static JSONObject getGeofenceZones()
    {
        JSONObject json = new JSONObject();
        String query2="SELECT * FROM LUGARES where status = 'Activo'";
        List<Lugares> lugaresList = Lugares.findWithQuery(Lugares.class, query2);
        if(lugaresList.size()>0)
        {
            try {
            for (int i = 0; i <= lugaresList.size()-1; i++) {

                    json.put("latitud", lugaresList.get(i).getLatitud());
                    json.put("longitud", lugaresList.get(i).getLongitud());
                    json.put("perimetro", lugaresList.get(i).getPerimetro());
                    json.put("nombre", lugaresList.get(i).getNombre());
            }
        } catch (Throwable e) {
        e.printStackTrace();
    }
        }
        else
        {
            Log.i("SIN REGISTROS", "SIN REGISTROS EN LOCALIDADES");
        }
        return json;
    }


    public static int getTotalDeEventosPorSincronizar()
    {
        return Eventos.findWithQuery(Eventos.class, "SELECT * FROM EVENTOS WHERE estatus = 0").size();
    }


    public static String getAtaudPorBitacora(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getAtaud() : "------" ;
    }

    public static String getPanteonPorBitacora(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getPanteon() : "------" ;
    }

    public static String getHoraInicioDeVelacion(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getVelacion() : "------" ;
    }

    public static String getHoraInicioDeCortejo(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getCortejo() : "------" ;
    }

    public static String getTemplo(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getTemplo() : "------" ;
    }

    public static String getCrematorioPorBitacora(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getCrematorio() : "------" ;
    }

    public static boolean laBitacoraExisteEnLaBaseDeDatos(String bitacora) {
        List<Bitacoras> lista = Bitacoras.findWithQuery(Bitacoras.class, "SELECT * FROM BITACORAS WHERE bitacoras = '"+ bitacora +"' and status = '0'");
        return lista.size() > 0;
    }

    public static String getTokenDeUsuario() {
        List<Usuario> lista = Usuario.findWithQuery(Usuario.class, "SELECT * FROM USUARIO ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getToken() : "Unknown" ;
    }

    public static String getIsProveedor() {
        List<Sesiones> lista = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getIsProveedor() : "0" ;
    }


    public static String getDatosDeAtaurnasEnLaBitacora(String bitacora) {
        List<Inventario> lista = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE bitacora = '" + bitacora + "' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getBorrado() : "0" ;
    }

    public static boolean hayDatosDeAtaurnasEnLaBitacora(String bitacora) {
        List<Inventario> lista = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE bitacora = '" + bitacora + "' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ;
    }

    public static String getTipoDeBitacora(String bitacora) {
        List<Eventos> lista = Eventos.findWithQuery(Eventos.class, "SELECT * FROM EVENTOS WHERE bitacora = '" + bitacora + "' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getMovimiento() : "DESCONOCIDO";
    }


    public static boolean yaExisteInformacionDelArticuloDeVelacionDeInstalacion(String serie, String bitacora) {
        List<Equipoinstalacion> lista = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacora + "' and serie ='" + serie + "'");
        return lista.size() > 0 ;
    }

    public static boolean yaExisteInformacionDelArticuloDeVelacionDeCortejo(String serie, String bitacora) {
        List<Equipocortejo> lista = Equipocortejo.findWithQuery(Equipocortejo.class, "SELECT * FROM EQUIPOCORTEJO WHERE bitacora = '" + bitacora + "' and serie ='" + serie + "'");
        return lista.size() > 0 ;
    }

    public static boolean yaExisteInformacionDelArticuloDeVelacionDeTraslado(String serie, String bitacora) {
        List<EquipoTraslado> lista = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO WHERE bitacora = '" + bitacora + "' and serie ='" + serie + "'");
        return lista.size() > 0 ;
    }

    public static boolean yaExisteInformacionDelArticuloDeVelacionDeRecoleccion(String serie, String bitacora) {
        List<EquipoRecoleccion> lista = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION WHERE bitacora = '" + bitacora + "' and serie ='" + serie + "'");
        return lista.size() > 0 ;
    }

    public static String entradaSalidaRecoleccion(String serie, String bitacora) {
        List<EquipoRecoleccion> lista = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION WHERE bitacora = '" + bitacora + "' and serie ='" + serie + "' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getTipo() : "";
    }

    public static String entradaSalidaTraslado(String serie, String bitacora) {
        List<EquipoTraslado> lista = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO WHERE bitacora = '" + bitacora + "' and serie ='" + serie + "' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getTipo() : "";
    }


    public static String getIDFromLaboratorioString(String nombre) {
        List<Laboratorios> lista = Laboratorios.findWithQuery(Laboratorios.class, "SELECT * FROM LABORATORIOS WHERE nombre = '" + nombre + "' ORDER BY id DESC LIMIT 1");
        return lista.size() > 0 ? lista.get(0).getIdLaboratorio() : "";
    }


    public static String didTheDriverCheckIn(String nombreChofer, String nombreAyudante) {
        if(getUserNameFromSesiones().equals(nombreChofer))
            return "1";
        else if(getUserNameFromSesiones().equals(nombreAyudante))
            return "2";
        else
            return "3";
    }

    public static void insertarCatalogoArticulos(String nombre, String letra){
        CatalogoArticulos.deleteAll(CatalogoArticulos.class);
        CatalogoArticulos catalogoArticulos = new CatalogoArticulos(nombre, letra);
        catalogoArticulos.save();
    }

}























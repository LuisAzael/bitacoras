package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class LoginZone extends SugarRecord {

    /**
     * {
     *   "config": [
     *     {
     *       "start_session_blocked": 0,
     *       "start_session_place": "Bunker",
     *       "start_session_lat": "20.65622600",
     *       "start_session_lng": "-103.34647900",
     *       "end_session_place": "Aeropuerto",
     *       "end_session_lat": "0.00000000",
     *       "end_session_lng": "0.00000000",
     *       "update": 1
     *     }
     *   ]
     * }
     */
    String start_session_blocked="", start_session_place="", start_session_lat="", start_session_lng="", end_session_place="",end_session_lat="",end_session_lng="", end_session_blocked="";

    public LoginZone() {
        super();
    }

    public LoginZone(String start_session_blocked, String start_session_place, String start_session_lat, String start_session_lng,
                     String end_session_place, String end_session_lat, String end_session_lng, String end_session_blocked) {
        this.start_session_blocked = start_session_blocked;
        this.start_session_place = start_session_place;
        this.start_session_lat = start_session_lat;
        this.start_session_lng = start_session_lng;
        this.end_session_place = end_session_place;
        this.end_session_lat = end_session_lat;
        this.end_session_lng = end_session_lng;
        this.end_session_blocked = end_session_blocked;
    }

    public String getStart_session_blocked() {
        return start_session_blocked;
    }

    public void setStart_session_blocked(String start_session_blocked) {
        this.start_session_blocked = start_session_blocked;
    }

    public String getStart_session_place() {
        return start_session_place;
    }

    public void setStart_session_place(String start_session_place) {
        this.start_session_place = start_session_place;
    }

    public String getStart_session_lat() {
        return start_session_lat;
    }

    public void setStart_session_lat(String start_session_lat) {
        this.start_session_lat = start_session_lat;
    }

    public String getStart_session_lng() {
        return start_session_lng;
    }

    public void setStart_session_lng(String start_session_lng) {
        this.start_session_lng = start_session_lng;
    }

    public String getEnd_session_place() {
        return end_session_place;
    }

    public void setEnd_session_place(String end_session_place) {
        this.end_session_place = end_session_place;
    }

    public String getEnd_session_lat() {
        return end_session_lat;
    }

    public void setEnd_session_lat(String end_session_lat) {
        this.end_session_lat = end_session_lat;
    }

    public String getEnd_session_lng() {
        return end_session_lng;
    }

    public void setEnd_session_lng(String end_session_lng) {
        this.end_session_lng = end_session_lng;
    }

    public String getEnd_session_blocked() {
        return end_session_blocked;
    }

    public void setEnd_session_blocked(String end_session_blocked) {
        this.end_session_blocked = end_session_blocked;
    }
}

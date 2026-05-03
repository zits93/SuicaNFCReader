package com.example.suicanfcreader.lib;

import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SuicaReader {

    public int termId;
    public int procId;
    public int year;
    public int month;
    public int day;
    public String kind;
    public int remain;
    public int seqNo;
    public int reasion;
    public int inStation;
    public int inLine;
    public int outStation;
    public int outLine;

    public String device;
    public String action;

    public static final SparseArray<String> DEVICE_LIST = new SparseArray<>();
    public static final SparseArray<String> ACTION_LIST = new SparseArray<>();

    public SuicaReader(){}

    public static SuicaReader parse(byte[] res, int off) {
        SuicaReader self = new SuicaReader();
        self.init(res, off);
        return self;
    }

    private void init(byte[] res, int off) {
        this.termId = res[off];
        this.procId = res[off+1];
        //2-3: ??
        int mixInt = toInt(res, off, 4,5);
        this.year  = (mixInt >> 9) & 0x07f;
        this.month = (mixInt >> 5) & 0x00f;
        this.day   = mixInt & 0x01f;

        if (isShopping(this.procId)) {
            this.kind = "물판";
        } else if (isBus(this.procId)) {
            this.kind = "버스";
        } else {
            this.kind = res[off+6] < 0x80 ? "JR" : "공영/사철" ;
        }

        this.inLine = toInt(res, off, 6);         // 6: 出線区
        this.inStation = toInt(res, off, 7);      // 7: 入駅
        this.outLine = toInt(res, off, 8);        // 8: 出線区
        this.outStation = toInt(res, off, 9);     // 9: 出駅
        this.remain  = toInt(res, off, 11,10);    // 10-11: 残高 (little endian)
        this.seqNo   = toInt(res, off, 12,13,14); // 12-14: 連番
        this.reasion = res[off+15];               // 15: リージョン

        this.device = DEVICE_LIST.get(this.termId);
        this.action = ACTION_LIST.get(this.procId);
    }

    private int toInt(byte[] res, int off, int... idx) {
        int num = 0;
        for (int j : idx) {
            num = num << 8;
            num += ((int) res[off + j]) & 0x0ff;
        }
        return num;
    }
    private boolean isShopping(int procId) {
        return procId == 70 || procId == 73 || procId == 74 || procId == 75 || procId == 198 || procId == 203;
    }
    private boolean isBus(int procId) {
        return procId == 13|| procId == 15|| procId ==  31|| procId == 35;
    }

    public static byte[] readWithoutEncryption(byte[] idm, int size)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

        bout.write(0);
        bout.write(0x06);
        bout.write(idm);
        bout.write(1);
        bout.write(0x0f);
        bout.write(0x09);
        bout.write(size);
        for (int i = 0; i < size; i++) {
            bout.write(0x80);
            bout.write(i);
        }

        byte[] msg = bout.toByteArray();
        msg[0] = (byte) msg.length;
        return msg;
    }

    static {
        DEVICE_LIST.put(3 , "정산기");
        DEVICE_LIST.put(4 , "휴대형 단말");
        DEVICE_LIST.put(5 , "차재 단말");
        DEVICE_LIST.put(7 , "매표기");
        DEVICE_LIST.put(8 , "매표기");
        DEVICE_LIST.put(9 , "입금기");
        DEVICE_LIST.put(18 , "매표기");
        DEVICE_LIST.put(20 , "매표기 등");
        DEVICE_LIST.put(21 , "매표기 등");
        DEVICE_LIST.put(22 , "개찰기");
        DEVICE_LIST.put(23 , "간이 개찰기");
        DEVICE_LIST.put(24 , "창구 단말");
        DEVICE_LIST.put(25 , "창구 단말");
        DEVICE_LIST.put(26 , "개찰 단말");
        DEVICE_LIST.put(27 , "휴대폰");
        DEVICE_LIST.put(28 , "환승 정산기");
        DEVICE_LIST.put(29 , "연락 개찰기");
        DEVICE_LIST.put(31 , "간이 입금기");
        DEVICE_LIST.put(70 , "VIEW ALTTE");
        DEVICE_LIST.put(72 , "VIEW ALTTE");
        DEVICE_LIST.put(199 , "물판 단말");
        DEVICE_LIST.put(200 , "자판기");

        ACTION_LIST.put(1 , "운임 지불(개찰 출장)");
        ACTION_LIST.put(2 , "충전");
        ACTION_LIST.put(3 , "권구(자기권 구입)");
        ACTION_LIST.put(4 , "정산");
        ACTION_LIST.put(5 , "정산 (입장 정산)");
        ACTION_LIST.put(6 , "창출 (개찰 창구 처리)");
        ACTION_LIST.put(7 , "신규 (신규 발행)");
        ACTION_LIST.put(8 , "공제 (창구 공제)");
        ACTION_LIST.put(13 , "버스 (PiTaPa계)");
        ACTION_LIST.put(15 , "버스 (IruCa계)");
        ACTION_LIST.put(17 , "재발 (재발행 처리)");
        ACTION_LIST.put(19 , "지불 (신칸센 이용)");
        ACTION_LIST.put(20 , "입A (입장 시 오토 차지)");
        ACTION_LIST.put(21 , "출A (출장 시 오토 차지)");
        ACTION_LIST.put(31 , "입금 (버스 충전)");
        ACTION_LIST.put(35 , "권구 (버스 노면 전차 기획권 구입)");
        ACTION_LIST.put(70 , "물판");
        ACTION_LIST.put(72 , "특전 (특전 충전)");
        ACTION_LIST.put(73 , "입금 (레지 입금)");
        ACTION_LIST.put(74 , "물판 취소");
        ACTION_LIST.put(75 , "입물 (입장 물판)");
        ACTION_LIST.put(198 , "물현 (현금 병용 물판)");
        ACTION_LIST.put(203 , "입물 (입장 현금 병용 물판)");
        ACTION_LIST.put(132 , "정산 (타사 정산)");
        ACTION_LIST.put(133 , "정산 (타사 입장 정산)");
    }

}